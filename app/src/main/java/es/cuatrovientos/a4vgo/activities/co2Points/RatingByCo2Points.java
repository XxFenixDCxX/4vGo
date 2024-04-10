package es.cuatrovientos.a4vgo.activities.co2Points;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.profile.publicProfile.PublicProfilActivity;
import es.cuatrovientos.a4vgo.adapters.RatingCo2PointsAdapter;
import es.cuatrovientos.a4vgo.models.UserProfile;
import es.cuatrovientos.a4vgo.utils.DialogUtils;

public class RatingByCo2Points extends AppCompatActivity {

    private final List<UserProfile> userList = new ArrayList<>();
    private RatingCo2PointsAdapter leaderboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_by_co2_points);

        leaderboardAdapter = new RatingCo2PointsAdapter(userList, (user) -> {
        });
        RecyclerView leaderView = findViewById(R.id.leaderView);
        leaderView.setAdapter(leaderboardAdapter);
        leaderView.setLayoutManager(new LinearLayoutManager(this));

        getTopUsersFromFirebase(10);
    }

    private void getTopUsersFromFirebase(int numberOfUsers) {
        CollectionReference personalDetailsRef = FirebaseFirestore.getInstance().collection("personalDetails");

        personalDetailsRef.orderBy("co2Points", Query.Direction.DESCENDING)
                .limit(numberOfUsers)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> userData = document.getData();

                            if (userData.containsKey("co2Points")) {
                                UserProfile userProfile = UserProfile.fromMap(userData);
                                userList.add(userProfile);
                            }
                        }

                        updateUI(userList);
                    } else {
                        DialogUtils.showErrorDialog(this, getString(R.string.error), getString(R.string.error_clasification));
                    }
                });
    }

    private void updateUI(List<UserProfile> userList) {
        if (userList.size() >= 3) {
            updateTopUserUI(userList.get(0), R.id.pic1, R.id.titleTop1Txt, R.id.scoreTop1Txt);
            updateTopUserUI(userList.get(1), R.id.pic2, R.id.titleTop2Txt, R.id.scoreTop2Txt);
            updateTopUserUI(userList.get(2), R.id.pic3, R.id.titleTop3Txt, R.id.scoreTop3Txt);

            List<UserProfile> remainingUsers = userList.subList(3, userList.size());

            setupRecyclerView(remainingUsers);
        }
    }

    private void updateTopUserUI(UserProfile user, int picId, int titleId, int scoreId) {

        ImageView pic = findViewById(picId);
        TextView title = findViewById(titleId);
        title.setText(user.getName());
        downloadImageAndSetBackground(user.getProfileImage(), pic);
        TextView score = findViewById(scoreId);
        score.setText(String.valueOf(user.getCo2Points()));
        pic.setOnClickListener(v -> {
            Intent intent = new Intent(RatingByCo2Points.this, PublicProfilActivity.class);
            intent.putExtra("dni", user.getDni());
            startActivity(intent);
        });
    }

    private void setupRecyclerView(List<UserProfile> remainingUsers) {
        RecyclerView recyclerView = findViewById(R.id.leaderView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RatingCo2PointsAdapter(remainingUsers, (user) -> {
            Intent intent = new Intent(RatingByCo2Points.this, PublicProfilActivity.class);
            intent.putExtra("dni", user.getDni());
            startActivity(intent);
        }));
    }
    @SuppressLint("StaticFieldLeak")
    private void downloadImageAndSetBackground(String imageUrl, ImageView profile) {
        new AsyncTask<Void, Void, Drawable>() {
            @Override
            protected Drawable doInBackground(Void... voids) {
                try {
                    // Download the image as a Drawable
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
                    // Set the downloaded image as the background
                    profile.setImageDrawable(drawable);
                } else {
                    // Set the default image
                    profile.setImageResource(R.drawable.ic_profile);
                }
            }
        }.execute();
    }
}
