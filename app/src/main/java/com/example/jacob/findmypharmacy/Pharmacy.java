package com.example.jacob.findmypharmacy;

import java.io.Serializable;

public class Pharmacy implements Serializable{
    private int phar_id;
    private String phar_name;
    private String street;
    private double latitude;
    private double longitude;
    private String phone;
    private String email;
    private String website;
    private String opening_at;
    private String closing_at;
    private String distance;

    public Pharmacy(int phar_id, String phar_name, String street, double latitude, double longitude, String phone, String email, String website, String opening_at, String closing_at, String distance) {
        this.phar_id = phar_id;
        this.phar_name = phar_name;
        this.street = street;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.opening_at = opening_at;
        this.closing_at = closing_at;
        this.distance = distance;
    }

    public int getPhar_id() {
        return phar_id;
    }

    public String getPhar_name() {
        return phar_name;
    }

    public String getStreet() {
        return street;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getOpening_at() {
        return opening_at;
    }

    public String getClosing_at() {
        return closing_at;
    }

    public String getDistance() { return distance; }
}
