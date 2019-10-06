package com.project.safewheels.Entity;

import com.google.android.gms.maps.model.LatLng;

public class Favorite {

    private String id;
    private String address;
    private LatLng latLng;
    private String name;

    public Favorite(String id, String address, LatLng latLng) {
        this.id = id;
        this.address = address;
        this.latLng = latLng;
        this.name = "";
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
