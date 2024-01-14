package com.example.proj;

public class FinalReportClass {
    String f_name;
    String phone;
    String longitude;
    String latitude;
    String userid;
    String Text_report;
    String Audio_report;
    String Image_Report;
    String emergencyType;
    String date;

    public FinalReportClass() {
    }

    public FinalReportClass(String f_name, String phone, String longitude, String latitude,
                            String userid, String text_report, String audio_report,
                            String image_Report, String emergencyType, String date) {
        this.f_name = f_name;
        this.phone = phone;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userid = userid;
        Text_report = text_report;
        Audio_report = audio_report;
        Image_Report = image_Report;
        this.emergencyType =emergencyType;
        this.date = date;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText_report() {
        return Text_report;
    }

    public void setText_report(String text_report) {
        Text_report = text_report;
    }

    public String getAudio_report() {
        return Audio_report;
    }

    public void setAudio_report(String audio_report) {
        Audio_report = audio_report;
    }

    public String getImage_Report() {
        return Image_Report;
    }

    public void setImage_Report(String image_Report) {
        Image_Report = image_Report;
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
