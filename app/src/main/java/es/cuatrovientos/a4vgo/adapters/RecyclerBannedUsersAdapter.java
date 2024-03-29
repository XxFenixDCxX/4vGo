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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.models.UserProfile;

public class RecyclerBannedUsersAdapter extends RecyclerView.Adapter<RecyclerBannedUsersAdapter.RecyclerDataHolder> {

    private final List<UserProfile> userList;
    private final FirebaseFirestore db;
    private String currentUserDni;

    public RecyclerBannedUsersAdapter(List<UserProfile> userList, FirebaseFirestore db) {
        this.userList = userList;
        this.db = db;
        this.getCurrentUserDNI();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getCurrentUserDNI() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            Query query = db.collection("personalDetails").whereEqualTo("email", email);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        currentUserDni = document.getString("dni");
                        notifyDataSetChanged(); // Notificar al adaptador después de obtener el DNI
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_user_item, parent, false);
        return new RecyclerDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerDataHolder holder, int position) {
        UserProfile user = userList.get(position);
        holder.assignData(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class RecyclerDataHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtEmail;
        ImageView delete;

        public RecyclerDataHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.textViewUsername);
            txtEmail = itemView.findViewById(R.id.textViewEmail);
            delete = itemView.findViewById(R.id.imgBtnDeleteVehicle);
        }

        public void assignData(UserProfile user) {
            txtUsername.setText(user.getUsername());
            txtEmail.setText(user.getEmail());

            String bannedUserDNI = user.getDni();
            delete.setOnClickListener(v -> deleteBannedUser(currentUserDni + "_" + bannedUserDNI, user));
        }

        @SuppressLint("NotifyDataSetChanged")
        private void deleteBannedUser(String documentId, UserProfile user) {
            FirebaseFirestore.getInstance().collection("bannedUsers").document(documentId).delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            userList.remove(user);
                            notifyDataSetChanged();
                        }
                    });
        }
    }
}
