package com.project.treasurehunt;

import com.google.android.gms.maps.model.LatLng;

public class GeoFenceClueData {

    private String id;
    private String hint;
    private String name;


    public GeoFenceClueData(String id, String hint, String name, double lat, double lng) {
        this.id = id;
        this.hint = hint;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    private double lat;

    public String getId() {
        return id;
    }

    public String getHint() {
        return hint;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    private double lng;

}
