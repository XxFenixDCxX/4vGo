package es.cuatrovientos.a4vgo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.createRoute.AddRouteActivity;
import es.cuatrovientos.a4vgo.activities.profile.ProfileActivity;
import es.cuatrovientos.a4vgo.maps.searchMapActivity;

public class MainRoutesActivity extends AppCompatActivity {
    private final int REQUEST_MAP_GO = 1;
    private final int REQUEST_MAP_BACK = 2;

    BottomNavigationView bottom;
    private TextView txtOrigin2;
    private TextView txtDestination;
    private TextView txtDateTime;

    private final Activity context = this ;
    private final Boolean goBackBool = true;
    private String routeType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_routes);
        txtOrigin2 = findViewById(R.id.txtOrigen2);
        txtDateTime = findViewById(R.id.txtDateTime);
        Button buttonSearch = findViewById(R.id.buttonSearch);
        bottom = findViewById(R.id.bnNavigation);
        bottom.setSelectedItemId(R.id.navigation_trips);
        ImageView imageSwitch = findViewById(R.id.imageSwitch);
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

        imageSwitch.setOnClickListener(new View.OnClickListener() {
            boolean isVuelta = false;

            @Override
            public void onClick(View v) {
                if (isVuelta) {
                    txtDestination.setText(getString(R.string.ci_cuatrovientos));
                    txtDestination.setEnabled(false);
                    txtOrigin2.setEnabled(true);
                    txtOrigin2.setText("");
                    routeType = getString(R.string.route_type_ida);
                } else {
                    txtOrigin2.setText(getString(R.string.ci_cuatrovientos));
                    txtDestination.setText("");
                    txtOrigin2.setEnabled(false);
                    txtDestination.setEnabled(true);
                    routeType = getString(R.string.route_type_vuelta);
                }

                isVuelta = !isVuelta;
            }
        });




        Spinner spinnerNumPeople = findViewById(R.id.spinnerNumPeople);
        String[] numbers = new String[]{"1", "2", "3", "4", "5"};

        ArrayAdapter<String> adapterNum = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numbers);
        adapterNum.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerNumPeople.setAdapter(adapterNum);

        txtDateTime.setOnClickListener(this::showDateTimePickerDialog);
        txtOrigin2.setOnClickListener(v -> {
            Intent intent = new Intent(MainRoutesActivity.this, searchMapActivity.class);
            intent.putExtra("REQUEST_CODE",REQUEST_MAP_GO );
            startActivity(intent);
        });
        buttonSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainRoutesActivity.this, searchMapActivity.class);
            intent.putExtra("REQUEST_CODE",REQUEST_MAP_GO );
            startActivity(intent);
        });
        txtDestination.setOnClickListener(v -> {
            Intent intent = new Intent(MainRoutesActivity.this, searchMapActivity.class);
            intent.putExtra("routeType", routeType);
            startActivity(intent);
        });

    }

    private void showDateTimePickerDialog(View view) {

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view1, year1, month1, dayOfMonth) -> {
                    // Formatea la fecha como desees
                    @SuppressLint("DefaultLocale") String formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                    txtDateTime.setText(formattedDate);
                },
                year,
                month,
                day
        );

        // Muestra el DatePickerDialog
        datePickerDialog.show();

    }
}