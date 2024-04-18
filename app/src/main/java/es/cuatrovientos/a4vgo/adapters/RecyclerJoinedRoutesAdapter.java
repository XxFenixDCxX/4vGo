package es.cuatrovientos.a4vgo.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.RouteDetailsActivity;
import es.cuatrovientos.a4vgo.models.Route;

public class RecyclerJoinedRoutesAdapter extends RecyclerView.Adapter<RecyclerJoinedRoutesAdapter.RecyclerDataHolder> {
    private final List<Route> list;
    private final List<Route> listCopy;
    private final FirebaseFirestore db;
    private static String dni;

    public RecyclerJoinedRoutesAdapter() {
        db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        listCopy = new ArrayList<>();
        getCurrentUserDNI();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchDataFromFirebase() {
        db.collection("routes").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String joinedUsersStr = document.getString("joinedUsersDNI");
                    if(joinedUsersStr != null){
                        ArrayList<String> joinedUsers = new ArrayList<>(Arrays.asList(joinedUsersStr.split(",")));
                        if (joinedUsers.contains(dni)){
                            String origin = document.getString("origin");
                            String destination = document.getString("destination");
                            String userId = document.getString("userId");
                            String selectedTime = document.getString("selectedTime");
                            String selectedDate = document.getString("selectedDate");
                            Long maxSeatsLong = document.getLong("maxSeats");
                            int numPeople = (maxSeatsLong != null) ? maxSeatsLong.intValue() : 0;
                            String routeType = document.getString("routeType");
                            String selectedVehicle = document.getString("selectedVehicle");
                            boolean frequent = Boolean.TRUE.equals(document.getBoolean("frequent"));
                            String coments = document.getString("comments");
                            String joinedUsersDNI = document.getString("joinedUsersDNI");
                            Route route = new Route(document.getId(), routeType, origin, destination, userId, selectedDate, selectedTime, numPeople, selectedVehicle, frequent, coments, joinedUsersDNI);

                            list.add(route);
                            listCopy.add(route);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_joined_routes_item, parent, false);
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
        TextView origin, destination, date, numPeople;
        public RecyclerDataHolder(@NonNull View itemView) {
            super(itemView);
            origin = itemView.findViewById(R.id.txtOriginHistory);
            destination = itemView.findViewById(R.id.txtDestinationHistory);
            date = itemView.findViewById(R.id.txtDate);
            numPeople = itemView.findViewById(R.id.txtSeatNumHistory);
        }

        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
        public void assignData(Route route) {
            double latitude = Double.parseDouble(route.getOrigin().split(",")[0]);
            double longitude = Double.parseDouble(route.getOrigin().split(",")[1]);
            origin.setText(getStreetName(latitude, longitude, itemView));
            latitude = Double.parseDouble(route.getDestination().split(",")[0]);
            longitude = Double.parseDouble(route.getDestination().split(",")[1]);
            destination.setText(getStreetName(latitude, longitude, itemView));
            date.setText(route.getSelectedDate());
            numPeople.setText(route.getMaxSeats() + "");

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), RouteDetailsActivity.class);
                intent.putExtra("route", route);
                intent.putExtra("dni", dni);
                intent.putExtra("ShowRatingBar", "true");
                itemView.getContext().startActivity(intent);
            });
        }
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
    public interface OnItemClickListener{
        void onItemClick( Route route);
    }
}