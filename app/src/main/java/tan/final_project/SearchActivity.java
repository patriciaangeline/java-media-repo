package tan.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class SearchActivity extends AppCompatActivity {

    RecyclerView recyclerSView;
    EditText searchInput;
    ImageView searchSButton;

    Button cancelSButton;

    Realm realm;

    String getUserUUID;

    public void init(){
        recyclerSView = findViewById(R.id.recyclerSView);
        searchInput = findViewById(R.id.searchInput);

        searchSButton = findViewById(R.id.searchSButton);
        searchSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEntry();
            }
        });

        cancelSButton = findViewById(R.id.cancelSButton);
        cancelSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize RecyclerView
        LinearLayoutManager searchLayoutManager = new LinearLayoutManager(this);
        searchLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerSView.setLayoutManager(searchLayoutManager);

        // Realm Initialization
        realm = Realm.getDefaultInstance();
        Intent welcomeIn = getIntent();
        Bundle welcomeBundle = welcomeIn.getExtras();
        getUserUUID = welcomeBundle.get("User UUID").toString();

        // Query all Media objects created by the logged-in User
        RealmResults<Media> userMediaList = realm.where(Media.class).equalTo("entriesUser.uuid", getUserUUID).findAll();

        // Adapter Initialization
        SearchAdapter searchAdapter = new SearchAdapter(this, userMediaList, true);
        recyclerSView.setAdapter(searchAdapter);
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
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }


    public void searchEntry(){
        RealmQuery<Media> mediaQuery = realm.where(Media.class).equalTo("entriesUser.uuid", getUserUUID);
        if(!searchInput.getText().toString().equals("")) {
            mediaQuery.contains("mediaTitle", searchInput.getText().toString(), Case.INSENSITIVE);
        }
        RealmResults<Media> mediaResults = mediaQuery.findAll();

        // Adapter Initialization
        SearchAdapter searchAdapter = new SearchAdapter(this, mediaResults, true);
        recyclerSView.setAdapter(searchAdapter);
    }
}