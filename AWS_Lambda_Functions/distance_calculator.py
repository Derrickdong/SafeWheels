# Reference: https://www.movable-type.co.uk/scripts/latlong.html

from math import sin, cos, sqrt, atan2, radians

def dist_calc(curr_point, dist_point):
    
    """ Returns the distance between two points
    curr_point: current lat and lon in a list.
    dist_point: destination lat and lon in a list.
    
    Ouput: the distance between two points in km.
    """
    
    curr_point = [float(x) for x in curr_point]    
    dist_point = [float(x) for x in dist_point]   
    
    lat1 = radians(curr_point[0])
    lon1 = radians(curr_point[1])
    lat2 = radians(dist_point[0])
    lon2 = radians(dist_point[1])

    R = 6373.0
    
    dlon = lon2 - lon1
    dlat = lat2 - lat1
    
    a = sin(dlat / 2)**2 + cos(lat1) * cos(lat2) * sin(dlon / 2)**2
    c = 2 * atan2(sqrt(a), sqrt(1 - a))
    
    distance = R * c
    
    return(distance)
