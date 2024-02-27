package es.cuatrovientos.a4vgo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.CreateRoute.AddRouteActivity;

public class MainRoutesActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_routes);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(
                item -> {
                    int id = item.getItemId();
                    if (id == R.id.trips) {

                        return true;
                    } else if (id == R.id.trips) {
                        overridePendingTransition(0, 0);

                        Intent intent = new Intent(MainRoutesActivity.this , AddRouteActivity.class);
                        startActivity(intent);

                        overridePendingTransition(0, 0);

                        return true;
                    } else if (id == R.id.profile) {
                        overridePendingTransition(0, 0);

                        Intent intent = new Intent(MainRoutesActivity.this , AddRouteActivity.class);
                        startActivity(intent);

                        overridePendingTransition(0, 0);

                        return true;
                    }



                    else {
                        return false;
                    }
                }
        );

    }
}