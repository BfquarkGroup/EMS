package com.oufar.ems.Upload;

public class UploadProfilePicture {

    private String mName;
    private String mImageUrl;

    public UploadProfilePicture(){
        // empty constructor needed
    }

    public UploadProfilePicture(String name, String imageUrl){

        if (name.trim().equals("")){
            name = "No Name";
        }

        mName = name;
        mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String Name) {
        mName = Name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
        mImageUrl = ImageUrl;
    }
}
