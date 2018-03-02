package com.widget.looprecycleview.myapplicationpresenter;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;

/**
 * Created by gella on 17-9-26.
 */

public class ReminderPresenterSelector extends PresenterSelector {
    private CalendarPresenter mCalendarPresenter;
    private ReminderPresenter mReminderPresenter;
    private Context mContext;

    public ReminderPresenterSelector(Context context) {
        super();
        mContext = context;
        mCalendarPresenter = new CalendarPresenter(mContext);
        mReminderPresenter = new ReminderPresenter(mContext);
    }

    @Override
    public Presenter getPresenter(Object item) {
        Presenter presenter = null;
        if (item instanceof CalendarData) {
            presenter = mCalendarPresenter;
        } else if (item instanceof  ReminderData) {
            presenter = mReminderPresenter;
        }

        return presenter;
    }

    @Override
    public Presenter[] getPresenters() {
        return super.getPresenters();
    }
}
