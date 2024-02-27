package es.cuatrovientos.a4vgo.activities.Register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.Utils.DialogUtils;
import es.cuatrovientos.a4vgo.activities.MainActivity;

public class FirstRegisterActivity extends AppCompatActivity {
    Button back;
    ImageButton next;
    CheckBox spam;
    EditText email;
    FirebaseFirestore db;
    Boolean continua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_register);

        back = findViewById(R.id.btnBack);
        next = findViewById(R.id.btnNext);
        spam = findViewById(R.id.chcSpam);
        email = findViewById(R.id.txtEmail);
        db = FirebaseFirestore.getInstance();

        back.setOnClickListener(view -> {
            Intent intent = new Intent(FirstRegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String emailText = email.getText().toString();
                if (emailText.length() > 2) {
                    next.setVisibility(View.VISIBLE);
                } else {
                    next.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        next.setOnClickListener(view -> {
            String emailText = email.getText().toString();

            if (validateEmail(emailText)){
                if (validateDomain(emailText)) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    continua = true;
                    assert user != null;
                    user.updateEmail(emailText).addOnSuccessListener(unused -> DialogUtils.showErrorDialog(this, getString(R.string.errorLoginTitle), getString(R.string.errorLogUserExist)))
                            .addOnFailureListener(e -> {
                        Intent intent = new Intent(FirstRegisterActivity.this, SecondRegisterActivity.class);
                        intent.putExtra( "email", emailText);
                        intent.putExtra("spam", spam.isChecked());
                        startActivity(intent);
                    });
                } else {
                    DialogUtils.showErrorDialog(this, getString(R.string.errorLoginTitle), getString(R.string.errorLogDomain));
                }
            }else {
                DialogUtils.showErrorDialog(this, getString(R.string.errorLoginTitle), getString(R.string.errorLogEmail));
            }
        });
    }

    private boolean validateDomain(String validEmail) {
        String[] validDomains = {"gmail.com", "hotmail.com"};

        String[] emailParts = validEmail.split("@");
        if (emailParts.length == 2) {
            String domain = emailParts[1].toLowerCase();

            for (String validDomain : validDomains) {
                if (domain.equals(validDomain)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean validateEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailPattern);

        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}