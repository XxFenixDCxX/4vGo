package es.cuatrovientos.a4vgo.models;

public class Vehicle {
    private final String plate;
    private final String color;
    private final String siteNumber;

    public Vehicle(String plate, String color, String siteNumber) {
        this.plate = plate;
        this.color = color;
        this.siteNumber = siteNumber;
    }

    public String getPlate() {
        return plate;
    }

    public String getColor() {
        return color;
    }

    public String getSiteNumber() {
        return siteNumber;
    }
}
