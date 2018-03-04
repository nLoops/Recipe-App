package com.nloops.myrecipe.utils;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * implementation to firebase JobService to be part of Job Schudele
 */

public class NotificationJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        RecipeUtils.fireNotification(getApplicationContext());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }
}
