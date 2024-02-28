package es.cuatrovientos.a4vgo.activities.profile;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        recyclerView = findViewById(R.id.rvVehicle);
        RecyclerVehicleAdapter adapter = new RecyclerVehicleAdapter();
        back = findViewById(R.id.btnBackVehicle);
        add = findViewById(R.id.fabAddVehicle);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        back.setOnClickListener(v -> {
            Intent intent = new Intent(VehicleActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        add.setOnClickListener(v -> {
            Intent intent = new Intent(VehicleActivity.this, AddVehicleActivity.class);
            startActivity(intent);
        });
    }
}