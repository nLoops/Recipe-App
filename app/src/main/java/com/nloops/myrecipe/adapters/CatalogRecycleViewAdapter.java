package com.nloops.myrecipe.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nloops.myrecipe.R;
import com.nloops.myrecipe.data.RecipeModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This Class will be the Recipe Catalog Activity Adapter That providing
 * Activity Recycleview with data
 */

public class CatalogRecycleViewAdapter extends
        RecyclerView.Adapter<CatalogRecycleViewAdapter.CatalogViewHolder> {


    /**
     * Declare Interface that handles on item click
     */
    public interface RecipeOnClickListener {
        void onClickRecipe(RecipeModel model);
    }

    // Arraylist of Data
    private ArrayList<RecipeModel> mRecipeArrayList;
    private RecipeOnClickListener mClickListener;
    private Context mContext;


    public CatalogRecycleViewAdapter(ArrayList<RecipeModel> recipeArraylist, RecipeOnClickListener listener, Context context) {
        this.mRecipeArrayList = recipeArraylist;
        this.mClickListener = listener;
        this.mContext = context;
    }

    @Override
    public CatalogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutID = R.layout.single_phone_item_layout;
        boolean shouldAttachedToParent = false;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutID, parent, shouldAttachedToParent);
        return new CatalogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CatalogViewHolder holder, int position) {
        String recipeName = mRecipeArrayList.get(position).getRecipeName();
        int drawableID = mRecipeArrayList.get(position).getDrawable(recipeName);
        String imageUrl = mRecipeArrayList.get(position).getImage();
        holder.mRecipeName.setText(recipeName);
        // we check if the API has existing URL first we will load it ,
        // instead we will load custom picture we add locally
        if (imageUrl.length() > 0) {
            Picasso.with(mContext).load(imageUrl).into(holder.mRecipeImage);
        } else {
            if (drawableID != -1) {
                holder.mRecipeImage.setImageResource(drawableID);
            }
        }


    }

    @Override
    public int getItemCount() {
        if (mRecipeArrayList == null) return 0;
        return mRecipeArrayList.size();
    }

    /**
     * Helper Method that set the adapter local arraylist to passing arraylist which has data
     *
     * @param arrayList
     */
    public void setArrayList(ArrayList<RecipeModel> arrayList) {
        this.mRecipeArrayList = arrayList;
        notifyDataSetChanged();
    }

    /**
     * Helper method that clear Adapter data.
     */
    public void clear() {
        if (mRecipeArrayList != null) {
            this.mRecipeArrayList = null;
            notifyDataSetChanged();
        }
    }

    class CatalogViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.iv_phone_cardview)
        public ImageView mRecipeImage;
        @BindView(R.id.tv_phone_cardview)
        public TextView mRecipeName;

        public CatalogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            Context context = itemView.getContext();
            AssetManager manager = context.getAssets();
            Typeface font = Typeface.createFromAsset(manager, "fonts/font_regular.ttf");
            mRecipeName.setTypeface(font);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            RecipeModel model = mRecipeArrayList.get(pos);
            mClickListener.onClickRecipe(model);
        }
    }
}
