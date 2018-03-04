package com.nloops.myrecipe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nloops.myrecipe.adapters.CatalogRecycleViewAdapter;
import com.nloops.myrecipe.data.IngredientsModel;
import com.nloops.myrecipe.data.RecipeModel;
import com.nloops.myrecipe.data.StepsModel;
import com.nloops.myrecipe.loaders.CatalogAsyncLoader;
import com.nloops.myrecipe.utils.NetworkUtils;
import com.nloops.myrecipe.utils.RecipeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeCatalog extends AppCompatActivity implements
        CatalogRecycleViewAdapter.RecipeOnClickListener,
        LoaderManager.LoaderCallbacks<List<RecipeModel>> {

    // find the layout views.
    @BindView(R.id.rv_recipe_catalog)
    RecyclerView mRecycleView;
    @BindView(R.id.tv_catalog_empty_view)
    TextView mEmptyView;
    @BindView(R.id.catalog_progress_bar)
    ProgressBar mProgressBar;

    Unbinder unbinder;

    // Declare object of our Adapter.
    private CatalogRecycleViewAdapter mAdapter;
    // Unique id for our loaderManager
    private static final int LOADER_ID = 22;
    private static final String RECYCLE_SCROLL_STATE = "recycle_state";
    public static final String EXTRAS_STEPS_ARRAYLIST = "com.nloops.myrecipe.EXTRAS.steps";
    public static final String EXTRAS_INGREDIENTS_ARRAYLIST = "com.nloops.myrecipe.EXTRAS.ingredients";
    public static final String EXTRAS_RECIPE_NAME = "com.nloops.EXTRAS.recipe.name";
    private int mScrollState;
    // flag to track if we are on tablet or phone to organize our LayoutManager
    private boolean isTablet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_catalog);
        unbinder = ButterKnife.bind(this);
        if (findViewById(R.id.constrain_tablet_activity) != null) {
            isTablet = true;
        }

        // Check if this is first app run
        if (RecipeUtils.isFirstRun(this)) {
            RecipeUtils.saveFirstRun(this, false);
            RecipeUtils.setLastNotifyTime(this, System.currentTimeMillis());

        }


        mAdapter = new CatalogRecycleViewAdapter(null, this, this);
        if (isTablet) {
            GridLayoutManager manager = new GridLayoutManager(
                    this,
                    2,
                    LinearLayoutManager.VERTICAL,
                    false);
            mRecycleView.setLayoutManager(manager);
        } else {
            LinearLayoutManager manager = new LinearLayoutManager
                    (this, LinearLayoutManager.VERTICAL, false);
            mRecycleView.setLayoutManager(manager);
        }
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setAdapter(mAdapter);

        loadCatalogData();

        //run FireBase schedule
        RecipeUtils.scheduleRecipesNotification(this);
    }

    /**
     * Helper Method to Load the data correctly
     */
    private void loadCatalogData() {
        if (isNetworkConnected()) {
            showDataView();
            LoaderManager loaderManager = getSupportLoaderManager();
            if (loaderManager == null) {
                loaderManager.initLoader(LOADER_ID, null, this);
            } else {
                loaderManager.restartLoader(LOADER_ID, null, this);
            }
        } else {
            showNoDataView();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // get recycle view layout manager
        RecyclerView.LayoutManager layoutManager = mRecycleView.getLayoutManager();
        mScrollState = 0;
        // set the position of recycle view items
        mScrollState = ((LinearLayoutManager) layoutManager)
                .findFirstCompletelyVisibleItemPosition();
        // save the position into Bundle to restore it after re-create the activity
        outState.putInt(RECYCLE_SCROLL_STATE, mScrollState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // if this Bundle is not null we retrieve our position and scroll back to it
        if (savedInstanceState != null) {
            mScrollState = savedInstanceState.getInt(RECYCLE_SCROLL_STATE);
        }
    }

    @Override
    public void onClickRecipe(RecipeModel model) {
        Intent intent = new Intent(RecipeCatalog.this, DetailActivity.class);
        // Create new Bundle to pass data within
        Bundle b = new Bundle();
        // get ArrayList of Steps to pass to DetailActivity intent
        // consider that StepsModel implement Parcelable to make passing data between
        // activities easy
        ArrayList<StepsModel> models = (ArrayList<StepsModel>) model.getSteps();
        ArrayList<IngredientsModel> ingredientsModels = (ArrayList<IngredientsModel>) model.getIngredients();
        b.putParcelableArrayList(EXTRAS_STEPS_ARRAYLIST, models);
        b.putParcelableArrayList(EXTRAS_INGREDIENTS_ARRAYLIST, ingredientsModels);
        b.putString(EXTRAS_RECIPE_NAME, model.getRecipeName());
        intent.putExtras(b);
        // this flag to clear prev activities stack
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    public Loader<List<RecipeModel>> onCreateLoader(int id, Bundle args) {
        mProgressBar.setVisibility(View.VISIBLE);
        return new CatalogAsyncLoader(this, NetworkUtils.BASE_JSON_RECIPE_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<RecipeModel>> loader, List<RecipeModel> data) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (data != null && data.size() > 0) {
            showDataView();
            mAdapter.setArrayList((ArrayList<RecipeModel>) data);
            mRecycleView.scrollToPosition(mScrollState);
        } else {
            showNoDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<RecipeModel>> loader) {
        mAdapter.clear();
    }

    /**
     * @return state of current network True if internet available , false if not.
     */
    private Boolean isNetworkConnected() {
        ConnectivityManager conMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo networkInfo = conMgr != null ? conMgr.getActiveNetworkInfo() : null;

        return networkInfo != null && networkInfo.isConnected();

    }

    /**
     * Helper Method control UI
     */
    private void showDataView() {
        mEmptyView.setVisibility(View.INVISIBLE);
        mRecycleView.setVisibility(View.VISIBLE);
    }

    /**
     * Helper Method control UI
     */
    private void showNoDataView() {
        mEmptyView.setVisibility(View.VISIBLE);
        mRecycleView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
