REGISTER utils.py USING jython as utils;

events = LOAD '../../data/events.csv' USING PigStorage(',') AS (patientid:int, eventid:chararray, eventdesc:chararray, timestamp:chararray, value:float);

events = FOREACH events GENERATE patientid, eventid, ToDate(timestamp, 'yyyy-MM-dd') AS etimestamp, value;

mortality = LOAD '../../data/mortality.csv' USING PigStorage(',') AS (patientid:int, timestamp:chararray, label:int);

mortality = FOREACH mortality GENERATE patientid, ToDate(timestamp, 'yyyy-MM-dd') AS mtimestamp, label;

eventswithmort = JOIN mortality BY patientid FULL OUTER, events BY patientid;

deadindex = FOREACH mortality GENERATE $0 AS patientid, SubtractDuration(mtimestamp,'P30D') AS indexdate;

deadevents = FILTER eventswithmort BY (mortality::label==1);

deadevents = JOIN deadevents BY mortality::patientid FULL OUTER, deadindex BY patientid;

deadevents = FOREACH deadevents GENERATE $0 AS patientid, $4 AS eventid, $6 AS value, $2 AS label, DaysBetween($8, $5) AS time_difference;

aliveevents = FILTER eventswithmort by (mortality::label IS NULL);

patientgroup = GROUP aliveevents BY events::patientid;

aliveindex = FOREACH patientgroup GENERATE flatten(aliveevents.events::patientid) as patientid, MAX(aliveevents.events::etimestamp) as indexdate;

aliveindex = DISTINCT aliveindex;

aliveevents = JOIN aliveevents BY events::patientid FULL OUTER, aliveindex BY patientid;

aliveevents = FOREACH aliveevents GENERATE $3 AS patientid, $4 as eventid, $6 AS value, 0 AS label, DaysBetween($8, $5) AS time_difference;

deadevents = ORDER deadevents BY patientid, eventid;
aliveevents = ORDER aliveevents BY patientid, eventid;
STORE aliveevents INTO 'aliveevents' USING PigStorage(',');
STORE deadevents INTO 'deadevents' USING PigStorage(',');

filtered = UNION deadevents, aliveevents;
filtered = FILTER filtered by (time_difference <= 2000);
filtered = FILTER filtered by (time_difference >=0);
filtered = FILTER filtered by (value is not NULL);

filteredgrpd = GROUP filtered BY 1;
filtered = FOREACH filteredgrpd GENERATE FLATTEN(filtered);
filtered = ORDER filtered BY patientid, eventid, time_difference;
STORE filtered INTO 'filtered' USING PigStorage(',');

featureswithid = GROUP filtered BY (patientid, eventid);
featureswithid = FOREACH featureswithid GENERATE FLATTEN(group.patientid), FLATTEN(group.eventid), FLATTEN(COUNT(filtered.eventid)) AS featurevalue;
featureswithid = FOREACH featureswithid GENERATE FLATTEN(patientid) AS patientid, FLATTEN(eventid) AS eventid, FLATTEN(featurevalue) AS featurevalue;
featureswithid = DISTINCT featureswithid;

--filteredgroup = GROUP filtered BY (patientid, eventid);
--featureswithid = FOREACH filteredgroup GENERATE FLATTEN(filtered.patientid) as patientid, FLATTEN(filtered.eventid) as eventid, COUNT(filtered.eventid) as featurevalue;
--featureswithid = DISTINCT featureswithid;

featureswithid = ORDER featureswithid BY patientid, eventid;
STORE featureswithid INTO 'features_aggregate' USING PigStorage(',');

groupedfeatures = GROUP featureswithid BY eventid;
eventids = FOREACH groupedfeatures GENERATE FLATTEN(featureswithid.eventid) as eventid;
eventids = DISTINCT eventids;
eventids = ORDER eventids BY eventid;
all_features = RANK eventids;
all_features = FOREACH all_features GENERATE rank_eventids-1 as idx, eventid;

STORE all_features INTO 'features' using PigStorage(' ');

joinedfeatures = JOIN featureswithid BY eventid FULL OUTER, all_features BY eventid;
features = FOREACH joinedfeatures GENERATE patientid, idx, featurevalue;
features = FOREACH features GENERATE featureswithid::patientid as patientid, all_features::idx as idx, featureswithid::featurevalue as featurevalue;

features = ORDER features BY patientid, idx;
STORE features INTO 'features_map' USING PigStorage(',');

groupedevents = GROUP features BY idx;
maxvalues = FOREACH groupedevents GENERATE FLATTEN(features.idx) as idx, MAX(features.featurevalue) as maxvalues;
maxvalues = DISTINCT maxvalues;

normalized = JOIN maxvalues BY idx FULL OUTER, features BY idx;

features = FOREACH normalized GENERATE features::patientid as patientid, maxvalues::idx as idx, (double) featurevalue/maxvalues as normalizedfeaturevalue;

features = ORDER features BY patientid, idx;
STORE features INTO 'features_normalized' USING PigStorage(',');

grpd = GROUP features BY patientid;
grpd_order = ORDER grpd BY $0;
features = FOREACH grpd_order
{
	sorted = ORDER features BY idx;
	generate group as patientid, utils.bag_to_svmlight(sorted) as sparsefeature;
}

labelgroup = FOREACH (GROUP filtered BY patientid) {
	GENERATE FLATTEN(filtered);
}
labels = FOREACH labelgroup GENERATE FLATTEN(filtered::patientid) as patientid, FLATTEN(filtered::label) as label;
labels = DISTINCT labels;

samples = JOIN features BY patientid, labels BY patientid;
samples = DISTINCT samples PARALLEL 1;
samples = ORDER samples BY $0;
samples = FOREACH samples GENERATE $3 AS label, $1 AS sparsefeature;

STORE samples INTO 'samples' USING PigStorage(' ');

samples = FOREACH samples GENERATE RANDOM() as assignmentkey, *;
SPLIT samples INTO testing IF assignmentkey <= 0.20, training OTHERWISE;
training = FOREACH training GENERATE $1..;
testing = FOREACH testing GENERATE $1..;

STORE testing INTO 'testing' USING PigStorage(' ');
STORE training INTO 'training' USING PigStorage(' ');