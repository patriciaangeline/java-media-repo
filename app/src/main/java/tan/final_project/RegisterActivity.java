package tan.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import io.realm.Realm;

public class RegisterActivity extends AppCompatActivity {

    EditText nameRInput;
    EditText passwordRInput;
    EditText confirmRInput;

    Button saveRButton;
    Button cancelRButton;

    ImageView imageRView;

    String registerImagePath;

    Realm realm;

    public static int REQUEST_CODE_IMAGE_SCREEN = 0;


    public void init(){
        nameRInput = findViewById(R.id.nameRInput);
        passwordRInput = findViewById(R.id.passwordRInput);
        confirmRInput = findViewById(R.id.confirmRInput);


        saveRButton = findViewById(R.id.saveRButton);
        saveRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });


        cancelRButton = findViewById(R.id.cancelRButton);
        cancelRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        imageRView = findViewById(R.id.imageRView);
        imageRView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goImageCapture();
            }
        });


        // Realm Initialization
        realm = Realm.getDefaultInstance();
    }


    public void onDestroy(){
        super.onDestroy();

        if(!realm.isClosed()){
            realm.close();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }


    public void saveUser() {
        String nameRText = nameRInput.getText().toString();
        String passwordRText = passwordRInput.getText().toString().trim();
        String confirmRText = confirmRInput.getText().toString().trim();

        // If user name field is empty
        if (nameRText.isEmpty()) {
            Toast blankToast = Toast.makeText(this, "Name must not be blank", Toast.LENGTH_LONG);
            blankToast.show();

        // If password or confirm password are empty
        } else if (passwordRText.matches("") || confirmRText.matches("")) {
            Toast blankPassToast = Toast.makeText(this, "Password or Confirm Password must not be blank", Toast.LENGTH_LONG);
            blankPassToast.show();

        // If password and confirm password do not match
        } else if (!(passwordRText.equals(confirmRText))) {
            Toast mismatchToast = Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_LONG);
            mismatchToast.show();

        // If user name is not empty and password matches confirm password
        } else {
            // Search for User with the corresponding name entered
            User findName = realm.where(User.class).equalTo("userName", nameRText).findFirst();

            // If there is a previously saved User object with the listed name
            if(findName!=null) {
                Toast existToast = Toast.makeText(this, "User already exists", Toast.LENGTH_LONG);
                existToast.show();

            // If there is no previously saved User object with the listed name
            } else {
                // Create new User
                User newUser = new User();
                newUser.setUserName(nameRText);
                newUser.setUserPassword(passwordRText);
                newUser.setUuid(UUID.randomUUID().toString());
                newUser.setUserImagePath(registerImagePath);

                long realmCount = 0;

                try {
                    // Save created User to Realm
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(newUser);
                    realm.commitTransaction();

                    // Determine number of Users created
                    realmCount = realm.where(User.class).count();

                    Toast newuserToast = Toast.makeText(this, "New User saved. Total: " + realmCount, Toast.LENGTH_LONG);
                    newuserToast.show();
                    finish();

                } catch(Exception e) {
                    Toast errorToast = Toast.makeText(this, "Error saving new user. Try again.", Toast.LENGTH_LONG);
                    errorToast.show();
                    e.printStackTrace();
                }
            }
        }
    }


    public void goImageCapture() {
        Intent imageIntent = new Intent(this, ImageActivity.class);
        startActivityForResult(imageIntent, REQUEST_CODE_IMAGE_SCREEN);
    }


    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        if(requestCode==REQUEST_CODE_IMAGE_SCREEN) {
            if(responseCode==ImageActivity.RESULT_CODE_IMAGE_TAKEN) {
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                if(jpeg!=null) {
                    registerImagePath = System.currentTimeMillis()+".jpeg"; //Set imagePath
                    try {
                        File savedImage = saveFile(jpeg, registerImagePath);
                        if(savedImage.exists()) {
                            refreshImageView(imageRView, savedImage);
                        } else {
                            imageRView.setImageResource(R.mipmap.ic_launcher);
                        }

                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }


    private File saveFile(byte[] jpeg, String name) throws IOException
    {
        // this is the root directory for the images
        File getImageDir = getExternalCacheDir();

        // just a sample, normally you have a diff image name each time
        File savedImage = new File(getImageDir, name);

        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        return savedImage;
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