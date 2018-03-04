package com.nloops.myrecipe.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.nloops.myrecipe.R;
import com.nloops.myrecipe.RecipeCatalog;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, String recipeName,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_app_widget);
        if (recipeName != null) {
            views.setTextViewText(R.id.appwidget_text, recipeName);
        } else {
            views.setTextViewText(R.id.appwidget_text, context.getString(R.string.no_added_recipe));
        }
        Intent intent = new Intent(context, ListViewService.class);
        views.setRemoteAdapter(R.id.lv_widget_items, intent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        WidgetIntentService.startActionChangeList(context);

    }

    public static void updateIngredientWidgets(Context context, AppWidgetManager manager,
                                               String recipeName, int[] appWidgetsIds) {
        for (int appWidgetId : appWidgetsIds) {
            updateAppWidget(context, manager, recipeName, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

