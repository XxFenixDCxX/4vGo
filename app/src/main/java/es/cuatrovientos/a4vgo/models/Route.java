package es.cuatrovientos.a4vgo.models;


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
    public static final double EARTH_RADIUS = 6371;


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
    public Route(String routeType, String origin, String destination,String userId, String selectedDate,
                 String selectedTime, int maxSeats, String selectedVehiclePlate,
                 boolean isFrequent, String comments) {
        this.routeType = routeType;
        this.origin = origin;
        this.destination = destination;
        this.userId = userId;
        this.selectedDate = selectedDate;
        this.selectedTime = selectedTime;
        this.maxSeats = maxSeats;
        this.selectedVehiclePlate = selectedVehiclePlate;
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
    private double calculateDistance() {

        if (origin == null || destination == null) {
            throw new IllegalStateException("Coordenadas de origen y destino son necesarias para calcular la distancia.");
        }

        String[] originCoords = origin.split(",");
        String[] destCoords = destination.split(",");

        double originLat = Double.parseDouble(originCoords[0]);
        double originLng = Double.parseDouble(originCoords[1]);
        double destLat = Double.parseDouble(destCoords[0]);
        double destLng = Double.parseDouble(destCoords[1]);

        double dLat = Math.toRadians(destLat - originLat);
        double dLng = Math.toRadians(destLng - originLng);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(originLat)) * Math.cos(Math.toRadians(destLat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public double calculateCreatorCO2Points() {
        double distance = calculateDistance();
        return distance * 5;
    }
    public double calculateUserCO2Points() {
        double distance = calculateDistance();
        return distance * 3;
    }

}
