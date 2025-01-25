package tan.final_project;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    EditText nameMInput;
    EditText passwordMInput;

    CheckBox rememberBox;
    Boolean rememberState;

    Button signinButton;
    Button registerButton;
    Button adminButton;
    Button clearButton;

    SharedPreferences loginCredentials;

    Realm realm;


    public void onDestroy() {
        super.onDestroy();

        if(!realm.isClosed()) {
            realm.close();
        }
    }


    public void init(){
        nameMInput = findViewById(R.id.nameMInput);
        passwordMInput = findViewById(R.id.passwordMInput);
        rememberBox = findViewById(R.id.rememberBox);


        signinButton = findViewById(R.id.signinButton);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goWelcome();
            }
        });


        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRegister();
            }
        });


        adminButton = findViewById(R.id.adminButton);
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAdmin();
            }
        });

        clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goClear();
            }
        });

        rememberBox = findViewById(R.id.rememberBox);
        rememberBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    rememberState = true;
                    rememberChecked();
                } else {
                    rememberState = false;
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        checkPermissions();
    }


    public void checkPermissions()
    {

        // REQUEST PERMISSIONS for Android 6+
        // THESE PERMISSIONS SHOULD MATCH THE ONES IN THE MANIFEST
        Dexter.withContext(this)
                .withPermissions(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA

                )

                .withListener(new BaseMultiplePermissionsListener()
                {
                    public void onPermissionsChecked(MultiplePermissionsReport report)
                    {
                        if (report.areAllPermissionsGranted())
                        {
                            // All permissions accepted proceed
                            init();
                            // If remember me is checked, display saved username and password when app is loaded
                            loginCredentials = getSharedPreferences("loginCredentials", MODE_PRIVATE);
                            Boolean getRememberState = loginCredentials.getBoolean("Remember State", false);
                            String getUUIDSP = loginCredentials.getString("uuid", null);


                            // REALM INITIALIZATION
                            realm = Realm.getDefaultInstance();

                            long realmCount = 0;
                            realmCount = realm.where(User.class).count();

                            // If Realm is empty, proceed as usual
                            if(realmCount==0) {

                            // If Realm is not empty and Remember me is true
                            } else {
                                User findUser = realm.where(User.class).equalTo("uuid", getUUIDSP).findFirst();
                                if(findUser!=null) {
                                    String findName = findUser.getUserName();
                                    String findPassword = findUser.getUserPassword();

                                    if(getRememberState) {
                                        rememberBox.setChecked(true);
                                        nameMInput.setText(findName);
                                        passwordMInput.setText(findPassword);
                                    }
                                }
                            }
                        }
                        else
                        {
                            // notify about permissions
                            toastRequirePermissions();
                        }
                    }
                })
                .check();
    }


    public void toastRequirePermissions() {
        Toast.makeText(this, "You must provide permissions for app to run", Toast.LENGTH_LONG).show();
        finish();
    }


    public void goWelcome() {
        String nameText = nameMInput.getText().toString();
        String passwordText = passwordMInput.getText().toString();

        // Search for User with the corresponding name entered
        User findUser = realm.where(User.class).equalTo("userName", nameText).findFirst();

        // If no User is found
        if(findUser==null) {
            Toast notfoundToast = Toast.makeText(this, "No User found", Toast.LENGTH_LONG);
            notfoundToast.show();

        // If a User is found
        } else if(findUser!=null) {
            // Retrieve password from Realm
            String findPassword = findUser.getUserPassword();

            // If the password entered does not match password in Realm
            if(!passwordText.equals(findPassword)) {
                Toast invalidToast = Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_LONG);
                invalidToast.show();

            // If password entered and Realm password match
            } else {
                String findUUID = findUser.getUuid();
                loginCredentials = getSharedPreferences("loginCredentials", MODE_PRIVATE);
                SharedPreferences.Editor uuidEditor = loginCredentials.edit();

                uuidEditor.putString("uuid", findUUID);
                uuidEditor.apply();

                Intent welcomeIntent = new Intent(this, WelcomeActivity.class);
                startActivity(welcomeIntent);
            }
        }
    }


    public void goRegister() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    public void goAdmin() {
        Intent adminIntent = new Intent(this, AdminActivity.class);
        startActivity(adminIntent);
    }


    public void goClear() {
        nameMInput.getText().clear();
        passwordMInput.getText().clear();
        rememberBox.setChecked(false);

        // Clears out SharedPreferences
        loginCredentials = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        SharedPreferences.Editor clearEditor = loginCredentials.edit();
        clearEditor.clear();
        clearEditor.apply();

        Toast clearToast = Toast.makeText(this, "Preferences cleared", Toast.LENGTH_LONG);
        clearToast.show();
    }


    public void rememberChecked() {
        // Saves state of "Remember Me" checkbox
        loginCredentials = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        SharedPreferences.Editor rememberEditor = loginCredentials.edit();
        rememberEditor.putBoolean("Remember State", rememberState);
        rememberEditor.apply();

    }
}