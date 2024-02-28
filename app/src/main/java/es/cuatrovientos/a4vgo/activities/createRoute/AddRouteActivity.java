package es.cuatrovientos.a4vgo.activities.createRoute;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.adapters.CustomSpinnerAdapter;

public class AddRouteActivity extends AppCompatActivity {

    private Spinner spinner ;
    private EditText editTextOrigen;
    private EditText editTextDestination;
    private ImageButton imageButtonDestination;
    private ImageButton imageButtonOrigin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);
        //Spinner adapter
        spinner = findViewById(R.id.spinner3);
        editTextOrigen = findViewById(R.id.editTextOrigin);
        editTextDestination = findViewById(R.id.editTextDestination);
        imageButtonDestination = findViewById(R.id.imageButtonDestination);
        imageButtonOrigin = findViewById(R.id.imageButtonOrigin);

        Spinner spinner = findViewById(R.id.spinner3);
        String[] items = new String[]{"Ida", "Vuelta"};

// Utiliza tu adaptador personalizado
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);


        // Spinner item selected listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateDestination(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }

            private void updateDestination(int selectedItemPosition) {
                if (selectedItemPosition == 0) { // Ida
                    editTextDestination.setText(getString(R.string.ci_cuatrovientos));
                    editTextDestination.setEnabled(false);
                    editTextOrigen.setEnabled(true);
                    imageButtonDestination.setVisibility(View.GONE);
                    imageButtonOrigin.setVisibility(View.VISIBLE);

                } else { // Vuelta
                    editTextOrigen.setText(getString(R.string.ci_cuatrovientos));
                    editTextDestination.setText("");

                    editTextOrigen.setEnabled(false);
                    editTextDestination.setEnabled(true);
                    imageButtonOrigin.setVisibility(View.GONE);
                    imageButtonDestination.setVisibility(View.VISIBLE);


                }
            }
        });


    }
}