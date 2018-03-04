package com.nloops.myrecipe.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.nloops.myrecipe.data.RecipeModel;
import com.nloops.myrecipe.utils.NetworkUtils;

import java.util.List;

/**
 * This class will extended from AsyncLoader to give us more flexibility
 * more than AsyncTask for example it keeps data even if the Activity destroyed
 * or the phone get landscape or portrait mode.
 */

public class CatalogAsyncLoader extends AsyncTaskLoader<List<RecipeModel>> {


    private String mUrl;

    public CatalogAsyncLoader(Context context, String urlString) {
        super(context);
        this.mUrl = urlString;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<RecipeModel> loadInBackground() {
        if (mUrl == null || mUrl.equals("")) return null;
        return NetworkUtils.fireUpNetwork(mUrl);
    }
}
