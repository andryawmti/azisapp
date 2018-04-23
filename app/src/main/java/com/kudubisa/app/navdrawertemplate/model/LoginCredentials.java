package com.kudubisa.app.navdrawertemplate.model;

/**
 * Created by asus on 4/14/18.
 */

public class LoginCredentials {
    private String email;
    private String password;
    private Boolean remember;

    public Boolean getRemember() {
        return remember;
    }

    public void setRemember(Boolean remember) {
        this.remember = remember;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
