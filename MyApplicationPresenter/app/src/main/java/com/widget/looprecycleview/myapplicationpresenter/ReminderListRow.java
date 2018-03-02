package com.widget.looprecycleview.myapplicationpresenter;

import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ObjectAdapter;

/**
 * Created by gella on 17-9-27.
 */

public class ReminderListRow extends ListRow {
    public ReminderListRow(HeaderItem header, ObjectAdapter adapter) {
        super(header, adapter);
    }

    public ReminderListRow(long id, HeaderItem header, ObjectAdapter adapter) {
        super(id, header, adapter);
    }

    public ReminderListRow(ObjectAdapter adapter) {
        super(adapter);
    }
}
