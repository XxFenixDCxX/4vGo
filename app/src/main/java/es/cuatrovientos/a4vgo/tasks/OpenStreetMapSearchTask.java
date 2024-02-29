package es.cuatrovientos.a4vgo.tasks;

import static es.cuatrovientos.a4vgo.maps.searchMapActivity.passCoordinates;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class OpenStreetMapSearchTask extends AsyncTask<String, Void, String> {


    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapter;
    private GoogleMap mMap;  // Add GoogleMap instance

    public OpenStreetMapSearchTask(AutoCompleteTextView autoCompleteTextView, ArrayAdapter<String> adapter, GoogleMap mMap) {
        this.autoCompleteTextView = autoCompleteTextView;
        this.adapter = adapter;
        this.mMap = mMap;

    }

    @Override
    protected String doInBackground(String... params) {
        String streetName = params[0];

        try {
            streetName = URLEncoder.encode(streetName, "UTF-8");
            String apiUrl = "https://nominatim.openstreetmap.org/search?format=json&q=" + streetName;
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
        Log.d("moha", "Result: " + result);

        if (result != null) {
            // Parsea el resultado y obtén la latitud y longitud
            try {
                JSONArray jsonArray = new JSONArray(result);
                if (jsonArray.length() > 0) {
                    JSONObject firstResult = jsonArray.getJSONObject(0);
                    double latitude = Double.parseDouble(firstResult.getString("lat"));
                    double longitude = Double.parseDouble(firstResult.getString("lon"));

                    adapter.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        String displayName = item.optString("display_name", "");
                        adapter.add(displayName);
                    }

                    adapter.notifyDataSetChanged();

                    LatLng location = new LatLng(latitude, longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));

                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(location).title("Ubicación encontrada").draggable(true));
                    passCoordinates(location);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
