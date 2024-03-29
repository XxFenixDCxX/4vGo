package es.cuatrovientos.a4vgo.models;

import com.google.gson.annotations.SerializedName;

public class Feature {
    @SerializedName("geometry")
    private Geometry geometry;
    public Geometry getGeometry() {
        return geometry;
    }
}
