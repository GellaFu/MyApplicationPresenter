package com.widget.looprecycleview.myapplicationpresenter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.zip.Inflater;

/**
 * Created by gella on 17-10-23.
 */

public class FocusShadowLinearLayout extends LinearLayout {
    private final static String TAG = FocusShadowLinearLayout.class.getSimpleName();
    Drawable mBackgroundDrawable;
    Drawable mShadowDrawable;
    int mLeftExtra = -100;
    int mRightExtra = 100;
    int mTopExtra = -100;
    int mBottomExtra = 100;
    Context mContext;
    TextView mTextView;
    ImageView mImage;
    LinearLayout mLayout;

    public FocusShadowLinearLayout(Context context) {
        super(context);
        mContext = context;
    }

    public FocusShadowLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public FocusShadowLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //LayoutInflater.from(getContext()).inflate(R.layout.reminder_item_layout, this);
        mShadowDrawable = getResources().getDrawable(R.drawable.image);
        mTextView = (TextView) findViewById(R.id.text_lable);
        //mImage = (ImageView) findViewById(R.id.iamge_view);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.i(TAG, "aaaa:b:" + b);
            }
        });
        mLayout = (LinearLayout) findViewById(R.id.layout);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        super.onDraw(canvas);


        Log.i(TAG, "Joyce--onDraw");
        if (mShadowDrawable != null && isFocused()) {
            Log.i("fuyouping", "fffffff, shadow, is focused true");
//
//            Rect r = new Rect();
//            mLayout.getGlobalVisibleRect(r);
//            Log.i(TAG, "ffffff:" + r.left + ", t:" + r.top + ", b:" + r.bottom + ", right:" + r.right);
            Rect rect = getBackground().getBounds();

            int left = rect.left + mLeftExtra;
            int top = rect.top + mTopExtra;
            int right = rect.right + mRightExtra;
            int bottom = rect.bottom + mBottomExtra;
            //mShadowDrawable.setBounds(rect.left + mLeftExtra, rect.top + mTopExtra, rect.right + mRightExtra,
             //       rect.bottom + mBottomExtra);
            mShadowDrawable.setBounds(left, top, right, bottom);
            Log.i(TAG, "left:" + left + ", top:" + top + ", right:" + right + ", bottom:" + bottom);
            mShadowDrawable.draw(canvas);
            mBackgroundDrawable = getBackground();
            if (mBackgroundDrawable != null) {
                mBackgroundDrawable.draw(canvas);
            }
            //mLayout.getGlobalVisibleRect()

            //this.animate().scaleX(1.5f).scaleY(1.5f);
            // mTextView.setText("fffffffffffff");
        } else if (mShadowDrawable != null && !isFocused()) {
            Log.i("fuyouping", "fffffff, shadow, is focused false");
            // mTextView.setText("nnnnnnnnnnnnnnnn");
            //this.animate().scaleX(1.0f).scaleY(1.0f);

        }
    }

}
