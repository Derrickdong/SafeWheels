import math
import re
import sys
import logging
import pymysql
import rds_config
import datetime
from geographiclib.geodesic import Geodesic
from geographiclib.constants import Constants
from shapely import geometry
from helper_functions import getBearing, getEndpoint, getBoundingBox

#rds settings
rds_host  = rds_config.db_host
name = rds_config.db_username
password = rds_config.db_password
db_name = rds_config.db_name

logger = logging.getLogger()
logger.setLevel(logging.INFO)

try:
    
    my_conn = pymysql.connect(rds_host, user=name, passwd=password, db=db_name, connect_timeout=5)
    
except pymysql.MySQLError as e:
    
    logger.error("ERROR: Unexpected error: Could not connect to MySQL instance.")
    logger.error(e)
    sys.exit()


logger.info("SUCCESS: Connection to RDS MySQL instance succeeded")


def lambda_handler(event, context):
    
    def sort_clockwise(x):
        return (math.atan2(x['lat'] - mlat, x['lng'] - mlng) + 2 * math.pi) % (2*math.pi)
    
    filtered_lanes = []
    lat1 = float(event['curr_lat'])
    lon1 = float(event['curr_lon'])
    lat2 = float(event['dest_lat'])
    lon2 = float(event['dest_lon'])
    
    bbox = getBoundingBox(lat1, lat2, lon1, lon2, 750)
    
    mlat = sum(x['lat'] for x in bbox) / len(bbox)
    mlng = sum(x['lng'] for x in bbox) / len(bbox)
    
    bbox.sort(key=sort_clockwise)

    bbox_sorted = []
    for e in bbox:
        bbox_sorted.append([e['lng'], e['lat']])
    
    bbox_sorted.append(bbox_sorted[0])
    bbox_mysql = str([str(point).replace(",", " ") for point in bbox_sorted]).replace("[", "").replace("]", "").replace("'", "")
    
    road_works = []
    
    query = "set @ploy = PolygonFromText('POLYGON((" + \
             bbox_mysql + \
             "))', 0);"
    
    with my_conn.cursor() as cur:
    
        cur.execute(query);
        
        cur.execute("""select 
                        vicroad_data_type
                        , incident_type
                	    , incident_id
                	    , geometry_lon
                        , geometry_lat
                        , incident_road_name
                        , incident_status
                        , incident_desc
                        , incident_start_dt
                        , incident_end_dt
                       from safewheels.roadworks_new_oct
                       where ST_CONTAINS(@ploy, geom_point);
                    """);
    
        my_conn.commit()
        
        if cur.rowcount == 0:
            accidents = []
            
        else:    
            for row in cur: 
                
                incident_desc = row[7]
                incident_desc_clean = []
                
                for text in incident_desc.split("."):
                    if not(re.search("Roadworks speed", str(text)) or re.search("Traffic delay", str(text))):
                        incident_desc_clean.append(str(text))
                incident_desc = '.'.join(incident_desc_clean)
                
                if row[0] == 'erc_point' or row[8] is None or row[9] is None:
                    incident_desc = 'Road may be closed. Please seek alter route.'
                else:
                    start_dt = '' if row[8] == None else row[8].date().strftime('%d/%m/%Y')
                    end_dt = '' if row[9] == None else row[9].date().strftime('%d/%m/%Y')
                    
                    start_dt_string = str(start_dt)
                    end_dt_string = str(end_dt)
                    incident_desc = incident_desc + 'Start: ' + start_dt_string + ' End: ' + end_dt_string
                    
                if row[8] == None:
                    incident_status = 'active'
                elif datetime.datetime.today().date() >= row[8].date():
                    incident_status = 'active'
                else: 
                    incident_status = row[6]

                incident = {
                        'vicroad_data_type' : row[0],
                        'incident_type' : row[1],
                        'incident_id' : row[2],
                        'coordinates' : [row[3], row[4]],
                        'incident_road_name' : row[5],
                        'incident_status' : incident_status,
                        'incident_desc' : incident_desc,
                        'incident_start_dt' : str(row[8]),
                        'incident_end_dt' : str(row[9])
                        }
            
                road_works.append(incident)
   
    return(road_works)
