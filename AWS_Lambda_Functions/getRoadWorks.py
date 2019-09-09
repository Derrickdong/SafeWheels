import sys
import logging
import pymysql
import rds_config

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
    
    geom_query = """ 
        select 
            vicroad_data_type
            , incident_type
    	    , incident_id
    	    , geometry_lon
            , geometry_lat
            , incident_road_name
            , incident_status
            , incident_desc
        from 
        	safewheels.road_works; 
    """
    
    road_works = []

    with my_conn.cursor() as cur:
        
        cur.execute(geom_query) 
                      
        my_conn.commit()
        
        if cur.rowcount == 0:
            
            road_works = []
        
        else: 
    
            for row in cur: 
                
                incident = {
                        
                        'vicroad_data_type' : row[0],
                        'incident_type' : row[1],
                        'incident_id' : row[2],
                        'coordinates' : [row[3], row[4]],
                        'incident_road_name' : row[5],
                        'incident_status' : row[6],
                        'incident_desc' : row[7]                    
                        
                        }
                
                road_works.append(incident)
        
    return(road_works)
