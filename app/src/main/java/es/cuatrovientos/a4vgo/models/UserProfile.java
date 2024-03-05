package es.cuatrovientos.a4vgo.models;


import java.util.Map;

public class UserProfile {
    private String firstName;
    private String lastName;
    private String birthdate;
    private String dni;
    private String email;
    private String language;
    private String name;
    private String profileImage;
    private boolean spam;
    private String surname;
    private String username;
    private double co2Points;

    public UserProfile() {
        // Constructor vacío necesario para Firestore
    }

    public static UserProfile fromMap(Map<String, Object> data) {
        UserProfile userProfile = new UserProfile();

        userProfile.setFirstName(getStringFromMap(data, "firstName"));
        userProfile.setLastName(getStringFromMap(data, "lastName"));
        userProfile.setBirthdate(getStringFromMap(data, "birthdate"));
        userProfile.setDni(getStringFromMap(data, "dni"));
        userProfile.setEmail(getStringFromMap(data, "email"));
        userProfile.setLanguage(getStringFromMap(data, "language"));
        userProfile.setName(getStringFromMap(data, "name"));
        userProfile.setProfileImage(getStringFromMap(data, "profileImage"));
        userProfile.setSpam(getBooleanFromMap(data, "spam"));
        userProfile.setSurname(getStringFromMap(data, "surname"));
        userProfile.setUsername(getStringFromMap(data, "username"));
        userProfile.setCo2Points(getDoubleFromMap(data, "co2Points"));

        return userProfile;
    }

    private static String getStringFromMap(Map<String, Object> data, String key) {
        return data.containsKey(key) ? (String) data.get(key) : "";
    }

    private static boolean getBooleanFromMap(Map<String, Object> data, String key) {
        return data.containsKey(key) && data.get(key) instanceof Boolean ? (Boolean) data.get(key) : false;
    }

    private static double getDoubleFromMap(Map<String, Object> data, String key) {
        return data.containsKey(key) && data.get(key) instanceof Number ? ((Number) data.get(key)).doubleValue() : 0.0;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public boolean isSpam() {
        return spam;
    }

    public void setSpam(boolean spam) {
        this.spam = spam;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getCo2Points() {
        return co2Points;
    }

    public void setCo2Points(double co2Points) {
        this.co2Points = co2Points;
    }
}