package com.example.proj;

public class locationHelper {
    private String Longitude;
    private String Latitude;

    public locationHelper(String longitude, String latitude) {
        this.Longitude = longitude;
        this.Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }
}
