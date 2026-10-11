# Step 3 - Emission calculation engine

Only NEW files. No existing file (pom.xml, application.yml, Step 1, Step 2) is modified.
Copy the `src` folder into your project (merge) and run.

## New endpoints
| Method | URL | Who | Purpose |
|---|---|---|---|
| GET  | /api/emission-factors[?category=TRANSPORT] | any logged-in user | current factors (for dropdowns) |
| POST | /api/emissions/calculate | any logged-in user | preview CO2e, nothing saved |
| GET  | /api/admin/emission-factors | ADMIN | all rows incl. old/inactive |
| POST | /api/admin/emission-factors | ADMIN | add a factor (new effectiveDate = revision) |
| PUT  | /api/admin/emission-factors/{id} | ADMIN | correct value/source or deactivate |

## Make yourself ADMIN (run in MySQL Workbench)
UPDATE carbontrack.users SET role = 'ADMIN' WHERE id = 1;
