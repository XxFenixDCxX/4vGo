package es.cuatrovientos.a4vgo.activities.createRoute;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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
import es.cuatrovientos.a4vgo.activities.MainActivity;
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
    private Button btnNext ;
    private Button btnBack ;

    private Activity context = this;
    private CheckBox checkBoxIntermediateStops ;
    private CheckBox checkBoxRouteFrequency ;
    private EditText editTextAdditionalComments;

    private String routeType;
    private String selectedCoordinates;
    private String date;
    private String time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route_second);
        Intent intent = getIntent();
        if (intent != null) {
            routeType = intent.getStringExtra("routeType");
            selectedCoordinates = intent.getStringExtra("selectedCoordinates");
            date = intent.getStringExtra("date");
            time = intent.getStringExtra("time");

            int availableSeats = intent.getIntExtra("availableSeats", 0);
            route = new Route ();
            route.setRouteType(routeType);
            if ("Ida".equals(routeType)) {
                route.setOrigin(selectedCoordinates);
                route.setDestination(getString(R.string.coordinates_ci_cuatrovientos));

            } else if ("Vuelta".equals(routeType)) {
                route.setOrigin(getString(R.string.coordinates_ci_cuatrovientos));
                route.setDestination(selectedCoordinates);
            }

            route.setSelectedDate(date);
            route.setSelectedTime(time);
            route.setMaxSeats(availableSeats);


        }

        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        checkBoxIntermediateStops = findViewById(R.id.checkBoxIntermediateStops);
        checkBoxRouteFrequency = findViewById(R.id.checkBoxRouteFrequency);
        editTextAdditionalComments = findViewById(R.id.editTextAdditionalComments);

        db = FirebaseFirestore.getInstance();
        btnShowVehicleList = findViewById(R.id.btnShowVehicleList);
        txtVehicle = findViewById(R.id.txtVehicle);
        btnShowVehicleList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchVehicleDataAndShowDialog();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndProceed();
                addRouteToDB();
            }
        });
    }

    private void fetchVehicleDataAndShowDialog() {
        // Obtener el ID del usuario actual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            assert currentUser != null;
            Query query = FirebaseFirestore.getInstance().collection("personalDetails").whereEqualTo("email", currentUser.getEmail());
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Obtener el DNI si existen detalles personales
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        dni = document.getString("dni");
                        route.setUserId(dni);


                        if (dni != null) {
                            db.collection("cars")
                                    .whereEqualTo("userDNI", dni)
                                    .get()
                                    .addOnCompleteListener(vehicleTask -> {
                                        if (vehicleTask.isSuccessful()) {
                                            Log.i("moha", "OK");
                                            vehiclesList = new ArrayList<>();

                                            for (DocumentSnapshot vehicleDocument : vehicleTask.getResult()) {
                                                String plate = vehicleDocument.getString("plate");
                                                String color = vehicleDocument.getString("color");
                                                String siteNumber = vehicleDocument.getString("siteNumber");
                                                vehiclesList.add(new Vehicle(plate, color, siteNumber));
                                            }

                                            showVehicleListDialog();

                                        } else {
                                            Log.i("moha", "KO");

                                            Exception exception = vehicleTask.getException();
                                            if (exception != null) {
                                                DialogUtils.showErrorDialog(this, getString(R.string.unkown_error), exception.toString());
                                            }
                                        }
                                    });
                        }
                    }
                } else {
                    // Manejar errores en la consulta de detalles personales
                    Exception exception = task.getException();
                    if (exception != null) {
                        DialogUtils.showErrorDialog(this, getString(R.string.unkown_error), exception.toString());
                    }
                }
            });


        }
    }

    private void showVehicleListDialog() {
        if (vehiclesList != null && !vehiclesList.isEmpty()) {
            String[] vehiclesArray = new String[vehiclesList.size()];

            for (int i = 0; i < vehiclesList.size(); i++) {
                Vehicle vehicle = vehiclesList.get(i);
                String vehicleInfo = vehicle.getPlate() + " - " + vehicle.getColor();
                vehiclesArray[i] = vehicleInfo;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.vehicule_selection))
                    .setItems(vehiclesArray, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Vehicle selectedVehicle = vehiclesList.get(which);
                            String plate = selectedVehicle.getPlate();
                            String color = selectedVehicle.getColor();
                            route.setSelectedVehicle(plate);

                            txtVehicle.setText(plate + " , "+color);

                            dialog.dismiss();
                        }
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
        db.collection("routes").document(generateDocRouteId())
                .set(route)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DialogUtils.showSuccessDialog(context, getString(R.string.success), getString(R.string.success_creation_route));
                        try {
                            Thread.sleep(9000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Intent intent = new Intent(AddRouteSecondActivity.this, MainRoutesActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        DialogUtils.showErrorDialog(context, getString(R.string.unkown_error), getString(R.string.route_addition_failed));
                    }
                });
    }


    private String generateDocRouteId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH" +
                "", Locale.getDefault());
        String currentDateAndTime = dateFormat.format(new Date());



        String routeId = removeCommaFromCoordinates(selectedCoordinates)+ "_" + currentDateAndTime + route.getUserId();

        return routeId;
    }
    private String removeCommaFromCoordinates(String selectedCoordinates) {
        return selectedCoordinates.replace(",", "");
    }


}
