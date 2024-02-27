package es.cuatrovientos.a4vgo.activities.CreateRoute;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import es.cuatrovientos.a4vgo.R;

public class AddRouteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);
        //Spinner adapter
        Spinner spinner = findViewById(R.id.spinner3);
        String[] items = new String[]{"Ida", "Vuelta"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        spinner.setAdapter(adapter);


    }
}