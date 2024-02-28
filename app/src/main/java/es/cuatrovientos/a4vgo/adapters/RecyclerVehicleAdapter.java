package es.cuatrovientos.a4vgo.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.activities.models.Vehicle;

public class RecyclerVehicleAdapter extends RecyclerView.Adapter<RecyclerVehicleAdapter.RecyclerDataHolder> {
    private final List<Vehicle> list;
    private final List<Vehicle> listCopy;
    private String dni;

    public RecyclerVehicleAdapter() {
        list = new ArrayList<>();
        listCopy = new ArrayList<>();
        fetchDataFromFirebase();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchDataFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cars")
                .whereEqualTo("userDNI", getCurrentUserDNI())
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String plate = document.getString("plate");
                        String color = document.getString("color");
                        int siteNumber = Objects.requireNonNull(document.getLong("siteNumber")).intValue();

                        Vehicle vehicle = new Vehicle(plate, color, siteNumber);
                        list.add(vehicle);
                        listCopy.add(vehicle);
                        }
                    notifyDataSetChanged();
                });
    }

    private String getCurrentUserDNI() {
        dni = "";
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        Query query = FirebaseFirestore.getInstance().collection("personalDetails").whereEqualTo("email", currentUser.getEmail());
        query.get().addOnCompleteListener(task -> {
            QuerySnapshot querySnapshot = task.getResult();
            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
            dni = document.getString("dni");
        });
        return dni;
    }

    @NonNull
    @Override
    public RecyclerDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_item, parent, false);
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
        TextView txtPlate, txtNumberSites;
        ImageView delete;
        public RecyclerDataHolder(@NonNull View itemView) {
            super(itemView);
            txtPlate = itemView.findViewById(R.id.txtPlate);
            txtNumberSites = itemView.findViewById(R.id.txtNumSit);
            delete = itemView.findViewById(R.id.imgBtnDeleteVehicle);
        }

        public void assignData(Vehicle vehicle) {
            txtPlate.setText(vehicle.getPlate());
            txtNumberSites.setText(String.valueOf(vehicle.getSiteNumber()));
            delete.setOnClickListener(v -> FirebaseFirestore.getInstance().collection("cars").document(vehicle.getPlate()).delete());
        }
    }
}