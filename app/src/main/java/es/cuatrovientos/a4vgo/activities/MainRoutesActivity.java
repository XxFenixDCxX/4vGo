package es.cuatrovientos.a4vgo.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.createRoute.AddRouteActivity;
import es.cuatrovientos.a4vgo.activities.profile.privateProfile.ProfileActivity;
import es.cuatrovientos.a4vgo.maps.searchMapActivity;
import es.cuatrovientos.a4vgo.utils.DialogUtils;

public class MainRoutesActivity extends AppCompatActivity {

    private TextView txtOrigin2;
    private TextView txtDestination;
    private TextView txtDateTime;
    private Button buttonSearch;
    private ImageView imageSwitch, btnBack;
    private String selectedCoordinates;
    private EditText editNumPeople;
    private String routeType;
    private String selectedLongitude;
    private String selectedLatitude;
    private LinearLayout history, favRoute, favBan;
    private RecyclerView rvHistory;
    private static final int REQUEST_MAP_GO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_routes);

        initializeViews();
        setBottomNavigationListener();
        setSwitchClickListener();
        setDateTimePickerListener();
        setOriginDestinationListeners();
        setSearchButtonListener();
        handleIntentExtras(getIntent());

        history.setOnClickListener(v ->{
            rvHistory.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
            favBan.setVisibility(View.INVISIBLE);
            favRoute.setVisibility(View.INVISIBLE);
        });

        btnBack.setOnClickListener(v ->{
            rvHistory.setVisibility(View.INVISIBLE);
            btnBack.setVisibility(View.INVISIBLE);
            favBan.setVisibility(View.VISIBLE);
            favRoute.setVisibility(View.VISIBLE);
        });
    }

    private void initializeViews() {
        txtOrigin2 = findViewById(R.id.txtOrigen2);
        txtDestination = findViewById(R.id.txtDestination);
        txtDateTime = findViewById(R.id.txtDateTime);
        buttonSearch = findViewById(R.id.buttonSearch);
        imageSwitch = findViewById(R.id.imageSwitch);
        editNumPeople = findViewById(R.id.editNumPeople);
        BottomNavigationView bottom = findViewById(R.id.bnNavigation);
        bottom.setSelectedItemId(R.id.navigation_trips);
        history = findViewById(R.id.routeHistory);
        rvHistory = findViewById(R.id.rvHistory);
        btnBack = findViewById(R.id.btnBackMain);
        favBan = findViewById(R.id.layoutFavBan);
        favRoute = findViewById(R.id.layoutFavRout);
        selectedCoordinates= "";
    }

    private void setBottomNavigationListener() {
        BottomNavigationView bottom = findViewById(R.id.bnNavigation);
        bottom.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_profile) {
                navigateToActivity(ProfileActivity.class);
            } else if (id == R.id.navigation_publish_route || id == R.id.navigation_chat) {
                navigateToActivity(AddRouteActivity.class);
            }
            return true;
        });
    }

    private void setSwitchClickListener() {
        imageSwitch.setOnClickListener(v -> toggleSwitch());
    }

    private void toggleSwitch() {
        boolean isVuelta = routeType.equals(getString(R.string.route_type_vuelta));
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
    }

    private void setDateTimePickerListener() {
        txtDateTime.setOnClickListener(v -> showDateTimePickerDialog());
    }

    private void showDateTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    @SuppressLint("DefaultLocale") String formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                    txtDateTime.setText(formattedDate);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    private void setOriginDestinationListeners() {
        txtOrigin2.setOnClickListener(v -> navigateToSearchMapActivity());
        txtDestination.setOnClickListener(v -> navigateToSearchMapActivity());
    }

    private void navigateToSearchMapActivity() {
        Intent intent = new Intent(MainRoutesActivity.this, searchMapActivity.class);
        intent.putExtra("routeType", routeType);
        intent.putExtra("REQUEST_MAP_GO", REQUEST_MAP_GO);
        startActivity(intent);
    }

    private void setSearchButtonListener() {
        buttonSearch.setOnClickListener(v -> navigateToRouteResultsActivity());
    }

    private void navigateToActivity(Class<?> destinationActivity) {
        Intent intent = new Intent(MainRoutesActivity.this, destinationActivity);
        startActivity(intent);
    }

    private void handleIntentExtras(Intent intent) {
        if (intent.hasExtra("streetName") && intent.hasExtra("latitudeStr") && intent.hasExtra("longitudeStr")) {

            String streetName = intent.getStringExtra("streetName");

            Bundle bundle = intent.getExtras();
            assert bundle != null;
            selectedLatitude = bundle.getString("latitudeStr");
            selectedLongitude = bundle.getString("longitudeStr");
            routeType = bundle.getString("routeType");
            selectedCoordinates = selectedLatitude + "," + selectedLongitude;

            if (routeType.equals("Ida")) {
                txtOrigin2.setText(streetName);
                txtDestination.setText(getString(R.string.ci_cuatrovientos));

            } else {
                txtDestination.setText(streetName);
                txtOrigin2.setText(getString(R.string.ci_cuatrovientos));

            }
        }else{
            txtOrigin2.setText("");
            txtDestination.setEnabled(false);
            txtOrigin2.setEnabled(true);
            txtDestination.setText(getString(R.string.ci_cuatrovientos));
            routeType = getString(R.string.route_type_ida);
        }
    }


    private void navigateToRouteResultsActivity() {
        if (validateSearchConditions()) {
            Intent intent = new Intent(MainRoutesActivity.this, JoinRoutesActivity.class);
            intent.putExtra("routeType", routeType);
            intent.putExtra("selectedLatitude", selectedLatitude);
            intent.putExtra("selectedLongitude", selectedLongitude);
            intent.putExtra("selectedDate", txtDateTime.getText().toString());
            intent.putExtra("numPeople", editNumPeople.getText().toString());
            startActivity(intent);
        }

    }

    private boolean validateSearchConditions() {
        if (!isLocationSelected()) {
            return false;
        }

        if (!isDateValid()) {
            return false;
        }

        return isNumberOfPeopleValid();



        // All conditions are met
    }

    private boolean isDateValid() {
        String selectedDateString = txtDateTime.getText().toString().trim();

        Calendar selectedDate = parseDateStringToCalendar(selectedDateString);

        if (selectedDate == null) {
            DialogUtils.showWarningDialog(this, getString(R.string.warning), getString(R.string.error_invalid_date_format));
            return false;
        }

        // Get the current date
        Calendar today = Calendar.getInstance();

        // Compare the selected date with today
        if (selectedDate.before(today)) {
               DialogUtils.showWarningDialog(this, getString(R.string.warning), getString(R.string.error_past_date));
            return false;
        }
        return true;
    }

    private Calendar parseDateStringToCalendar(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    private boolean isNumberOfPeopleValid() {
        String numPeopleStr = editNumPeople.getText().toString().trim();

        if (numPeopleStr.isEmpty()) {
            DialogUtils.showWarningDialog(this, getString(R.string.warning), getString(R.string.error_invalid_number_of_people));
            return false;
        }
        try {
            int numberOfPeople = Integer.parseInt(numPeopleStr);

            if (numberOfPeople >= 1 && numberOfPeople <= 4) {
                return true;
            } else {
                DialogUtils.showWarningDialog(this, getString(R.string.warning), getString(R.string.num_person_error));
                return false;
            }
        } catch (NumberFormatException e) {
            DialogUtils.showWarningDialog(this, getString(R.string.warning), getString(R.string.error_exceeded_max_people));

            return false;
        }
    }


    private boolean isLocationSelected() {
        if ( selectedCoordinates.isEmpty()) {
            DialogUtils.showWarningDialog(this, getString(R.string.error), getString(R.string.error_no_location_selected));
            return false;
        } else {
            return true;
        }
    }



}
