package es.cuatrovientos.a4vgo.activities.createRoute;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.MainRoutesActivity;
import es.cuatrovientos.a4vgo.activities.profile.ProfileActivity;
import es.cuatrovientos.a4vgo.adapters.CustomSpinnerAdapter;

public class AddRouteActivity extends AppCompatActivity {

    private EditText editTextOrigen;
    private EditText editTextDestination;
    private ImageButton imageButtonDestination;
    private ImageButton imageButtonOrigin;
    BottomNavigationView bottom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);
        //Spinner adapter
        Spinner spinner1 = findViewById(R.id.spinner3);
        editTextOrigen = findViewById(R.id.editTextOrigin);
        editTextDestination = findViewById(R.id.editTextDestination);
        imageButtonDestination = findViewById(R.id.imageButtonDestination);
        imageButtonOrigin = findViewById(R.id.imageButtonOrigin);
        bottom = findViewById(R.id.bnNavigation);
        bottom.setSelectedItemId(R.id.navigation_publish_route);

        Spinner spinner = findViewById(R.id.spinner3);
        String[] items = new String[]{"Ida", "Vuelta"};

        bottom.setOnItemSelectedListener(item -> {
                    int id = item.getItemId();
                    if (id == R.id.navigation_trips) {
                        overridePendingTransition(0, 0);
                        Intent intent = new Intent(AddRouteActivity.this , MainRoutesActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    } else if (id == R.id.navigation_profile) {
                        overridePendingTransition(0, 0);
                        Intent intent = new Intent(AddRouteActivity.this , ProfileActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    } else if (id == R.id.navigation_chat){
                        overridePendingTransition(0, 0);
                        Intent intent = new Intent(AddRouteActivity.this , AddRouteActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                    return true;
                }
        );

// Utiliza tu adaptador personalizado
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, items);
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
                    editTextDestination.setText(getString(R.string.ci_cuatrovientos));
                    editTextDestination.setEnabled(false);
                    editTextOrigen.setEnabled(true);
                    imageButtonDestination.setVisibility(View.GONE);
                    imageButtonOrigin.setVisibility(View.VISIBLE);

                } else { // Vuelta
                    editTextOrigen.setText(getString(R.string.ci_cuatrovientos));
                    editTextDestination.setText("");

                    editTextOrigen.setEnabled(false);
                    editTextDestination.setEnabled(true);
                    imageButtonOrigin.setVisibility(View.GONE);
                    imageButtonDestination.setVisibility(View.VISIBLE);


                }
            }
        });


    }
}