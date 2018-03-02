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
 * Created by gella on 17-9-26.
 */

public class CalendarPresenter extends Presenter {
    private final static String TAG = CalendarPresenter.class.getSimpleName();
    private Context mContext;

    public CalendarPresenter(Context context) {
        mContext = context;
    }

    @Override
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item_layout, parent, false);
        return new CalendarPresenter.ViewHolder(view);
    }

    @CallSuper
    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        CalendarData data = (CalendarData) item;
        if (viewHolder instanceof CalendarPresenter.ViewHolder) {
            final CalendarPresenter.ViewHolder tabTileViewHolder = (CalendarPresenter.ViewHolder) viewHolder;

            if (tabTileViewHolder.mIcon != null && data != null) {
                tabTileViewHolder.mIcon.setText(data.getTTitle());
            }
            if (tabTileViewHolder.mItem != null) {
                tabTileViewHolder.mItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
                            view.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                            view.animate().scaleX(1.2f).scaleY(1.2f);
                        } else {
                            view.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                            view.animate().scaleX(1.0f).scaleY(1.0f);

                        }
                    }
                });
            }
        }
    }

    @CallSuper
    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        if (viewHolder instanceof CalendarPresenter.ViewHolder) {
            final CalendarPresenter.ViewHolder tabTileViewHolder = (CalendarPresenter.ViewHolder) viewHolder;
        }
    }

    public class ViewHolder extends Presenter.ViewHolder {
        public  TextView mIcon;
        public  LinearLayout mItem;

        public ViewHolder(View tabTileView) {
            super(tabTileView);
            mIcon = (TextView) tabTileView.findViewById(R.id.text_lable);
            mItem = (LinearLayout) tabTileView.findViewById(R.id.calendar_item);
        }
    }
}
