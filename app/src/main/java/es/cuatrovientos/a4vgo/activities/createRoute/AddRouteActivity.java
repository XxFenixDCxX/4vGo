package es.cuatrovientos.a4vgo.activities.createRoute;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.MainRoutesActivity;
import es.cuatrovientos.a4vgo.activities.profile.ProfileActivity;
import es.cuatrovientos.a4vgo.adapters.CustomSpinnerAdapter;
import es.cuatrovientos.a4vgo.maps.searchMapActivity;
import es.cuatrovientos.a4vgo.models.Route;
import es.cuatrovientos.a4vgo.utils.DialogUtils;

public class AddRouteActivity extends AppCompatActivity {

    private Spinner spinner ;
    private TextView txtOrigin;
    private TextView txtDestination;
    private TextView txtDateTime;

    private Intent intent;
    private TextView txtTime;

    private final Context context = this;
    private ImageButton imageButtonDestination;
    private ImageButton imageButtonOrigin;

    private EditText editTextAvailableSeats ;
    private Button btnNext ;
    private String routeType;
    private String selectedItem;
    private String selectedCoordinates ;
    private String lonLatCuatrovientos;
    private BottomNavigationView bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);
        //Spinner adapter
        spinner = findViewById(R.id.spinner3);
        txtOrigin = findViewById(R.id.txtOrigin);
        txtDestination = findViewById(R.id.txtDestination);
        txtDateTime = findViewById(R.id.txtDateTime);
        txtTime = findViewById(R.id.txtTime);


        editTextAvailableSeats = findViewById(R.id.editTextAvailableSeats);
        imageButtonDestination = findViewById(R.id.imageButtonDestination);
        imageButtonOrigin = findViewById(R.id.imageButtonOrigin);
        btnNext = findViewById(R.id.btnNext);
        bottom = findViewById(R.id.bnNavigation);
        Spinner spinner = findViewById(R.id.spinner3);
        String[] items = new String[]{getString(R.string.route_type_ida), getString(R.string.route_type_vuelta)};



        Intent intent2 = getIntent();
        if (intent2.hasExtra("streetName") && intent2.hasExtra("latitudeStr") && intent2.hasExtra("longitudeStr")) {
            // Data is passed, extract and display
            String streetName = intent2.getStringExtra("streetName");
            Bundle bundle  ;
            bundle = getIntent().getExtras();
            assert bundle != null;
            String selectedLatitude = bundle.getString("latitudeStr");
            String selectedLongitude = bundle.getString("longitudeStr");

            String selectedCoordinates = selectedLatitude + "," + selectedLongitude;

            Log.i("moha", "Street Name: " + streetName);
            Log.i("moha", "Selected Latitude: " + selectedLatitude);
            Log.i("moha", "Selected Longitude: " + selectedLongitude);
            Log.i("moha", "Selected Coordinates: " + selectedCoordinates);

            txtOrigin.setHint(streetName);
        }
        txtDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePickerDialog(view);
            }
        });
        imageButtonDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddRouteActivity.this, searchMapActivity.class);
                startActivity(intent);
            }
        });
        imageButtonOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddRouteActivity.this, searchMapActivity.class);
                startActivity(intent);
            }
        });

        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndProceed();
            }
        });
        bottom.setSelectedItemId(R.id.navigation_publish_route);

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
                    txtDestination.setText(getString(R.string.ci_cuatrovientos));
                    txtDestination.setEnabled(false);
                    txtOrigin.setEnabled(true);
                    txtOrigin.setText("");

                    imageButtonDestination.setVisibility(View.GONE);
                    imageButtonOrigin.setVisibility(View.VISIBLE);

                    routeType = getString(R.string.route_type_ida);



                } else { // Vuelta
                    txtOrigin.setText(getString(R.string.ci_cuatrovientos));
                    txtDestination.setText("");
                    txtOrigin.setEnabled(false);
                    txtDestination.setEnabled(true);

                    imageButtonOrigin.setVisibility(View.GONE);
                    imageButtonDestination.setVisibility(View.VISIBLE);

                    routeType = getString(R.string.route_type_vuelta);

                }
            }

        });
    }
    public void showTimePickerDialog(View view) {
        // Obtén el contexto de la actividad
        final Calendar calendar = Calendar.getInstance();

        // Crea el TimePickerDialog para seleccionar la hora
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Formatea la hora como desees
                        String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                        // Agrega la hora seleccionada al TextView txtTime
                        txtTime.setText(formattedTime);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false // No muestra el formato 24 horas
        );

        // Muestra el TimePickerDialog
        timePickerDialog.show();
    }
    private void showDateTimePickerDialog(View view) {

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Crea el DatePickerDialog para seleccionar la fecha
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

    private void validateAndProceed() {
        String selectedDateStr = txtDateTime.getText().toString();
        String selectedTimeStr = txtTime.getText().toString();
        String plazasStr = editTextAvailableSeats.getText().toString();

        if (selectedDateStr.isEmpty() || selectedTimeStr.isEmpty()) {
            showMessage(getString(R.string.select_date_time_error));
            return;
        }


        if (!isDateValid(selectedDateStr, selectedTimeStr)) {
            showMessage(getString(R.string.date_time_error));
            return;
        }


        if (plazasStr.isEmpty()) {
            showMessage(getString(R.string.seats_empty_error));
            return;
        }

        int plazas;
        try {
            plazas = Integer.parseInt(plazasStr);
        } catch (NumberFormatException e) {
            showMessage(getString(R.string.seats_empty_error));
            return;
        }

        if (!isPlazasValid(plazas)) {
            showMessage(getString(R.string.seats_error));
            return;
        }

        Intent intent = new Intent(this, AddRouteSecondActivity.class);
        intent.putExtra("routeType", routeType);
        intent.putExtra("selectedCoordinates", selectedCoordinates);
        intent.putExtra("date", selectedDateStr);
        intent.putExtra("time", selectedTimeStr);

        intent.putExtra("availableSeats", plazas);
        startActivity(intent);
    }

    private boolean isDateValid(String dateStr, String timeStr) {
        Calendar currentDate = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar selectedDate = Calendar.getInstance();
        try {
            selectedDate.setTime(dateFormat.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        return selectedDate.get(Calendar.YEAR) > currentYear ||
                (selectedDate.get(Calendar.YEAR) == currentYear &&
                        (selectedDate.get(Calendar.MONTH) + 1) > currentMonth) ||
                (selectedDate.get(Calendar.YEAR) == currentYear &&
                        (selectedDate.get(Calendar.MONTH) + 1) == currentMonth &&
                        selectedDate.get(Calendar.DAY_OF_MONTH) >= currentDay);
    }


    private boolean isPlazasValid(int plazas) {
        // Implement your seats validation logic here
        return plazas >= 1 && plazas <= 10;
    }

    private void showMessage(String message) {
        DialogUtils.showWarningDialog(this, getString(R.string.errorLoginTitle), message);
    }

}