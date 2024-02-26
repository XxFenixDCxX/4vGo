package es.cuatrovientos.a4vgo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import es.cuatrovientos.a4vgo.R;

public class RegisterActivity extends AppCompatActivity {
    Button register;
    EditText name, email, password, rePassword;
    TextView error;

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = findViewById(R.id.btnRegister);
        name = findViewById(R.id.txtName);
        email = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPassword);
        rePassword = findViewById(R.id.txtRePassword);
        error = findViewById(R.id.txtErrorLog);
        db = FirebaseFirestore.getInstance();

        register.setOnClickListener(view -> {
            String nameText, emailText, passwordText, rePasswordText;
            nameText = name.getText().toString();
            emailText = email.getText().toString();
            passwordText = password.getText().toString();
            rePasswordText = rePassword.getText().toString();

            if(nameText.length() < 2 || nameText.length() > 30){

            }
        });
    }
}