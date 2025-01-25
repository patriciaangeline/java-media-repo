package tan.final_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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

public class ExploreOthersActivity extends AppCompatActivity {

    TextView nameEOText;
    TextView numberEOLikes;
    TextView numberEODislikes;

    EditText titleEOInput;

    EditText descriptionEOInput;

    ImageView imageEOView;
    ImageView cancelEOButton;

    AutoCompleteTextView mediaEOMenu;

    RatingBar ratingEOBar;

    Realm realm;


    public void init() {
        nameEOText = findViewById(R.id.nameEOText);
        numberEOLikes = findViewById(R.id.numberEOLikes);
        numberEODislikes = findViewById(R.id.numberEODislikes);

        titleEOInput = findViewById(R.id.titleEOInput);
        descriptionEOInput = findViewById(R.id.descriptionEOInput);

        imageEOView = findViewById(R.id.imageEOView);
        cancelEOButton = findViewById(R.id.cancelEOButton);
        cancelEOButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCancel();
            }
        });

        mediaEOMenu = findViewById(R.id.mediaEOMenu);

        ratingEOBar = findViewById(R.id.ratingEOBar);

        // Realm Initialization
        realm = Realm.getDefaultInstance();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_explore_others);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        // Retrieve data from ExploreActivity
        Intent exploreIn = getIntent();
        Bundle exploreBundle = exploreIn.getExtras();

        if(exploreBundle!=null) {
            String getUserName = exploreBundle.getString("User Name").toString();
            int getMediaID = exploreBundle.getInt("Media ID");

            Media findMedia = realm.where(Media.class).equalTo("id", getMediaID).findFirst();
            String getMediaImagePath = findMedia.getMediaImagePath();
            String getMediaType = findMedia.getMediaType().toString();
            float getMediaRating = findMedia.getUserRating();
            String getMediaLikes = String.valueOf(findMedia.getMediaLikes());
            String getMediaDislikes = String.valueOf(findMedia.getMediaDislikes());
            String getMediaTitle = findMedia.getMediaTitle().toString();
            String getMediaDescription = findMedia.getMediaDescription().toString();

            nameEOText.setText(getUserName + "'s post");
            mediaEOMenu.setText(getMediaType);
            ratingEOBar.setRating(getMediaRating);
            numberEOLikes.setText("Likes: " + getMediaLikes);
            numberEODislikes.setText("Dislikes: " + getMediaDislikes);
            titleEOInput.setText(getMediaTitle);
            descriptionEOInput.setText(getMediaDescription);

            // Retrieve and show the original image taken during logged-in User's activity in either Add or EditEntryActivity
            File getImageDir = getExternalCacheDir();
            if(getMediaImagePath!=null) {
                File editFile = new File(getImageDir, getMediaImagePath);

                if(editFile.exists()) {
                    refreshImageView(imageEOView, editFile);
                }
            }

        }
    }


    public void confirmCancel() {
        // Confirm action of going back to explore page using AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Go back to Explore page?");

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