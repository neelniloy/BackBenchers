package com.sarker.backbenchersextended.model;


public class TeacherInfo {

    String key,dp,name,initial,email,phone,room,classCode,uid;

    public TeacherInfo() {

    }

    public TeacherInfo(String key, String dp, String name, String initial, String email, String phone, String room, String classCode, String uid) {
        this.key = key;
        this.dp = dp;
        this.name = name;
        this.initial = initial;
        this.email = email;
        this.phone = phone;
        this.room = room;
        this.classCode = classCode;
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
