package com.ohhoonim.demo_spel.inventor;

public final class PlaceOfBirth {
    private final String city;
    private final String country;

    public PlaceOfBirth(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}
