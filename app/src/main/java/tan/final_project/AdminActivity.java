package tan.final_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

public class AdminActivity extends AppCompatActivity {
    RecyclerView recyclerAdminView;

    Button clearAdminButton;
    Button backAdminButton;

    Realm realm;


    public void init() {
        recyclerAdminView = findViewById(R.id.recyclerAdminView);

        clearAdminButton = findViewById(R.id.clearAdminButton);
        clearAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllUsers();
            }
        });


        backAdminButton = findViewById(R.id.backAdminButton);
        backAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Initialize RecyclerView
        LinearLayoutManager adminLayoutManager = new LinearLayoutManager(this);
        adminLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerAdminView.setLayoutManager(adminLayoutManager);

        // Realm Initialization
        realm = Realm.getDefaultInstance();
        RealmResults<User> userList = realm.where(User.class).findAll();

        // Initialize Adapter
        AdminAdapter adminAdapter = new AdminAdapter(this, userList, true);
        recyclerAdminView.setAdapter(adminAdapter);
    }

    public void onDestroy() {

        super.onDestroy();
        if(!realm.isClosed()) {
            realm.close();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    // RecyclerView Buttons
    public void editUser(User u){
        String userUUID = u.getUuid().toString();
        Intent editIntent = new Intent(this, EditUserActivity.class);
        editIntent.putExtra("User UUID", userUUID);
        startActivity(editIntent);
    }


    public void deleteUser(User u){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete user?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(u.isValid()) {
                    realm.beginTransaction();
                    u.getUserEntries().deleteAllFromRealm();
                    u.deleteFromRealm();
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


    // AdminActivity Buttons
    public void clearAllUsers(){

        // Confirm action of clearing all users using AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete all existing users?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realm.beginTransaction();
                realm.deleteAll();
                realm.commitTransaction();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog deleteAllDialog = builder.create();
        deleteAllDialog.show();
    }
}