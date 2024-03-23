package es.cuatrovientos.a4vgo.activities.profile.publicProfile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import es.cuatrovientos.a4vgo.R;

public class PublicProfilActivity extends AppCompatActivity {
    private LinearLayout favoriteButton;
    private LinearLayout bannedButton;
    private FirebaseFirestore db;
    private String currentUserDni;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profil);
        db = FirebaseFirestore.getInstance();
        currentUserDni = getUserDni();

        favoriteButton = findViewById(R.id.AddToFavorite);
        bannedButton = findViewById(R.id.BanUser);

        // Configurar clics en los botones
        favoriteButton.setOnClickListener(view -> addToFavorites(currentUserDni));
        bannedButton.setOnClickListener(view -> banUser(currentUserDni, "DNI del usuario a banear"));

        retrieveUserInfo(currentUserDni);

    }
    private void addToFavorites(String userDni) {

        db.collection("favoritos")
                .document(userDni)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // El usuario ya está en la lista de favoritos
                            Log.d("addToFavorites", "El usuario ya está en la lista de favoritos");
                        } else {
                            // Agregar al usuario a la lista de favoritos
                            db.collection("favoritos")
                                    .document(userDni)
                                    .set(new HashMap<>()) // Puedes agregar más datos si es necesario
                                    .addOnSuccessListener(aVoid -> Log.d("addToFavorites", "Usuario agregado a favoritos"))
                                    .addOnFailureListener(e -> Log.e("addToFavorites", "Error al agregar usuario a favoritos", e));
                        }
                    } else {
                        Log.e("addToFavorites", "Error al verificar si el usuario está en la lista de favoritos", task.getException());
                    }
                });
    }


    private void banUser(String userDni, String banningUserId) {
        String documentId = userDni + "_" + banningUserId;

        Map<String, Object> userData = new HashMap<>();
        userData.put("userDni", userDni);
        userData.put("bannedUser", banningUserId);
        userData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("baneados")
                .document(documentId) // Utilizar el ID personalizado
                .set(userData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("banUser", "Usuario baneado correctamente");
                })
                .addOnFailureListener(e -> Log.e("banUser", "Error al banear usuario", e));


    }



    private String getUserDni() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Query query = db.collection("personalDetails").whereEqualTo("email", currentUser.getEmail());
            try {
                Task<QuerySnapshot> querySnapshotTask = query.get();
                querySnapshotTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                String dni = document.getString("dni");
                                // Utiliza el DNI obtenido aquí
                                currentUserDni = dni;
                            }
                        } else {
                            Log.e("getUserDni", "Error getting documents: ", task.getException());
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("getUserDni", e.toString());
            }
        }
        return null;
    }

    private void retrieveUserInfo(String userDni) {
        db.collection("usuarios")
                .document(userDni)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int co2Points = documentSnapshot.getLong("co2Points").intValue();
                        String email = documentSnapshot.getString("email");
                        String phoneNumber = documentSnapshot.getString("phoneNumber");
                        String profileImage = documentSnapshot.getString("profileImage");
                        String username = documentSnapshot.getString("username");


                    } else {
                        Log.e("retrieveUserInfo", "No existe el documento para el usuario con DNI: " + userDni);
                    }
                })
                .addOnFailureListener(e -> Log.e("retrieveUserInfo", "Error al recuperar la información del usuario", e));
    }
}