package es.cuatrovientos.a4vgo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.Register.FirstRegisterActivity;

public class loginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btn = findViewById(R.id.buttonLogin);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, MainRoutesActivity.class);
                startActivity(intent);
            }
        });

    }
}