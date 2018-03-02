package com.widget.looprecycleview.myapplicationpresenter;

import android.content.Context;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.ItemBridgeAdapter;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnChildSelectedListener;
import android.support.v17.leanback.widget.OnChildViewHolderSelectedListener;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.ShadowOverlayContainer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by gella on 17-9-27.
 */

public class ReminderListRowPresenter extends ListRowPresenter {
    private final String TAG = "fuyouping";
    private HorizontalGridView mHorizontalGridView;
    ItemBridgeAdapter mItemBridgeAdapter;
    ItemBridgeAdapter.ViewHolder mLastVIewHolder;



    public ReminderListRowPresenter() {
        super();
    }

    public ReminderListRowPresenter(int focusZoomFactor) {
        super(focusZoomFactor);
    }

    public ReminderListRowPresenter(int focusZoomFactor, boolean useFocusDimmer) {
        super(focusZoomFactor, useFocusDimmer);
    }
    @Override
    protected void initializeRowViewHolder(RowPresenter.ViewHolder holder) {
        super.initializeRowViewHolder(holder);
        Log.d(TAG, "initializeRowViewHolder() called with: holder = [" + holder + "]");
        Log.d(TAG, "initializeRowViewHolder: view=" + holder.view);
        Log.d(TAG, "initializeRowViewHolder: row=" + holder.getRow());

        final ReminderListRowPresenter.ViewHolder rowViewHolder = (ReminderListRowPresenter.ViewHolder) holder;
        final HorizontalGridView gridView = rowViewHolder.getGridView();
        //Log.d(TAG, "initializeRowViewHolder: row=" + rowViewHolder.getBridgeAdapter().getItemCount());

        // gridView.setClipToPadding(false);
       // gridView.setClipChildren(false);
        mHorizontalGridView = gridView;

        // set margin between tiles
        gridView.setItemMargin(50);
        gridView.setPadding(60, 50, 30, 0);
        gridView.setNumRows(1);
        gridView.setHasOverlappingRendering(true);
        gridView.setClipChildren(false);
        gridView.setClipToPadding(false);
        disableClipOnParents(gridView);
        //gridView.setFocusScrollStrategy(0);
        gridView.setFocusDrawingOrderEnabled(false);

        View headView = (View) holder.getHeaderViewHolder().view;

        //headView.setPadding(60, 0, 0, 0);


    }

    public ItemBridgeAdapter.ViewHolder getLastViewHolder() {
        return mLastVIewHolder;
    }

    @Override
    public boolean isUsingDefaultListSelectEffect() {
        return false;
    }

    @Override
    public boolean isUsingDefaultShadow() {
        return false;
    }

//    @Override
//    public boolean canDrawOutOfBounds() {
//        return false;
//    }


    @Override
    protected boolean isClippingChildren() {
        return false;
    }

    @Override
    protected void onRowViewSelected(RowPresenter.ViewHolder holder, boolean selected) {
        super.onRowViewSelected(holder, selected);
        Log.i(TAG, "rsssssssrrrrrrrrrrrrr");
    }

    private void initViewTree() {
        ViewTreeObserver treeObserver = mHorizontalGridView.getViewTreeObserver();
        if (treeObserver.isAlive()) {
            treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    for (int i = 0; i < mHorizontalGridView.getChildCount(); i ++) {
                        Log.i(TAG, "fffffffffhh:" + mHorizontalGridView.getChildAt(i));
                        if (mHorizontalGridView.getChildAt(i) != null && mHorizontalGridView instanceof ViewGroup) {
                            Log.i(TAG, "fffffffffhhh: +" + mHorizontalGridView.getChildAt(i));
                            ViewGroup vg = (ViewGroup) mHorizontalGridView.getChildAt(i);
                            if (vg instanceof ShadowOverlayContainer) {
                                Log.i(TAG, "fffffffff ssssssssss");
                                //((ShadowOverlayContainer) vg).useStaticShadow();
                                ((ShadowOverlayContainer) vg).setShadowFocusLevel(0f);
                              //  ((ShadowOverlayContainer) vg).wrap(mHorizontalGridView);
                            }
                        }
                    }
                }
            });
        } else {
            Log.w(TAG, " treeObserver isn't alive ");
        }
    }


//    final ShadowOverlayContainer shadowOverlayContainer = new ShadowOverlayContainer(this);
//    ShadowOverlayHelper shadowOverlayHelper = new ShadowOverlayHelper();
//    private void initShadow() {
//        shadowOverlayContainer.setShadowFocusLevel(2f);
//        shadowOverlayContainer.setClipChildren(false);
//        shadowOverlayContainer.setClipToPadding(false);
//       // shadowOverlayContainer.setBackground(getResources().getDrawable(R.drawable.image));
//        shadowOverlayContainer.useStaticShadow();
//        //shadowOverlayContainer.
//    }

    public void disableClipOnParents(View v) {
        if (v.getParent() == null) {
            return;
        }

        if (v instanceof ViewGroup) {
            Log.i(TAG, "ffffffffff:" + v);
            ((ViewGroup) v).setClipChildren(false);
            ((ViewGroup) v).setClipToPadding(false);
        }

        if (v.getParent() instanceof View) {
            disableClipOnParents((View) v.getParent());
        }
    }

    @Override
    public boolean isUsingZOrder(Context context) {
        return false;
    }

    @Override
    protected void onSelectLevelChanged(RowPresenter.ViewHolder holder) {
        super.onSelectLevelChanged(holder);
        Log.i(TAG, "fffffffff:onSelectLevelChanged");
    }


    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);
        Log.i(TAG, "fffffffffffff," + mHorizontalGridView.getChildCount());

//        if (mHorizontalGridView.getChildCount() != 0) {
//            if (mHorizontalGridView.getChildAt(0) != null) {
//                Log.i(TAG, "ffffffff, not null");
//                if (mHorizontalGridView.getChildAt(0) instanceof ViewGroup) {
//
//                    ViewGroup vg = (ViewGroup) mHorizontalGridView.getChildAt(0);
//                    Log.i(TAG, "ffffffff, vg:" + vg);
//
//                    disableClipOnParents(vg);
//                }
//            }
//        }

    }

    public  void setSelectedPosition(int position) {
        mHorizontalGridView.setSelectedPosition(position);
    }
}
