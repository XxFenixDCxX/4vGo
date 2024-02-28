package es.cuatrovientos.a4vgo.tasks;

import android.os.AsyncTask;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class OpenStreetMapSearchTask extends AsyncTask<String, Void, String> {

    private EditText editTextOrigin;

    public OpenStreetMapSearchTask(EditText editTextOrigin) {
        this.editTextOrigin = editTextOrigin;
    }

    @Override
    protected String doInBackground(String... params) {
        String streetName = params[0];

        try {
            streetName = URLEncoder.encode(streetName, "UTF-8");
            // Agrega el filtro de región (en este caso, Navarra) a la URL
            String apiUrl = "https://nominatim.openstreetmap.org/search?format=json&q=" + streetName + "&country=Spain&state=Navarre";
            URL url = new URL(apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            return response.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // Aquí puedes manejar el resultado de la búsqueda
        // El resultado es un JSON que contiene información sobre la dirección encontrada
        // Puedes parsear el JSON y mostrar la información relevante en tu aplicación
    }
}
