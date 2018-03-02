package com.widget.looprecycleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.widget.looprecycleview.looprecycleview.R;

import java.util.List;

/**
 * Created by gella on 17-10-14.
 */

public class LoopRecyclerViewAdapter  extends RecyclerView.Adapter<LoopRecyclerViewAdapter.LoopViewHolder>{
    private final static String TAG = LoopRecyclerViewAdapter.class.getSimpleName();
    private final boolean DEBUG = true;
    private LayoutInflater inflater;
    private Context mContext;
    private List<String> mDatas;
    private int mScrolledPosition = 0;
    private boolean mFirstTimeGetFocus = true;
    private boolean mItemRequestFocus = false;

    private OnLoopRecycleViewItemClickListener mOnLoopRecycleViewItemClickListener;
    private OnLoopRecycleViewItemSelectedListener mOnLoopRecycleViewItemSelectedListener;

    public LoopRecyclerViewAdapter(Context context , List<String> datas){
        this.mContext = context;
        this.mDatas = datas;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public LoopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.time_spinner_item_layout , parent , false);
        LoopViewHolder viewHolder = new LoopViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final LoopViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder, position:" + position);
        if (position == -1) {
            position = 0;
        }
        final int realPosition = position % mDatas.size();

        holder.tv.setText(mDatas.get(realPosition));
        holder.mItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (DEBUG) {
                    Log.i(TAG, "LoopRecyclerViewAdapter, on focus change");
                }
                if (mOnLoopRecycleViewItemSelectedListener != null) {
                    if (b) {
                        mOnLoopRecycleViewItemSelectedListener.loopRecycleViewItemSelected(holder.mItem, realPosition);
                    } else {
                        mOnLoopRecycleViewItemSelectedListener.loopRecycleViewItemUnSelected(holder.mItem, realPosition);
                    }
                }
            }
        });
        holder.mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DEBUG) {
                    Log.i(TAG, "LoopRecyclerViewAdapter, on click");
                }
                if (mOnLoopRecycleViewItemClickListener != null) {
                    mOnLoopRecycleViewItemClickListener.loopRecycleViewItemClicked(realPosition);
                }
            }
        });
        if (DEBUG) {
            Log.i(TAG, "LoopRecyclerViewAdapter, position:" + position + ", mFirstTimeGetFocus:" +
                    mFirstTimeGetFocus + ", mItemRequestFocus:" + mItemRequestFocus + ", mscrolledP:" + mScrolledPosition);
        }
        if (mFirstTimeGetFocus && realPosition == mScrolledPosition && mItemRequestFocus) {
            if (DEBUG) {
                Log.i(TAG, "LoopRecyclerViewAdapter, need focus position:" + position);
            }
            holder.mItem.requestFocus();
            mFirstTimeGetFocus = false;
        }
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;

    }

    @Override
    public long getItemId(int position) {
        return position % mDatas.size();
    }

    public List<String> getData() {
        return mDatas;
    }

    public void setScrolledPosition(int position) {
        if (DEBUG) {
            Log.i(TAG, "setScrolledPosition:" + position);
        }
        mScrolledPosition = position;
    }

    public void setItemRequestFocus(boolean need) {
        mItemRequestFocus = need;
    }

    public void setOnLoopRecyclerViewItemClickListener(OnLoopRecycleViewItemClickListener listener) {
        mOnLoopRecycleViewItemClickListener = listener;
    }

    public void setOnLoopRecyclerViewItemSelectedListener(OnLoopRecycleViewItemSelectedListener listener) {
        mOnLoopRecycleViewItemSelectedListener = listener;
    }

    class LoopViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        View mItem;

        public LoopViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.time_spinner_title);
            mItem = (LinearLayout) itemView.findViewById(R.id.time_spinner_item);
        }
    }

    public interface OnLoopRecycleViewItemClickListener {
        void loopRecycleViewItemClicked(int position);
    }

    public interface OnLoopRecycleViewItemSelectedListener {
        void loopRecycleViewItemSelected(View view, int position);
        void loopRecycleViewItemUnSelected(View view, int position);
    }
}
