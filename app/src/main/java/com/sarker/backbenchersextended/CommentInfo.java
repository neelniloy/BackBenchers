package com.sarker.backbenchersextended;

import java.io.Serializable;

public class CommentInfo implements Serializable {

    private String comment,uid,coomentKey,date,commentPostKey,classCode;

    public CommentInfo(){

    }

    public CommentInfo(String comment, String uid, String coomentKey, String date, String commentPostKey, String classCode) {
        this.comment = comment;
        this.uid = uid;
        this.coomentKey = coomentKey;
        this.date = date;
        this.commentPostKey = commentPostKey;
        this.classCode = classCode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCoomentKey() {
        return coomentKey;
    }

    public void setCoomentKey(String coomentKey) {
        this.coomentKey = coomentKey;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommentPostKey() {
        return commentPostKey;
    }

    public void setCommentPostKey(String commentPostKey) {
        this.commentPostKey = commentPostKey;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }
}
