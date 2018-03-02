package com.widget.looprecycleview.myapplicationpresenter;

import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ObjectAdapter;

/**
 * Created by gella on 17-9-27.
 */

public class CalendarRow extends ListRow {

    public CalendarRow(HeaderItem header, ObjectAdapter adapter) {
        super(header, adapter);
    }

    public CalendarRow(long id, HeaderItem header, ObjectAdapter adapter) {
        super(id, header, adapter);
    }

    public CalendarRow(ObjectAdapter adapter) {
        super(adapter);
    }
}
