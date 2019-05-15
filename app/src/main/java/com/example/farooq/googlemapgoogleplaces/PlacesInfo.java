package com.example.farooq.googlemapgoogleplaces;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class PlacesInfo {

    private String name;
    private String address;
    private String attribute;
    private String id;
    private LatLng latLng;
    private Uri websiteUri;
    private String phoneNumber;
    private float rating;
    public PlacesInfo() { }
    public PlacesInfo(String name, String address, String attribute,
                      String id, LatLng latLng, Uri websiteUri, String phoneNumber, float rating) {
        this.name = name;
        this.address = address;
        this.attribute = attribute;
        this.id = id;
        this.latLng = latLng;
        this.websiteUri = websiteUri;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "PlacesInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", attribute='" + attribute + '\'' +
                ", id='" + id + '\'' +
                ", latLng=" + latLng +
                ", websiteUri=" + websiteUri +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", rating=" + rating +
                '}';
    }
}
