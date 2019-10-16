"""
Created on Mon Sep 30 18:24:37 2019

@author: chris
Disclaimer: This script is largely based on: Using Shapely and Fiona to Locate High-Risk Traffic Areas
https://www.azavea.com/blog/2016/10/05/philippines-road-safety-using-shapely-fiona-locate-high-risk-traffic-areas/
Licence: Apache License 2.0
"""

import csv
from dateutil import parser
from dateutil.relativedelta import relativedelta
import fiona
import itertools
import rtree
from shapely.geometry import mapping, shape, LineString, MultiPoint, Point
from shapely.ops import transform, unary_union

# Define helper functions 

def read_records(records_csv, 
                 col_id,
                 col_x, 
                 col_y, 
                 col_occurred):
    """Reads records csv, projects points, and localizes datetimes
    :param records_csv: Path to CSV containing record data
    :param road_projection: Projection CRS for road data
    :param record_projection: Projection CRS for record data
    :param tz: Timezone id for record data
    :param col_id: Record id column name
    :param col_x: Record x-coordinate column name
    :param col_y: Record y-coordinate column name
    :param col_occurred: Record occurred datetime column name
    """
    records = []
    min_occurred = None
    max_occurred = None
    with open(records_csv, 'rt') as records_file:
        csv_reader = csv.DictReader(records_file)
        for row in csv_reader:
            # Collect min and max occurred datetimes, as they'll be used later on
            try:
                parsed_dt = parser.parse(row[col_occurred])
                occurred = parsed_dt

            except:
                # Skip the record if it has an invalid datetime
                continue

            if not min_occurred or occurred < min_occurred:
                min_occurred = occurred
            if not max_occurred or occurred > max_occurred:
                max_occurred = occurred

            records.append({
                'id': row[col_id],
                'point': Point(float(row[col_x]), float(row[col_y])),
                'occurred': occurred
            })

    return records, min_occurred, max_occurred

  
def read_roads(roads_shp, records, buffer_size):
    """Reads shapefile and extracts roads and projection
    :param roads_shp: Path to the shapefile containing roads
    :param records: List of shapely geometries representing record points
    :param buffer_size: Number of units to buffer record for checking if road should be kept
    """
    # Create a spatial index for record buffers to efficiently find intersecting roads
    record_buffers_index = rtree.index.Index()
    for idx, record in enumerate(records):
        record_point = record['point']
        record_buffer_bounds = record_point.buffer(buffer_size).bounds
        record_buffers_index.insert(idx, record_buffer_bounds)

    shp_file = fiona.open(roads_shp)
    roads = []
    for road in shp_file:
        road_shp = shape(road['geometry'])
        if should_keep_road(road, road_shp, record_buffers_index):
            roads.append(road_shp)

    return (roads, shp_file.bounds)


def should_keep_road(road, road_shp, record_buffers_index):
    """Returns true if road should be considered for segmentation
    :param road: Dictionary representation of the road (with properties)
    :param roads_shp: Shapely representation of the road
    :param record_buffers_index: RTree index of the record_buffers
    """
    # If the road has no nearby records, then we can discard it early on.
    # This provides a major optimization since the majority of roads don't have recorded accidents.
    if not len(list(record_buffers_index.intersection(road_shp.bounds))):
        return False
    else:
        return True


def get_intersections(roads):
    """Calculates the intersection points of all roads
    :param roads: List of shapely geometries representing road segments
    """
    intersections = []
    for road1, road2 in itertools.combinations(roads, 2):
        if road1.intersects(road2):
            intersection = road1.intersection(road2)
            if 'Point' == intersection.type:
                intersections.append(intersection)
            elif 'MultiPoint' == intersection.type:
                intersections.extend([pt for pt in intersection])
            elif 'MultiLineString' == intersection.type:
                multiLine = [line for line in intersection]
                first_coords = multiLine[0].coords[0]
                last_coords = multiLine[len(multiLine)-1].coords[1]
                intersections.append(Point(first_coords[0], first_coords[1]))
                intersections.append(Point(last_coords[0], last_coords[1]))

    # The unary_union removes duplicate points
    unioned = unary_union(intersections)

    return unioned


def get_intersection_buffers(intersections, intersection_buffer_units):
    buffered_intersections = [intersection.buffer(intersection_buffer_units)
                                           for intersection in intersections]
    return unary_union(buffered_intersections)

  
# Create aggregated crash data

# Step 1: Read in crash last five years and speed zone data
# NOTE: To work out all the intersections in Victoria may take VERY long time. 
# For simplicity, please use QGIS or alike to select roads in Melbourne metro regions.
records, min_occurred, max_occurred = read_records(
        "PATH TO YOUR CRASH FILE",
        'ACCIDENT_NO', "VICGRID_X", "VICGRID_Y",
        "ACCIDENT_DATE"
    )

roads, road_bounds = read_roads("PATH TO YOUR SHAPEFILE", records, 15)


# Step 2: Call the get_intersection function to find out all the intersection in Victoria
# Depending on the processing power of the machine and region selected in previous step,
# this step may take up to few hours to process.
intersections = get_intersections(roads)


# Step 3: Create buffer for the intersection file
int_buffers = get_intersection_buffers(intersections, 15)


# Step 4: Divide up each road into segments that overlap the intersection buffers from 
# the previous step, and sections that are not part of any road intersection.
int_buffers_index = rtree.index.Index()

for idx, intersection_buffer in enumerate(int_buffers):
    int_buffers_index.insert(idx, intersection_buffer.bounds)

segments_map = {}
non_int_lines = []
for road in roads:
    road_int_buffers = []
    for idx in int_buffers_index.intersection(road.bounds):
        int_buffer = int_buffers[idx]
        if int_buffer.intersects(road):
            if idx not in segments_map:
                segments_map[idx] = []
            segments_map[idx].append(int_buffer.intersection(road))
            road_int_buffers.append(int_buffer)
    if len(road_int_buffers) > 0:
        diff = road.difference(unary_union(road_int_buffers))
        if 'LineString' == diff.type:
             non_int_lines.append(diff)
        elif 'MultiLineString' == diff.type:
            non_int_lines.extend([line for line in diff])
    else:
        non_int_lines.append(road)

int_multilines = [unary_union(lines) for _, lines in segments_map.items()]
combined_segments = int_multilines + non_int_lines


# Step 5: Matchup crashes to nearest segment or intersection
segments_index = rtree.index.Index()
for idx, element in enumerate(combined_segments):
    segments_index.insert(idx, element.bounds)
    
segments_with_records = {}
for record in records:
    record_point = record['point']
    record_buffer_bounds = record_point.buffer(15).bounds
    nearby_segments = segments_index.intersection(record_buffer_bounds)
    segment_id_with_distance = [
        (segment_id, combined_segments[segment_id].distance(record_point))
        for segment_id in nearby_segments
    ]
    if len(segment_id_with_distance):
        nearest = min(segment_id_with_distance, key=lambda tup: tup[1])
        segment_id = nearest[0]
        if segment_id not in segments_with_records:
            segments_with_records[segment_id] = []
        segments_with_records[segment_id].append(record)

        
# Step 6: Aggregate crash data into 2 years period and attach to the segments and intersections data
schema = {
    'geometry': 'MultiLineString',
    'properties': {
        'id': 'int', 'length': 'float', 'lines': 'int',
        'pointx': 'float', 'pointy': 'float', 'records': 'int'
    }
}
    
num_years = (max_occurred - min_occurred).days / (52 * 7)
year_ranges = [
    (max_occurred - relativedelta(years=offset),
     max_occurred - relativedelta(years=(offset + 2)),
     't{}records'.format(offset))
    for offset in range(round(num_years))
]

for year_range in year_ranges:
    _, _, records_label = year_range
    schema['properties'][records_label] = 'int'
    
segments_with_data = []
for idx, segment in enumerate(combined_segments):
    is_intersection = 'MultiLineString' == segment.type
    records = segments_with_records.get(idx)
    data = {
        'id': idx, 'length': segment.length,
        'lines': len(segment) if is_intersection else 1,
        'pointx': segment.centroid.x, 'pointy': segment.centroid.y,
        'records': len(records) if records else 0
    }
    for year_range in year_ranges:
        max_occurred, min_occurred, records_label = year_range
        if records:
            records_in_range = [
                record for record in records
                if min_occurred < record['occurred'] <= max_occurred
            ]
            data[records_label] = len(records_in_range)
        else:
            data[records_label] = 0
    segments_with_data.append((segment, data))

    
# Step 7: Output the result in shapefile format   
with fiona.open("PATH TO OUTPUT FILE", 'w', driver='ESRI Shapefile',
                schema=schema) as output:
    for segment_with_data in segments_with_data:
        segment, data = segment_with_data
        
        if segment.geom_type == 'MultiLineString':
        
            output.write({
                'geometry': mapping(segment),
                'properties': data
            })
