package com.sarker.backbenchersextended;

import java.io.Serializable;

public class Posts implements Serializable {
    public String uid, time, date, postimage, description,key,classCode;

    public Posts()
    {

    }

    public Posts(String uid, String time, String date, String postimage, String description, String key, String classCode) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postimage = postimage;
        this.description = description;
        this.key = key;
        this.classCode = classCode;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }
}
