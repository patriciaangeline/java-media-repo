package tan.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;

public class ExploreActivity extends AppCompatActivity {

    RecyclerView recyclerExView;
    String getUserUUID;

    RealmResults<Media> exploreList;

    RealmResults<User> omitCurrentUser;

    Realm realm;


    public void init() {
        recyclerExView = findViewById(R.id.recyclerExView);

        // RecyclerView Initialization
        LinearLayoutManager exploreLayoutManager = new LinearLayoutManager(this);
        exploreLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerExView.setLayoutManager(exploreLayoutManager);

        // Realm Initialization
        realm = Realm.getDefaultInstance();
        Intent welcomeIn = getIntent();
        Bundle welcomeBundle = welcomeIn.getExtras();
        getUserUUID = welcomeBundle.get("User UUID").toString();

        // Search for all Media objects created by other Users
        exploreList = realm.where(Media.class).notEqualTo("entriesUser.uuid", getUserUUID).findAll();

        ExploreAdapter exploreAdapter = new ExploreAdapter(this, exploreList, true);
        recyclerExView.setAdapter(exploreAdapter);
    }


    public void onDestroy()
    {
        super.onDestroy();
        if (!realm.isClosed())
        {
            realm.close();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_explore);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }


    // RecyclerView Buttons
    public void viewExploreEntry(Media m) {
        int mediaID = m.getId();
        // Query to get logged-in user's uuid in relation to Media ID
        String userID = realm.where(User.class).equalTo("userEntries.id", mediaID).findFirst().getUserName();
        Intent exploreOtherIntent = new Intent(this, ExploreOthersActivity.class);
        exploreOtherIntent.putExtra("Media ID", mediaID);
        exploreOtherIntent.putExtra("User Name", userID);
        startActivity(exploreOtherIntent);
    }


    public void incrementLikes(Media m) {
        int currentMediaLikes = m.getMediaLikes();

        try {
            realm.beginTransaction();
            m.setMediaLikes(currentMediaLikes + 1);
            realm.copyToRealmOrUpdate(m);
            realm.commitTransaction();

            Toast likedToast = Toast.makeText(this, "You liked a post.", Toast.LENGTH_SHORT);
            likedToast.show();

        } catch(Exception e) {
            Toast exceptionToast = Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_LONG);
            exceptionToast.show();
        }
    }


    public void incrementDislikes(Media m) {
        int currentMediaDislikes = m.getMediaDislikes();

        try {
            realm.beginTransaction();
            m.setMediaDislikes(currentMediaDislikes + 1);
            realm.copyToRealmOrUpdate(m);
            realm.commitTransaction();

            Toast likedToast = Toast.makeText(this, "You disliked a post.", Toast.LENGTH_SHORT);
            likedToast.show();

        } catch(Exception e) {
            Toast exceptionToast = Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_LONG);
            exceptionToast.show();
        }
    }
}