package es.cuatrovientos.a4vgo.activities.createRoute;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.MainRoutesActivity;
import es.cuatrovientos.a4vgo.activities.profile.privateProfile.ProfileActivity;
import es.cuatrovientos.a4vgo.adapters.CustomSpinnerAdapter;
import es.cuatrovientos.a4vgo.maps.searchMapActivity;
import es.cuatrovientos.a4vgo.utils.DialogUtils;

public class AddRouteActivity extends AppCompatActivity {
    private static final int REQUEST_MAP_GO = 2;
    private Spinner spinner;
    private TextView txtOrigin;
    private TextView txtDestination;
    private TextView txtDateTime;
    private TextView txtTime;
    private EditText editTextAvailableSeats;
    private ImageButton imageButtonDestination;
    private ImageButton imageButtonOrigin;
    private Button btnNext;
    private BottomNavigationView bottom;
    private final Context context = this;
    private String routeType;
    private String selectedCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);

        initializeViews();
        setupSpinner();
        Intent intent2 = getIntent();
        if (intent2.hasExtra("streetName") && intent2.hasExtra("latitudeStr") && intent2.hasExtra("longitudeStr")) {
            handleIntentExtras(intent2);
        }

        setClickListeners();
        setBottomNavigationListener();
    }

    private void initializeViews() {
        bottom = findViewById(R.id.bnNavigation);
        bottom.setSelectedItemId(R.id.navigation_publish_route);
        spinner = findViewById(R.id.spinner3);
        txtOrigin = findViewById(R.id.txtOrigin);
        txtDestination = findViewById(R.id.txtDestination);
        txtDateTime = findViewById(R.id.txtDateTime);
        txtTime = findViewById(R.id.txtTime);
        editTextAvailableSeats = findViewById(R.id.editTextAvailableSeats);
        imageButtonDestination = findViewById(R.id.imageButtonDestination);
        imageButtonOrigin = findViewById(R.id.imageButtonOrigin);
        btnNext = findViewById(R.id.btnNext);


    }

    private void setupSpinner() {
        String[] items = new String[]{getString(R.string.route_type_ida), getString(R.string.route_type_vuelta)};
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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

    private void handleIntentExtras(Intent intent) {
        String streetName = intent.getStringExtra("streetName");
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        String selectedLatitude = bundle.getString("latitudeStr");
        String selectedLongitude = bundle.getString("longitudeStr");
        routeType = bundle.getString("routeType");
        selectedCoordinates = selectedLatitude + "," + selectedLongitude;

        if (routeType.equals("Ida")) {
            spinner.setSelection(0);
            txtOrigin.setHint(streetName);
        } else {
            spinner.setSelection(1);
            txtDestination.setHint(streetName);
        }
    }

    private void setClickListeners() {
        txtDateTime.setOnClickListener(this::showDateTimePickerDialog);

        imageButtonDestination.setOnClickListener(v -> {
            Intent intent = new Intent(AddRouteActivity.this, searchMapActivity.class);
            intent.putExtra("routeType", routeType);
            intent.putExtra("REQUEST_MAP_GO", REQUEST_MAP_GO);
            startActivity(intent);
        });

        imageButtonOrigin.setOnClickListener(v -> {
            Intent intent = new Intent(AddRouteActivity.this, searchMapActivity.class);
            intent.putExtra("routeType", routeType);
            intent.putExtra("REQUEST_MAP_GO", REQUEST_MAP_GO);
            startActivity(intent);
        });

        txtTime.setOnClickListener(this::showTimePickerDialog);

        btnNext.setOnClickListener(view -> validateAndProceed());
    }

    private void setBottomNavigationListener() {
        bottom.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_trips) {
                navigateToActivity(MainRoutesActivity.class);
            } else if (id == R.id.navigation_profile) {
                navigateToActivity(ProfileActivity.class);
            } else if (id == R.id.navigation_chat) {
                navigateToActivity(AddRouteActivity.class);
            }
            return true;
        });
    }

    private void navigateToActivity(Class<?> activityClass) {
        overridePendingTransition(0, 0);
        Intent intent = new Intent(AddRouteActivity.this, activityClass);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void showTimePickerDialog(View view) {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                (view1, hourOfDay, minute) -> {
                    String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                    txtTime.setText(formattedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void showDateTimePickerDialog(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view1, year1, month1, dayOfMonth) -> {
                    String formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                    txtDateTime.setText(formattedDate);
                },
                year,
                month,
                day
        );
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
        return plazas >= 1 && plazas <= 10;
    }

    private void showMessage(String message) {
        DialogUtils.showWarningDialog(this, getString(R.string.errorLoginTitle), message);
    }
}
