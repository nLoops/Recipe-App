package com.nloops.myrecipe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nloops.myrecipe.adapters.DetailRecycleViewAdapter;
import com.nloops.myrecipe.data.IngredientsModel;
import com.nloops.myrecipe.data.StepsModel;
import com.nloops.myrecipe.ui.RecipeFragment;
import com.nloops.myrecipe.widgets.WidgetIntentService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DetailActivity extends AppCompatActivity implements
        DetailRecycleViewAdapter.DetailClickListener {

    @BindView(R.id.rv_recipe_details)
    RecyclerView mRecycleView;
    Unbinder unbinder;

    private DetailRecycleViewAdapter mAdapter;
    public static ArrayList<IngredientsModel> mIngredientsList;
    private ArrayList<StepsModel> mModelArrayList;
    public static String mRecipeName;
    private boolean mTwoPane;

    // EXTRAS
    public static final String EXTRAS_MODEL_POSITION = "com.nloops.myrecipe.EXTRAS.model.pos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        unbinder = ButterKnife.bind(this);
        if (findViewById(R.id.cons_detail_activity) != null) {
            mTwoPane = true;
        }
        Bundle extras = getIntent().getExtras();
        mModelArrayList = extras.
                getParcelableArrayList(RecipeCatalog.EXTRAS_STEPS_ARRAYLIST);
        mIngredientsList = extras.
                getParcelableArrayList(RecipeCatalog.EXTRAS_INGREDIENTS_ARRAYLIST);
        // get the name of recipe
        mRecipeName = extras.getString(RecipeCatalog.EXTRAS_RECIPE_NAME);
        // set the name of recipe to the title of Activity
        setTitle(mRecipeName);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
        );
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setHasFixedSize(true);
        mAdapter = new DetailRecycleViewAdapter(mModelArrayList, this);
        mRecycleView.setAdapter(mAdapter);

    }


    @OnClick(R.id.btn_ingredients)
    public void launchIngredients(View view) {
        Intent intent = new Intent(DetailActivity.this, IngredientsActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelableArrayList(RecipeCatalog.EXTRAS_INGREDIENTS_ARRAYLIST, mIngredientsList);
        extras.putString(RecipeCatalog.EXTRAS_RECIPE_NAME, mRecipeName);
        intent.putExtras(extras);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onDetailClick(StepsModel model) {
        if (!mTwoPane) {
            Bundle b = new Bundle();
            b.putInt(EXTRAS_MODEL_POSITION, mAdapter.getPosition(model));
            b.putParcelableArrayList(RecipeCatalog.EXTRAS_STEPS_ARRAYLIST, mModelArrayList);
            b.putParcelableArrayList(RecipeCatalog.EXTRAS_INGREDIENTS_ARRAYLIST, mIngredientsList);
            b.putString(RecipeCatalog.EXTRAS_RECIPE_NAME, mRecipeName);
            Intent displayIntent = new Intent(DetailActivity.this, DisplayActivity.class);
            displayIntent.putExtras(b);
            startActivity(displayIntent);
            finish();
        } else {
            RecipeFragment fragment = new RecipeFragment();
            fragment.setVideoURL(model.getVideoURL());
            fragment.setDescription(model.getDescription());
            fragment.setThumbinalURL(model.getThumbnailURL());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame_layout, fragment)
                    .commit();
        }
    }

    private void backToParent() {
        Intent intent = new Intent(DetailActivity.this, RecipeCatalog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToParent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backToParent();
                return true;
            case R.id.action_add_widget_btn:
                WidgetIntentService.startActionChangeList(this);
                Toast.makeText(this,
                        getString(R.string.toast_widget),
                        Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
