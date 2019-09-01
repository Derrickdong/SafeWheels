import json   
import pandas as pd
import collections


def flatten(x):
    if isinstance(x, collections.Iterable):
        return [a for i in x for a in flatten(i)]
    else:
        return [x]


# Download Vic bike lane GeoJSON file from below link
# https://opendata.arcgis.com/datasets/c126b57531da48ae8991bda91f1b1d83_0.geojson


with open('C:\\Users\\HOME\\Desktop\\New folder\\Principal_Bicycle_Network.geojson') as data_file:
    data = json.load(data_file)


features = data['features']
no_road = len(features)

# Initialise an empty list
appended_data = []

# Loop through all lanes
for road in range(no_road):
     
    coord_data = []
    
    road_info = pd.DataFrame(features[road]['properties'], index=[road])
    coordinates_info = flatten(features[road]['geometry']['coordinates']) 
    
    for i in range(0, len(coordinates_info), 2):
        coord_data.append([coordinates_info[i], coordinates_info[i + 1]])
        
    coord_info = pd.DataFrame(coord_data, dtype = 'float64') * 100000
    
    road_info['join_key'] = 1
    coord_info['join_key'] = 1
    
    road_all_info = pd.merge(road_info, coord_info)
    
    appended_data.append(road_all_info)
    
appended_data = pd.concat(appended_data)

# Save the result as csv file
appended_data.to_csv(r'C:\Users\HOME\Desktop\Principal_Bicycle_Network_original.csv', index = None, header=True, float_format='%.20f')
