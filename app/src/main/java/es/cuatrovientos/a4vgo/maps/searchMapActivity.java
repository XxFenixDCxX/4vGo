package es.cuatrovientos.a4vgo.maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.LoginActivity;
import es.cuatrovientos.a4vgo.activities.MainActivity;
import es.cuatrovientos.a4vgo.activities.createRoute.AddRouteActivity;
import es.cuatrovientos.a4vgo.tasks.OpenStreetMapSearchTask;

public class searchMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private AutoCompleteTextView autoCompleteTextViewIntroduceDirection;
    private ArrayAdapter<String> adapter;
    private Handler searchHandler;
    static LatLng currentLocation;
    private Geocoder geocoder;
    private List<Address> addresses;
    private Marker marker;
    private LocationManager locationManager;

    private FloatingActionButton fabSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_map);
        fabSave = findViewById(R.id.fabSave);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLocationAndNavigateBack();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        checkGPS();
        mapFragment.getMapAsync(this);

        autoCompleteTextViewIntroduceDirection = findViewById(R.id.autoCompleteTextViewIntroduceDirection);

        // Configurar el adaptador para el AutoCompleteTextView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextViewIntroduceDirection.setAdapter(adapter);

        // Inicializar el manejador
        searchHandler = new Handler();

        autoCompleteTextViewIntroduceDirection.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchHandler.removeCallbacksAndMessages(null);

                searchHandler.postDelayed(() -> performSearch(charSequence.toString()), 500);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void performSearch(String query) {
        new OpenStreetMapSearchTask(autoCompleteTextViewIntroduceDirection, adapter, mMap).execute(query);
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        geocoder = new Geocoder(this, Locale.getDefault());
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Toast.makeText(searchMapActivity.this, "Changeg!->GPS", Toast.LENGTH_LONG).show();
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker draggedMarker) {
                currentLocation = draggedMarker.getPosition();

                double latitude = marker.getPosition().latitude;
                double longitude = marker.getPosition().longitude;

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Address address = addresses.get(0);

                Toast.makeText(searchMapActivity.this, "Address:" + address.getAddressLine(0) + "\n" +
                        "City: " + address.getLocality() + "\n" +
                        "State: " + address.getAdminArea() + "\n" +
                        "Country: " + address.getCountryName() + "\n" +
                        "Postal Code: " + address.getPostalCode() + "\n", Toast.LENGTH_LONG).show();
            }


            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {

            }
        });
    }

    private void checkGPS(){
        try {
            int gpsSignal = Settings.Secure.getInt(this.getContentResolver(),Settings.Secure.LOCATION_MODE);
            if (gpsSignal==0){
                showInfoAlert();
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();

        }
    }
    private void showInfoAlert(){
        new AlertDialog.Builder(this)
                .setTitle("GPS Alert")
                .setMessage("You don´t have the GPS signal enabled, Would you like to enable the GPS signal now?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel",null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length == 0 ||
                        grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, AddRouteActivity.class);
                    startActivity(intent);

                }
        }

    }

    private void saveLocationAndNavigateBack() {
        if (currentLocation != null) {
            double latitude = currentLocation.latitude;
            double longitude = currentLocation.longitude;
            String latitudeStr = String.valueOf(latitude);
            String longitudeStr = String.valueOf(longitude);

            String streetName = getStreetName(latitude, longitude);

            Intent intent = new Intent(searchMapActivity.this, AddRouteActivity.class);
            intent.putExtra("streetName", streetName);
            intent.putExtra("latitudeStr", latitude);
            intent.putExtra("longitudeStr", longitude);

            Log.i("moha2", "Street Name: " + streetName);
            Log.i("moha2", "Selected Longitude: " + latitude);
            Log.i("moha2", "Selected Coordinates: " + longitude);
            startActivity(intent);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }

        finish();
    }

    private String getStreetName(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                return addresses.get(0).getThoroughfare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
    public static LatLng passCoordinates(LatLng location){
        currentLocation = location;
        return location;
    }

}

