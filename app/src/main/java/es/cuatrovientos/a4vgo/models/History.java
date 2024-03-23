package es.cuatrovientos.a4vgo.models;

public class History {
    private String date, destination, dni, numPeople, origin;

    public History(String date, String destination, String dni, String numPeople, String origin) {
        this.date = date;
        this.destination = destination;
        this.dni = dni;
        this.numPeople = numPeople;
        this.origin = origin;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNumPeople() {
        return numPeople;
    }

    public void setNumPeople(String numPeople) {
        this.numPeople = numPeople;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
