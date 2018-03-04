package com.nloops.myrecipe.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Holds Steps Data
 */

public class StepsModel implements Parcelable {

    private int mID;
    private String mShortDescription;
    private String mDescription;
    private String mVideoURL;
    private String mThumbnailURL;

    public StepsModel(int id, String shortDescription, String description,
                      String videoURL, String thumbnailURL) {
        this.mID = id;
        this.mShortDescription = shortDescription;
        this.mDescription = description;
        this.mVideoURL = videoURL;
        this.mThumbnailURL = thumbnailURL;
    }

    public int getID() {
        return mID;
    }

    public String getShortDescription() {
        return mShortDescription;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getVideoURL() {
        return mVideoURL;
    }

    public String getThumbnailURL() {
        return mThumbnailURL;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mID);
        dest.writeString(this.mShortDescription);
        dest.writeString(this.mDescription);
        dest.writeString(this.mVideoURL);
        dest.writeString(this.mThumbnailURL);
    }

    protected StepsModel(Parcel in) {
        this.mID = in.readInt();
        this.mShortDescription = in.readString();
        this.mDescription = in.readString();
        this.mVideoURL = in.readString();
        this.mThumbnailURL = in.readString();
    }

    public static final Parcelable.Creator<StepsModel> CREATOR = new Parcelable.Creator<StepsModel>() {
        @Override
        public StepsModel createFromParcel(Parcel source) {
            return new StepsModel(source);
        }

        @Override
        public StepsModel[] newArray(int size) {
            return new StepsModel[size];
        }
    };
}
