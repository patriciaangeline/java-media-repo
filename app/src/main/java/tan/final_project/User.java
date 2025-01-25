package tan.final_project;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {
    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();
    private String userName;
    private String userPassword;
    private String userImagePath;

    private RealmList<Media> userEntries;


    public User() {}


    public User(String uuid, String userName, String userPassword, String userImagePath, RealmList<Media> userEntries) {
        this.uuid = uuid;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userImagePath = userImagePath;
        this.userEntries = userEntries;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserImagePath() {
        return userImagePath;
    }

    public void setUserImagePath(String userImagePath) {
        this.userImagePath = userImagePath;
    }

    public RealmList<Media> getUserEntries() {
        return userEntries;
    }

    public void setUserEntries(RealmList<Media> userEntries) {
        this.userEntries = userEntries;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userImagePath='" + userImagePath + '\'' +
                '}';
    }
}
