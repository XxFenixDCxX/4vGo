package es.cuatrovientos.a4vgo.activities.createRoute;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.MainRoutesActivity;
import es.cuatrovientos.a4vgo.models.Route;
import es.cuatrovientos.a4vgo.models.Vehicle;
import es.cuatrovientos.a4vgo.utils.DialogUtils;

public class AddRouteSecondActivity extends AppCompatActivity {

    private ImageButton btnShowVehicleList;
    private FirebaseFirestore db;
    private List<Vehicle> vehiclesList;
    private TextView txtVehicle;
    private Route route;
    private String dni;
    private Button btnNext;
    private Button btnBack;
    private final Activity context = this;
    private CheckBox checkBoxIntermediateStops;
    private CheckBox checkBoxRouteFrequency;
    private EditText editTextAdditionalComments;
    private String routeType;
    private String selectedCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route_second);

        initializeViews();
        initializeRouteData();
        setupClickListeners();
    }

    private void initializeViews() {
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        checkBoxIntermediateStops = findViewById(R.id.checkBoxIntermediateStops);
        checkBoxRouteFrequency = findViewById(R.id.checkBoxRouteFrequency);
        editTextAdditionalComments = findViewById(R.id.editTextAdditionalComments);
        btnShowVehicleList = findViewById(R.id.btnShowVehicleList);
        txtVehicle = findViewById(R.id.txtVehicle);
    }

    private void initializeRouteData() {
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            routeType = intent.getStringExtra("routeType");
            selectedCoordinates = intent.getStringExtra("selectedCoordinates");
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");

            int availableSeats = intent.getIntExtra("availableSeats", 0);
            route = new Route();
            route.setRouteType(routeType);
            setRouteOriginDestination();
            route.setSelectedDate(date);
            route.setSelectedTime(time);
            route.setMaxSeats(availableSeats);
        }
    }

    private void setRouteOriginDestination() {
        route.setUserId(getUserDni());
        if ("Ida".equals(routeType)) {
            route.setOrigin(selectedCoordinates);
            route.setDestination(getString(R.string.coordinates_ci_cuatrovientos));
        } else if ("Vuelta".equals(routeType)) {
            route.setOrigin(getString(R.string.coordinates_ci_cuatrovientos));
            route.setDestination(selectedCoordinates);
        }
    }

    private String getUserDni() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Query query = db.collection("personalDetails").whereEqualTo("email", currentUser.getEmail());
            try {
                QuerySnapshot querySnapshot = query.get().getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    return document.getString("dni");
                }
            } catch (Exception e) {
                Log.e("getUserDni", e.toString());
            }
        }
        return null;
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnShowVehicleList.setOnClickListener(this::fetchVehicleDataAndShowDialog);
        btnNext.setOnClickListener(v -> {
            validateAndProceed();
            addRouteToDB();
        });
    }

    private void fetchVehicleDataAndShowDialog(View v) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Query query = FirebaseFirestore.getInstance().collection("personalDetails").whereEqualTo("email", currentUser.getEmail());
            query.get().addOnCompleteListener(this::handlePersonalDetailsQuery);
        }
    }

    private void handlePersonalDetailsQuery(Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            QuerySnapshot querySnapshot = task.getResult();
            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                dni = document.getString("dni");
                route.setUserId(dni);

                if (dni != null) {
                    fetchVehiclesAndShowDialog();
                }
            }
        } else {
            Exception exception = task.getException();
            if (exception != null) {
                DialogUtils.showErrorDialog(this, getString(R.string.unkown_error), exception.toString());
            }
        }
    }

    private void fetchVehiclesAndShowDialog() {
        db.collection("cars")
                .whereEqualTo("userDNI", dni)
                .get()
                .addOnCompleteListener(this::handleVehicleQuery);
    }

    private void handleVehicleQuery(Task<QuerySnapshot> vehicleTask) {
        if (vehicleTask.isSuccessful()) {
            vehiclesList = new ArrayList<>();
            for (DocumentSnapshot vehicleDocument : vehicleTask.getResult()) {
                String plate = vehicleDocument.getString("plate");
                String color = vehicleDocument.getString("color");
                String siteNumber = vehicleDocument.getString("siteNumber");
                vehiclesList.add(new Vehicle(plate, color, siteNumber));
            }
            showVehicleListDialog();
        } else {
            Exception exception = vehicleTask.getException();
            if (exception != null) {
                DialogUtils.showErrorDialog(this, getString(R.string.unkown_error), exception.toString());
            }
        }
    }

    private void showVehicleListDialog() {
        if (vehiclesList != null && !vehiclesList.isEmpty()) {
            String[] vehiclesArray = vehiclesList.stream()
                    .map(vehicle -> vehicle.getPlate() + " - " + vehicle.getColor())
                    .toArray(String[]::new);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.vehicule_selection))
                    .setItems(vehiclesArray, (dialog, which) -> {
                        Vehicle selectedVehicle = vehiclesList.get(which);
                        String plate = selectedVehicle.getPlate();
                        String color = selectedVehicle.getColor();
                        route.setSelectedVehicle(plate);
                        txtVehicle.setText(plate + " , " + color);
                        dialog.dismiss();
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void validateAndProceed() {
        if (txtVehicle.getText().toString().isEmpty() || route.getSelectedVehicle().isEmpty() || route.getSelectedVehicle() == null) {
            DialogUtils.showWarningDialog(this, getString(R.string.errorLoginTitle), getString(R.string.warning_vehicle_selection));
            return;
        }
        boolean hasIntermediateStops = checkBoxIntermediateStops.isChecked();
        boolean isFrequentRoute = checkBoxRouteFrequency.isChecked();
        String additionalComments = editTextAdditionalComments.getText().toString();

        route.setHasIntermediateStops(hasIntermediateStops);
        route.setFrequent(isFrequentRoute);
        route.setComments(additionalComments);
    }

    private void addRouteToDB() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.creating_route));
        progressDialog.setCancelable(false);
        progressDialog.show();

        db.collection("routes").document(generateDocRouteId())
                .set(route)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();

                    showSuccessDialog();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();  // Cerrar el ProgressDialog en caso de error
                    DialogUtils.showErrorDialog(context, getString(R.string.unkown_error), getString(R.string.route_addition_failed));
                });
    }



    private String generateDocRouteId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH", Locale.getDefault());
        String currentDateAndTime = dateFormat.format(new Date());

        return removeCommaFromCoordinates(selectedCoordinates) + "_" + currentDateAndTime + route.getUserId() + route.getRouteType() + route.getSelectedVehicle();
    }

    private String removeCommaFromCoordinates(String selectedCoordinates) {
        return selectedCoordinates.replace(",", "");
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.success_operation));
        builder.setMessage(getString(R.string.success_creation_route));
        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            Intent intent = new Intent(AddRouteSecondActivity.this, MainRoutesActivity.class);
            startActivity(intent);
        });

        AlertDialog successDialog = builder.create();
        successDialog.show();
    }
}
