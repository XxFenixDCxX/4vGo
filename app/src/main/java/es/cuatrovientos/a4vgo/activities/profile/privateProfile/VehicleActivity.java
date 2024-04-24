package es.cuatrovientos.a4vgo.activities.profile.privateProfile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.adapters.RecyclerVehicleAdapter;

public class VehicleActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Button back;
    FloatingActionButton add;
    RecyclerVehicleAdapter adapter;
    private static final int ADD_VEHICLE_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        recyclerView = findViewById(R.id.rvVehicle);
        adapter = new RecyclerVehicleAdapter();
        back = findViewById(R.id.btnBackVehicle);
        add = findViewById(R.id.fabAddVehicle);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        back.setOnClickListener(v -> finish());

        add.setOnClickListener(v -> {
            Intent intent = new Intent(VehicleActivity.this, AddVehicleActivity.class);
            startActivityForResult(intent, ADD_VEHICLE_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_VEHICLE_REQUEST_CODE && resultCode == RESULT_OK) {
            adapter.fetchDataFromFirebase();
        }
    }

}