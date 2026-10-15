# Step 2 - JWT authentication

## What to do
Copy the `src` folder from this zip into your project (merge). That is all.

- NO existing file is modified (pom.xml, application.yml and every Step 1 file stay exactly as they are).
- NO new Maven dependency is needed (JWT is built with the JDK + Jackson, both already present).
- application.yml is optional: the JWT secret and expiry have defaults in code.
  To override later, add at the end of application.yml:
      jwt:
        secret: <at least 32 characters>
        expiration-ms: 86400000

## If you already applied the earlier Step 2 zip
Overwrite JwtService.java and JwtAuthenticationFilter.java with these versions and add InvalidTokenException.java.
The jjwt lines you may have added to pom.xml are now unused: you can delete them.
