package com.ohhoonim.demo_spel.inventor;

import java.time.LocalDate;
import java.util.Objects;

public class Inventor {
    private String name;
    private String nationality;
    private String[] inventions = new String[0];
    private LocalDate birthdate;
    private PlaceOfBirth placeOfBirth;

    public Inventor(String name, String nationality) {
        this.name = name;
        this.nationality = nationality;
        this.birthdate = LocalDate.now();
    }

    public Inventor(String name, LocalDate birthdate, String nationality) {
        this.name = name;
        this.nationality = nationality;
        this.birthdate = birthdate;
    }

    public Inventor() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public PlaceOfBirth getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(PlaceOfBirth placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public void setInventions(String[] inventions) {
        this.inventions = inventions;
    }

    public String[] getInventions() {
        return inventions;
    }

    @Override
    public String toString() {
        return "Inventor{name='" + name + "', nationality='" + nationality + "', birthdate=" + birthdate + "}";
    }
}
