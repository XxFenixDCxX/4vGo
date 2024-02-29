package es.cuatrovientos.a4vgo.adapters;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.models.Route;

public class RecyclerRoutesAdapter extends RecyclerView.Adapter<RecyclerRoutesAdapter.RecyclerDataHolder> {
    private final List<Route> list;
    private final List<Route> listCopy;
    private final FirebaseFirestore db;
    private static String dni;
    private final String routeType;
    private final String cords;
    private final String selectedDate;
    int numPeople;
    private static String currentUsername;

    public RecyclerRoutesAdapter(String routeType, String cords, String selectedDate, int numPeople) {
        this.routeType = routeType;
        this.cords = cords;
        this.selectedDate  = selectedDate;
        this.numPeople = numPeople;
        db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        listCopy = new ArrayList<>();
        getCurrentUserDNI();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchDataFromFirebase() {
        double latitudOriginal = Double.parseDouble(this.cords.split(",")[0]);
        double longitudOrginal = Double.parseDouble(this.cords.split(",")[1]);
        CollectionReference routesCollection = db.collection("routes");
        if (routeType.equals("Vuelta")){
            routesCollection.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String routeType = document.getString("routeType");
                        assert routeType != null;
                        if(routeType.equals(this.routeType)){
                            String destination = document.getString("destination");
                            assert destination != null;
                            double latitud = Double.parseDouble(destination.split(",")[0]);
                            double longitud = Double.parseDouble(destination.split(",")[1]);
                            double distance = haversine(latitudOriginal, longitudOrginal, latitud, longitud);
                            if(distance <= 2){
                                String selectedDate = document.getString("selectedDate");
                                if (this.selectedDate.equals(selectedDate)){
                                    Long maxSeatsLong = document.getLong("maxSeats");
                                    int numPeople = (maxSeatsLong != null) ? maxSeatsLong.intValue() : 0;
                                    if(this.numPeople <= numPeople){
                                        String origin = document.getString("origin");
                                        String userId = document.getString("userId");
                                        String selectedTime = document.getString("selectedTime");
                                        String selectedVehicle = document.getString("selectedVehicle");
                                        boolean frequent = Boolean.TRUE.equals(document.getBoolean("frequent"));
                                        String coments = document.getString("comments");
                                        Route route = new Route(routeType, destination, origin, userId, selectedDate, selectedTime, numPeople, selectedVehicle, frequent, coments);

                                        list.add(route);
                                        listCopy.add(route);
                                    }
                                }
                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }else {
            routesCollection.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String routeType = document.getString("routeType");
                        assert routeType != null;
                        if(routeType.equals(this.routeType)){
                            String destination = document.getString("destination");
                            assert destination != null;
                            double latitud = Double.parseDouble(destination.split(",")[0]);
                            double longitud = Double.parseDouble(destination.split(",")[1]);
                            double distance = haversine(latitudOriginal, longitudOrginal, latitud, longitud);
                            if(distance <= 2){
                                String selectedDate = document.getString("selectedDate");
                                if (this.selectedDate.equals(selectedDate)){
                                    Long maxSeatsLong = document.getLong("maxSeats");
                                    int numPeople = (maxSeatsLong != null) ? maxSeatsLong.intValue() : 0;
                                    if(this.numPeople <= numPeople){
                                        String origin = document.getString("origin");
                                        String userId = document.getString("userId");
                                        String selectedTime = document.getString("selectedTime");
                                        String selectedVehicle = document.getString("selectedVehicle");
                                        boolean frequent = Boolean.TRUE.equals(document.getBoolean("frequent"));
                                        String coments = document.getString("comments");
                                        Route route = new Route(routeType, destination, origin, userId, selectedDate, selectedTime, numPeople, selectedVehicle, frequent, coments);

                                        list.add(route);
                                        listCopy.add(route);
                                    }
                                }
                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void getCurrentUserDNI() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        Query query = db.collection("personalDetails").whereEqualTo("email", currentUser.getEmail());
        query.get().addOnCompleteListener(task -> {
            QuerySnapshot querySnapshot = task.getResult();
            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
            dni = document.getString("dni");
            fetchDataFromFirebase();
        });
    }

    @NonNull
    @Override
    public RecyclerDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_route_item, parent, false);
        return new RecyclerDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerDataHolder holder, int position) {
        holder.assignData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class RecyclerDataHolder extends RecyclerView.ViewHolder {
        TextView origin, destination, originTime, destinaionTime, seatNum, elapsedTime, username;
        ImageView profileImg;
        public RecyclerDataHolder(@NonNull View itemView) {
            super(itemView);
            origin = itemView.findViewById(R.id.OriginCity);
            destination = itemView.findViewById(R.id.DestinationCity);
            originTime = itemView.findViewById(R.id.OriginTime);
            destinaionTime = itemView.findViewById(R.id.DestinationTime);
            elapsedTime = itemView.findViewById(R.id.elapsedTime);
            seatNum = itemView.findViewById(R.id.seat_num);
            username = itemView.findViewById(R.id.user_name);
            profileImg = itemView.findViewById(R.id.profileImg);
        }

        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n", "StaticFieldLeak"})
        public void assignData(Route route) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            CollectionReference collection = FirebaseFirestore.getInstance().collection("personalDetails");
            assert currentUser != null;
            Query query = collection.whereEqualTo("email", currentUser.getEmail());
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    currentUsername = document.getString("username");
                    double destinationLat = Double.parseDouble(route.getDestination().split(",")[0]);
                    double destinationLon = Double.parseDouble(route.getDestination().split(",")[1]);


                    String streetName;
                    String time;

                    if(route.getRouteType().equals("Ida")){
                        streetName = getStreetName(Double.parseDouble(route.getOrigin().split(",")[0]), Double.parseDouble(route.getOrigin().split(",")[1]), itemView);

                        destination.setText(R.string.center);
                        originTime.setText(route.getSelectedTime());
                        destinaionTime.setText("08:30");
                        elapsedTime.setText(getHowMuchTime(route.getSelectedTime(), "08:30"));
                        origin.setText(streetName);
                    }else{
                        streetName = getStreetName(Double.parseDouble(route.getDestination().split(",")[0]), Double.parseDouble(route.getDestination().split(",")[1]), itemView);

                        origin.setText(R.string.center);
                        originTime.setText("14:30");
                        elapsedTime.setText(getHowMuchTime("14:30", route.getSelectedTime()));
                        destinaionTime.setText(route.getSelectedTime());
                        destination.setText(streetName);
                    }
                    seatNum.setText(String.valueOf(route.getMaxSeats()));
                    username.setText(currentUsername);

                    CollectionReference collection1 = FirebaseFirestore.getInstance().collection("personalDetails");
                    collection1.document(dni).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String imageUrl = documentSnapshot.getString("profileImage");

                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        new AsyncTask<Void, Void, Drawable>() {
                                            @Override
                                            protected Drawable doInBackground(Void... voids) {
                                                try {
                                                    InputStream inputStream = new URL(imageUrl).openStream();
                                                    return Drawable.createFromStream(inputStream, null);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    return null;
                                                }
                                            }

                                            @Override
                                            protected void onPostExecute(Drawable drawable) {
                                                if (drawable != null) {
                                                    profileImg.setBackground(drawable);
                                                } else {
                                                    profileImg.setBackgroundResource(R.drawable.ic_profile);
                                                }
                                            }
                                        }.execute();
                                    } else {
                                        profileImg.setBackgroundResource(R.drawable.ic_profile);
                                    }
                                }
                            });
                }
            });
        }
    }
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double RADIUS_OF_EARTH = 6371;
        return RADIUS_OF_EARTH * c;
    }
    private static String getStreetName(double latitude, double longitude, View itemView) {

        Geocoder geocoder = new Geocoder(itemView.getContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String thoroughfare = addresses.get(0).getThoroughfare();
                String featureName = addresses.get(0).getFeatureName();
                String addressLine = addresses.get(0).getAddressLine(0);

                if (thoroughfare != null && !thoroughfare.isEmpty()) {
                    return thoroughfare;
                } else if (featureName != null && !featureName.isEmpty()) {
                    return featureName;
                } else if (addressLine != null && !addressLine.isEmpty()) {
                    return addressLine;
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return "";
    }
    private static String getHowMuchTime(String originTime, String destinationTime){
        int originHour = Integer.parseInt(originTime.split(":")[0]);
        int originMinute = Integer.parseInt(originTime.split(":")[1]);
        int destinationHour = Integer.parseInt(destinationTime.split(":")[0]);
        int destinationMinute = Integer.parseInt(destinationTime.split(":")[1]);

        int hour = Math.abs(destinationHour - originHour);
        int minute = Math.abs(destinationMinute - originMinute);
        return hour + "h " + minute + "m";
    }
}