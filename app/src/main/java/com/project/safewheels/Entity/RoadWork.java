package com.project.safewheels.Entity;

import com.google.android.gms.maps.model.LatLng;

public class RoadWork {

    private String incident_type;
    private String incident_status;
    private String incident_desc;
    private LatLng latLng;
    private String roadName;

    public RoadWork(String incident_type, String incident_status, String incident_desc, LatLng latLng, String roadName) {
        this.incident_type = incident_type;
        this.incident_status = incident_status;
        this.incident_desc = incident_desc;
        this.latLng = latLng;
        this.roadName = roadName;
    }

    public String getIncident_type() {
        return incident_type;
    }

    public void setIncident_type(String incident_type) {
        this.incident_type = incident_type;
    }

    public String getIncident_status() {
        return incident_status;
    }

    public void setIncident_status(String incident_status) {
        this.incident_status = incident_status;
    }

    public String getIncident_desc() {
        return incident_desc;
    }

    public void setIncident_desc(String incident_desc) {
        this.incident_desc = incident_desc;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }
}
