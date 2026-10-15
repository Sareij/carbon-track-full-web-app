# Step 4 - Activity logging APIs

Only NEW files. No existing file or folder is modified. No new migration (tables already exist).
Copy the `src` folder into your project (merge) and restart.

| Method | URL | Payload fields |
|---|---|---|
| POST | /api/activities/transport   | transportMode, distanceKm, [date] |
| POST | /api/activities/electricity | energySource, kwh, [date] |
| POST | /api/activities/food        | mealType, servings, [date] |
| POST | /api/activities/shopping    | productCategory, spendAmount, [date] |
| GET  | /api/activities?from=&to=&category= | your logs (default last 30 days) |
| GET  | /api/activities/{id} | one of your logs |
| DELETE | /api/activities/{id} | delete one of your logs |
