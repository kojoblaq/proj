package com.example.proj;

public class PersonnelHelperClass {
    String phone, PID;

    public PersonnelHelperClass(String phone, String PID) {
        this.phone = phone;
        this.PID = PID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }
}
