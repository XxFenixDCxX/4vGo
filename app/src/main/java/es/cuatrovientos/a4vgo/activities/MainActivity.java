package es.cuatrovientos.a4vgo.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

import es.cuatrovientos.a4vgo.R;

import es.cuatrovientos.a4vgo.activities.register.FirstRegisterActivity;

public class MainActivity extends AppCompatActivity {
    FirebaseUser user;
    String languagueCode;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isNetworkAvailable()) {
            showNoInternetDialog();
            return;
        }
        setDefaultLanguage();

        db = FirebaseFirestore.getInstance();
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout2);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){

            Query query = db.collection("personalDetails").whereEqualTo("email", user.getEmail());
            query.get().addOnCompleteListener(task -> {
                QuerySnapshot querySnapshot = task.getResult();
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                languagueCode = document.getString("languague");
                setDefaultLanguage();
            });
        }
        if (user != null){
            Intent intent = new Intent(MainActivity.this, MainRoutesActivity.class);
            startActivity(intent);
        }
        constraintLayout.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        ConstraintLayout constraintLayout1 = findViewById(R.id.constraintLayout3);
        constraintLayout1.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FirstRegisterActivity.class);
            startActivity(intent);
        });
    }
    private void setDefaultLanguage() {
        if(languagueCode == null || languagueCode.isEmpty()){
            languagueCode = "es";
        }
        Locale locale = new Locale(languagueCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        Resources resources = getResources();
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    // Método para mostrar un diálogo si no hay conexión a Internet
    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No hay conexión a Internet");
        builder.setMessage("Por favor, verifica tu conexión a Internet y vuelve a intentarlo.");
        builder.setPositiveButton("Cerrar aplicación", (dialog, which) -> finish());
        builder.setCancelable(false); // Para evitar que el usuario cierre el diálogo pulsando fuera de él
        builder.show();
    }
}