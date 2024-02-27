package es.cuatrovientos.a4vgo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import es.cuatrovientos.a4vgo.R;

public class loginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicialization of the Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Autentication call
        login();
    }

    private void login() {
        findViewById(R.id.buttonLogin).setOnClickListener(v -> {
            EditText editTextEmail = findViewById(R.id.editTextEmail);
            EditText editTextPassword = findViewById(R.id.editTextPassword);

            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            // Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(loginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, go to the principal activity
                            Intent intent = new Intent(loginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, clear the input fields and show a popup
                            editTextEmail.setText("");
                            editTextPassword.setText("");

                            new AlertDialog.Builder(loginActivity.this)
                                    .setTitle(R.string.errorLoginTitle)
                                    .setMessage(R.string.errorLoginMessage)
                                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                        // Refresh the activity
                                        finish();
                                        startActivity(getIntent());
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });
        });
    }
}