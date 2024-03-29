package es.cuatrovientos.a4vgo.apiServices;

import es.cuatrovientos.a4vgo.models.Route;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/v2/directions/driving-car")
    Call<Route> getRoute(@Query("api_key") String key , @Query(value = "start", encoded = true) String start, @Query(value = "end", encoded = true) String end);
}

