package com.nloops.myrecipe.widgets;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.nloops.myrecipe.DetailActivity;
import com.nloops.myrecipe.R;
import com.nloops.myrecipe.data.IngredientsModel;

import java.util.ArrayList;

/**
 * Act like ListView Data adapter
 */

public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private ArrayList<IngredientsModel> mIngredientsModel;

    public ListRemoteViewsFactory(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        mIngredientsModel = DetailActivity.mIngredientsList;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (null == mIngredientsModel) return 0;
        return mIngredientsModel.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_view_item);
        remoteViews.setTextViewText(R.id.tv_widget_quantity, String.valueOf(mIngredientsModel.get(i).getQuantity()));
        remoteViews.setTextViewText(R.id.tv_widget_unit, mIngredientsModel.get(i).getMeasure());
        remoteViews.setTextViewText(R.id.tv_widget_ingredients, mIngredientsModel.get(i).getIngredient());
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
