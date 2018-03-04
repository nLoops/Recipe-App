package com.nloops.myrecipe.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nloops.myrecipe.R;
import com.nloops.myrecipe.data.IngredientsModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This Class will be the Ingredients Activity Adapter That providing
 * Activity Recycleview with data
 */

public class IngredientsRecycleViewAdapter
        extends RecyclerView.Adapter<IngredientsRecycleViewAdapter.IngredientsViewHolder> {

    private ArrayList<IngredientsModel> mIngredients;

    public IngredientsRecycleViewAdapter(ArrayList<IngredientsModel> data) {
        this.mIngredients = data;
    }

    @Override
    public IngredientsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutID = R.layout.single_phone_ingredients_layout;
        Context context = parent.getContext();
        boolean shouldAttachedToParent = false;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutID, parent, shouldAttachedToParent);
        return new IngredientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientsViewHolder holder, int position) {
        String ingredient = mIngredients.get(position).getQuantity() + " - "
                + mIngredients.get(position).getMeasure()
                + " \n" + mIngredients.get(position).getIngredient();
        holder.mIngredientTextView.setText(ingredient);

    }

    @Override
    public int getItemCount() {
        if (mIngredients == null) return 0;
        return mIngredients.size();
    }

    public void setIngredientsData(ArrayList<IngredientsModel> data) {
        if (data != null) {
            mIngredients = data;
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if (mIngredients != null) {
            mIngredients = null;
            notifyDataSetChanged();
        }
    }

    class IngredientsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_single_ingredients)
        TextView mIngredientTextView;

        public IngredientsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
