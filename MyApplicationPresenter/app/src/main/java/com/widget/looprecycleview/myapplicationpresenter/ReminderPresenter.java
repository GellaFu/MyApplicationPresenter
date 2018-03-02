package com.widget.looprecycleview.myapplicationpresenter;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v17.leanback.widget.Presenter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by gella on 17-9-26.
 */

public class ReminderPresenter extends Presenter {
    private final static String TAG = ReminderPresenter.class.getSimpleName();
    private Context mContext;

    public ReminderPresenter(Context context) {
        mContext = context;
    }

    @Override
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item_layout, parent, false);
        return new ReminderPresenter.ViewHolder(view);
    }

    @CallSuper
    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        ReminderData data = (ReminderData) item;
        if (viewHolder instanceof ReminderPresenter.ViewHolder) {
            final ReminderPresenter.ViewHolder tabTileViewHolder = (ReminderPresenter.ViewHolder) viewHolder;

            if (tabTileViewHolder.mIcon != null && data != null) {
                tabTileViewHolder.mIcon.setText(data.getTTitle());
            }
            if (tabTileViewHolder.mItem != null) {
                tabTileViewHolder.mItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        Log.i(TAG,"Joyce--onFocusChange :" + view + ", focus:" + b);
                        if (b) {
                            view.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                            view.animate().scaleX(1.2f).scaleY(1.2f);

                        } else {
                            view.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                            view.animate().scaleX(1.0f).scaleY(1.0f);
                        }
                    }
                });
                //disableClipOnParents(tabTileViewHolder.mImage);
            }
        }
    }


    public void disableClipOnParents(View v) {
        if (v.getParent() == null) {
            return;
        }

        if (v instanceof ViewGroup) {
            ((ViewGroup) v).setClipChildren(false);
            ((ViewGroup) v).setClipToPadding(false);
        }

        if (v.getParent() instanceof View) {
            disableClipOnParents((View) v.getParent());
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
        public final TextView mIcon;

        public LinearLayout mItem;
        //public ImageView mImage;

        public ViewHolder(View tabTileView) {
            super(tabTileView);
            mIcon = (TextView) tabTileView.findViewById(R.id.text_lable);
            mItem = (LinearLayout) tabTileView.findViewById(R.id.reminder_item);
           // mImage = (ImageView) tabTileView.findViewById(R.id.image);
        }
    }
}
