package tan.final_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

import io.realm.Realm;

public class EditEntryActivity extends AppCompatActivity {

    EditText titleEInput;
    EditText descriptionEInput;

    TextView numberELikes;
    TextView numberEDislikes;


    ImageView imageEView;

    RatingBar ratingEBar;

    Button saveEButton;
    ImageView cancelEButton;

    String editEntryImagePath;
    float mediaEditRating;


    String[] mediaEditItems = {
            "Book",
            "Movie",
            "Painting",
            "Poem",
            "Poster",
    };

    AutoCompleteTextView mediaEditMenu;

    ArrayAdapter<String> adapterEditItems;

    Realm realm;

    Media mediaBuffer;

    String editMediaType;

    int getMediaID;
    public static int REQUEST_CODE_IMAGE_SCREEN = 0;


    public void init() {
        titleEInput = findViewById(R.id.titleEInput);
        descriptionEInput = findViewById(R.id.descriptionEInput);

        numberELikes = findViewById(R.id.numberELikes);
        numberEDislikes = findViewById(R.id.numberEDislikes);

        imageEView = findViewById(R.id.imageEView);
        imageEView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goImageCapture();
            }
        });


        saveEButton = findViewById(R.id.saveEButton);
        saveEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditedEntry();
            }
        });


        cancelEButton = findViewById(R.id.cancelEButton);
        cancelEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEdits();
            }
        });

        ratingEBar = findViewById(R.id.ratingEBar);
        ratingEBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mediaEditRating = ratingEBar.getRating();
            }
        });

        // Functionality for Dropdown Menu
        mediaEditMenu = findViewById(R.id.mediaEditMenu);
        adapterEditItems = new ArrayAdapter<String>(this, R.layout.media_items,mediaEditItems);
        mediaEditMenu.setAdapter(adapterEditItems);
        mediaEditMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editMediaType = adapterEditItems.getItem(position).toString();

            }

        });

        // Realm Initialization
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
        setContentView(R.layout.activity_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        // Retrieve data from ViewActivity
        Intent viewIn = getIntent();
        Bundle viewBundle = viewIn.getExtras();

        if(viewBundle!=null) {
            getMediaID = viewBundle.getInt("Media ID");
            Media findMediaEntry = realm.where(Media.class).equalTo("id", getMediaID).findFirst();
            mediaBuffer = realm.copyFromRealm(findMediaEntry);

            String getMediaType = findMediaEntry.getMediaType().toString();
            float getMediaRating = findMediaEntry.getUserRating();
            String getLikes = String.valueOf(findMediaEntry.getMediaLikes());
            String getDislikes = String.valueOf(findMediaEntry.getMediaDislikes());
            String getMediaTitle = findMediaEntry.getMediaTitle().toString();
            String getMediaDescription = findMediaEntry.getMediaDescription().toString();
            String getOriginalMediaPath = findMediaEntry.getMediaImagePath();

            mediaEditMenu.setText(getMediaType);
            ratingEBar.setRating(getMediaRating);
            numberELikes.setText("Likes: " + getLikes);
            numberEDislikes.setText("Dislikes: " + getDislikes);
            titleEInput.setText(getMediaTitle);
            descriptionEInput.setText(getMediaDescription);

            // Retrieve and show the original image taken during AddActivity
            File getImageDir = getExternalCacheDir();
            if(getOriginalMediaPath!=null) {
                File editFile = new File(getImageDir, getOriginalMediaPath);
                if(editFile.exists()) {
                    refreshImageView(imageEView, editFile);
                }
            }
        }

    }


    public void saveEditedEntry(){
        String newMediaType = mediaEditMenu.getText().toString();
        float newMediaRating = ratingEBar.getRating();
        String newMediaTitle = titleEInput.getText().toString();
        String newMediaDescription = descriptionEInput.getText().toString();

        // Used buffer copy
        mediaBuffer.setMediaType(newMediaType);
        mediaBuffer.setUserRating(newMediaRating);
        mediaBuffer.setMediaTitle(newMediaTitle);
        mediaBuffer.setMediaDescription(newMediaDescription);
        mediaBuffer.setMediaImagePath(editEntryImagePath);

        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(mediaBuffer);
            realm.commitTransaction();

            Toast editedToast = Toast.makeText(this, "Successfully edited entry", Toast.LENGTH_LONG);
            editedToast.show();

            finish();

        } catch(Exception e) {
            Toast exceptionToast = Toast.makeText(this, "Error editing. Please try again.", Toast.LENGTH_LONG);
            exceptionToast.show();
        }
    }


    public void cancelEdits(){
        // Confirm action of not saving edits using AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard edits?");

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


    public void goImageCapture() {
        Intent imageIntent = new Intent(this, ImageActivity.class);
        startActivityForResult(imageIntent, REQUEST_CODE_IMAGE_SCREEN);
    }


    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode == REQUEST_CODE_IMAGE_SCREEN) {
            if (responseCode == ImageActivity.RESULT_CODE_IMAGE_TAKEN) {
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                if(jpeg!=null) {
                    editEntryImagePath = System.currentTimeMillis()+".jpeg";
                    try {
                        File savedImage = saveFile(jpeg, editEntryImagePath);
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