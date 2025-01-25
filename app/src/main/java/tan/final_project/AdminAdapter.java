package tan.final_project;

import android.view.View;
import android.view.ViewGroup;
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

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class AdminAdapter extends RealmRecyclerViewAdapter<User, AdminAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userAAName;
        TextView userAAPassword;
        ImageView imageAAView;
        ImageView editAAButton;
        ImageView deleteAAButton;


        public ViewHolder(@NonNull View itemView){
            super(itemView);

            userAAName = itemView.findViewById(R.id.userAAName);
            userAAPassword = itemView.findViewById(R.id.userAAPassword);

            imageAAView = itemView.findViewById(R.id.imageAAView);

            editAAButton = itemView.findViewById(R.id.editAAButton);
            deleteAAButton = itemView.findViewById(R.id.deleteAAButton);
        }
    }

    AdminActivity activity;
    public AdminAdapter(AdminActivity activity, @Nullable OrderedRealmCollection<User> userData, boolean autoUpdate) {
        super(userData, autoUpdate);

        this.activity = activity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = activity.getLayoutInflater().inflate(R.layout.admin_row_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get User at given position
        User u = getItem(position);

        File getImageDir = activity.getExternalCacheDir();
        File adminFile = new File(getImageDir, u.getUserImagePath());

        if(adminFile.exists()){
            Picasso.get()
                    .load(adminFile)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.imageAAView);
        } else {
            holder.imageAAView.setImageResource(R.mipmap.ic_launcher);
        }

        holder.userAAName.setText(String.valueOf(u.getUserName()));
        holder.userAAPassword.setText(String.valueOf(u.getUserPassword()));


        holder.editAAButton.setTag(u);
        holder.editAAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.editUser(u);
            }
        });


        holder.deleteAAButton.setTag(u);
        holder.deleteAAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.deleteUser(u);
            }
        });

    }
}
