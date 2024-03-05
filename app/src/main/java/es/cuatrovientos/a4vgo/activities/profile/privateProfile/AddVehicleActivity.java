package es.cuatrovientos.a4vgo.activities.profile.privateProfile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import es.cuatrovientos.a4vgo.R;

public class AddVehicleActivity extends AppCompatActivity {
    Button add;
    ImageButton back;
    FirebaseFirestore db;
    EditText plate, color, numSit;
    String dni;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        add = findViewById(R.id.btnAddVehicle);
        back = findViewById(R.id.btnBackVehicleAdd);
        db = FirebaseFirestore.getInstance();
        plate = findViewById(R.id.txtPlateVehicle);
        color = findViewById(R.id.txtColorVehicle);
        numSit = findViewById(R.id.txtSiteNumVehicle);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        Query query = FirebaseFirestore.getInstance().collection("personalDetails").whereEqualTo("email", currentUser.getEmail());
        query.get().addOnCompleteListener(task -> {
            QuerySnapshot querySnapshot = task.getResult();
            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
            dni = document.getString("dni");
        });

        back.setOnClickListener(v -> {
            Intent intent = new Intent(AddVehicleActivity.this, VehicleActivity.class);
            startActivity(intent);
        });

        add.setOnClickListener(v -> {
            String plateString = plate.getText().toString();
            String colorString = color.getText().toString();
            String numSitString = numSit.getText().toString();

            Map<String, String> carsMap = new HashMap<>();
            carsMap.put("plate", plateString.toLowerCase());
            carsMap.put("color", colorString.toLowerCase());
            carsMap.put("numSit", numSitString.toLowerCase());
            carsMap.put("userDNI", dni.toUpperCase());
            db.collection("cars").document(plateString.toLowerCase()).set(carsMap);
            Intent intent = new Intent(AddVehicleActivity.this, VehicleActivity.class);
            startActivity(intent);
        });
    }
}