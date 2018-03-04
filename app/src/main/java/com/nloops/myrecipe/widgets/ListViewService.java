package com.nloops.myrecipe.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Implement RemoteViewService To Update ListView Data Adapter
 */

public class ListViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this);
    }
}
