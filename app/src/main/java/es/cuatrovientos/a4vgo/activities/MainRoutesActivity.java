package es.cuatrovientos.a4vgo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.createRoute.AddRouteActivity;
import es.cuatrovientos.a4vgo.activities.profile.ProfileActivity;

public class MainRoutesActivity extends AppCompatActivity {
    BottomNavigationView bottom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_routes);

        bottom = findViewById(R.id.bnNavigation);
        bottom.setSelectedItemId(R.id.navigation_trips);

        bottom.setOnItemSelectedListener(item -> {
                    int id = item.getItemId();
                    if (id == R.id.navigation_profile) {
                        overridePendingTransition(0, 0);
                        Intent intent = new Intent(MainRoutesActivity.this , ProfileActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    } else if (id == R.id.navigation_publish_route) {
                        overridePendingTransition(0, 0);
                        Intent intent = new Intent(MainRoutesActivity.this , AddRouteActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    } else if (id == R.id.navigation_chat){
                        overridePendingTransition(0, 0);
                        Intent intent = new Intent(MainRoutesActivity.this , AddRouteActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                    return true;
                }
        );
    }
}