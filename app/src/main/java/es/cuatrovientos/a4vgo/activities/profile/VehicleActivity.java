package es.cuatrovientos.a4vgo.activities.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.adapters.RecyclerVehicleAdapter;

public class VehicleActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        recyclerView = findViewById(R.id.rvVehicle);
        RecyclerVehicleAdapter adapter = new RecyclerVehicleAdapter();

        recyclerView.setAdapter(adapter);
    }
}