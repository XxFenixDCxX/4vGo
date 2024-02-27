package es.cuatrovientos.a4vgo.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.firestore.FirebaseFirestore;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.Utils.DialogUtils;

public class SecondRegisterActivity extends AppCompatActivity {
    Button back;
    ImageButton next;
    EditText name, surname, dni;
    String sendEmail;
    Boolean sendSpam;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_register);

        back = findViewById(R.id.btnBack);
        next = findViewById(R.id.btnNext);
        name = findViewById(R.id.txtName);
        surname = findViewById(R.id.txtSurname);
        dni = findViewById(R.id.txtDni);
        bundle = getIntent().getExtras();
        assert bundle != null;
        sendEmail = bundle.getString("email");
        sendSpam = bundle.getBoolean("spam");

        back.setOnClickListener(view -> {
            Intent intent = new Intent(SecondRegisterActivity.this, FirstRegisterActivity.class);
            startActivity(intent);
        });

        next.setOnClickListener(view -> {
            String dniText = dni.getText().toString();
            if(validateDNI(dniText)){
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("personalDetails").document(dniText).get().addOnSuccessListener(documentSnapshot -> {
                    next.setVisibility(View.INVISIBLE);
                    if(documentSnapshot.exists()){
                        next.setVisibility(View.INVISIBLE);
                        DialogUtils.showErrorDialog(this, getString(R.string.errorLoginTitle), getString(R.string.errorLogDniExist));
                    } else{
                        Intent intent = new Intent(SecondRegisterActivity.this, ThirdRegisterActivity.class);
                        String nameText = name.getText().toString();
                        String surnameText = surname.getText().toString();
                        intent.putExtra("name", nameText);
                        intent.putExtra("surname", surnameText);
                        intent.putExtra("dni", dniText);
                        intent.putExtra( "email", sendEmail);
                        intent.putExtra("spam", sendSpam);
                        startActivity(intent);
                    }
                }).addOnFailureListener(e -> {
                });
            } else {
                next.setVisibility(View.INVISIBLE);
                DialogUtils.showErrorDialog(this, getString(R.string.errorLoginTitle), getString(R.string.errorLogDNI));
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonVisibility();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        surname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonVisibility();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        dni.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonVisibility();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private void buttonVisibility(){
        String nameText = name.getText().toString();
        String surnameText = surname.getText().toString();
        String dniText = dni.getText().toString();
        if(nameText.length() > 2 && surnameText.length() > 2 && dniText.length() > 2){
            next.setVisibility(View.VISIBLE);
        } else {
            next.setVisibility(View.INVISIBLE);
        }
    }

    public static boolean validateDNI(String dni) {
        if (dni.length() != 9) {
            return false;
        }

        String numbers = dni.substring(0, 8);
        String charDNI = dni.substring(8);

        try {
            Integer.parseInt(numbers);
        } catch (NumberFormatException e) {
            return false;
        }

        char calculatedChar = calculateDNIChar(Integer.parseInt(numbers));

        return charDNI.equalsIgnoreCase(String.valueOf(calculatedChar));
    }

    private static char calculateDNIChar(int numbers) {
        String charsDNI = "TRWAGMYFPDXBNJZSQVHLCKE";
        int index = numbers % 23;
        return charsDNI.charAt(index);
    }
}