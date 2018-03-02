package com.widget.looprecycleview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import java.util.List;

/**
 * Created by gella on 17-10-14.
 */

public class LoopRecyclerView extends RecyclerView {
    private final String TAG = LoopRecyclerView.class.getSimpleName();
    private final boolean DEBUG = true;
    private int mScrolledPosition = 0;
    private int MAX_INIT_VALUE = 2000;

    public LoopRecyclerView(Context context) {
        super(context);
        init();
    }

    public LoopRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoopRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LinearLayoutManager linerLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        setLayoutManager(linerLayoutManager);
        setFocusable(true);
        setClickable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public void scrollToPosition(int position) {
        if (getAdapter() != null && getAdapter() instanceof LoopRecyclerViewAdapter) {
            List<String> data = ((LoopRecyclerViewAdapter)((LoopRecyclerViewAdapter) getAdapter())).getData();
            if (data != null && data.size() > 0) {
                if (DEBUG) {
                    Log.i(TAG, "scrollToPosition, position:" + position);
                }
                position = MAX_INIT_VALUE * data.size() + position;
            }
        }
        mScrolledPosition = position;
        if (getAdapter() != null && getAdapter() instanceof LoopRecyclerViewAdapter) {
            ((LoopRecyclerViewAdapter) getAdapter()).setScrolledPosition(position);
        }
        super.scrollToPosition(position);
    }

    public void setOnLoopRecycleViewItemClickListener(LoopRecyclerViewAdapter.OnLoopRecycleViewItemClickListener listener) {
        if (getAdapter() != null && getAdapter() instanceof LoopRecyclerViewAdapter) {
            (((LoopRecyclerViewAdapter) getAdapter()))
                    .setOnLoopRecyclerViewItemClickListener(listener);
        }
    }

    public void setOnLoopRecycleViewItemSelectedListener(LoopRecyclerViewAdapter.OnLoopRecycleViewItemSelectedListener listener) {
        if (getAdapter() != null && getAdapter() instanceof LoopRecyclerViewAdapter) {
            (((LoopRecyclerViewAdapter) getAdapter()))
                    .setOnLoopRecyclerViewItemSelectedListener(listener);
        }
    }
}
