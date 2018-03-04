package com.nloops.myrecipe.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Holds Ingredients Data
 */

public class IngredientsModel implements Parcelable {

    private double mQuantity;
    private String mMeasure;
    private String mIngredient;

    public IngredientsModel(double quantity, String measure, String ingredient) {
        this.mQuantity = quantity;
        this.mMeasure = measure;
        this.mIngredient = ingredient;
    }

    public IngredientsModel() {
    }

    public double getQuantity() {
        return mQuantity;
    }

    public String getMeasure() {
        return mMeasure;
    }

    public String getIngredient() {
        return mIngredient;
    }

    @Override
    public String toString() {
        return "IngredientsModel{" +
                "mQuantity=" + mQuantity +
                ", mMeasure='" + mMeasure + '\'' +
                ", mIngredient='" + mIngredient + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.mQuantity);
        dest.writeString(this.mMeasure);
        dest.writeString(this.mIngredient);
    }

    protected IngredientsModel(Parcel in) {
        this.mQuantity = in.readDouble();
        this.mMeasure = in.readString();
        this.mIngredient = in.readString();
    }

    public static final Parcelable.Creator<IngredientsModel>
            CREATOR = new Parcelable.Creator<IngredientsModel>() {
        @Override
        public IngredientsModel createFromParcel(Parcel source) {
            return new IngredientsModel(source);
        }

        @Override
        public IngredientsModel[] newArray(int size) {
            return new IngredientsModel[size];
        }
    };
}
