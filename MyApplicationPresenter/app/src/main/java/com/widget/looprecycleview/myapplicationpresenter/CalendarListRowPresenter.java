package com.widget.looprecycleview.myapplicationpresenter;

import android.content.Context;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.ItemBridgeAdapter;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnChildViewHolderSelectedListener;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by gella on 17-9-26.
 */

public class CalendarListRowPresenter extends ListRowPresenter {
    private final String TAG = "fuyouping";
    private Context mContext;
    private HorizontalGridView mHorizontalGridView;

    public CalendarListRowPresenter(Context context) {
        super();
        mContext = context;
    }

    @Override
    protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
        return super.createRowViewHolder(parent);
    }

    @Override
    protected void onRowViewSelected(RowPresenter.ViewHolder holder, boolean selected) {
        super.onRowViewSelected(holder, selected);
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);
    }

    @Override
    protected void initializeRowViewHolder(RowPresenter.ViewHolder holder) {
        super.initializeRowViewHolder(holder);
        Log.d(TAG, "initializeRowViewHolder() called with: holder = [" + holder + "]");
        Log.d(TAG, "initializeRowViewHolder: view=" + holder.view);
        Log.d(TAG, "initializeRowViewHolder: row=" + holder.getRow());

        final CalendarListRowPresenter.ViewHolder rowViewHolder = (CalendarListRowPresenter.ViewHolder) holder;
        final HorizontalGridView gridView = rowViewHolder.getGridView();
        mHorizontalGridView = gridView;

        // set margin between tiles
        gridView.setItemMargin(50);
        gridView.setNumRows(1);
        gridView.setPadding(60, 50, 30, 0);
        gridView.setNumRows(1);
        gridView.setClipChildren(false);
        gridView.setClipToPadding(false);
        gridView.setFadingLeftEdge(false);
        gridView.setNestedScrollingEnabled(true);
        gridView.setLayoutEnabled(true);

       // gridView.setFocusScrollStrategy(1);
       // gridView.setChildCanMoveToCenter(false);

        gridView.setFocusDrawingOrderEnabled(false);
        //final JamdeoGridLayoutManager lm = (JamdeoGridLayoutManager) gridView.getLayoutManager();
       // lm.setMixedScrollBehaviour(false);
    }

    public  void setSelectedPosition(int position) {
        mHorizontalGridView.setSelectedPosition(position);
    }
}
