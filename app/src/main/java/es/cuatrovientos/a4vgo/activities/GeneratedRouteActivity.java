package es.cuatrovientos.a4vgo.activities;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.apiServices.ApiService;
import es.cuatrovientos.a4vgo.models.Route;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeneratedRouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText txtLatitud, txtLongitud;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private List<Address> addresses;
    private MarkerOptions markerOptions;
    private Marker marker;
    private Location currentLocation;

    private String start ;
    private String end ;

    private LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_route);
        Button btnCalulate = findViewById(R.id.btn2);
        btnCalulate.setOnClickListener(view -> {
            start = "-1.5977543559219463,42.814164000000005";
            end = "-1.6598970153413861,42.82437339317052";
            createRoute();

        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    public Retrofit getRetrofit(){
        return new Retrofit.Builder()
                .baseUrl("https://api.openrouteservice.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }
    @SuppressLint("StaticFieldLeak")
    public void createRoute() {
        new AsyncTask<Void, Void, Route>() {
            @Override
            protected Route doInBackground(Void... voids) {
                ApiService service = getRetrofit().create(ApiService.class);
                Call<Route> call = service.getRoute("5b3ce3597851110001cf62489460c1c290314ef5948389a099dc4ca3", start, end);
                Route ruta = null;
                try {
                    Response<Route> response = call.execute();
                    Log.i("moha",start);
                    Log.i("moha",end);

                    if (response.isSuccessful()) {
                        ruta = response.body();
                        Log.i("moha","OK");
                    }else{

                        Log.i("moha","KO");

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return ruta;
            }

            @Override
            protected void onPostExecute(Route ruta) {
                if (ruta != null) {
                    drawRoute(ruta);
                }
            }
        }.execute();
    }

    public void drawRoute(Route ruta) {
        PolylineOptions polyLineOptions = new PolylineOptions();
        List<List<Double>> coordinates = ruta.getFeatures().get(0).getGeometry().getCoordinates();
        for (List<Double> coordinate : coordinates) {
            polyLineOptions.add(new LatLng(coordinate.get(1), coordinate.get(0)));
        }
        polyLineOptions.width(15);
        polyLineOptions.color(Color.GREEN); // Color de la línea
        polyLineOptions.pattern(Arrays.asList(new Dot(), new Gap(10))); // Patrón de la línea (puntos y espacios)
        // Agrega pines para origen y destino
        LatLng origen = new LatLng(coordinates.get(0).get(1), coordinates.get(0).get(0));
        LatLng destino = new LatLng(coordinates.get(coordinates.size() - 1).get(1), coordinates.get(coordinates.size() - 1).get(0));

        MarkerOptions origenMarkerOptions = new MarkerOptions()
                .position(origen)
                .title("Origen");

        MarkerOptions destinoMarkerOptions = new MarkerOptions()
                .position(destino)
                .title("Destino");
        runOnUiThread(() -> {
            mMap.addMarker(origenMarkerOptions);
            mMap.addMarker(destinoMarkerOptions);
            Polyline poly = mMap.addPolyline(polyLineOptions);
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

    }
}


