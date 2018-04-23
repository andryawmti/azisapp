package com.kudubisa.app.navdrawertemplate.model;

/**
 * Created by asus on 4/14/18.
 */

public class UserLogin {
    Boolean error;
    String message;
    User user = new User();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }
}
