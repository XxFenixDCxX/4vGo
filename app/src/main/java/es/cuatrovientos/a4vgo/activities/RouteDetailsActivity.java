package es.cuatrovientos.a4vgo.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
    Button back, joinUnjoinRoute;
    Route route;
    Bundle bundle;
    String dni, joinedUsersDNI;
    TextView maxSeats, origin, destination, date, time, name, surname, plate, carColor;
    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
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
        joinUnjoinRoute = findViewById(R.id.btnJoinUnJoinRoute);
        dni = getIntent().getStringExtra("dni");

        if (route.getJoinedUsersDNI() != null && route.getJoinedUsersDNI().contains(dni)) {
            joinUnjoinRoute.setText(R.string.deJoinRoute);
            joinUnjoinRoute.setBackgroundColor(getColor(R.color.red));
        } else {
            joinUnjoinRoute.setText(R.string.joinRoute);
            joinUnjoinRoute.setBackgroundColor(getColor(R.color.green));
        }

        back.setOnClickListener(v -> finish());

        joinUnjoinRoute.setOnClickListener(v -> {
            joinedUsersDNI = route.getJoinedUsersDNI();
            if(joinUnjoinRoute.getText().equals(getString(R.string.joinRoute))){
                if(route.getMaxSeats() != 0){
                    if(joinedUsersDNI == null || joinedUsersDNI.isEmpty()){
                        joinedUsersDNI = dni;
                    } else {
                        joinedUsersDNI += ","+dni;
                    }
                    DocumentReference rutaRef = FirebaseFirestore.getInstance().collection("routes").document(route.getRouteID());
                    rutaRef.update("joinedUsersDNI", joinedUsersDNI).addOnSuccessListener(aVoid -> {
                        route.setJoinedUsersDNI(joinedUsersDNI);
                        route.setMaxSeats(route.getMaxSeats()-1);
                        rutaRef.update("maxSeats", route.getMaxSeats());
                        joinUnjoinRoute.setText(R.string.deJoinRoute);
                        maxSeats.setText(route.getMaxSeats() + "");
                        joinUnjoinRoute.setBackgroundColor(getColor(R.color.red));
                    });
                }
            }
            if(joinUnjoinRoute.getText().equals(getString(R.string.deJoinRoute))){
                joinedUsersDNI = null;
                for(String userDNI : route.getJoinedUsersDNI().split(",")){
                    if(!userDNI.equals(dni)){
                        if(joinedUsersDNI == null){
                            joinedUsersDNI = userDNI;
                        } else {
                            joinedUsersDNI += ","+userDNI;
                        }
                    }
                }
                DocumentReference rutaRef = FirebaseFirestore.getInstance().collection("routes").document(route.getRouteID());
                rutaRef.update("joinedUsersDNI", joinedUsersDNI).addOnSuccessListener(aVoid -> {
                    route.setJoinedUsersDNI(joinedUsersDNI);
                    route.setMaxSeats(route.getMaxSeats()+1);
                    rutaRef.update("maxSeats", route.getMaxSeats());
                    joinUnjoinRoute.setText(R.string.joinRoute);
                    maxSeats.setText(route.getMaxSeats() + "");
                    joinUnjoinRoute.setBackgroundColor(getColor(R.color.green));
                });
            }
        });
        maxSeats.setText(route.getMaxSeats() + "");
        double latitude, longitude;
        if(route.getRouteType().equals("Ida")){
            latitude = Double.parseDouble(route.getOrigin().split(",")[0]);
            longitude = Double.parseDouble(route.getOrigin().split(",")[1]);
            origin.setText(getStreetName(latitude, longitude));
            destination.setText(R.string.ci_cuatrovientos);
        } else if (route.getRouteType().equals("Vuelta")){
            latitude = Double.parseDouble(route.getDestination().split(",")[0]);
            longitude = Double.parseDouble(route.getDestination().split(",")[1]);
            destination.setText(getStreetName(latitude, longitude));
            origin.setText(R.string.ci_cuatrovientos);
        }
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