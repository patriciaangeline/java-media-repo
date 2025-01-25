package tan.final_project;

import android.media.Image;
import android.media.Rating;
import android.view.View;
import android.view.ViewGroup;
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

public class SearchAdapter extends RealmRecyclerViewAdapter<Media, SearchAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleSEAText;
        TextView mediaTypeSEAText;
        RatingBar ratingSEABar;

        ImageView imageSEAView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleSEAText = itemView.findViewById(R.id.titleSEAText);
            mediaTypeSEAText = itemView.findViewById(R.id.mediaTypeSEAText);

            ratingSEABar = itemView.findViewById(R.id.ratingSEABar);

            imageSEAView = itemView.findViewById(R.id.imageSEAView);
        }
    }


    SearchActivity activity;
    public SearchAdapter(SearchActivity activity, @Nullable OrderedRealmCollection<Media> data, boolean autoUpdate) {
        super(data, autoUpdate);

        this.activity = activity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = activity.getLayoutInflater().inflate(R.layout.search_row_layout, parent, false);  // VERY IMPORTANT TO USE THIS STYLE

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Media m = getItem(position);

        File getImageDir = activity.getExternalCacheDir();
        File mediaFile = new File(getImageDir, m.getMediaImagePath());

        if(mediaFile.exists()){
            Picasso.get()
                    .load(mediaFile)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.imageSEAView);
        } else {
            holder.imageSEAView.setImageResource(R.mipmap.ic_launcher);
        }

        holder.titleSEAText.setText(String.valueOf(m.getMediaTitle()));
        holder.mediaTypeSEAText.setText(String.valueOf(m.getMediaType()));
        holder.ratingSEABar.setRating(m.getUserRating());

    }
}
