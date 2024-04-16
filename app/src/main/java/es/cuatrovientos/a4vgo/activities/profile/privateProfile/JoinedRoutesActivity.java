package es.cuatrovientos.a4vgo.activities.profile.privateProfile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.adapters.RecyclerJoinedRoutesAdapter;

public class JoinedRoutesActivity extends AppCompatActivity {
    Button btnBack;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_routes);

        btnBack = findViewById(R.id.btnBackJoinedRoutes);
        recyclerView = findViewById(R.id.rvJoinedRoutes);

        RecyclerJoinedRoutesAdapter adapter = new RecyclerJoinedRoutesAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        btnBack.setOnClickListener(v -> finish());

    }

}