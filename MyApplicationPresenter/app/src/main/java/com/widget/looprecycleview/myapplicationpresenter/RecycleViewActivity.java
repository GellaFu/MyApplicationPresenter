package com.widget.looprecycleview.myapplicationpresenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.widget.looprecycleview.LoopRecyclerView;
import com.widget.looprecycleview.LoopRecyclerViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

/**
 * Created by gella on 17-10-14.
 */

public class RecycleViewActivity extends Activity {
    private String TAG = RecycleViewActivity.class.getSimpleName();
    private List<String> mData;
    private List<String> mMData;
    private LoopRecyclerViewAdapter myAdapter;
    private LoopRecyclerView mRecyclerView;
    private LoopRecyclerView mMRecyclerView;
    private LoopRecyclerViewAdapter mMiteAdapter;
    private View mLastHoView;
    private View mLastMView;
    private ViewTreeObserver mViewTreeObserver;
    boolean mMHasFocus = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_recycle_layout);
        initdata();
        initUI();
    }

    private void initdata() {
        mData = new ArrayList<String>();
        for (int i = 0; i < 12; i++) {
            mData.add("item" + i);
            Log.i(TAG, "initdata: item" + i);
        }
        mMData = new ArrayList<String>();
        for (int i = 0; i < 59; i++) {
            mMData.add("item" + i);
            Log.i(TAG, "initdata: minute" + i);
        }
        computeTime();
    }

    private void computeTime() {
        int year = 2007;
        int month = 11;
        int day = 23;
        int hour = 12;
        int minute = 13;
        int second = 00;
        String dateStr = "2007-11-23 12:13:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {

        }
        Log.i(TAG, "computeTime," + date.getTime());
    }

    private void initUI() {
        mMRecyclerView = (LoopRecyclerView) findViewById(R.id.minute_recycle_view);
        mMiteAdapter = new LoopRecyclerViewAdapter(this, mMData);
        mMRecyclerView.setAdapter(mMiteAdapter);
        mMRecyclerView.scrollToPosition(30);
        mMRecyclerView.setOnLoopRecycleViewItemClickListener(new LoopRecyclerViewAdapter.OnLoopRecycleViewItemClickListener() {
            @Override
            public void loopRecycleViewItemClicked(int position) {
                Log.i(TAG, "setOnLoopRecycleViewItemClickListener: position:" + position);
            }
        });
        mMRecyclerView.setOnLoopRecycleViewItemSelectedListener(new LoopRecyclerViewAdapter.OnLoopRecycleViewItemSelectedListener() {
            @Override
            public void loopRecycleViewItemSelected(View view, int position) {
                Log.i(TAG, "MloopRecycleViewItemSelected: position:" + position);
                //view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                mLastMView = view;
            }

            @Override
            public void loopRecycleViewItemUnSelected(View view, int position) {
                Log.i(TAG, "MloopRecycleViewItemUnSelected: position:" + position);
              //  view.setBackgroundColor(getResources().getColor(R.color.lb_tv_white));
            }
        });
        mRecyclerView = (LoopRecyclerView) findViewById(R.id.hour_recycle_view);
        myAdapter = new LoopRecyclerViewAdapter(this, mData);
        mRecyclerView.setAdapter(myAdapter);

        mRecyclerView.requestFocus();
        mRecyclerView.scrollToPosition(6);
        mRecyclerView.setSelected(true);
        myAdapter.setItemRequestFocus(true);
        myAdapter.setScrolledPosition(6);
        mRecyclerView.setOnLoopRecycleViewItemClickListener(new LoopRecyclerViewAdapter.OnLoopRecycleViewItemClickListener() {
            @Override
            public void loopRecycleViewItemClicked(int position) {
                Log.i(TAG, "setOnLoopRecycleViewItemClickListener: position:" + position);
            }
        });
        mRecyclerView.setOnLoopRecycleViewItemSelectedListener(new LoopRecyclerViewAdapter.OnLoopRecycleViewItemSelectedListener() {
            @Override
            public void loopRecycleViewItemSelected(View view, int position) {
                Log.i(TAG, "HloopRecycleViewItemSelected: position:" + position);
                //view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                mLastHoView = view;
            }

            @Override
            public void loopRecycleViewItemUnSelected(View view, int position) {
                Log.i(TAG, "HloopRecycleViewItemUnSelected: position:" + position);
               // view.setBackgroundColor(getResources().getColor(R.color.lb_tv_white));
            }
        });
        mViewTreeObserver = getWindow().getDecorView().getViewTreeObserver();
        mViewTreeObserver.addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View view, View view1) {
                Log.i(TAG, "onGlobalFocusChanged, new:" + view + ", \n old:" + view1);
            }
        });
        mMRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i(TAG, "onScrollStateChanged:" + newState);
                if (newState != 0) {
                    if (mMRecyclerView.hasFocus()) {
                        mMHasFocus = true;
                    }
                    mRecyclerView.setFocusableInTouchMode(false);
                    mRecyclerView.setFocusable(false);
                    mRecyclerView.setEnabled(false);
                    mRecyclerView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                } else {
                    if (mMHasFocus) {
                       // mMRecyclerView.requestFocus();
                    }
                    mRecyclerView.setFocusableInTouchMode(true);
                    mRecyclerView.setFocusable(true);
                    mRecyclerView.setEnabled(true);
                    mRecyclerView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i(TAG, "onScrolled:" + dx + ", dy:" + dy);
                if (mMHasFocus) {
                   // mMRecyclerView.requestFocus();
                }
            }
        });
        mMRecyclerView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View view, MotionEvent motionEvent) {
                Log.i(TAG, "MonGenericMotion:" + motionEvent);
                return false;
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i(TAG, "dispatchKeyEvent");
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (mMRecyclerView.hasFocus()) {
                    Log.i(TAG, "dispatchKeyEvent, hour need to get focus");
                    if (mLastHoView != null) {
                        mLastHoView.requestFocus();
                    }
                    //mRecyclerView.requestFocus();
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mRecyclerView.hasFocus()) {
                    Log.i(TAG, "dispatchKeyEvent, minute need to get focus");
                    if (mLastMView != null) {

                        mLastMView.requestFocus();
                    }
                    //mMRecyclerView.requestFocus();
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
