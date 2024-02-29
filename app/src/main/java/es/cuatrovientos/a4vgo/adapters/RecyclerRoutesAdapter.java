package es.cuatrovientos.a4vgo.adapters;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.models.Route;

public class RecyclerRoutesAdapter extends RecyclerView.Adapter<RecyclerRoutesAdapter.RecyclerDataHolder> {
    private final List<Route> list;
    private final FirebaseFirestore db;
    private String dni;
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
        List<Route> listCopy = new ArrayList<>();
        getCurrentUserDNI();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchDataFromFirebase() {
        double latitudOriginal = Double.parseDouble(this.cords.split(",")[0]);
        double longitudOrginal = Double.parseDouble(this.cords.split(",")[1]);
        CollectionReference routesCollection = db.collection("routes");
        if (routeType.equals("Ida")){
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
                                    int numPeople = Integer.parseInt(Objects.requireNonNull(document.getString("maxSeats")));
                                    if(this.numPeople <= numPeople){
                                        String origin = document.getString("origin");
                                        String userId = document.getString("userId");
                                        String selectedTime = document.getString("selectedTime");
                                        String selectedVehicle = document.getString("selectedVehicle");
                                        boolean frequent = Boolean.TRUE.equals(document.getBoolean("frequent"));
                                        String coments = document.getString("comments");

                                        list.add(new Route(routeType, destination, origin, userId, selectedDate, selectedTime, this.numPeople, selectedVehicle, frequent, coments));
                                    }
                                }
                            }
                        }
                    }
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
                                    int numPeople = Integer.parseInt(Objects.requireNonNull(document.getString("maxSeats")));
                                    if(this.numPeople <= numPeople){
                                        String origin = document.getString("origin");
                                        String userId = document.getString("userId");
                                        String selectedTime = document.getString("selectedTime");
                                        String selectedVehicle = document.getString("selectedVehicle");
                                        boolean frequent = Boolean.TRUE.equals(document.getBoolean("frequent"));
                                        String coments = document.getString("comments");

                                        list.add(new Route(routeType, destination, origin, userId, selectedDate, selectedTime, this.numPeople, selectedVehicle, frequent, coments));
                                    }
                                }
                            }
                        }
                    }
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
        public RecyclerDataHolder(@NonNull View itemView) {
            super(itemView);
            origin = itemView.findViewById(R.id.OriginCity);
            destination = itemView.findViewById(R.id.DestinationCity);
            originTime = itemView.findViewById(R.id.OriginTime);
            destinaionTime = itemView.findViewById(R.id.DestinationTime);
            elapsedTime = itemView.findViewById(R.id.elapsedTime);
            seatNum = itemView.findViewById(R.id.seat_num);
            username = itemView.findViewById(R.id.user_name);
        }

        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
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
                }
            });

            if(route.getRouteType().equals("Ida")){
                destination.setText(R.string.center);
                //toDo destination
                originTime.setText(route.getSelectedTime());
                destinaionTime.setText("8:20");
            }else{
                origin.setText(R.string.center);
                //toDo destination
                originTime.setText("14:30");
                destinaionTime.setText(route.getSelectedTime());
            }
            seatNum.setText(route.getMaxSeats());
            username.setText(currentUsername);
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
}