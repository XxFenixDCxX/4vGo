package es.cuatrovientos.a4vgo.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Geometry {
    @SerializedName("coordinates")
    public List<List<Double>> coordinates;

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }
}
