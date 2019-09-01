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
    
    
    query_getLaneName = "select \
                         OBJECTID \
                         from safewheels.bike_lane where \
                         LAT <= _upper_lat and \
                         LAT >= _lower_lat and \
                         LON <= _upper_lon and \
                         LON >= _lower_lon;"
        
        
    # About 5 km away from current location
    upper_lat = (float(event['lat']) + 0.01) * 100000
    lower_lat = (float(event['lat'])  - 0.01) * 100000
    
    upper_lon = (float(event['lon'])  + 0.01) * 100000
    lower_lon = (float(event['lon'])  - 0.01) * 100000
    
    
    query_getLaneName = query_getLaneName.replace('_upper_lat', str(upper_lat))
    query_getLaneName = query_getLaneName.replace('_lower_lat', str(lower_lat))
    
    
    query_getLaneName = query_getLaneName.replace('_upper_lon', str(upper_lon))
    query_getLaneName = query_getLaneName.replace('_lower_lon', str(lower_lon))
    
    
    object_ids = []

    
    with my_conn.cursor() as cur:
        
        cur.execute(query_getLaneName)
        my_conn.commit()
        
        for row in cur:
            object_ids.append(row)
    
    
    object_ids = [ "%s" % x for x in object_ids ]
        
        
    query_getLaneInfo = "select OBJECTID \
                         , LON \
                         , LAT \
                         from safewheels.bike_lane \
                         where OBJECTID in (_object_ids);"
    
    if len(object_ids) != 0:
    
        query_getLaneInfo = query_getLaneInfo.replace('_object_ids', str(object_ids).strip('[]'))
        
        
        lane_info = []
         
        
        with my_conn.cursor() as cur:
    
            cur.execute(query_getLaneInfo)
            my_conn.commit()
    
            for row in cur:
            
                object_id = row[0]  
                coordinates = [row[1], row[2]]
            
                if len(lane_info) != 0:
                
                    if lane_info[-1]['OBJECTID'] == object_id:
                
                        lane_info[-1]['coordinates'].append([row[1], row[2]])
                        
                    else:
                        
                        lane = {'OBJECTID' : object_id, 'coordinates' : [coordinates]}
                
                else:
                
                    lane = {'OBJECTID' : object_id, 'coordinates' : [coordinates]}
                
                lane_info.append(lane)
        
        
        final_lane_info = [i for n, i in enumerate(lane_info) if i not in lane_info[n + 1:]]
    
    else:
        
        final_lane_info = []        
            
    return(final_lane_info)
