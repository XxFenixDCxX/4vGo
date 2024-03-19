package es.cuatrovientos.a4vgo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.adapters.RecyclerRoutesAdapter;

public class JoinRoutesActivity extends AppCompatActivity {
    RecyclerView routes;
    ImageView back;
    TextView destination, origin, date, peopleNum;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_routes);

        bundle = getIntent().getExtras();
        String routeType = bundle.getString("routeType");
        String selectedDate = bundle.getString("selectedDate");
        int numPeople = Integer.parseInt(bundle.getString("numPeople"));
        double latitude = Double.parseDouble(bundle.getString("selectedLatitude"));
        double longitude = Double.parseDouble(bundle.getString("selectedLongitude"));
        String cords = latitude + "," + longitude;
        routes = findViewById(R.id.rvRoutes);
        RecyclerRoutesAdapter adapter = new RecyclerRoutesAdapter(routeType, cords, selectedDate, numPeople);
        back = findViewById(R.id.btnBackRoutesJoin);
        destination = findViewById(R.id.DestinationCity);
        origin = findViewById(R.id.OriginCity);
        date = findViewById(R.id.txtDate);
        peopleNum = findViewById(R.id.txtPeopleNum);
        routes.setAdapter(adapter);
        routes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        back.setOnClickListener(v -> {
            Intent intent = new Intent(JoinRoutesActivity.this, MainRoutesActivity.class);
            startActivity(intent);
        });
        String streetName = getStreetName(latitude, longitude);
        if (routeType.equals("Ida")){
            origin.setText(streetName);
            destination.setText(R.string.center);
        }else {
            origin.setText(R.string.center);
            destination.setText(streetName);
        }
        peopleNum.setText(String.valueOf(numPeople));
        date.setText(selectedDate);
    }
    private String getStreetName(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String thoroughfare = addresses.get(0).getThoroughfare();
                String featureName = addresses.get(0).getFeatureName();
                String addressLine = addresses.get(0).getAddressLine(0);

                if (thoroughfare != null && !thoroughfare.isEmpty()) {
                    return thoroughfare;
                } else if (featureName != null && !featureName.isEmpty()) {
                    return featureName;
                } else if (addressLine != null && !addressLine.isEmpty()) {
                    return addressLine;
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return "";
    }
}