package com.sarker.backbenchersextended.model;

import java.io.Serializable;

public class BLCInfo implements Serializable {

    String key,title,des,classCode,from;

    public BLCInfo() {


    }

    public BLCInfo(String key, String title, String des, String classCode, String from) {
        this.key = key;
        this.title = title;
        this.des = des;
        this.classCode = classCode;
        this.from = from;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}
