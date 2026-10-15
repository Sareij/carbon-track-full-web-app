-- V3: seed data.
-- NOTE: factor values are ILLUSTRATIVE starting points for development.
-- Before any real use, verify each against the current IPCC / EPA / national grid tables.
-- Because they live in the DB, an admin can correct them without redeploying.

INSERT INTO emission_factors (category, activity_type, unit, kg_co2e_per_unit, source, effective_date) VALUES
-- TRANSPORT (per km)
('TRANSPORT', 'CAR_PETROL',        'KM',  0.192000, 'Illustrative (EPA/IPCC based)', '2024-01-01'),
('TRANSPORT', 'CAR_DIESEL',        'KM',  0.171000, 'Illustrative (EPA/IPCC based)', '2024-01-01'),
('TRANSPORT', 'FLIGHT_SHORT_HAUL', 'KM',  0.156000, 'Illustrative (EPA/IPCC based)', '2024-01-01'),
('TRANSPORT', 'FLIGHT_LONG_HAUL',  'KM',  0.150000, 'Illustrative (EPA/IPCC based)', '2024-01-01'),
('TRANSPORT', 'BUS',               'KM',  0.105000, 'Illustrative (EPA/IPCC based)', '2024-01-01'),
('TRANSPORT', 'TRAIN',             'KM',  0.041000, 'Illustrative (EPA/IPCC based)', '2024-01-01'),
('TRANSPORT', 'METRO',             'KM',  0.033000, 'Illustrative (EPA/IPCC based)', '2024-01-01'),
-- ELECTRICITY (per kWh, depends on energy source)
('ELECTRICITY', 'GRID',            'KWH', 0.710000, 'Illustrative (national grid average)', '2024-01-01'),
('ELECTRICITY', 'SOLAR',           'KWH', 0.041000, 'Illustrative (IPCC lifecycle)',        '2024-01-01'),
('ELECTRICITY', 'WIND',            'KWH', 0.011000, 'Illustrative (IPCC lifecycle)',        '2024-01-01'),
-- FOOD (per serving)
('FOOD', 'BEEF_MEAL',              'SERVING', 6.900000, 'Illustrative (food LCA studies)', '2024-01-01'),
('FOOD', 'CHICKEN_MEAL',           'SERVING', 1.800000, 'Illustrative (food LCA studies)', '2024-01-01'),
('FOOD', 'FISH_MEAL',              'SERVING', 1.400000, 'Illustrative (food LCA studies)', '2024-01-01'),
('FOOD', 'VEGETARIAN_MEAL',        'SERVING', 0.900000, 'Illustrative (food LCA studies)', '2024-01-01'),
('FOOD', 'VEGAN_MEAL',             'SERVING', 0.500000, 'Illustrative (food LCA studies)', '2024-01-01'),
-- SHOPPING (per USD spent)
('SHOPPING', 'ELECTRONICS',        'USD', 0.500000, 'Illustrative (spend-based EEIO)', '2024-01-01'),
('SHOPPING', 'CLOTHING',           'USD', 0.300000, 'Illustrative (spend-based EEIO)', '2024-01-01'),
('SHOPPING', 'FURNITURE',          'USD', 0.350000, 'Illustrative (spend-based EEIO)', '2024-01-01'),
('SHOPPING', 'GROCERIES',          'USD', 0.400000, 'Illustrative (spend-based EEIO)', '2024-01-01');

INSERT INTO badges (name, description, trigger_type, threshold) VALUES
('Week Warrior',    'Logged activities 7 days in a row',     'STREAK',    7),
('Goal Getter',     'Achieved your first reduction goal',    'GOAL',      1),
('Carbon Cutter 10','Reduced your footprint by 10 kg CO2e',  'REDUCTION', 10),
('Carbon Cutter 25','Reduced your footprint by 25 kg CO2e',  'REDUCTION', 25),
('Carbon Cutter 50','Reduced your footprint by 50 kg CO2e',  'REDUCTION', 50);
