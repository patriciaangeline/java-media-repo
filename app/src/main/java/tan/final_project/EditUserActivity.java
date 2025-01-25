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

import io.realm.Realm;

public class EditUserActivity extends AppCompatActivity {

    EditText nameEInput;
    EditText passwordEInput;
    EditText confirmEInput;

    ImageView imageEUView;

    Button saveEUButton;
    Button cancelEUButton;

    Realm realm;

    User buffer;

    String editUserImagePath;

    public static int REQUEST_CODE_IMAGE_SCREEN = 0;


    public void init(){
        nameEInput = findViewById(R.id.nameEInput);
        passwordEInput = findViewById(R.id.passwordEInput);
        confirmEInput = findViewById(R.id.confirmEInput);

        imageEUView = findViewById(R.id.imageEUView);
        imageEUView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goImageCapture();
            }
        });

        saveEUButton = findViewById(R.id.saveEUButton);
        saveEUButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditedData();
            }
        });

        cancelEUButton = findViewById(R.id.cancelEUButton);
        cancelEUButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        realm = Realm.getDefaultInstance();
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
        setContentView(R.layout.activity_edit_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        // Retrieve data from AdminActivity
        Intent adminIn = getIntent();
        Bundle adminBundle = adminIn.getExtras();

        if(adminBundle!=null) {
            String getUserUUID = adminBundle.getString("User UUID").toString();
            User findUser = realm.where(User.class).equalTo("uuid", getUserUUID).findFirst();
            buffer = realm.copyFromRealm(findUser);
            String getOriginalName = findUser.getUserName().toString();
            String getOriginalPassword = findUser.getUserPassword().toString();
            String getOriginalPath = findUser.getUserImagePath();

            // Place saved username and password in respective EditTexts
            nameEInput.setText(getOriginalName);
            passwordEInput.setText(getOriginalPassword);
            confirmEInput.setText(getOriginalPassword);

            // Retrieve and show the original image taken during RegisterActivity
            File getImageDir = getExternalCacheDir();
            if(getOriginalPath!=null) {
                File editFile = new File(getImageDir, getOriginalPath);

                if(editFile.exists()) {
                    refreshImageView(imageEUView, editFile);
                }
            }
        }
    }


    public void saveEditedData() {
        String newName = nameEInput.getText().toString();
        String newPassword = passwordEInput.getText().toString();

        // Use buffer copy
        buffer.setUserName(newName);
        buffer.setUserPassword(newPassword);
        buffer.setUserImagePath(editUserImagePath);

        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(buffer);
            realm.commitTransaction();

            Toast editedToast = Toast.makeText(this, "Edited", Toast.LENGTH_LONG);
            editedToast.show();

        } catch(Exception e) {
            Toast exceptionToast = Toast.makeText(this, "Error editing. Please try again.", Toast.LENGTH_LONG);
            exceptionToast.show();
        }

        finish();
    }


    public void goImageCapture(){
        Intent imageIntent = new Intent(this, ImageActivity.class);
        startActivityForResult(imageIntent, REQUEST_CODE_IMAGE_SCREEN);
    }


    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode == REQUEST_CODE_IMAGE_SCREEN) {
            if (responseCode == ImageActivity.RESULT_CODE_IMAGE_TAKEN) {
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                if(jpeg!=null) {
                    editUserImagePath = System.currentTimeMillis()+".jpeg";
                    try {
                        File savedImage = saveFile(jpeg, editUserImagePath);
                        if(savedImage.exists()) {
                            refreshImageView(imageEUView, savedImage);
                        } else {
                            imageEUView.setImageResource(R.mipmap.ic_launcher);
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