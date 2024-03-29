package es.cuatrovientos.a4vgo.activities.profile.privateProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.adapters.RecyclerBannedUsersAdapter;
import es.cuatrovientos.a4vgo.models.UserProfile;

public class BannedUsersActivity extends AppCompatActivity {
    private RecyclerView rvBannedUsers;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<UserProfile> bannedUsersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banned_users);

        rvBannedUsers = findViewById(R.id.rvBannedUsers);
        Button back = findViewById(R.id.btnBack);

        setupRecyclerView();
        fetchCurrentUserDNI();

        back.setOnClickListener(v -> {
            Intent intent = new Intent(BannedUsersActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        rvBannedUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
    }

    private void fetchCurrentUserDNI() {
        FirebaseAuth.getInstance().getCurrentUser().getEmail(); // Supongamos que es email por ahora
        db.collection("personalDetails").whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String currentUserDNI = "";
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        currentUserDNI = documentSnapshot.getString("dni");
                        break;
                    }
                    fetchBannedUsers(currentUserDNI);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching user DNI", e));
    }

    private void fetchBannedUsers(String currentUserDNI) {
        db.collection("bannedUsers").whereEqualTo("userDni", currentUserDNI)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> bannedUserDNIs = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        bannedUserDNIs.add(documentSnapshot.getString("bannedUser"));
                    }
                    fetchUserDetails(bannedUserDNIs);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching banned users", e));
    }

    private void fetchUserDetails(@NonNull List<String> userDNIs) {
        for (String userDNI : userDNIs) {
            db.collection("personalDetails").whereEqualTo("dni", userDNI).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            UserProfile user = documentSnapshot.toObject(UserProfile.class);
                            bannedUsersList.add(user);
                        }
                        updateRecyclerView();
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error fetching user details", e));
        }
    }

    private void updateRecyclerView() {
        if(bannedUsersList.size() > 0) {
            RecyclerBannedUsersAdapter adapter = new RecyclerBannedUsersAdapter(bannedUsersList, db);
            rvBannedUsers.setAdapter(adapter);
        }
    }
}