package es.cuatrovientos.a4vgo.activities.profile.publicProfile;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.MainRoutesActivity;
import es.cuatrovientos.a4vgo.activities.createRoute.AddRouteActivity;
import es.cuatrovientos.a4vgo.models.UserProfile;
import es.cuatrovientos.a4vgo.utils.DialogUtils;

public class PublicProfilActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String currentUserDni;
    private ImageView profile;
    BottomNavigationView bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profil);
        db = FirebaseFirestore.getInstance();
         getUserDni();
         currentUserDni = "12345678Z";
        profile = findViewById(R.id.imgProfileP);

        LinearLayout favoriteButton = findViewById(R.id.AddToFavorite);
        LinearLayout bannedButton = findViewById(R.id.BanUser);
        bottom = findViewById(R.id.bnNavigation);
        bottom.setSelectedItemId(R.id.navigation_profile);
        favoriteButton.setOnClickListener(view -> addToFavorites(currentUserDni));
        bannedButton.setOnClickListener(view -> banUser(currentUserDni));
        bottom.setOnItemSelectedListener(item -> {
                    int id = item.getItemId();
                    if (id == R.id.navigation_trips) {
                        overridePendingTransition(0, 0);
                        Intent intent = new Intent(PublicProfilActivity.this , MainRoutesActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    } else if (id == R.id.navigation_publish_route) {
                        overridePendingTransition(0, 0);
                        Intent intent = new Intent(PublicProfilActivity.this , AddRouteActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    } else if (id == R.id.navigation_ranking){
                        overridePendingTransition(0, 0);
                        Intent intent = new Intent(PublicProfilActivity.this , PublicProfilActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                    return true;
                }
        );
        retrieveUserInfo(currentUserDni);

    }
    private void addToFavorites(String userDni) {
        ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.adding_to_favorites_progress), true);

        String documentId = userDni + "_" + "57945210E";
        Map<String, Object> userData = new HashMap<>();
        userData.put("userDni", userDni);
        userData.put("favoriteUser", "57945210E");
        userData.put("timestamp", FieldValue.serverTimestamp());

        db.collection(getString(R.string.collection_favoritos))
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            DialogUtils.showWarningDialog(this, getString(R.string.warning), getString(R.string.dialog_message_user_already_favorite));
                        } else {
                            db.collection(getString(R.string.collection_favoritos))
                                    .document(documentId)
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> DialogUtils.showSuccessDialog(this, getString(R.string.success), getString(R.string.log_message_user_added_to_favorites)))
                                    .addOnFailureListener(e -> DialogUtils.showWarningDialog(this, getString(R.string.warning), getString(R.string.error_message_failed_to_add_user_to_favorites)));
                        }
                    } else {
                        DialogUtils.showErrorDialog(this, getString(R.string.error), getString(R.string.error_message_failed_to_check_user_in_favorites));
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    DialogUtils.showErrorDialog(this, getString(R.string.error), getString(R.string.error_message_failed_to_check_user_in_favorites));
                });
    }



    private void banUser(String userDni) {
        ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.banning_user_progress), true);

        String documentId = userDni + "_" + "57945210E";

        Map<String, Object> userData = new HashMap<>();
        userData.put("userDni", userDni);
        userData.put("bannedUser", "57945210E");
        userData.put("timestamp", FieldValue.serverTimestamp());

        db.collection(getString(R.string.collection_baneados))
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            DialogUtils.showWarningDialog(this, getString(R.string.warning), getString(R.string.dialog_message_user_already_banned));
                        } else {
                            db.collection(getString(R.string.collection_baneados))
                                    .document(documentId)
                                    .set(userData)
                                    .addOnSuccessListener(documentReference -> DialogUtils.showSuccessDialog(this, getString(R.string.success), getString(R.string.warning_message_user_banned)))
                                    .addOnFailureListener(e -> DialogUtils.showErrorDialog(this, getString(R.string.error), getString(R.string.error_message_failed_to_ban_user)));
                        }
                    } else {
                        DialogUtils.showErrorDialog(this, getString(R.string.error), getString(R.string.error_message_failed_to_check_user_banned));
                    }
                });
    }





    private void getUserDni() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Query query = db.collection("personalDetails").whereEqualTo("email", currentUser.getEmail());
            try {
                Task<QuerySnapshot> querySnapshotTask = query.get();
                QuerySnapshot querySnapshot = Tasks.await(querySnapshotTask);
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    String dni = document.getString("dni");
                } else {
                    Log.e("getUserDni", "No documents found for the user");
                }
            } catch (Exception e) {
                Log.e("getUserDni", "Error getting user document: " + e.getMessage());
            }
        } else {
            Log.e("getUserDni", "Current user is null");
        }
    }


    private void retrieveUserInfo(String userDni) {
        db.collection("personalDetails")
                .document(userDni)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int co2Points = documentSnapshot.getLong("co2Points").intValue();
                        String email = documentSnapshot.getString("email");
                        String phoneNumber = documentSnapshot.getString("phoneNumber");
                        String profileImage = documentSnapshot.getString("profileImage");
                        String username = documentSnapshot.getString("username");
                        updateUI(username, email , co2Points, profileImage);
                        getUserRanking(userDni);
                        loadProfileImage(userDni);

                    } else {
                        Log.e("retrieveUserInfo", "No existe el documento para el usuario con DNI: " + userDni);
                    }
                })
                .addOnFailureListener(e -> Log.e("retrieveUserInfo", "Error al recuperar la información del usuario", e));
    }

    private void updateUI(String username, String email, int co2Points, String profileImage) {
        // Accede a los elementos del layout
        TextView usernameTextView = findViewById(R.id.txtUsername);
        TextView emailTextView = findViewById(R.id.txtEmailProfile);
        TextView co2PointsTextView = findViewById(R.id.co2PointsTextView);
        ImageView profileImageView = findViewById(R.id.imgProfileP);


        usernameTextView.setText(username);
        emailTextView.setText(email);
        co2PointsTextView.setText(String.format(getString(R.string.co2PointsProfile), co2Points));

    }
    private void getUserRanking(String dni) {
        CollectionReference personalDetailsRef = FirebaseFirestore.getInstance().collection("personalDetails");

        personalDetailsRef.orderBy("co2Points", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean userFound = false;
                        int rank = 1;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> userData = document.getData();

                            if (userData.containsKey("dni") && userData.get("dni").equals(dni)) {
                                userFound = true;
                                UserProfile userProfile = UserProfile.fromMap(userData);
                                updateUIWithRank(userProfile, rank);
                                break;
                            }

                            rank++;
                        }

                        if (!userFound) {
                            DialogUtils.showErrorDialog(this, getString(R.string.error), getString(R.string.user_not_found));
                        }
                    } else {
                        DialogUtils.showErrorDialog(this, getString(R.string.error), getString(R.string.error_clasification));
                    }
                });
    }

    private void updateUIWithRank(UserProfile user, int rank) {
        TextView rankTextView = findViewById(R.id.rankTextView);
        rankTextView.setText(String.valueOf(rank));
    }

    private void loadProfileImage(String currenUserDNI) {
        CollectionReference collection = FirebaseFirestore.getInstance().collection("personalDetails");

        collection.document(currenUserDNI).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageUrl = documentSnapshot.getString("profileImage");

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            downloadImageAndSetBackground(imageUrl);
                        } else {
                            profile.setBackgroundResource(R.drawable.ic_profile);
                        }
                    }
                })
                .addOnFailureListener(e -> DialogUtils.showErrorDialog(this, getString(R.string.errorLoginTitle), getString(R.string.errorLogImage)));
    }

    @SuppressLint("StaticFieldLeak")
    private void downloadImageAndSetBackground(String imageUrl) {
        new AsyncTask<Void, Void, Drawable>() {
            @Override
            protected Drawable doInBackground(Void... voids) {
                try {
                    InputStream inputStream = new URL(imageUrl).openStream();
                    return Drawable.createFromStream(inputStream, null);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                if (drawable != null) {
                    profile.setBackground(drawable);
                } else {
                    profile.setBackgroundResource(R.drawable.ic_profile);
                }
            }
        }.execute();
    }


}
