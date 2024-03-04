package es.cuatrovientos.a4vgo.models;


public class User {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String birthdate;
    private String dni;
    private String email;
    private String language;
    private String name;
    private String profileImage;
    private boolean spam;
    private String surname;
    private String username;
    private double rating;
    private double co2Points;

    public User(String firstName, String lastName, String passwordHash,
                           String phoneNumber, String birthdate, String dni,
                           String email, String language, String name, String profileImage,
                           boolean spam, String surname, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.birthdate = birthdate;
        this.dni = dni;
        this.email = email;
        this.language = language;
        this.name = name;
        this.profileImage = profileImage;
        this.spam = spam;
        this.surname = surname;
        this.username = username;
        this.rating = 0.0;
        this.co2Points = 0.0;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getCo2Points() {
        return co2Points;
    }

    public void setCo2Points(double co2Points) {
        this.co2Points = co2Points;
    }
}
