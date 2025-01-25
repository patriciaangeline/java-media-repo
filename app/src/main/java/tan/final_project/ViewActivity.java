package tan.final_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;

public class ViewActivity extends AppCompatActivity {

    RecyclerView recyclerVView;

    Realm realm;


    public void init() {
        recyclerVView = findViewById(R.id.recyclerVView);

        // Initialize Layout Manager
        LinearLayoutManager viewLayoutManager = new LinearLayoutManager(this);
        viewLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerVView.setLayoutManager(viewLayoutManager);

        // Realm Initialization
        realm = Realm.getDefaultInstance();
        Intent welcomeIn = getIntent();
        Bundle welcomeBundle = welcomeIn.getExtras();
        String getUserUUID = welcomeBundle.get("User UUID").toString();

        // Query all Media objects created by the logged-in User
        RealmResults<Media> userMediaList = realm.where(Media.class).equalTo("entriesUser.uuid", getUserUUID).findAll();

        // Adapter Initialization
        ViewAdapter viewAdapter = new ViewAdapter(this, userMediaList, true);
        recyclerVView.setAdapter(viewAdapter);
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
        setContentView(R.layout.activity_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }


    // RecyclerView Buttons
    public void editMediaEntry(Media m){
        int mediaID = m.getId();
        Intent editEntryIntent = new Intent(this, EditEntryActivity.class);
        editEntryIntent.putExtra("Media ID", mediaID);
        startActivity(editEntryIntent);
    }


    public void deleteMediaEntry(Media m) {
        // Confirm deletion of selected entry using AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete entry?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(m.isValid()) {
                    realm.beginTransaction();
                    m.deleteFromRealm();
                    realm.commitTransaction();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog deleteDialog = builder.create();
        deleteDialog.show();
    }
}