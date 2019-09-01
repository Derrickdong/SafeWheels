import pymysql

my_conn = pymysql.connect(
        host=host,
        port=3306,
        db=db,
        user=user,
        password=password,
        local_infile=1)
        
with my_conn.cursor() as cur:
    cur.execute("LOAD DATA LOCAL INFILE 'C:/Users/HOME/Desktop/bicycle_lane.csv' INTO TABLE safewheels.bicycle_lane FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY \'\n\' IGNORE 1 ROWS \
                  (LANEID, \
                  OBJECTID, \
                  NETWORK, \
                  TYPE, \
                  STATUS, \
                  STRATEGIC_CYCLING_CORRIDOR, \
                  LOCAL_NAME, \
                  LOCAL_TYPE, \
                  RD_NUM, \
                  NAME, \
                  SIDE, \
                  FACILITY_LEFT, \
                  SURFACE_LEFT, \
                  WIDTH_LEFT, \
                  FACILITY_RIGHT, \
                  SURFACE_RIGHT, \
                  WIDTH_RIGHT, \
                  LIGHTING, \
                  VERIFIED_DATE, \
                  BEARING, \
                  SCC_NAME, \
                  join_key, \
                  lon, \
                  lat);") 
                  
    my_conn.commit()
    
