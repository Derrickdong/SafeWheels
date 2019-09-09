
# Reference: https://www.movable-type.co.uk/scripts/latlong.html

import math

def midpoint(curr_point, dest_point):
  
    """ Returns the midpoint of two points
    curr_point: current lat and lon in a list.
    dest_point: destination lat and lon in a list.
    
    Ouput: midpoint lat and lon in a list
    """
    
    curr_point = [float(x) for x in curr_point]    
    dest_point = [float(x) for x in dest_point]   
    
    lat1 = math.radians(curr_point[0])
    lon1 = math.radians(curr_point[1])
    lat2 = math.radians(dest_point[0])
    lon2 = math.radians(dest_point[1])

    bx = math.cos(lat2) * math.cos(lon2 - lon1)
    by = math.cos(lat2) * math.sin(lon2 - lon1)
    lat3 = math.atan2(math.sin(lat1) + math.sin(lat2), \
           math.sqrt((math.cos(lat1) + bx) * (math.cos(lat1) \
           + bx) + by**2))
    lon3 = lon1 + math.atan2(by, math.cos(lat1) + bx)

    return([math.degrees(lat3), math.degrees(lon3)])
