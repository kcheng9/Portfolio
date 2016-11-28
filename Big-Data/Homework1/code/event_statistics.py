import time
import csv
import pandas as pd
import numpy as np
import datetime

def read_csv(filepath):
    '''
    Read the events.csv and mortality_events.csv files. Variables returned from this function are passed as input to the metric functions.
    This function needs to be completed.
    '''

    filePath = filepath+'/events.csv'
    events = pd.read_csv(filePath)

    filePath = filepath+'/mortality_events.csv'
    mortality = pd.read_csv(filePath)

    return events, mortality

def event_count_metrics(events, mortality):
    '''
    Event count is defined as the number of events recorded for a given patient.
    This function needs to be completed.
    '''

    #print events.head(5)
    #print mortality.head(5)
    #alivePatientEventCount = pd.DataFrame()
    #print mortality.patient_id[0]

    # eventCountsAll = pd.Series(events.patient_id.value_counts(),index=events.patient_id)
    # print eventCountsAll
    #
    # deadOrNot = eventCountsAll.isin(mortality.patient_id)

    ##joined is a dataframe that merges events and mortality. In the label column, if the patient is dead, it will say
    ##1. If the patient is not dead, it will say NaN. Dead patient example 19. Not dead patient example 11981.
    joined = pd.merge(events,mortality,how='left',on='patient_id')

    deadJoined = joined[joined.label == 1]
    aliveJoined = joined[joined.label != 1]

    #This next line gives me the count per patient
    deadCount = deadJoined.groupby('patient_id').count()
    aliveCount = aliveJoined.groupby('patient_id').count()

    avg_dead_event_count = deadCount['label'].mean()

    max_dead_event_count = deadCount['label'].max()

    min_dead_event_count = deadCount['label'].min()

    avg_alive_event_count = aliveCount['event_id'].mean()

    max_alive_event_count = aliveCount['event_id'].max()

    min_alive_event_count = aliveCount['event_id'].min()

    return min_dead_event_count, max_dead_event_count, avg_dead_event_count, min_alive_event_count, max_alive_event_count, avg_alive_event_count

def encounter_count_metrics(events, mortality):
    '''
    Encounter count is defined as the count of unique dates on which a given patient visited the ICU. 
    This function needs to be completed.
    '''

    joined = pd.merge(events,mortality,how='left',on='patient_id')

    deadJoined = joined[joined.label == 1]
    aliveJoined = joined[joined.label != 1]

    #print events.groupby('patient_id')['timestamp'].nunique()

    deadEncounters = deadJoined.groupby('patient_id')['timestamp_x'].nunique()
    aliveEncounters = aliveJoined.groupby('patient_id')['timestamp_x'].nunique()

    avg_dead_encounter_count = deadEncounters.mean()

    max_dead_encounter_count = deadEncounters.max()

    min_dead_encounter_count = deadEncounters.min()

    avg_alive_encounter_count = aliveEncounters.mean()

    max_alive_encounter_count = aliveEncounters.max()

    min_alive_encounter_count = aliveEncounters.min()

    return min_dead_encounter_count, max_dead_encounter_count, avg_dead_encounter_count, min_alive_encounter_count, max_alive_encounter_count, avg_alive_encounter_count

def record_length_metrics(events, mortality):
    '''
    Record length is the duration between the first event and the last event for a given patient. 
    This function needs to be completed.
    '''

    joined = pd.merge(events,mortality,how='left',on='patient_id')

    deadJoined = joined[joined.label == 1]
    aliveJoined = joined[joined.label != 1]

    deadMin = deadJoined.groupby('patient_id').min()
    deadMax = deadJoined.groupby('patient_id').max()
    aliveMin = aliveJoined.groupby('patient_id').min()
    aliveMax = aliveJoined.groupby('patient_id').max()

    deadMinTimes = deadMin['timestamp_x'].apply(lambda x: datetime.datetime.strptime(x,'%Y-%m-%d'))
    deadMaxTimes = deadMax['timestamp_x'].apply(lambda x: datetime.datetime.strptime(x,'%Y-%m-%d'))
    aliveMinTimes = aliveMin['timestamp_x'].apply(lambda x: datetime.datetime.strptime(x,'%Y-%m-%d'))
    aliveMaxTimes = aliveMax['timestamp_x'].apply(lambda x: datetime.datetime.strptime(x,'%Y-%m-%d'))

    deltaDead = (np.asarray(deadMaxTimes) - np.asarray(deadMinTimes)).astype('timedelta64[D]').astype(int)
    deltaAlive = (np.asarray(aliveMaxTimes) - np.asarray(aliveMinTimes)).astype('timedelta64[D]').astype(int)

    avg_dead_rec_len = np.mean(deltaDead)

    max_dead_rec_len = np.max(deltaDead)

    min_dead_rec_len = np.min(deltaDead)

    avg_alive_rec_len = np.mean(deltaAlive)

    max_alive_rec_len = np.max(deltaAlive)

    min_alive_rec_len = np.min(deltaAlive)

    return min_dead_rec_len, max_dead_rec_len, avg_dead_rec_len, min_alive_rec_len, max_alive_rec_len, avg_alive_rec_len

def main():
    '''
    DONOT MODIFY THIS FUNCTION. 
    Just update the train_path variable to point to your train data directory.
    '''
    #Modify the filepath to point to the CSV files in train_data
    train_path = r'../data/train'
    events, mortality = read_csv(train_path)

    #Compute the event count metrics
    start_time = time.time()
    event_count = event_count_metrics(events, mortality)
    end_time = time.time()
    print("Time to compute event count metrics: " + str(end_time - start_time) + "s")
    print event_count

    #Compute the encounter count metrics
    start_time = time.time()
    encounter_count = encounter_count_metrics(events, mortality)
    end_time = time.time()
    print("Time to compute encounter count metrics: " + str(end_time - start_time) + "s")
    print encounter_count

    #Compute record length metrics
    start_time = time.time()
    record_length = record_length_metrics(events, mortality)
    end_time = time.time()
    print("Time to compute record length metrics: " + str(end_time - start_time) + "s")
    print record_length
    
if __name__ == "__main__":
    main()



