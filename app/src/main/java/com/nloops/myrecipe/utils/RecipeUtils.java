package com.nloops.myrecipe.utils;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;


import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.nloops.myrecipe.R;
import com.nloops.myrecipe.RecipeCatalog;

import java.util.concurrent.TimeUnit;

/**
 * This class will hold most utils used to schedule and run notifications
 */

public class RecipeUtils {

    // Notification Channel ID
    private static final String NOTIFICATION_CHANNEL_ID = "main_channel";
    // Notification ID
    private static final int NOTIFICATION_ID = 33;
    // For Job Dispatcher
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String JOB_DISPATCHER_TAG = "notification_dispatcher";


    /**
     * This Method will notify user daily from first run of the app
     *
     * @param context
     */
    public static void notifyUserDaily(Context context) {

        // Get Large Image Of Notification
        Resources resources = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_large_image);
        // build intent that fire RecipeCatalog when click on Notification
        Intent intent = new Intent(context, RecipeCatalog.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Get Notification Manager
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Ref of Notification Channel
        NotificationChannel mChannel;

        // Create Notification Channel this only for OS O and further.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence cName = context.getString(R.string.notify_channel_name);
            String cDescription = context.getString(R.string.notify_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, cName, importance);
            mChannel.setDescription(cDescription);
            manager.createNotificationChannel(mChannel);
        }

        // Build up notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_small_image)
                .setLargeIcon(largeIcon)
                .setContentTitle(context.getString(R.string.notify_title))
                .setContentText(context.getString(R.string.notify_body))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        // finally, notify the user
        manager.notify(NOTIFICATION_ID, builder.build());

        //update Notify Time
        setLastNotifyTime(context, System.currentTimeMillis());


    }

    /**
     * Helper Method to handle When to Notify the user
     *
     * @param context
     */
    synchronized public static void fireNotification(Context context) {
        long ellapsedTimeFromLastNotify = getEllapsedTimeSinceLastNotification(context);
        if (ellapsedTimeFromLastNotify >= DateUtils.DAY_IN_MILLIS) {
            notifyUserDaily(context);
        }
    }


    /**
     * Helper Method will save the time for each new notification.
     *
     * @param context
     * @param notifyTime
     */
    public static void setLastNotifyTime(Context context, long notifyTime) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        String lastNotify = context.getString(R.string.key_last_notify);
        editor.putLong(lastNotify, notifyTime);
        editor.apply();
    }

    /**
     * Helper Method get EllapsedTime from Now at system and Last Time Notify
     *
     * @param context
     * @return
     */
    public static long getEllapsedTimeSinceLastNotification(Context context) {
        long lastNotify = getLastNotificationTime(context);
        long timeSinceNotify = System.currentTimeMillis() - lastNotify;
        return timeSinceNotify;
    }

    /**
     * Helper method return last time user was notified
     *
     * @param context
     * @return
     */
    public static long getLastNotificationTime(Context context) {
        String notifyTimekey = context.getString(R.string.key_last_notify);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        long lastNotify = preferences.getLong(notifyTimekey, 0);
        return lastNotify;
    }

    /**
     * Helper method that save when the app run for the first time to start our count fot
     * notify our user
     *
     * @param context
     * @param isFirstRun
     */
    public static void saveFirstRun(Context context, boolean isFirstRun) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        String firstRunKey = context.getString(R.string.key_first_run);
        editor.putBoolean(firstRunKey, isFirstRun);
        editor.apply();
    }

    /**
     * Helper Method return true if this first time app is running
     *
     * @param context
     * @return
     */
    public static boolean isFirstRun(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isFirstTime = preferences.getBoolean(
                context.getString(R.string.key_first_run), true);
        return isFirstTime;
    }

    /**
     * This method will fire up our jobDispatcher to check for run JobService that notify the user
     *
     * @param context
     */
    synchronized public static void scheduleRecipesNotification(@NonNull Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        // build up our Job
        Job recipeJob = dispatcher.newJobBuilder()
                .setService(NotificationJobService.class)
                .setTag(JOB_DISPATCHER_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS
                ))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(recipeJob);
    }


}
