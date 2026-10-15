# Step 5 - JUnit 5 tests for the emission engine

Only NEW files (under src/test). No existing file or folder is modified, pom.xml is NOT changed
(spring-boot-starter-test = JUnit 5 + Mockito is already a dependency).

| File | What it tests |
|---|---|
| EmissionComputeTest | compute() maths: 4 parameterised tables (transport, electricity, food, shopping) + rounding |
| EmissionCalculationServiceTest | zero, maximum, above-maximum, negative, null, unknown activity, category mismatch, input normalisation |

No database, no Spring context and no running app are needed.

## Run
IntelliJ: right-click the folder  src/test/java  ->  Run 'All Tests'
Maven   : mvn test
