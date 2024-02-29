package es.cuatrovientos.a4vgo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.createRoute.AddRouteActivity;
import es.cuatrovientos.a4vgo.activities.profile.ProfileActivity;
public class MainRoutesActivity extends AppCompatActivity {
    BottomNavigationView bottom;
    private TextView txtOrigin2;
    private TextView txtDestination;

    private String routeType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_routes);
        txtOrigin2 = findViewById(R.id.txtOrigen2);

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
        txtDestination = findViewById(R.id.txtDestination);

        Spinner spinner = findViewById(R.id.spinner3);
        String[] items = new String[]{getString(R.string.route_type_ida), getString(R.string.route_type_vuelta)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        // Spinner item selected listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateDestination(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }

            private void updateDestination(int selectedItemPosition) {
                if (selectedItemPosition == 0) { // Ida
                    txtDestination.setText(getString(R.string.ci_cuatrovientos));
                    txtDestination.setEnabled(false);
                    txtOrigin2.setEnabled(true);
                    txtOrigin2.setText("");



                    routeType = getString(R.string.route_type_ida);



                } else { // Vuelta
                    txtOrigin2.setText(getString(R.string.ci_cuatrovientos));
                    txtDestination.setText("");
                    txtOrigin2.setEnabled(false);
                    txtDestination.setEnabled(true);


                    routeType = getString(R.string.route_type_vuelta);

                }
            }

        });

        Spinner spinnerNumPeople = findViewById(R.id.spinnerNumPeople);
        String[] numbers = new String[]{"1", "2", "3", "4", "5"};

        ArrayAdapter<String> adapterNum = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numbers);
        adapterNum.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerNumPeople.setAdapter(adapterNum);

    }
}