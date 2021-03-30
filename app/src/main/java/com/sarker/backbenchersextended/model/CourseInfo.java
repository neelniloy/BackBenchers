package com.sarker.backbenchersextended.model;

import java.io.Serializable;

public class CourseInfo implements Serializable {

    String courseName,courseImage,key,classCode;

    public CourseInfo(){

    }

    public CourseInfo(String courseName, String courseImage, String key, String classCode) {
        this.courseName = courseName;
        this.courseImage = courseImage;
        this.key = key;
        this.classCode = classCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseImage() {
        return courseImage;
    }

    public void setCourseImage(String courseImage) {
        this.courseImage = courseImage;
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
