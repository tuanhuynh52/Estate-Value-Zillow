package com.tuan.Estate_Value_Zillow;

/**
 * Created by Tuan on 10/11/2016.
 *
 */

public class PropertyDetail {

    //neighborhood information
    private String zpid;
    private String street, city, state, zipcode, zEstimate;
    private Double lng, lat;


    public PropertyDetail(String zpid, String street, String city, String state, String zipcode,
                          Double lng, Double lat,
                          String zEstimate) {
        this.zpid = zpid;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.lng = lng;
        this.lat = lat;
        this.zEstimate = zEstimate;
    }

    public String getZpid() {
        return zpid;
    }

    public void setZpid(String zpid) {
        this.zpid = zpid;
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

    public String getzEstimate() {
        return zEstimate;
    }

    public void setzEstimate(String zEstimate) {
        this.zEstimate = zEstimate;
    }
}
