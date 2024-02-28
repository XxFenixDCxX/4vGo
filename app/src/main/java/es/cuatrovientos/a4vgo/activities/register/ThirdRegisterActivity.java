package es.cuatrovientos.a4vgo.activities.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.Utils.DialogUtils;

public class ThirdRegisterActivity extends AppCompatActivity {
    EditText birthdate;
    Button back;
    ImageButton next;
    String sendName, sendSurname, sendDni, sendEmail;
    Boolean sendSpam;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_register);

        birthdate = findViewById(R.id.txtBirthDate);
        next = findViewById(R.id.btnNext);
        back = findViewById(R.id.btnBack);
        bundle = getIntent().getExtras();
        assert bundle != null;
        sendName = bundle.getString("name");
        sendSurname = bundle.getString("surname");
        sendDni = bundle.getString("dni");
        sendEmail = bundle.getString("email");
        sendSpam = bundle.getBoolean("spam");

        back.setOnClickListener(view -> {
            Intent intent = new Intent(ThirdRegisterActivity.this, SecondRegisterActivity.class);
            startActivity(intent);
        });

        birthdate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String dateString = birthdate.getText().toString();
                if(dateString.length() > 2){
                    next.setVisibility(View.VISIBLE);
                }else {
                    next.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        next.setOnClickListener(view -> {
            String dateString = birthdate.getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES"));
            dateFormat.setLenient(false);

            try {
                Date date = dateFormat.parse(dateString);
                Date currentDate = new Date();
                assert date != null;
                long differenceInMillis = currentDate.getTime() - date.getTime();
                long years = differenceInMillis / (365L * 24 * 60 * 60 * 1000);
                if(years >= 16){
                    Intent intent = new Intent(ThirdRegisterActivity.this, FourthRegisterActivity.class);
                    intent.putExtra("name", sendName);
                    intent.putExtra("surname", sendSurname);
                    intent.putExtra("dni", sendDni);
                    intent.putExtra( "email", sendEmail);
                    intent.putExtra("birthdate", dateString);
                    intent.putExtra("spam", sendSpam);
                    startActivity(intent);
                }else {
                    next.setVisibility(View.INVISIBLE);
                    DialogUtils.showErrorDialog(this, getString(R.string.errorLoginTitle), getString(R.string.errorLogYears));
                }
            } catch (ParseException e) {
                next.setVisibility(View.INVISIBLE);
                DialogUtils.showErrorDialog(this, getString(R.string.errorLoginTitle), getString(R.string.errorLogDate));
            }
        });
    }
}