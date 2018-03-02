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
 * Created by gella on 17-9-27.
 */

public class NewReminderPresenter extends Presenter {
    private final static String TAG = ReminderPresenter.class.getSimpleName();
    private Context mContext;

    public NewReminderPresenter(Context context) {
        mContext = context;
    }

    @Override
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.new_reminder_layout, parent, false);
        return new NewReminderPresenter.ViewHolder(view);
    }

    @CallSuper
    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        NewReminderData data = (NewReminderData) item;
        if (viewHolder instanceof NewReminderPresenter.ViewHolder) {
            final NewReminderPresenter.ViewHolder tabTileViewHolder = (NewReminderPresenter.ViewHolder) viewHolder;

            if (tabTileViewHolder.mIcon != null && data != null) {
                tabTileViewHolder.mIcon.setText(data.getTTitle());
            }
            if (tabTileViewHolder.mItem != null) {
                tabTileViewHolder.mItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        if (viewHolder instanceof NewReminderPresenter.ViewHolder) {
            final NewReminderPresenter.ViewHolder tabTileViewHolder = (NewReminderPresenter.ViewHolder) viewHolder;
        }
    }

    public class ViewHolder extends Presenter.ViewHolder {
        public final TextView mIcon;

        public LinearLayout mItem;

        public ViewHolder(View tabTileView) {
            super(tabTileView);
            mIcon = (TextView) tabTileView.findViewById(R.id.text_lable);
            mItem = (LinearLayout) tabTileView.findViewById(R.id.reminder_item);
        }
    }
}
