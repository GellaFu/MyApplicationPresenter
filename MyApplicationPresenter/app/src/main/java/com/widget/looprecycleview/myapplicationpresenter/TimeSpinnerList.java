package com.widget.looprecycleview.myapplicationpresenter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ItemBridgeAdapter;
import android.support.v17.leanback.widget.VerticalGridView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by gella on 17-10-7.
 */

public class TimeSpinnerList extends LinearLayout {
    private final String TAG = TimeSpinnerList.class.getSimpleName();
    private LinearLayout mTimeSpinner;
    private TextView mTitle;
    private ImageView mImageView;
    private PopupWindow mSpinnerListView;
    private VerticalGridView mHourVerticalGridView;
    private VerticalGridView mMinuteVerticalGridView;
    private VerticalGridView mAmPmVerticalGridView;
    private ArrayObjectAdapter mHourArrayObjectAdapter;
    private ArrayObjectAdapter mMinuteArrayObjectAdapter;
    private ArrayObjectAdapter mAmPmArrayObjectAdapter;
    private ItemBridgeAdapter mHourItemBridgeAdapter;
    private ItemBridgeAdapter mMinuteItemBridgeAdapter;
    private ItemBridgeAdapter mAmPmItemBridgeAdapter;
    private TimeSpinnerItemPresenter mTimeSpinnerItemPresenter;

    public TimeSpinnerList(Context context) {
        super(context);
    }

    public TimeSpinnerList(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeSpinnerList(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init(getContext());
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.time_spinner_layout, this);
        mTitle = (TextView) findViewById(R.id.spinner_title);
        mImageView = (ImageView) findViewById(R.id.spinner_image);
        mTimeSpinner = (LinearLayout) findViewById(R.id.time_spinner);
        mTimeSpinner.requestFocus();
        mTimeSpinner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick");
                mSpinnerListView.showAsDropDown(TimeSpinnerList.this, 0, 0);
                mHourVerticalGridView.requestFocus();

            }
        });
        View popWin = LayoutInflater.from(context).inflate(R.layout.time_spinner_list_layout, null);
        mSpinnerListView = new PopupWindow(popWin, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mSpinnerListView.setFocusable(true);
        mSpinnerListView.setTouchable(true);
        mSpinnerListView.setOutsideTouchable(true);
        mSpinnerListView.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //TODO
            }
        });
        mHourVerticalGridView = (VerticalGridView) popWin.findViewById(R.id.hour_list_view);
        mMinuteVerticalGridView = (VerticalGridView) popWin.findViewById(R.id.minute_list_view);
        mAmPmVerticalGridView = (VerticalGridView) popWin.findViewById(R.id.am_pm_list_view);
        initData(context);
        mHourVerticalGridView.setAdapter(mHourItemBridgeAdapter);
        mMinuteVerticalGridView.setAdapter(mMinuteItemBridgeAdapter);
        mAmPmVerticalGridView.setAdapter(mAmPmItemBridgeAdapter);
    }

    private void initData(Context context) {
        //init arrayOjectAdapter
        mTimeSpinnerItemPresenter = new TimeSpinnerItemPresenter(context);
        mHourArrayObjectAdapter = new ArrayObjectAdapter(mTimeSpinnerItemPresenter);
        mMinuteArrayObjectAdapter = new ArrayObjectAdapter(mTimeSpinnerItemPresenter);
        mAmPmArrayObjectAdapter = new ArrayObjectAdapter(mTimeSpinnerItemPresenter);
        for (int i = 0; i < 11; i ++) {
            int hour = i;
            if (i == 0) {
                hour = 12;
            }
            mHourArrayObjectAdapter.add(String.valueOf(hour));
        }
        for (int i = 0; i < 60; i ++) {
            mMinuteArrayObjectAdapter.add(String.format("%02d", i));
        }

        mAmPmArrayObjectAdapter.add("am");
        mAmPmArrayObjectAdapter.add("pm");

        mHourItemBridgeAdapter = new ItemBridgeAdapter(mHourArrayObjectAdapter);
        mMinuteItemBridgeAdapter = new ItemBridgeAdapter(mMinuteArrayObjectAdapter);
        mAmPmItemBridgeAdapter = new ItemBridgeAdapter(mAmPmArrayObjectAdapter);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }
}
