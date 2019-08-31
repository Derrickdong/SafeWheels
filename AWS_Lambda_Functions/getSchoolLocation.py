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
    This function fetches content from MySQL RDS instance
    """
    
    data = []

    query_school_name = event['school_name']
    
    query_getSchoolLocation = "select \
                               school_no \
                               , school_name \
                               , address_line_1 \
                               , address_town \
                               , address_postcode \
                               , school_type \
                               , y as lat \
                               , x as lon \
                               from safewheels.school where \
                               school_name LIKE '%_query_school_name%';"
                               
                               
    query_getSchoolLocation = query_getSchoolLocation.replace('_query_school_name', str(query_school_name))
    

    with conn.cursor() as cur:
        
        cur.execute(query_getSchoolLocation)
        conn.commit()
        
        for row in cur:
            
            schools = {
                'school_no' : row[0],
                'school_info' : {
                    'school_name' : row[1],
                    'address_line_1' : row[2],
                    'address_town' : row[3],
                    'address_postcode' : row[4],
                    'school_type' : row[5],
                    'school_lat' : row[6],
                    'school_lon' : row[7]
                    }
                }
                
            data.append(schools)
        
    
    return(data)
