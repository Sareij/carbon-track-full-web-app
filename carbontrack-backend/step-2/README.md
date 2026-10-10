# CarbonTrack Backend (Milestone 1 - Step 1)

## Run
1. Start MySQL:  `docker compose up -d`
2. Start app:   `mvn spring-boot:run`  (Java 17+, Maven 3.9+)
3. Flyway runs V1, V2, V3 automatically on startup.

## Verify
```
docker exec -it carbontrack-mysql mysql -ucarbon -pcarbon123 carbontrack -e "SHOW TABLES; SELECT activity_type, unit, kg_co2e_per_unit FROM emission_factors LIMIT 5; SELECT * FROM flyway_schema_history;"
```
