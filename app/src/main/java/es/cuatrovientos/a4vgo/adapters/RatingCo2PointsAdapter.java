package es.cuatrovientos.a4vgo.adapters;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowTxt = itemView.findViewById(R.id.rowTxt);
            pic = itemView.findViewById(R.id.pic);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            scoreTxt = itemView.findViewById(R.id.scoreTxt);
            this.itemView = itemView;
        }

        public void assignData(UserProfile userProfile, OnItemClickListener listener) {
            rowTxt.setText(String.valueOf(getAdapterPosition() + 1));
            titleTxt.setText(userProfile.getName());

            downloadImageAndSetBackground(userProfile.getProfileImage(), pic);
            scoreTxt.setText(String.valueOf(userProfile.getCo2Points()));

            this.itemView.setOnClickListener(v -> listener.onItemClick(userProfile));
        }

        @SuppressLint("StaticFieldLeak")
        private void downloadImageAndSetBackground(String imageUrl, ImageView profile) {
            new AsyncTask<Void, Void, Drawable>() {
                @Override
                protected Drawable doInBackground(Void... voids) {
                    try {
                        // Download the image as a Drawable
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
                        // Set the downloaded image as the background
                        profile.setImageDrawable(drawable);
                    } else {
                        // Set the default image
                        profile.setImageResource(R.drawable.ic_profile);
                    }
                }
            }.execute();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(UserProfile userProfile);
    }
}
