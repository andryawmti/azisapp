package com.kudubisa.app.azisapp.model;

/**
 * Created by asus on 7/22/18.
 */

public class Favourite {
    private String id;
    private String userId;
    private String destinationId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }
}
