package es.cuatrovientos.a4vgo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

    private Button buttonSearch;
    private Activity context = this ;
    private Boolean goBackBool = true;
    private ImageView imageSwitch;
    private String routeType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_routes);
        txtOrigin2 = findViewById(R.id.txtOrigen2);
        txtDateTime = findViewById(R.id.txtDateTime);
        bottom = findViewById(R.id.bnNavigation);
        bottom.setSelectedItemId(R.id.navigation_trips);
        imageSwitch = findViewById(R.id.imageSwitch);
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

        txtDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog(v);
            }
        });
        txtOrigin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainRoutesActivity.this, searchMapActivity.class);
                intent.putExtra("REQUEST_CODE",REQUEST_MAP_GO );
                startActivity(intent);
            }
        });
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainRoutesActivity.this, searchMapActivity.class);
                intent.putExtra("REQUEST_CODE",REQUEST_MAP_GO );
                startActivity(intent);
            }
        });
        txtDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainRoutesActivity.this, searchMapActivity.class);
                intent.putExtra("routeType", routeType);
                /*
                intent.putExtra("selectedCoordinates", selectedCoordinates);
                intent.putExtra("date", selectedDateStr);
                intent.putExtra("time", selectedTimeStr);
                intent.putExtra("availableSeats", plazas);*/
                startActivity(intent);
            }
        });

    }

    private void showDateTimePickerDialog(View view) {

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Formatea la fecha como desees
                        String formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        txtDateTime.setText(formattedDate);
                    }
                },
                year,
                month,
                day
        );

        // Muestra el DatePickerDialog
        datePickerDialog.show();

    }


}