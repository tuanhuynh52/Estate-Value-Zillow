package com.tuan.Estate_Value_Zillow;

/**
 * Created by Tuan on 10/11/2016.
 *
 */

public class PropertyDetail {

    //neighborhood information
    //private String zpid;
    private String street, city, state, zipcode;
    private Double lng, lat;
    private String yearBuilt, finishedSize, bedroom, bathroom, zEstimate;

    public PropertyDetail(String street, String city, String state, String zipcode,
                          Double lng, Double lat,
                          String yearBuilt, String finishedSize, String bedroom,
                          String bathroom, String zEstimate) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.lng = lng;
        this.lat = lat;
        this.yearBuilt = yearBuilt;
        this.finishedSize = finishedSize;
        this.bedroom = bedroom;
        this.bathroom = bathroom;
        this.zEstimate = zEstimate;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getYearBuilt() {
        return yearBuilt;
    }

    public void setYearBuilt(String yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public String getFinishedSize() {
        return finishedSize;
    }

    public void setFinishedSize(String finishedSize) {
        this.finishedSize = finishedSize;
    }

    public String getBedroom() {
        return bedroom;
    }

    public void setBedroom(String bedroom) {
        this.bedroom = bedroom;
    }

    public String getBathroom() {
        return bathroom;
    }

    public void setBathroom(String bathroom) {
        this.bathroom = bathroom;
    }

    public String getzEstimate() {
        return zEstimate;
    }

    public void setzEstimate(String zEstimate) {
        this.zEstimate = zEstimate;
    }
}
