**step-4 in milestone 1**



**A. going to  Save  4 tests (201 Created)**

**URL	Body	Expected co2eKg**

**POST .../api/activities/transport	{"transportMode":"CAR\_PETROL","distanceKm":25}	4.8000**

**POST .../api/activities/electricity	{"energySource":"GRID","kwh":150}	106.5000**

**POST .../api/activities/food	{"mealType":"BEEF\_MEAL","servings":2}	13.8000**

**POST .../api/activities/shopping	{"productCategory":"ELECTRONICS","spendAmount":200}	100.0000**



**Response ippadi irukkum:**



**json**

**{ "id": 1, "category": "TRANSPORT", "activityType": "CAR\_PETROL", "quantity": 25.000,**

&#x20; **"unit": "KM", "kgCo2ePerUnit": 0.192000, "co2eKg": 4.8000,**

&#x20; **"emissionFactorId": 1, "logDate": "2026-10-11" }**



**B. wrong cases (everything 400 will come)**

&#x09;

|**Body**|**Expected message**|
|-|-|
|**{"transportMode":"CAR\_PETROL","distanceKm":0}**|**distanceKm: "Distance must be greater than 0"**|
|**{"transportMode":"CAR\_PETROL","distanceKm":-5}**|**same**|
|**{"transportMode":"CAR\_PETROL","distanceKm":25000}**|**"Distance must not exceed 20,000 km per entry"**|
|**{"transportMode":"ROCKET","distanceKm":10}**|**"No active emission factor for activity 'ROCKET'..."**|
|**{"transportMode":"BEEF\_MEAL","distanceKm":10}**|**"Activity 'BEEF\_MEAL' belongs to category FOOD, not TRANSPORT"**|
|**transport {"transportMode":"CAR\_PETROL"}**|"Distance is required"|
|**"date":"2030-01-01"**|**"Date cannot be in the future"**|
|**{"mealType":"VEGAN\_MEAL","servings":500}**|**"Servings must not exceed 100 per entry"**|





**C. To see Logs  / delete**



**GET http://localhost:8090/api/activities  --> it iell store all the logs and also add the new log at first**



**GET http://localhost:8090/api/activities?category=FOOD → for food only.**



**GET http://localhost:8090/api/activities?from=2026-10-01\&to=2026-10-31  => date range.**



**GET  http://localhost:8090/api/activities?from=2026-12-01\&to=2026-10-01   => 400 “‘from’ date must not be after ‘to’ date”.**



**GET http://localhost:8090/api/activities/1  => only that log**



**D. Security**



**if there is not token  GET /api/activities → 401.**



**E. Workbench DB verify**



&#x09;

