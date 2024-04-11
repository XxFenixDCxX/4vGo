package es.cuatrovientos.a4vgo.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.models.Route;

public class RouteDetailsActivity extends AppCompatActivity {
    Button back;
    Route route;
    TextView maxSeats, origin, destination, date, time, name, surname, plate, carColor;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);
        Route route = getIntent().getParcelableExtra("route");
        back = findViewById(R.id.btnBack);
        maxSeats = findViewById(R.id.txtSeats);
        origin = findViewById(R.id.txtOrigin);
        destination = findViewById(R.id.txtDestination);
        date = findViewById(R.id.txtDateAndTime1);
        time = findViewById(R.id.txtDateAndTime2);
        name = findViewById(R.id.txtName);
        surname = findViewById(R.id.txtApellidos);
        plate = findViewById(R.id.txtMatricula);
        carColor = findViewById(R.id.txtVehiculeColor);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(RouteDetailsActivity.this, MainRoutesActivity.class);
            startActivity(intent);
        });
        maxSeats.setText(route.getMaxSeats() + "");
        double latitude = Double.parseDouble(route.getOrigin().split(",")[0]);
        double longitude = Double.parseDouble(route.getOrigin().split(",")[1]);
        origin.setText(getStreetName(latitude, longitude));
        latitude = Double.parseDouble(route.getDestination().split(",")[0]);
        longitude = Double.parseDouble(route.getDestination().split(",")[1]);
        destination.setText(getStreetName(latitude, longitude));
        date.setText(route.getSelectedDate());
        time.setText(route.getSelectedTime());
        DocumentReference userDoc = FirebaseFirestore.getInstance().collection("personalDetails").document(route.getUserId());
        userDoc.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot user = task.getResult();
                if(user.exists()){
                    name.setText(user.getString("name"));
                    surname.setText(user.getString("surname"));
                }
            }
        });
        DocumentReference carDoc = FirebaseFirestore.getInstance().collection("cars").document(route.getSelectedVehicle());
        carDoc.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot car = task.getResult();
                if(car.exists()){
                    plate.setText(car.getString("plate"));
                    carColor.setText(car.getString("color"));
                }
            }
        });

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