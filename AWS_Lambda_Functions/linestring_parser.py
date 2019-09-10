def linestring_parser(row):
    
    object_id = row[0]
    object_content = row[1]
    
    if "MULTILINESTRING" in object_content:
        lanes = []        
        line_points = [non_decimal.sub('', element).split(",") for element in object_content.split("),(")]

        for linestring in line_points:
            lanes.append({'OBJECTID' : object_id, 'COORDINATES' : linestring})
        return(lanes)

    else:         
        linestring = [x.strip() for x in object_content[object_content.find("(") + 1 : object_content.find(")")].split(',')]
        return([{'OBJECTID' : object_id, 'COORDINATES' : linestring}])
