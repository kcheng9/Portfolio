-- create events table
DROP TABLE IF EXISTS events;
CREATE EXTERNAL TABLE events (
	patient_id STRING,
	event_id STRING,
	event_description STRING,
	time DATE,
	value DOUBLE)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '/input/events';

--create mortality events table
DROP TABLE IF EXISTS mortality;
CREATE EXTERNAL TABLE mortality (
	patient_id STRING,
	time DATE,
	label INT)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '/input/mortality';

--alive events
DROP VIEW IF EXISTS alive_events;
CREATE VIEW alive_events AS
SELECT events.patient_id, events.event_id, events.time
FROM events
LEFT OUTER JOIN mortality
ON mortality.patient_id = events.patient_id
WHERE mortality.label is NULL;

--dead events
DROP VIEW IF EXISTS dead_events;
CREATE VIEW dead_events AS
SELECT events.patient_id, events.event_id, events.time
FROM events
LEFT OUTER JOIN mortality
ON mortality.patient_id = events.patient_id
WHERE mortality.label = 1;

--alive event metrics
SELECT avg(event_count), min(event_count), max(event_count)
FROM (
	SELECT count(*) event_count
	FROM alive_events
	GROUP BY patient_id
) a;

--dead event metrics
SELECT avg(event_count), min(event_count), max(event_count)
FROM (
	SELECT count(*) event_count
	FROM dead_events
	GROUP BY patient_id
) a;

--alive encounter
SELECT avg(encounter_count), min(encounter_count), max(encounter_count)
FROM (
	SELECT count(DISTINCT time) encounter_count
	FROM alive_events
	GROUP BY patient_id
) a;

--dead encounter
SELECT avg(encounter_count), min(encounter_count), max(encounter_count)
FROM (
	SELECT count(DISTINCT time) encounter_count
	FROM dead_events
	GROUP BY patient_id
) a;

--alive record length
SELECT avg(record_length), min(record_length), max(record_length)
FROM (
	SELECT datediff(maxtime, mintime) as record_length
	FROM (
		SELECT min(time) as mintime, max(time) as maxtime
		FROM alive_events
		GROUP BY patient_id
	) a
) b;

--dead record length
SELECT avg(record_length), min(record_length), max(record_length)
FROM (
	SELECT datediff(maxtime, mintime) as record_length
	FROM (
		SELECT min(time) as mintime, max(time) as maxtime
		FROM dead_events
		GROUP BY patient_id
	) a
) b;

--alive diag/lab/med most common
SELECT event_id, count(*) AS diag_count
FROM alive_events
WHERE event_id like "DIAG%"
GROUP BY event_id
ORDER BY diag_count
DESC LIMIT 5;

SELECT event_id, count(*) AS lab_count
FROM alive_events
WHERE event_id like "LAB%"
GROUP BY event_id
ORDER BY lab_count
DESC LIMIT 5;

SELECT event_id, count(*) AS med_count
FROM alive_events
WHERE event_id like "DRUG%"
GROUP BY event_id
ORDER BY med_count
DESC LIMIT 5;

--dead diag/lab/med most common
SELECT event_id, count(*) AS diag_count
FROM dead_events
WHERE event_id like "DIAG%"
GROUP BY event_id
ORDER BY diag_count
DESC LIMIT 5;

SELECT event_id, count(*) AS lab_count
FROM dead_events
WHERE event_id like "LAB%"
GROUP BY event_id
ORDER BY lab_count
DESC LIMIT 5;

SELECT event_id, count(*) AS med_count
FROM dead_events
WHERE event_id like "DRUG%"
GROUP BY event_id
ORDER BY med_count
DESC LIMIT 5;
