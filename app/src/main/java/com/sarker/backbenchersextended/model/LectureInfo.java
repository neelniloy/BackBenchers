package com.sarker.backbenchersextended.model;

import java.io.Serializable;

public class LectureInfo implements Serializable {

    String postTime,image,lecture,uid,course,key,classCode;

    public LectureInfo(){

    }

    public LectureInfo(String postTime, String image, String lecture, String uid, String course, String key, String classCode) {
        this.postTime = postTime;
        this.image = image;
        this.lecture = lecture;
        this.uid = uid;
        this.course = course;
        this.key = key;
        this.classCode = classCode;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLecture() {
        return lecture;
    }

    public void setLecture(String lecture) {
        this.lecture = lecture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
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
