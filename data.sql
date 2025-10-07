CREATE DATABASE weatherdb;

USE weatherdb;

SELECT * FROM locations;


UPDATE locations l
SET trashed = false
WHERE l.code = 'NYC_USA';

SELECT * FROM realtime_weather;

SELECT *
FROM locations
WHERE city_name = 'New York City' AND country_code = 'US';

ALTER TABLE locations
DROP COLUMN location_code;

ALTER TABLE locations
RENAME COLUMN code TO location_code;

SELECT * FROM weather_hourly WHERE location_code = 'DELHI_IN';

