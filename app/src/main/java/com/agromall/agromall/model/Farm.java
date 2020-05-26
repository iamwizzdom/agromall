package com.agromall.agromall.model;

/**
 * Created by Wisdom Emenike.
 * Date: 5/24/2020
 * Time: 11:53 PM
 */
public class Farm {

    private int farmID, farmOwnerID;
    private String name, location, coordinates;

    public int getFarmID() {
        return farmID;
    }

    public void setFarmID(int farmID) {
        this.farmID = farmID;
    }

    public int getFarmOwnerID() {
        return farmOwnerID;
    }

    public void setFarmOwnerID(int farmOwnerID) {
        this.farmOwnerID = farmOwnerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
}
