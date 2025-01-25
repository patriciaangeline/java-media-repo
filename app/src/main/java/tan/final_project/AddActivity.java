package tan.final_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Random;

import io.realm.Realm;

public class AddActivity extends AppCompatActivity {

    EditText titleAInput;
    EditText descriptionAInput;

    TextView numberALikes;

    ImageView imageEView;
    ImageButton cancelAButton;

    RatingBar ratingABar;

    Button saveAButton;

    String[] mediaItems = {
            "Book",
            "Movie",
            "Painting",
            "Poem",
            "Poster",
    };

    AutoCompleteTextView autoCompleteTextView;

    Realm realm;

    ArrayAdapter<String> adapterItems;

    String mediaImagePath;

    String addMediaItem;
    float mediaRating;
    String getUserUUID;

    Random random;

    public static int REQUEST_CODE_IMAGE_SCREEN = 0;


    public void init() {

        titleAInput = findViewById(R.id.titleEOInput);
        descriptionAInput = findViewById(R.id.descriptionEOInput);

        numberALikes = findViewById(R.id.numberALikes);

        imageEView = findViewById(R.id.imageEView);
        imageEView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goImageCapture();
            }
        });

        cancelAButton = findViewById(R.id.cancelAButton);
        cancelAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCancel();
            }
        });

        saveAButton = findViewById(R.id.saveAButton);
        saveAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEntry();
            }
        });

        ratingABar = findViewById(R.id.ratingABar);
        ratingABar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mediaRating = ratingABar.getRating();

            }
        });

        // Functionality for Dropdown Menu
        autoCompleteTextView = findViewById(R.id.mediaMenu);
        adapterItems = new ArrayAdapter<String>(this, R.layout.media_items,mediaItems);
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addMediaItem = adapterItems.getItem(position).toString();

            }
        });


        // Realm Initialization
        realm = Realm.getDefaultInstance();

        // Random Class Initialization for Media ID
        random = new Random();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        // Retrieve data from WelcomeActivity
        Intent welcomeIn = getIntent();
        Bundle welcomeBundle = welcomeIn.getExtras();
        if(welcomeBundle!=null){
            getUserUUID = welcomeBundle.get("User UUID").toString();
        }
    }


    public void saveEntry(){
        String titleAText = titleAInput.getText().toString().trim();
        String descriptionAText = descriptionAInput.getText().toString().trim();

        // If a media type was not chosen
        if(addMediaItem==null) {
            Toast typeToast = Toast.makeText(this, "Media Type has not been specified", Toast.LENGTH_LONG);
            typeToast.show();

        // If the entry was not given a rating
        } else if(mediaRating==0) {
            Toast ratingToast = Toast.makeText(this, "Entry does not have a rating", Toast.LENGTH_LONG);
            ratingToast.show();

        // If title and description are both empty
        } else if(titleAText.matches("") || descriptionAText.matches("")) {
            Toast emptyToast = Toast.makeText(this, "Title or Description must not be blank", Toast.LENGTH_LONG);
            emptyToast.show();


        } else {
            // Search for Media Entry with corresponding title entered within the User's RealmList of Media
            Media findExistingMedia = realm.where(Media.class).equalTo("mediaTitle", titleAText).equalTo("entriesUser.uuid", getUserUUID).findFirst();

            // If there is a previously saved Media object with the listed name
            if(findExistingMedia!=null) {
                Toast existToast = Toast.makeText(this, "Media entry already exists", Toast.LENGTH_LONG);
                existToast.show();

            } else {
                // Create a new Media Entry
                Media newMedia = new Media();
                newMedia.setId(random.nextInt(500)); // Set a random integer as Media ID
                newMedia.setMediaTitle(titleAText);
                newMedia.setMediaDescription(descriptionAText);
                newMedia.setMediaType(addMediaItem);
                newMedia.setUserRating(mediaRating);
                newMedia.setMediaLikes(0);
                newMedia.setMediaDislikes(0);
                newMedia.setMediaImagePath(mediaImagePath);


                long realmEntryCount = 0;

                try {
                    User currentUser = realm.where(User.class).equalTo("uuid", getUserUUID).findFirst();

                    // Save created Entry to Realm
                    realm.beginTransaction();
                    newMedia = realm.copyToRealmOrUpdate(newMedia);
                    currentUser.getUserEntries().add(newMedia); // Add new media entry to the User's RealmList of Media
                    realm.commitTransaction();

                    realmEntryCount = realm.where(Media.class).equalTo("entriesUser.uuid", getUserUUID).count();
                    Toast newEntryToast = Toast.makeText(this, "New entry saved. Total: " + realmEntryCount, Toast.LENGTH_LONG);
                    newEntryToast.show();

                    finish();

                } catch(Exception e){
                    Toast errorToast = Toast.makeText(this, "Error saving new entry. Try again.", Toast.LENGTH_LONG);
                    errorToast.show();

                }
            }
        }
    }


    public void confirmCancel(){

        // Confirm deletion of unsaved entry using AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete unsaved entry?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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


    public void goImageCapture(){
        Intent imageIntent = new Intent(this, ImageActivity.class);
        startActivityForResult(imageIntent, REQUEST_CODE_IMAGE_SCREEN);
    }


    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        if(requestCode==REQUEST_CODE_IMAGE_SCREEN) {
            if(responseCode==ImageActivity.RESULT_CODE_IMAGE_TAKEN) {
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                if(jpeg!=null) {
                    mediaImagePath = System.currentTimeMillis()+".jpeg"; //Set imagePath
                    try {
                        File savedImage = saveFile(jpeg, mediaImagePath);
                        if(savedImage.exists()) {
                            refreshImageView(imageEView, savedImage);
                        } else {
                            imageEView.setImageResource(R.mipmap.ic_launcher);
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