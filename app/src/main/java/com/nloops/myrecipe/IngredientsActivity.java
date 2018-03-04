package com.nloops.myrecipe;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.nloops.myrecipe.adapters.IngredientsRecycleViewAdapter;
import com.nloops.myrecipe.data.IngredientsModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class IngredientsActivity extends AppCompatActivity {

    @BindView(R.id.rv_ingredients_layout)
    RecyclerView mRecycleView;

    Unbinder unbinder;

    private IngredientsRecycleViewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        unbinder = ButterKnife.bind(this);
        Bundle extras = getIntent().getExtras();
        ArrayList<IngredientsModel> dataModel =
                extras.getParcelableArrayList(RecipeCatalog.EXTRAS_INGREDIENTS_ARRAYLIST);
        String mTitle = extras.getString(RecipeCatalog.EXTRAS_RECIPE_NAME);
        setTitle(mTitle);
        mAdapter = new IngredientsRecycleViewAdapter(dataModel);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false
        );
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setAdapter(mAdapter);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
