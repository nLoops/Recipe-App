package com.nloops.myrecipe.widgets;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.nloops.myrecipe.DetailActivity;
import com.nloops.myrecipe.R;

/**
 * Handle Widget Background Service
 */

public class WidgetIntentService extends IntentService {

    public static final String ACTION_CHANGE_WIDGET_LIST = "com.nloops.myrecipe.ACTON_CHANGE_LIST";

    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION_CHANGE_WIDGET_LIST)) {
                handleActionChangeList();
            }
        }
    }

    public static void startActionChangeList(Context context) {
        Intent intent = new Intent(context, WidgetIntentService.class);
        intent.setAction(ACTION_CHANGE_WIDGET_LIST);
        context.startService(intent);
    }

    private void handleActionChangeList() {
        // get WidgetManager
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        // get AllWidgets
        int[] appWidgetIds = manager.getAppWidgetIds
                (new ComponentName(this, IngredientsAppWidget.class));
        // Notify Data Changed
        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_widget_items);
        // Update Widget
        IngredientsAppWidget.updateIngredientWidgets(
                this,
                manager,
                DetailActivity.mRecipeName,
                appWidgetIds
        );


    }
}
