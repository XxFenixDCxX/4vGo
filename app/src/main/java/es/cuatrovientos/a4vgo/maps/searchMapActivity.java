package es.cuatrovientos.a4vgo.maps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import es.cuatrovientos.a4vgo.activities.MainRoutesActivity;
import es.cuatrovientos.a4vgo.activities.createRoute.AddRouteActivity;
import es.cuatrovientos.a4vgo.tasks.OpenStreetMapSearchTask;
import es.cuatrovientos.a4vgo.utils.DialogUtils;

public class searchMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private int locationRequestedBy = 1;
    private GoogleMap mMap;
    private AutoCompleteTextView autoCompleteTextViewIntroduceDirection;
    private ArrayAdapter<String> adapter;
    private Handler searchHandler;
    static LatLng currentLocation;
    private Geocoder geocoder;
    private List<Address> addresses;
    private Marker marker;

    private String routeType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_map);
        Intent intent1 = getIntent();

        if (intent1 != null && intent1.hasExtra("routeType") && intent1.hasExtra("REQUEST_MAP_GO")) {
            locationRequestedBy = intent1.getIntExtra("REQUEST_MAP_GO", 1);
            routeType = intent1.getStringExtra("routeType");
        }
        FloatingActionButton fabSave = findViewById(R.id.fabSave);
        fabSave.setOnClickListener(view -> saveLocationAndNavigateBack());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        checkGPS();
        mapFragment.getMapAsync(this);

        autoCompleteTextViewIntroduceDirection = findViewById(R.id.autoCompleteTextViewIntroduceDirection);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextViewIntroduceDirection.setAdapter(adapter);

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

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mMap.setOnMapClickListener(latLng -> {

            mMap.clear();

            if (marker != null) {
                marker.remove();
            }

            MarkerOptions markerOptions = new MarkerOptions().position(latLng).draggable(true);
            marker = mMap.addMarker(markerOptions);
            currentLocation = latLng;

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
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel",null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
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

            Intent intent = null;

            switch (locationRequestedBy) {
                case 1:
                    intent = new Intent(searchMapActivity.this, MainRoutesActivity.class);
                    break;
                case 2:
                    intent = new Intent(searchMapActivity.this, AddRouteActivity.class);
                    break;

                default:
                    break;
            }

            if (intent != null) {
                intent.putExtra("streetName", streetName);
                intent.putExtra("latitudeStr", latitudeStr);
                intent.putExtra("longitudeStr", longitudeStr);
                intent.putExtra("routeType", routeType);
                startActivity(intent);
            } else {
                DialogUtils.showErrorDialog(this, String.valueOf(R.string.error), String.valueOf(R.string.error_saving_localization));
            }

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

