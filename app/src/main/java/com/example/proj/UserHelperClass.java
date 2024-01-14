package com.example.proj;

public class UserHelperClass {
    String fname, phone, userid;

    public UserHelperClass(String fname, String phone, String userid) {
        this.fname = fname;
        this.phone = phone;
        this.userid = userid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}

