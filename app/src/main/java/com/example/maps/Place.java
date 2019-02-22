package com.example.maps;

import java.io.Serializable;

public class Place implements Serializable {
    private String namePlace;
    private double latitude;
    private double longitude;

    public String getNamePlace() {
        return namePlace;
    }

    public void setNamePlace(String namePlace) {
        this.namePlace = namePlace;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Place(String namePlace, double latitude, double longitude) {
        this.namePlace = namePlace;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
