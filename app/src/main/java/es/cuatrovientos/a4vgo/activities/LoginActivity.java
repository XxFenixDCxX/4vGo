package es.cuatrovientos.a4vgo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.utils.DialogUtils;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText editTextEmail;
    private EditText editTextPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicialization of the Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Inicialization of the views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        //Autentication call
        login();
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainRoutesActivity.class);
        startActivity(intent);
    }

    private void handleLoginError() {
        editTextEmail.setText(R.string.whiteString);
        editTextPassword.setText(R.string.whiteString);

        DialogUtils.showErrorDialog(this, getString(R.string.errorLoginTitle), getString(R.string.errorLoginMessage));
    }

    private void login() {
        findViewById(R.id.buttonLogin).setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                handleLoginError();
                return;
            }

            // Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            navigateToMain();
                        } else {
                            handleLoginError();
                        }
                    });
        });
    }
}