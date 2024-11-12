package Models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String name;
    private String email;
    private String userid;
    private String phno;
    private int messagecount;

    public int getMessagecount() {
        return messagecount;
    }

    public void setMessagecount(int messagecount) {
        this.messagecount = messagecount;
    }

    // Default constructor
    public User() {}

    // Constructor with parameters
    public User(String name, String email, String phno, String userid) {
        this.name = name;
        this.email = email;
        this.phno = phno;
        this.userid = userid;
    }

    public User(String phno, String name, String userid) {
        this.name = name;
        this.phno = phno;
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





    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    // Parcelable methods
    protected User(Parcel in) {
        name = in.readString();
        email = in.readString();
        userid = in.readString();
        phno = in.readString();
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
        dest.writeString(phno);
    }
}