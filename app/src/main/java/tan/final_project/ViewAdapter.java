package tan.final_project;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class ViewAdapter extends RealmRecyclerViewAdapter<Media, ViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleVAText;
        TextView mediaTypeVAText;

        RatingBar ratingVABar;

        ImageView deleteVAButton;

        ImageView editVAButton;

        ImageView imageVAView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleVAText = itemView.findViewById(R.id.titleVAText);
            mediaTypeVAText = itemView.findViewById(R.id.mediaTypeVAText);

            ratingVABar = itemView.findViewById(R.id.ratingVABar);

            deleteVAButton = itemView.findViewById(R.id.deleteVAButton);
            editVAButton = itemView.findViewById(R.id.editVAButton);

            imageVAView = itemView.findViewById(R.id.imageVAView);
        }
    }


    ViewActivity activity;
    public ViewAdapter(ViewActivity activity, @Nullable OrderedRealmCollection<Media> mediaData, boolean autoUpdate) {
        super(mediaData, autoUpdate);

        this.activity = activity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = activity.getLayoutInflater().inflate(R.layout.view_entries_row_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get User at given position
        Media m = getItem(position);

        // Camera Functionality for RecyclerView
        File getImageDir = activity.getExternalCacheDir();
        String mediaViewPath = m.getMediaImagePath();
        if (mediaViewPath != null) {
            File recyclerFile = new File(getImageDir, mediaViewPath);

            if (recyclerFile.exists()) {
                Picasso.get()
                        .load(recyclerFile)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(holder.imageVAView);
            } else {
                holder.imageVAView.setImageResource(R.mipmap.ic_launcher);

            }
        }

        holder.titleVAText.setText(String.valueOf(m.getMediaTitle()));
        holder.mediaTypeVAText.setText(String.valueOf(m.getMediaType()));
        holder.ratingVABar.setRating(m.getUserRating());

        holder.editVAButton.setTag(m);
        holder.editVAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.editMediaEntry(m);
            }
        });

        holder.deleteVAButton.setTag(m);
        holder.deleteVAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.deleteMediaEntry(m);
            }
        });
    }
}
