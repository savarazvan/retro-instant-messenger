package com.example.ressenger;

public class FindNewFriends {

    public String name;
    public String status;
    public String uid;

    public  FindNewFriends(){}

    public FindNewFriends(String username, String status) {
        this.name = username;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setUsername(String username) {
        this.name = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {return uid;}

    public void setUid(String uid) {this.uid = uid;}


}
