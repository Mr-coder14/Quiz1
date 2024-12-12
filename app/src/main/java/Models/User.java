package Models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String name;
    private String email;
    private String userid;
    private int currentstrike;
    private int higheststrike;
    private long lastQuizDate;
    private String bio;
    private String profile;
    private int coin;
    private int messagecount;

    public User() {
    }

    public User(String name, String email, String userid, int coin, String bio,int currentstrike,int higheststrike,String profile) {
        this.name = name;
        this.email = email;
        this.userid = userid;
        this.coin = coin;
        this.higheststrike=higheststrike;
        this.currentstrike=currentstrike;
        this.profile=profile;
        this.bio = bio;
    }

    public User(String name, String userid) {
        this.name = name;
        this.userid = userid;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getCurrentstrike() {
        return currentstrike;
    }

    public void setCurrentstrike(int currentstrike) {
        this.currentstrike = currentstrike;
    }

    public int getHigheststrike() {
        return higheststrike;
    }

    public void setHigheststrike(int higheststrike) {
        this.higheststrike = higheststrike;
    }

    public long getLastQuizDate() {
        return lastQuizDate;
    }

    public void setLastQuizDate(long lastQuizDate) {
        this.lastQuizDate = lastQuizDate;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getMessagecount() {
        return messagecount;
    }

    public void setMessagecount(int messagecount) {
        this.messagecount = messagecount;
    }

    // Parcelable methods
    protected User(Parcel in) {
        name = in.readString();
        email = in.readString();
        userid = in.readString();
        coin = in.readInt();
        bio = in.readString();
        profile = in.readString();
        currentstrike = in.readInt();
        higheststrike = in.readInt();
        lastQuizDate = in.readLong();
        messagecount = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(userid);
        dest.writeInt(coin);
        dest.writeString(bio);
        dest.writeString(profile);
        dest.writeInt(currentstrike);
        dest.writeInt(higheststrike);
        dest.writeLong(lastQuizDate);
        dest.writeInt(messagecount);
    }
}
