package com.nloops.myrecipe.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nloops.myrecipe.R;
import com.nloops.myrecipe.data.StepsModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This Class will be the Recipe Detail Activity Adapter That providing
 * Activity Recycleview with data
 */

public class DetailRecycleViewAdapter extends
        RecyclerView.Adapter<DetailRecycleViewAdapter.DetailViewHolder> {

    public interface DetailClickListener {
        void onDetailClick(StepsModel model);
    }

    private ArrayList<StepsModel> mModelsArrayList;
    private DetailClickListener mClickListener;

    public DetailRecycleViewAdapter(ArrayList<StepsModel> data, DetailClickListener listener) {
        this.mModelsArrayList = data;
        this.mClickListener = listener;
    }

    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutID = R.layout.single_phone_detail_item_layout;
        Context context = parent.getContext();
        boolean shouldAttachedToParent = false;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutID, parent, shouldAttachedToParent);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailViewHolder holder, final int position) {
        String stepName = mModelsArrayList.get(position).getShortDescription();
        holder.mStepButton.setText(stepName);
        holder.mStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onDetailClick(mModelsArrayList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mModelsArrayList == null) return 0;
        return mModelsArrayList.size();
    }

    /**
     * Helper Method that return position of specific model.
     *
     * @param model
     */
    public int getPosition(StepsModel model) {
        return mModelsArrayList.indexOf(model);
    }

    class DetailViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.single_detail_btn_layout)
        Button mStepButton;

        public DetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
