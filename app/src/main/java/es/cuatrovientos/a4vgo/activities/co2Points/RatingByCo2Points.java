package es.cuatrovientos.a4vgo.activities.co2Points;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.adapters.RatingCo2PointsAdapter;
import es.cuatrovientos.a4vgo.models.User;

public class RatingByCo2Points extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_by_co2_points);

        List<User> userList = createFakeUserList();

        RatingCo2PointsAdapter leaderboardAdapter = new RatingCo2PointsAdapter(userList, new RatingCo2PointsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user, int position) {
                // Manejar el evento de clic si es necesario
            }
        });

        RecyclerView leaderView = findViewById(R.id.leaderView);
        leaderView.setAdapter(leaderboardAdapter);
        leaderView.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<User> createFakeUserList() {
        List<User> userList = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            User user = new User(
                    "FirstName" + i,
                    "LastName" + i,
                    "PasswordHash" + i,
                    "PhoneNumber" + i,
                    "12/04/2002",
                    "12345678Z",
                    "prueba" + i + "@gmail.com",
                    "es",
                    "Name" + i,
                    "https://firebasestorage.googleapis.com/v0/b/v-go-6ce57.appspot.com/o/profile_images%2F12345678Z.jpg?alt=media&token=724d9483-5626-4e8f-8b9b-dc4bbe1d95b6",
                    false,
                    "Bensbaho" + i,
                    "xxstux" + i
            );

            userList.add(user);
        }

        return userList;
    }
}
