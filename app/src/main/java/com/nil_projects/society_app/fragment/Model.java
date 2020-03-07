package com.nil_projects.society_app.fragment;

public class Model {
    private String Wing;
    private String UserID;
    private String profile_Pic_url;
    private String UserName;
    private String UserEmail;
    private String city;
    private String societyname;
    private String FlatNo;
    private String UserRelation;
    private String userAuth;
    private String MobileNumber;

    public Model() {
    }

    public Model(String Wing, String UserID, String profile_Pic_url, String UserName, String UserEmail, String city, String societyname, String FlatNo, String UserRelation, String userAuth,String MobileNumber) {
        this.Wing = Wing;
        this.MobileNumber = MobileNumber;
        this.UserID = UserID;
        this.profile_Pic_url = profile_Pic_url;
        this.UserName = UserName;
        this.UserEmail = UserEmail;
        this.city = city;
        this.societyname = societyname;
        this.FlatNo = FlatNo;
        this.UserRelation = UserRelation;
        this.userAuth = userAuth;
    }

    public String getUserID() {
        return UserID;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public String getProfile_Pic_url() {
        return profile_Pic_url;
    }

    public String getUserName() {
        return UserName;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public String getCity() {
        return city;
    }

    public String getSocietyname() {
        return societyname;
    }

    public String getFlatNo() {
        return FlatNo;
    }

    public String getUserRelation() {
        return UserRelation;
    }

    public String getUserAuth() {
        return userAuth;
    }
    public String getWing(){
        return Wing;
    }

}
