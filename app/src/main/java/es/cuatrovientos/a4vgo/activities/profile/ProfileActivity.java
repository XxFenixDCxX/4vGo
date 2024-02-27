package es.cuatrovientos.a4vgo.activities.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.Utils.DialogUtils;
import es.cuatrovientos.a4vgo.activities.MainActivity;

public class ProfileActivity extends AppCompatActivity {
    ConstraintLayout logout, personalDetails;
    CollectionReference collection;
    FirebaseUser currentUser;
    TextView username, email;
    Query query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logout = findViewById(R.id.logoutAction);
        personalDetails = findViewById(R.id.personalDetailsAction);
        collection = FirebaseFirestore.getInstance().collection("personalDetails");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        username = findViewById(R.id.txtUsername);
        email = findViewById(R.id.txtEmailProfile);
        query = collection.whereEqualTo("email", currentUser.getEmail());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                String usernameString = document.getString("username");
                assert usernameString != null;
                usernameString = usernameString.toUpperCase().charAt(0) + usernameString.substring(1);
                username.setText(usernameString);
                email.setText(document.getString("email"));
            } else {
                DialogUtils.showErrorDialog(this, getString(R.string.errorLogCantLoadDetails), getString(R.string.errorLoginMessage));
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
    }
}