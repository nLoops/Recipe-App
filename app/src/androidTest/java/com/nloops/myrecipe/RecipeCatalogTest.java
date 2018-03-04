package com.nloops.myrecipe;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.ComponentNameMatchers;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static org.hamcrest.Matchers.allOf;


/**
 * Contains Tests of Recipe Catalog Activity
 */

@RunWith(AndroidJUnit4.class)
public class RecipeCatalogTest {

    private static final String INGREDIENTS = "Ingredients";

    // we will test the intent of the clicked
    @Rule
    public IntentsTestRule<RecipeCatalog> mActivityRule =
            new IntentsTestRule<>(RecipeCatalog.class);


    @Test
    public void recipeCatalog_intentTo_DetailActivity() {
        // find view (RecycleView item at pos)
        // perform an action
        Espresso.onView(ViewMatchers.withId(R.id.rv_recipe_catalog))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(0, click()));
        // check if the intent has right data
        Intents.intended(allOf(
                IntentMatchers.hasComponent
                        (ComponentNameMatchers.hasShortClassName(".DetailActivity")),
                IntentMatchers.toPackage("com.nloops.myrecipe")));

        // check if the ingredients button has the right name
        Espresso.onView(ViewMatchers.withId(R.id.btn_ingredients))
                .check(ViewAssertions.matches(ViewMatchers.withText(INGREDIENTS)));
    }


}
