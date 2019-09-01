import json   
import pandas as pd

# Download Vic bike lane GeoJSON file from below link
# https://opendata.arcgis.com/datasets/c126b57531da48ae8991bda91f1b1d83_0.geojson


with open('C:\\Users\\HOME\Downloads\\Strategic_Cycling_Corridor.geojson') as data_file:
    data = json.load(data_file)


features = data['features']
no_road = len(features)

# Initialise an empty list
appended_data = []

# Loop through all lanes
for road in range(no_road):
     
    coord_data = []
    coord_pair = []
    
    road_info = pd.DataFrame(features[road]['properties'], index=[road])
      
    for row in features[road]['geometry']['coordinates']:
        for ele in row:
            coord_pair.append(ele * 100000)
            
        coord_data.append(coord_pair)
        coord_pair = []
    
    coord_info = pd.DataFrame(coord_data, dtype = 'float64')
    
    road_info['join_key'] = 1
    coord_info['join_key'] = 1
    
    road_all_info = pd.merge(road_info, coord_info)
    
    appended_data.append(road_all_info)
    
appended_data = pd.concat(appended_data)

# Save the result as csv file
appended_data.to_csv(r'C:\Users\HOME\Desktop\bicycle_lane2.csv', index = None, header=True, float_format='%.20f')
