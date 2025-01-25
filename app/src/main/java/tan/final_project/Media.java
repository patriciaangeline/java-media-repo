package tan.final_project;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

public class Media extends RealmObject {
    @PrimaryKey
    private int id;

    private String mediaImagePath;
    private String mediaType;
    private float userRating;

    private String mediaTitle;

    private String mediaDescription;

    private int mediaLikes;
    private int mediaDislikes;

    // LinkingObject to query parent attributes
    @LinkingObjects("userEntries")
    private final RealmResults<User> entriesUser = null;


    public Media(){

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMediaImagePath() {
        return mediaImagePath;
    }

    public void setMediaImagePath(String mediaImagePath) {
        this.mediaImagePath = mediaImagePath;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }

    public String getMediaTitle() {
        return mediaTitle;
    }

    public void setMediaTitle(String mediaTitle) {
        this.mediaTitle = mediaTitle;
    }

    public String getMediaDescription() {
        return mediaDescription;
    }

    public void setMediaDescription(String mediaDescription) {
        this.mediaDescription = mediaDescription;
    }

    public int getMediaLikes() {
        return mediaLikes;
    }

    public void setMediaLikes(int mediaLikes) {
        this.mediaLikes = mediaLikes;
    }

    public int getMediaDislikes() {
        return mediaDislikes;
    }

    public void setMediaDislikes(int mediaDislikes) {
        this.mediaDislikes = mediaDislikes;
    }


    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", mediaImagePath='" + mediaImagePath + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", userRating=" + userRating +
                ", mediaTitle='" + mediaTitle + '\'' +
                ", mediaDescription='" + mediaDescription + '\'' +
                ", mediaLikes=" + mediaLikes +
                ", mediaDislikes=" + mediaDislikes +
                '}';
    }
}

