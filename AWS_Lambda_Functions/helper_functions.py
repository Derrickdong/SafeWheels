from geographiclib.geodesic import Geodesic
from geographiclib.constants import Constants
from shapely import geometry
from shapely.geometry import LineString
import math


def getBearing(lat1, lat2, lon1, lon2):
    brng = Geodesic.WGS84.Inverse(lat1, lon1, lat2, lon2)['azi1']
    return brng

def getEndpoint(lat1, lon1, brng, d):
    geod = Geodesic(Constants.WGS84_a, Constants.WGS84_f)
    d = geod.Direct(lat1, lon1, brng, d)
    return {'lat' : d['lat2'], 'lng' : d['lon2']}


def getBoundingBox(lat1, lat2, lon1, lon2, d):
    
    bearing = getBearing(lat1, lat2, lon1, lon2)
    
    if bearing == 0 or bearing == 90 or bearing == 180:
        bearing += 0.000001
    
    if bearing > 0 and bearing < 90:
        front = getEndpoint(lat2, lon2, bearing, d)
        back = getEndpoint(lat1, lon1, bearing, -d)
        
        bb1 = getEndpoint(front['lat'], front['lng'], bearing + 90, -d)       
        bb2 = getEndpoint(front['lat'], front['lng'], bearing + 90, d)
        bb3 = getEndpoint(back['lat'], back['lng'], bearing + 90, -d)
        bb4 = getEndpoint(back['lat'], back['lng'], bearing + 90, d)
    
    elif bearing > 90 and bearing < 180:
        front = getEndpoint(lat2, lon2, bearing, d)
        back = getEndpoint(lat1, lon1, bearing, -d)
                
        bb1 = getEndpoint(front['lat'], front['lng'], bearing - 90, +d)  
        bb2 = getEndpoint(front['lat'], front['lng'], bearing - 90, -d)
        bb3 = getEndpoint(back['lat'], back['lng'], bearing - 90, d)
        bb4 = getEndpoint(back['lat'], back['lng'], bearing - 90, -d)
    
    elif bearing < 0:
        bearing = 360 + bearing

        front = getEndpoint(lat1, lon1, bearing, -d)
        back = getEndpoint(lat2, lon2, bearing, +d)
               
        bb1 = getEndpoint(front['lat'], front['lng'], bearing - 90, -d)
        bb2 = getEndpoint(front['lat'], front['lng'], bearing - 90, +d)
        bb3 = getEndpoint(back['lat'], back['lng'], bearing - 90, -d)
        bb4 = getEndpoint(back['lat'], back['lng'], bearing - 90, +d)
        
    return [bb1, bb2, bb3, bb4]

def sort_clockwise(x):
    return (math.atan2(x['lat'] - mlat, x['lng'] - mlng) + 2 * math.pi) % (2*math.pi)
