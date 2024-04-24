package es.cuatrovientos.a4vgo.activities.profile.privateProfile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.adapters.RecyclerFavoriteUsersAdapter;
import es.cuatrovientos.a4vgo.models.UserProfile;

public class FavoriteUsersActivity extends AppCompatActivity {

    private RecyclerView rvFavoriteUsers;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<UserProfile> favoriteUsersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_users);

        rvFavoriteUsers = findViewById(R.id.rvFavoriteUsers);
        Button back = findViewById(R.id.btnBack);

        setupRecyclerView();
        fetchCurrentUserDNI();

        back.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        rvFavoriteUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
    }

    private void fetchCurrentUserDNI() {
        db.collection("personalDetails").whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String currentUserDNI = "";
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        currentUserDNI = documentSnapshot.getString("dni");
                        break;
                    }
                    fetchFavoriteUsers(currentUserDNI);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching user DNI", e));
    }

    private void fetchFavoriteUsers(String currentUserDNI) {
        db.collection("favorits").whereEqualTo("userDni", currentUserDNI)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> favoriteUserDNIs = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        favoriteUserDNIs.add(documentSnapshot.getString("favoriteUser"));
                    }
                    fetchUserDetails(favoriteUserDNIs);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching favorite users", e));
    }

    private void fetchUserDetails(@NonNull List<String> userDNIs) {
        for (String userDNI : userDNIs) {
            db.collection("personalDetails").whereEqualTo("dni", userDNI).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            UserProfile user;
                            user = documentSnapshot.toObject(UserProfile.class);
                            favoriteUsersList.add(user);
                        }
                        updateRecyclerView();
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error fetching user details", e));
        }
    }

    private void updateRecyclerView() {
        if(favoriteUsersList.size() > 0) {
            RecyclerFavoriteUsersAdapter adapter = new RecyclerFavoriteUsersAdapter(favoriteUsersList);
            rvFavoriteUsers.setAdapter(adapter);
        }
    }

}
