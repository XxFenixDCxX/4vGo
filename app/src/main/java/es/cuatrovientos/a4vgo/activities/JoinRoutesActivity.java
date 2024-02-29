package es.cuatrovientos.a4vgo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import es.cuatrovientos.a4vgo.R;

public class JoinRoutesActivity extends AppCompatActivity {
    RecyclerView routes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_routes);

        routes = findViewById(R.id.rvRoutes);
    }
}