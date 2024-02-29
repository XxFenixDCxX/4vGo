package es.cuatrovientos.a4vgo.models;

import java.util.List;

public class Route {
    private String routeType;
    private String origin;
    private String destination;

    private String userId;

    private String selectedDate;
    private String selectedTime;
    private int maxSeats;
    private String  selectedVehiclePlate;
    private boolean hasIntermediateStops;
    private boolean isFrequent;
    private String comments;

    // Constructor
    public Route(String routeType, String origin, String destination,String userId, String selectedDate,
                 String selectedTime, int maxSeats, String selectedVehiclePlate,
                 boolean hasIntermediateStops, boolean isFrequent, String comments) {
        this.routeType = routeType;
        this.origin = origin;
        this.destination = destination;
        this.userId = userId;
        this.selectedDate = selectedDate;
        this.selectedTime = selectedTime;
        this.maxSeats = maxSeats;
        this.selectedVehiclePlate = selectedVehiclePlate;
        this.hasIntermediateStops = hasIntermediateStops;
        this.isFrequent = isFrequent;
        this.comments = comments;
    }
    public Route(String routeType, String origin, String destination, String selectedDate,
                 String selectedTime, int maxSeats) {
        this.routeType = routeType;
        this.origin = origin;
        this.destination = destination;
        this.selectedDate = selectedDate;
        this.selectedTime = selectedTime;
        this.maxSeats = maxSeats;

    }
    public Route() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public void setSelectedTime(String selectedTime) {
        this.selectedTime = selectedTime;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    public String getSelectedVehicle() {
        return selectedVehiclePlate;
    }

    public void setSelectedVehicle(String selectedVehiclePlate) {
        this.selectedVehiclePlate = selectedVehiclePlate;
    }

    public boolean hasIntermediateStops() {
        return hasIntermediateStops;
    }

    public void setHasIntermediateStops(boolean hasIntermediateStops) {
        this.hasIntermediateStops = hasIntermediateStops;
    }

    public boolean isFrequent() {
        return isFrequent;
    }

    public void setFrequent(boolean frequent) {
        isFrequent = frequent;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
