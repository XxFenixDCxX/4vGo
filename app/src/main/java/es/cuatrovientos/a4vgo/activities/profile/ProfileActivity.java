    package es.cuatrovientos.a4vgo.activities.profile;

    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.constraintlayout.widget.ConstraintLayout;

    import android.annotation.SuppressLint;
    import android.content.Intent;
    import android.content.res.Configuration;
    import android.content.res.Resources;
    import android.graphics.drawable.Drawable;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.google.android.material.bottomnavigation.BottomNavigationView;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.CollectionReference;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.firestore.Query;
    import com.google.firebase.firestore.QuerySnapshot;

    import java.io.IOException;
    import java.io.InputStream;
    import java.net.URL;
    import java.util.Locale;

    import es.cuatrovientos.a4vgo.R;
    import es.cuatrovientos.a4vgo.activities.JoinRoutesActivity;
    import es.cuatrovientos.a4vgo.activities.MainRoutesActivity;
    import es.cuatrovientos.a4vgo.activities.co2Points.RatingByCo2Points;
    import es.cuatrovientos.a4vgo.activities.createRoute.AddRouteActivity;
    import es.cuatrovientos.a4vgo.utils.DialogUtils;
    import es.cuatrovientos.a4vgo.activities.MainActivity;

    public class ProfileActivity extends AppCompatActivity {
        ConstraintLayout logout, personalDetails, languague, vehicle,co2PointsAction;
        CollectionReference collection;
        FirebaseUser currentUser;
        TextView username, email;
        Query query;
        ImageView profile;
        String currenUserDNI;
        BottomNavigationView bottom;
        String[] languagues;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);

            logout = findViewById(R.id.logoutAction);
            languague = findViewById(R.id.languageAction);
            personalDetails = findViewById(R.id.personalDetailsAction);
            collection = FirebaseFirestore.getInstance().collection("personalDetails");
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            username = findViewById(R.id.txtUsername);
            email = findViewById(R.id.txtEmailProfile);
            profile = findViewById(R.id.imgProfileP);
            query = collection.whereEqualTo("email", currentUser.getEmail());
            vehicle = findViewById(R.id.vehicleAction);
            bottom = findViewById(R.id.bnNavigation);
            bottom.setSelectedItemId(R.id.navigation_profile);
            co2PointsAction = findViewById(R.id.co2PointsAction);
            languagues = new String[]{
                    getString(R.string.spanish_languague),
                    getString(R.string.english_languague),
                    getString(R.string.basque_languauge)
            };


            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    String usernameString = document.getString("username");
                    currenUserDNI = document.getString("dni");
                    assert usernameString != null;
                    usernameString = usernameString.toUpperCase().charAt(0) + usernameString.substring(1);
                    username.setText(usernameString);
                    email.setText(document.getString("email"));
                    loadProfileImage();
                } else {
                    DialogUtils.showErrorDialog(this, getString(R.string.errorLoginTitle), getString(R.string.errorLogCantLoadDetails));
                }
            });

            logout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            });

            personalDetails.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, PersonalDetailsActivity.class);
                startActivity(intent);
            });

            languague.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle(getString(R.string.selectLanguage))
                        .setItems(languagues, (dialog, which) -> {
                            String selectedLanguague = languagues[which];
                            Toast.makeText(ProfileActivity.this, getString(R.string.selectedLanguague) + selectedLanguague, Toast.LENGTH_SHORT).show();
                            String languagueCode;
                            String spanish = getString(R.string.spanish_languague);
                            String english = getString(R.string.english_languague);
                            String basque = getString(R.string.basque_languauge);
                            if(selectedLanguague.equals(spanish)){
                                languagueCode = "es";
                                collection.document(currenUserDNI).update("languague", languagueCode);
                                changeLanguague(languagueCode);
                            } else if(selectedLanguague.equals(english)){
                                languagueCode = "en";
                                collection.document(currenUserDNI).update("languague", languagueCode);
                                changeLanguague(languagueCode);
                            }else if (selectedLanguague.equals(basque)){
                                languagueCode = "eu";
                                collection.document(currenUserDNI).update("languague", languagueCode);
                                changeLanguague(languagueCode);
                            }
                        });
                builder.create().show();
            });

            vehicle.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, VehicleActivity.class);
                startActivity(intent);
            });
            co2PointsAction.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, RatingByCo2Points.class);
                startActivity(intent);
            });
            bottom.setOnItemSelectedListener(item -> {
                        int id = item.getItemId();
                        if (id == R.id.navigation_trips) {
                            overridePendingTransition(0, 0);
                            Intent intent = new Intent(ProfileActivity.this , MainRoutesActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        } else if (id == R.id.navigation_publish_route) {
                            overridePendingTransition(0, 0);
                            Intent intent = new Intent(ProfileActivity.this , AddRouteActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        } else if (id == R.id.navigation_chat){
                            overridePendingTransition(0, 0);
                            Intent intent = new Intent(ProfileActivity.this , JoinRoutesActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                        return true;
                    }
            );
        }
        private void loadProfileImage() {
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
        private void changeLanguague(String languagueCode){
            Locale locale = new Locale(languagueCode);
            Locale.setDefault(locale);

            Configuration config = new Configuration();
            config.locale = locale;

            Resources resources = this.getResources();
            resources.updateConfiguration(config, resources.getDisplayMetrics());

            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }