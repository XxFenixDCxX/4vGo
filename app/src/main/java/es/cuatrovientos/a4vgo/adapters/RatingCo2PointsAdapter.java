package es.cuatrovientos.a4vgo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import es.cuatrovientos.a4vgo.R;
import es.cuatrovientos.a4vgo.models.UserProfile;

public class RatingCo2PointsAdapter extends RecyclerView.Adapter<RatingCo2PointsAdapter.ViewHolder> {

    private final List<UserProfile> userProfileList;
    private final OnItemClickListener listener;

    public RatingCo2PointsAdapter(List<UserProfile> userProfileList, OnItemClickListener listener) {
        this.userProfileList = userProfileList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item_co2_points, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.assignData(userProfileList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return userProfileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rowTxt;
        ShapeableImageView pic;
        TextView titleTxt;
        TextView scoreTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowTxt = itemView.findViewById(R.id.rowTxt);
            pic = itemView.findViewById(R.id.pic);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            scoreTxt = itemView.findViewById(R.id.scoreTxt);
        }

        public void assignData(UserProfile userProfile, OnItemClickListener listener) {
            rowTxt.setText(String.valueOf(getAdapterPosition() + 1));
            titleTxt.setText(userProfile.getName());

            pic.setImageResource(R.drawable.user_2);


            scoreTxt.setText(String.valueOf(userProfile.getCo2Points()));

            itemView.setOnClickListener(v -> listener.onItemClick(userProfile, getAdapterPosition()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(UserProfile userProfile, int position);
    }
}
