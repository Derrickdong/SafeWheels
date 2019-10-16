import math
import re
import sys
import logging
import pymysql
import rds_config
from geographiclib.geodesic import Geodesic
from geographiclib.constants import Constants
from shapely import geometry
from helper_functions import getBearing, getEndpoint, getBoundingBox, linestring_parser

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
    
    accidents = []
    
    query = "set @ploy = ST_POLYGONFROMTEXT('POLYGON((" + \
             bbox_mysql + \
             "))', 2);"
    
    with my_conn.cursor() as cur:
    
        cur.execute(query);
        
        cur.execute("""select OGR_FID,
                          id, 
                          t0records,
                          st_astext(SHAPE) 
                   from safewheels.crash_multilinestring_15 
                   where ST_Intersects(@ploy, SHAPE)
                   and t0records != 0;
                """);

        my_conn.commit()
        
        if cur.rowcount == 0:
            accidents = []
        else:    
            for row in cur:
                accidents.extend(linestring_parser(row))
   
    return(accidents)
