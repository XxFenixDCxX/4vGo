package es.cuatrovientos.a4vgo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.register.FirstRegisterActivity;

public class MainActivity extends AppCompatActivity {
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout2);
        user = FirebaseAuth.getInstance().getCurrentUser();

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
}