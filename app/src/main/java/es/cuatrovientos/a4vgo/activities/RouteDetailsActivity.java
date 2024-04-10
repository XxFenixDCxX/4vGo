package es.cuatrovientos.a4vgo.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.utils.DialogUtils;

public class RouteDetailsActivity extends AppCompatActivity {
    Bundle bundle;
    String routeId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);
        bundle = getIntent().getExtras();
        routeId = bundle.getString("routeID");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("routes").document(routeId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    //toDo asignar valores a los campos
                } else {
                    DialogUtils.showErrorDialog(RouteDetailsActivity.this, getString(R.string.errorLogCantLoadDetails), getString(R.string.errorLoginMessage));
                }
            } else {
                DialogUtils.showErrorDialog(RouteDetailsActivity.this, getString(R.string.errorLogCantLoadDetails), getString(R.string.errorLoginMessage));
            }
        });
    }
}