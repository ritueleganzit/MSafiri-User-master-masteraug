package com.eleganz.msafiri;

public class PassengerData {

    String user_id,username,user_img,status;

    public PassengerData(String user_id, String username, String user_img) {
        this.user_id = user_id;
        this.username = username;
        this.user_img = user_img;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }
}
