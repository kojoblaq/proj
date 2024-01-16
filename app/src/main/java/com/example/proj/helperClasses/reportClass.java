package com.example.proj.helperClasses;

public class reportClass {
    private String phone;
    private String fname;
    private String audio_report;
    private String image_Report;
    private String text_report;
    private String latitude;
    private String longitude;
    private String date;
    private String emergencyType;


    public reportClass() {
    }

    public reportClass(String phone, String fname, String audio_report, String image_Report, String text_report, String latitude, String longitude, String date, String emergencyType) {
        this.phone = phone;
        this.fname = fname;
        this.audio_report = audio_report;
        this.image_Report = image_Report;
        this.text_report = text_report;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.emergencyType = emergencyType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getAudio_report() {
        return audio_report;
    }

    public void setAudio_report(String audio_report) {
        this.audio_report = audio_report;
    }

    public String getImage_Report() {
        return image_Report;
    }

    public void setImage_Report(String image_Report) {
        this.image_Report = image_Report;
    }

    public String getText_report() {
        return text_report;
    }

    public void setText_report(String text_report) {
        this.text_report = text_report;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }
}