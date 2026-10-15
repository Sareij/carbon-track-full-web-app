package com.carbontrack.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Creates and verifies JWTs (HS256) using only the JDK + Jackson (already in Spring Boot).
 *
 * A JWT is three Base64URL parts joined by dots:   HEADER . PAYLOAD . SIGNATURE
 *   HEADER    = {"alg":"HS256","typ":"JWT"}
 *   PAYLOAD   = {"sub":"saranya","role":"USER","iat":...,"exp":...}
 *   SIGNATURE = HMAC-SHA256(HEADER.PAYLOAD, secretKey)
 * Anyone can READ a token, but nobody can change it without the secret, because the signature would no longer match.
 */
@Service
public class JwtService {

    private static final Base64.Encoder B64_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder B64_DECODER = Base64.getUrlDecoder();
    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final byte[] secret;
    private final long expirationMs;
    private final ObjectMapper mapper = new ObjectMapper();

    // The ":default" part means application.yml is OPTIONAL. Add jwt.secret / jwt.expiration-ms there later to override.
    public JwtService(
            @Value("${jwt.secret:carbontrack-dev-secret-key-change-me-in-production-0123456789}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        if (this.secret.length < 32) {
            throw new IllegalStateException("jwt.secret must be at least 32 characters (256 bits)");
        }
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, String role) {
        long nowSeconds = Instant.now().getEpochSecond();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", username);
        payload.put("role", role);
        payload.put("iat", nowSeconds);
        payload.put("exp", nowSeconds + expirationMs / 1000);

        try {
            String header = B64_ENCODER.encodeToString(HEADER_JSON.getBytes(StandardCharsets.UTF_8));
            String body = B64_ENCODER.encodeToString(mapper.writeValueAsBytes(payload));
            String signingInput = header + "." + body;
            return signingInput + "." + sign(signingInput);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not build JWT", e);
        }
    }

    /** Verifies signature + algorithm + expiry, then returns the username. Throws InvalidTokenException otherwise. */
    public String extractUsername(String token) {
        Object sub = verifyAndParse(token).get("sub");
        if (!(sub instanceof String s) || s.isBlank()) {
            throw new InvalidTokenException("Token has no subject");
        }
        return s;
    }

    public boolean isTokenValid(String token, String expectedUsername) {
        return expectedUsername.equals(extractUsername(token));
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    // ---------------------------------------------------------------- internals

    private Map<String, Object> verifyAndParse(String token) {
        String[] parts = token.split("\\.", -1);
        if (parts.length != 3) {
            throw new InvalidTokenException("Malformed token");
        }
        try {
            // 1. Header must say HS256 (blocks the classic "alg":"none" forgery trick)
            Map<String, Object> header = mapper.readValue(B64_DECODER.decode(parts[0]), MAP_TYPE);
            if (!"HS256".equals(header.get("alg"))) {
                throw new InvalidTokenException("Unsupported algorithm");
            }

            // 2. Recompute the signature and compare in constant time (prevents timing attacks)
            String expected = sign(parts[0] + "." + parts[1]);
            if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                                       parts[2].getBytes(StandardCharsets.UTF_8))) {
                throw new InvalidTokenException("Invalid signature");
            }

            // 3. Signature is good -> payload can be trusted. Check expiry.
            Map<String, Object> payload = mapper.readValue(B64_DECODER.decode(parts[1]), MAP_TYPE);
            Object exp = payload.get("exp");
            if (!(exp instanceof Number n) || n.longValue() <= Instant.now().getEpochSecond()) {
                throw new InvalidTokenException("Token expired");
            }
            return payload;
        } catch (IOException | IllegalArgumentException e) {
            // bad Base64 or bad JSON
            throw new InvalidTokenException("Malformed token");
        }
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);   // Mac is not thread-safe, so one per call
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return B64_ENCODER.encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HMAC-SHA256 not available", e);
        }
    }
}
