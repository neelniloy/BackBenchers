package com.sarker.backbenchersextended.model;

import java.io.Serializable;

public class StudentInfo implements Serializable {

    String key,name,id,email,blood,phone,classCode,photo,uid;

    public StudentInfo() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public StudentInfo(String key, String name, String id, String email, String blood, String phone, String classCode, String photo, String uid) {
        this.key = key;
        this.name = name;
        this.id = id;
        this.email = email;
        this.blood = blood;
        this.phone = phone;
        this.classCode = classCode;
        this.photo = photo;
        this.uid = uid;


    }
}
