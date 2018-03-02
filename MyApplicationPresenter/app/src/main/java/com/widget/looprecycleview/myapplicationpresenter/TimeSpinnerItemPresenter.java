package com.widget.looprecycleview.myapplicationpresenter;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by gella on 17-10-7.
 */

public class TimeSpinnerItemPresenter extends Presenter {
    private final static String TAG = ReminderPresenter.class.getSimpleName();
    private Context mContext;

    public TimeSpinnerItemPresenter(Context context) {
        mContext = context;
    }

    @Override
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.time_spinner_item_layout, parent, false);
        return new TimeSpinnerItemPresenter.ViewHolder(view);
    }

    @CallSuper
    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        if (viewHolder instanceof TimeSpinnerItemPresenter.ViewHolder) {
            final TimeSpinnerItemPresenter.ViewHolder timeSpinnerViewHolder = (TimeSpinnerItemPresenter.ViewHolder) viewHolder;

            if (timeSpinnerViewHolder.mTitle != null && item != null) {
                timeSpinnerViewHolder.mTitle.setText((String)item);
            }
            if (timeSpinnerViewHolder.mItem != null) {
                timeSpinnerViewHolder.mItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
                            view.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                        } else {
                            view.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                        }
                    }
                });
            }
        }
    }

    @CallSuper
    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        if (viewHolder instanceof ReminderPresenter.ViewHolder) {
            final ReminderPresenter.ViewHolder tabTileViewHolder = (ReminderPresenter.ViewHolder) viewHolder;
        }
    }

    public class ViewHolder extends Presenter.ViewHolder {
        public final TextView mTitle;

        public LinearLayout mItem;

        public ViewHolder(View tabTileView) {
            super(tabTileView);
            mTitle = (TextView) tabTileView.findViewById(R.id.time_spinner_title);
            mItem = (LinearLayout) tabTileView.findViewById(R.id.time_spinner_item);
        }
    }
}
