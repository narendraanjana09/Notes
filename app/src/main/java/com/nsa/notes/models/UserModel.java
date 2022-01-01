package com.nsa.notes.models;

public class UserModel {
    private String uid;
    private String name;
    private String email;
    private String joinedDate;
    private boolean syncEnabled;

    public UserModel() {
    }

    public UserModel(String uid, String name, String email, String joinedDate, boolean syncEnabled) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.joinedDate = joinedDate;
        this.syncEnabled = syncEnabled;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public boolean isSyncEnabled() {
        return syncEnabled;
    }

    public void setSyncEnabled(boolean syncEnabled) {
        this.syncEnabled = syncEnabled;
    }
}
