package com.example.swipingcards.activities;

import java.util.ArrayList;

public class User {
    public String userId;
    public String userName;
    public String userEmail;
    public String userGender;
    public String userPreferredGender;
    public String dateOfBirth;
    public ArrayList<UserImage> userImages;


    public User(String userId, String userName, String userEmail,
                String userGender, String userPreferredGender, String age, ArrayList<UserImage> userImages) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userGender = userGender;
        this.userPreferredGender = userPreferredGender;
        this.dateOfBirth = age;
        this.userImages = userImages;



    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserPreferredGender() {
        return userPreferredGender;
    }

    public void setUserPreferredGender(String userPreferredGender) {
        this.userPreferredGender = userPreferredGender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public User(){

    }
}
