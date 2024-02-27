package es.cuatrovientos.a4vgo.activities.Register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;


import java.util.HashMap;
import java.util.Map;

import es.cuatrovientos.a4vgo.R;

public class FourthRegisterActivity extends AppCompatActivity {
    EditText password, rePassword;
    ImageButton next;
    Button back;
    Bundle bundle;
    String sendName, sendSurname, sendDni, sendEmail, sendBirthdate;
    Boolean sendSpam;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth_register);

        password = findViewById(R.id.txtPassword);
        rePassword = findViewById(R.id.txtRePassword);
        next = findViewById(R.id.btnNext);
        back = findViewById(R.id.btnBack);
        bundle = getIntent().getExtras();
        assert bundle != null;
        sendName = bundle.getString("name");
        sendSurname = bundle.getString("surname");
        sendDni = bundle.getString("dni");
        sendEmail = bundle.getString("email");
        sendBirthdate = bundle.getString("birthdate");
        sendSpam = bundle.getBoolean("spam");
        db = FirebaseFirestore.getInstance();

        back.setOnClickListener(view -> {
            Intent intent = new Intent(FourthRegisterActivity.this, ThirdRegisterActivity.class);
            startActivity(intent);
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passwordString = password.getText().toString();
                String alphabet = "abcdefghijklmnopqrstuvwxyz";
                String numbers = "1234567890";
                if (passwordString.length() >= 8 && containsLetter(passwordString, alphabet) && containsNumber(passwordString, numbers)) {
                    rePassword.setVisibility(View.VISIBLE);
                } else {
                    rePassword.setVisibility(View.INVISIBLE);
                    next.setVisibility(View.INVISIBLE);
                    rePassword.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        rePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String rePasswordString = rePassword.getText().toString();

                if(rePasswordString.length() > 2){
                    next.setVisibility(View.VISIBLE);
                }else {
                    next.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        next.setOnClickListener(view -> {
            String passwordString = password.getText().toString();
            String rePasswordString = rePassword.getText().toString();

            if(passwordString.equals(rePasswordString)){
                //toDo Mandar a la pantalla principal
                Map<String, String> personalDetailsMap = new HashMap<>();
                personalDetailsMap.put("dni", sendDni);
                personalDetailsMap.put("username", sendEmail.split("@")[0]);
                personalDetailsMap.put("name", sendName);
                personalDetailsMap.put("surname", sendSurname);
                personalDetailsMap.put("birthdate", sendBirthdate);
                personalDetailsMap.put("spam", String.valueOf(sendSpam));
                personalDetailsMap.put("userId", sendEmail);
                db.collection("personalDetails").document(sendDni).set(personalDetailsMap);

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(sendEmail, passwordString);
            }else{
                next.setVisibility(View.INVISIBLE);
                View contentView = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar.make(contentView, R.string.errorLogPassword, Snackbar.LENGTH_SHORT);
                snackbar.setTextColor(Color.RED);
                snackbar.setBackgroundTint(Color.BLACK);
                snackbar.show();
            }
        });
    }
    private boolean containsLetter(String input, String alphabet) {
        for (char c : input.toCharArray()) {
            if (alphabet.contains(String.valueOf(c).toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsNumber(String input, String numbers) {
        for (char c : input.toCharArray()) {
            if (numbers.contains(String.valueOf(c))) {
                return true;
            }
        }
        return false;
    }
}