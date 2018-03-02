package com.widget.looprecycleview.myapplicationpresenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.ItemBridgeAdapter;
import android.view.KeyEvent;

/**
 * Created by gella on 17-10-31.
 */

public class HoriMainActivity extends Activity {
    private final static String TAG = HoriMainActivity.class.getSimpleName();
    HorizontalGridView mReminderHorizontalGridView;
    HorizontalGridView mCalendarHorizontalGridView;
    ArrayObjectAdapter mCalendarArrayObjectAdapter;
    ArrayObjectAdapter mReminderArrayObjectAdapter;
    ItemBridgeAdapter mReminderItemBridgeAdapter;
    ItemBridgeAdapter mCalendarItemBridgeAdapter;

    CalendarPresenter mCalendarPresenter;
    ReminderPresenter mReminderPresenter;
    ClassPresenterSelector mClassPresenterSelector;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_hori_layout);
        initUI();
        initData();
        setData();
    }

    private void initUI() {
        mReminderHorizontalGridView = (HorizontalGridView) findViewById(R.id.reminder_list);
        mCalendarHorizontalGridView = (HorizontalGridView) findViewById(R.id.calendar_list);
    }

    private void initData() {
        mCalendarPresenter = new CalendarPresenter(this);
        mReminderPresenter = new ReminderPresenter(this);
        mClassPresenterSelector = new ClassPresenterSelector();
        mClassPresenterSelector.addClassPresenter(ReminderData.class, mReminderPresenter);
        mClassPresenterSelector.addClassPresenter(CalendarData.class, mCalendarPresenter);

        mCalendarArrayObjectAdapter = new ArrayObjectAdapter(mCalendarPresenter);
        mReminderArrayObjectAdapter = new ArrayObjectAdapter(mClassPresenterSelector);

        mReminderItemBridgeAdapter = new ItemBridgeAdapter(mReminderArrayObjectAdapter);
        mCalendarItemBridgeAdapter = new ItemBridgeAdapter(mCalendarArrayObjectAdapter);

        mCalendarHorizontalGridView.setAdapter(mCalendarItemBridgeAdapter);
        mReminderHorizontalGridView.setAdapter(mReminderItemBridgeAdapter);
        mCalendarHorizontalGridView.requestFocus();
    }

    private void setData() {
        ReminderData data1 = new ReminderData("1nnnn1");
        ReminderData data2 = new ReminderData("122nnnn1");
        ReminderData data3 = new ReminderData("133nnnnnn3");
        CalendarData data4 = new CalendarData("4444");


        mReminderArrayObjectAdapter.add(data1);
        mReminderArrayObjectAdapter.add(data2);
        mReminderArrayObjectAdapter.add(data3);
        mReminderArrayObjectAdapter.add(data3);
        mReminderArrayObjectAdapter.add(data4);


        mCalendarArrayObjectAdapter.add(data4);
        mCalendarArrayObjectAdapter.add(data4);
        mCalendarArrayObjectAdapter.add(data4);

        mReminderItemBridgeAdapter.notifyDataSetChanged();
        mCalendarItemBridgeAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                mReminderHorizontalGridView.requestFocus();
                return true;
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                mCalendarHorizontalGridView.requestFocus();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
