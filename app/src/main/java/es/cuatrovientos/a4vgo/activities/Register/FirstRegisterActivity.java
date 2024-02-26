package es.cuatrovientos.a4vgo.activities.Register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.MainActivity;

public class FirstRegisterActivity extends AppCompatActivity {
    Button register, back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = findViewById(R.id.btnRegister);
        back = findViewById(R.id.btnBack);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(FirstRegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}