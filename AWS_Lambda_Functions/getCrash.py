import sys
import logging
import pymysql
import json
import rds_config

#rds settings
rds_host  = rds_config.db_host
name = rds_config.db_username
password = rds_config.db_password
db_name = rds_config.db_name

logger = logging.getLogger()
logger.setLevel(logging.INFO)

try:
    conn = pymysql.connect(rds_host, user=name, passwd=password, db=db_name, connect_timeout=5)
except pymysql.MySQLError as e:
    logger.error("ERROR: Unexpected error: Could not connect to MySQL instance.")
    logger.error(e)
    sys.exit()

logger.info("SUCCESS: Connection to RDS MySQL instance succeeded")


def lambda_handler(event, context):
    
    """
    This function fetches bike accidents within a radius of 5km
    """
    
    data = []
    
    getCrash = "select \
                ACCIDENT_NO \
                , LONGITUDE \
                , LATITUDE \
                , BICYCLIST \
                from safewheels.crash where \
                BICYCLIST != 0 and \
                LATITUDE <= _upper_lat and \
                LATITUDE >= _lower_lat and \
                LONGITUDE <= _upper_lon and \
                LONGITUDE >= _lower_lon;"
    
    
    # About 5 km away from current location
    upper_lat = float(event['lat']) + 0.005
    lower_lat = float(event['lat']) - 0.005
    
    upper_lon = float(event['lon']) + 0.005
    lower_lon = float(event['lon']) - 0.005
    
    
    getCrash = getCrash.replace('_upper_lat', str(upper_lat))
    getCrash = getCrash.replace('_lower_lat', str(lower_lat))
    
    
    getCrash = getCrash.replace('_upper_lon', str(upper_lon))
    getCrash = getCrash.replace('_lower_lon', str(lower_lon))
    

    with conn.cursor() as cur:
        
        cur.execute(getCrash)
        conn.commit()
        
        for row in cur:
            accident = {
                'ACCIDENT_NO': row[0],
                'ACCIDENT_INFO': {  
                    'LATITUDE': row[1],
                    'LONGITUDE': row[2],
                    'BICYCLIST': row[3]
                    }
                }
                
            data.append(accident)
        
    
    return(data)
