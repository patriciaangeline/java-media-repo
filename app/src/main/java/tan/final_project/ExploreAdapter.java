package tan.final_project;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class ExploreAdapter extends RealmRecyclerViewAdapter<Media, ExploreAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mediaTitleEEText;
        TextView mediaTypeEEText;

        ImageView imageEEView;
        ImageView likeEEButton;
        ImageView dislikeEEButton;
        ImageView viewEEButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mediaTitleEEText = itemView.findViewById(R.id.mediaTitleEEText);
            mediaTypeEEText = itemView.findViewById(R.id.mediaTypeEEText);

            imageEEView = itemView.findViewById(R.id.imageEEView);
            likeEEButton = itemView.findViewById(R.id.likeEEButton);
            dislikeEEButton = itemView.findViewById(R.id.dislikeEEButton);
            viewEEButton = itemView.findViewById(R.id.viewEEButton);
        }

    }


    ExploreActivity activity;

    public ExploreAdapter(ExploreActivity activity, @Nullable OrderedRealmCollection<Media> mediaData, boolean autoUpdate) {
        super(mediaData, autoUpdate);

        this.activity = activity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = activity.getLayoutInflater().inflate(R.layout.explore_entries_row_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Media m = getItem(position);

        // Camera Functionality for RecyclerView
        File getImageDir = activity.getExternalCacheDir();
        String mediaPath = m.getMediaImagePath();
        if (mediaPath != null) {
            File recyclerFile = new File(getImageDir, mediaPath);

            if (recyclerFile.exists()) {
                Picasso.get()
                        .load(recyclerFile)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(holder.imageEEView);
            } else {
                holder.imageEEView.setImageResource(R.mipmap.ic_launcher);

            }
        }

        holder.mediaTitleEEText.setText(String.valueOf(m.getMediaTitle()));
        holder.mediaTypeEEText.setText(String.valueOf(m.getMediaType()));

        holder.likeEEButton.setTag(m);
        holder.likeEEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.incrementLikes(m);
            }
        });

        holder.dislikeEEButton.setTag(m);
        holder.dislikeEEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.incrementDislikes(m);
            }
        });

        holder.viewEEButton.setTag(m);
        holder.viewEEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.viewExploreEntry(m);
            }
        });
    }
}
