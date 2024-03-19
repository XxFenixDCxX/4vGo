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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.models.History;

public class RecyclerHistoryAdapter extends RecyclerView.Adapter<RecyclerHistoryAdapter.RecyclerDataHolder> {
    private final List<History> list;
    private final List<History> listCopy;
    private final FirebaseFirestore db;
    private String dni;

    public RecyclerHistoryAdapter() {
        db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        listCopy = new ArrayList<>();
        getCurrentUserDNI();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchDataFromFirebase() {
        db.collection("historyRoutes").whereEqualTo("dni", dni).get().addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        History history = new History(document.getString("date"), document.getString("destination"), document.getString("dni"), document.getString("numPeople"), document.getString("origin"));
                        list.add(history);
                        listCopy.add(history);
                        }
                    notifyDataSetChanged();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_history_item, parent, false);
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

        @SuppressLint("NotifyDataSetChanged")
        public void assignData(History history) {
            origin.setText(history.getOrigin());
            destination.setText(history.getDestination());
            date.setText(history.getDate());
            numPeople.setText(history.getNumPeople());
        }
    }
}