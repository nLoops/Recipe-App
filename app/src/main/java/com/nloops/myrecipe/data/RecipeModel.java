package com.nloops.myrecipe.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.nloops.myrecipe.R;

import java.util.List;

/**
 * Holds Recipe Data.
 */

public class RecipeModel implements Parcelable {

    private int mRecID;
    private String mName;
    private List<IngredientsModel> mIngredients;
    private List<StepsModel> mSteps;
    private int mServings;
    private String mImage;

    public RecipeModel(int recID, String name,
                       List<IngredientsModel> ingredients,
                       List<StepsModel> steps, int servings, String image) {
        this.mRecID = recID;
        this.mName = name;
        this.mIngredients = ingredients;
        this.mSteps = steps;
        this.mServings = servings;
        this.mImage = image;
    }


    public int getRecID() {
        return mRecID;
    }

    public String getRecipeName() {
        return mName;
    }

    public List<IngredientsModel> getIngredients() {
        return mIngredients;
    }

    public List<StepsModel> getSteps() {
        return mSteps;
    }

    public int getServings() {
        return mServings;
    }

    public String getImage() {
        return mImage;
    }

    public int getDrawable(String name) {
        int drawable = -1;
        // check first if the current model has image
        if (getImage().length() <= 0) {

            if (name.equals("Nutella Pie")) {
                drawable = R.drawable.nutella_pie;
            } else if (name.equals("Brownies")) {
                drawable = R.drawable.brownies;
            } else if (name.equals("Yellow Cake")) {
                drawable = R.drawable.yellow_cake;
            } else if (name.equals("Cheesecake")) {
                drawable = R.drawable.cheesecake;
            }
        }
        return drawable;

    }

    @Override
    public String toString() {
        return "RecipeModel{" +
                "mRecID=" + mRecID +
                ", mName='" + mName + '\'' +
                ", mIngredients=" + mIngredients +
                ", mSteps=" + mSteps +
                ", mServings=" + mServings +
                ", mImage='" + mImage + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRecID);
        dest.writeString(this.mName);
        dest.writeTypedList(this.mIngredients);
        dest.writeTypedList(this.mSteps);
        dest.writeInt(this.mServings);
        dest.writeString(this.mImage);
    }

    protected RecipeModel(Parcel in) {
        this.mRecID = in.readInt();
        this.mName = in.readString();
        this.mIngredients = in.createTypedArrayList(IngredientsModel.CREATOR);
        this.mSteps = in.createTypedArrayList(StepsModel.CREATOR);
        this.mServings = in.readInt();
        this.mImage = in.readString();
    }

    public static final Parcelable.Creator<RecipeModel> CREATOR = new Parcelable.Creator<RecipeModel>() {
        @Override
        public RecipeModel createFromParcel(Parcel source) {
            return new RecipeModel(source);
        }

        @Override
        public RecipeModel[] newArray(int size) {
            return new RecipeModel[size];
        }
    };
}
