"""
@author: Chris
Purpose of this script is to get latest road works and emergency road closure
from the VicRoads API.

NOTE: Please apply API key from VicRoads before execute this code.
http://api.vicroads.vic.gov.au/
"""


import requests
import pandas as pd
from datetime import datetime
from sqlalchemy import create_engine
import sqlalchemy


def utc_to_local(utc_dt):
    if utc_dt == None:
        return(None)
    else:
        utc_dt = utc_dt.replace("Z", "").replace("T", " ")
        out = str(datetime.strptime(utc_dt, '%Y-%m-%d %H:%M:%S')).split('+')[0]
    return(out)


road_work_url = """http://api.vicroads.vic.gov.au/vicroads/wfs?
    SERVICE=WFS&VERSION=1.1.0
    &REQUEST=GetFeature
    &TYPENAMES=vicroads:rwe_point
    &OUTPUTFORMAT=application/json
    &SRSNAME=EPSG:4326
    &AUTH=YOUR API KEY HERE"""

road_work_response = requests.get(road_work_url)
rw_data = road_work_response.json()
rw_features = rw_data['features']


erc_url = """http://api.vicroads.vic.gov.au/vicroads/wfs?
    SERVICE=WFS&VERSION=1.1.0
    &REQUEST=GetFeature
    &TYPENAMES=vicroads:erc_point
    &OUTPUTFORMAT=application/json
    &SRSNAME=EPSG:4326
    &AUTH=YOUR API KEY HERE"""


erc_response = requests.get(erc_url)
erc_data = erc_response.json()
erc_features = erc_data['features']


all_features = rw_features + erc_features

current_dt = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

vicroad_data_type = []
incident_type = []
incident_id = []
road_closure_type = []
geometry_type = []
geometry_lon = []
geometry_lat = []
incident_road_name = []
incident_locality = []
incident_status = []
incident_desc = []
incident_last_modified_dt = []
incident_start_dt = []
incident_end_dt = []
batch_run_dt = []

for feature in all_features:
    
    if feature['geometry_name'] == 'rwe_point':
        
        vicroad_data_type.append(feature['geometry_name'])
        incident_type.append(feature['properties']['rwe_type'])
        incident_id.append(feature['properties']['rwe_id'])
        road_closure_type.append(feature['properties']['rwe_closure_type'].strip())
        geometry_type.append(feature['geometry']['type'])
        geometry_lon.append(feature['geometry']['coordinates'][0])
        geometry_lat.append(feature['geometry']['coordinates'][1])
        incident_road_name.append(feature['properties']['subject_pref_rdname'])
        incident_locality.append(feature['properties']['lrs_start_locality'])
        incident_status.append(feature['properties']['rwe_status'].lower())
        incident_desc.append(feature['properties']['rwe_publish_text'])
        incident_last_modified_dt.append(feature['properties']['dt_modified'])
        batch_run_dt.append(current_dt)
        incident_start_dt.append(feature['properties']['rwe_start_dt'])
        incident_end_dt.append(feature['properties']['rwe_end_dt'])
        
    if feature['geometry_name'] == 'erc_point':
        
        vicroad_data_type.append('erc_point')
        incident_type.append(feature['properties']['incident_type'])
        incident_id.append(feature['properties']['erc_id'])
        road_closure_type.append(feature['properties']['road_closure_type'].strip())
        geometry_type.append(feature['geometry']['type'])
        geometry_lon.append(feature['geometry']['coordinates'][0])
        geometry_lat.append(feature['geometry']['coordinates'][1])
        incident_road_name.append(feature['properties']['start_int_road_name'])
        incident_locality.append(feature['properties']['incident_locality'])
        incident_status.append(feature['properties']['incident_status'].lower())
        incident_desc.append(feature['properties']['comms_comment'])
        incident_last_modified_dt.append(feature['properties']['dt_updated'])
        batch_run_dt.append(current_dt)
        incident_start_dt.append(None)
        incident_end_dt.append(None)


output_dict = {
        
        'vicroad_data_type' : vicroad_data_type,
        'incident_type' : incident_type,
        'incident_id' : incident_id,
        'road_closure_type' : road_closure_type,
        'geometry_type' : geometry_type,
        'geometry_lon' : geometry_lon,
        'geometry_lat' : geometry_lat,
        'incident_road_name' :incident_road_name,
        'incident_locality' : incident_locality,
        'incident_status' : incident_status,
        'incident_desc' : incident_desc,
        'incident_last_modified_dt' : incident_last_modified_dt,
        'batch_dt' : batch_run_dt,
        'incident_start_dt' : incident_start_dt,
        'incident_end_dt' : incident_end_dt
        
        }


output_df = pd.DataFrame.from_dict(output_dict, orient='index').transpose()

output_df['incident_start_dt'] = output_df['incident_start_dt'].apply(utc_to_local)
output_df['incident_end_dt'] = output_df['incident_end_dt'].apply(utc_to_local)
output_df['incident_last_modified_dt'] = output_df['incident_last_modified_dt'].apply(utc_to_local)

sqlEngine = create_engine('mysql+pymysql://admin:TiPVf6Sh3fGCEs5FcPMR@safewheelsdb.cl3cdix96eff.ap-southeast-2.rds.amazonaws.com/safewheels')
dbConnection = sqlEngine.connect()

truncate_query = sqlalchemy.text("TRUNCATE TABLE roadworks_new_oct")
dbConnection.execution_options(autocommit=True).execute(truncate_query)

output_df.to_sql(con = dbConnection, name = 'roadworks_new_oct', if_exists = 'append', index = False)

dbConnection.close()
