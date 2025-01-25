package tan.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;

public class WelcomeActivity extends AppCompatActivity {

    TextView userWText;
    TextView rememberWText;

    ImageView imageWView;

    Button addWButton;
    Button viewWButton;
    Button searchWButton;
    Button exploreWButton;

    SharedPreferences loginCredentials;

    Realm realm;

    User findUser;

    public void init(){
        userWText = findViewById(R.id.userWText);
        rememberWText = findViewById(R.id.rememberWText);

        imageWView = findViewById(R.id.imageWView);

        addWButton = findViewById(R.id.addWButton);
        addWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAddEntry();
            }
        });


        viewWButton = findViewById(R.id.viewWButton);
        viewWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goViewEntries();
            }
        });


        searchWButton = findViewById(R.id.searchWButton);
        searchWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSearchEntry();
            }
        });


        exploreWButton = findViewById(R.id.exploreWButton);
        exploreWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goExploreEntries();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        loginCredentials = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        Boolean getRememberState = loginCredentials.getBoolean("Remember State", false);

        // Retrieve UUID saved in Shared Preferences
        String findUUIDSP = loginCredentials.getString("uuid", null);


        // Realm Initialization
        realm = Realm.getDefaultInstance();

        // Extract User object using UUID saved in Shared Preferences
        findUser = realm.where(User.class).equalTo("uuid", findUUIDSP).findFirst();
        String getUserName = findUser.getUserName().toString();
        String getImage = findUser.getUserImagePath();

        // Retrieve and show the image from either Register or EditActivity
        File getImageDir = getExternalCacheDir();
        if(getImage!=null) {
            File userImageFile = new File(getImageDir, getImage);

            if(userImageFile.exists()) {
                refreshImageView(imageWView, userImageFile);
            }
        }

        if(getRememberState) {
            userWText.setText(getUserName + "!");
            rememberWText.setText("You will be remembered.");
        } else {
            userWText.setText(getUserName + "!");
            rememberWText.setText("");
        }
    }


    // WelcomeActivity Buttons will relay logged-in User's UUID to perform their respective operations
    public void goAddEntry(){
        Intent addEntryIntent = new Intent(this, AddActivity.class);
        String getUserUUID = findUser.getUuid().toString();
        addEntryIntent.putExtra("User UUID", getUserUUID);
        startActivity(addEntryIntent);
    }


    public void goViewEntries(){
        Intent viewEntriesIntent = new Intent(this, ViewActivity.class);
        String getUserUUID = findUser.getUuid().toString();
        viewEntriesIntent.putExtra("User UUID", getUserUUID);
        startActivity(viewEntriesIntent);
    }


    public void goSearchEntry(){
        Intent searchEntryIntent = new Intent(this, SearchActivity.class);
        String getUserUUID = findUser.getUuid().toString();
        searchEntryIntent.putExtra("User UUID", getUserUUID);
        startActivity(searchEntryIntent);
    }


    public void goExploreEntries(){
        Intent exploreEntriesIntent = new Intent(this, ExploreActivity.class);
        String getUserUUID = findUser.getUuid().toString();
        exploreEntriesIntent.putExtra("User UUID", getUserUUID);
        startActivity(exploreEntriesIntent);
    }

    private void refreshImageView(ImageView imageView, File savedImage) {

        // this will put the image saved to the file system to the imageview
        Picasso.get()
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView);
    }
}