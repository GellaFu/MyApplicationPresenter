package com.widget.looprecycleview.myapplicationpresenter;

import android.app.Activity;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.ItemBridgeAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnChildViewHolderSelectedListener;
import android.support.v17.leanback.widget.ShadowOverlayContainer;
import android.support.v17.leanback.widget.VerticalGridView;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.widget.looprecycleview.myapplicationpresenter.shadow.FocusDrawer;

public class MainActivity extends Activity {

    private final static String TAG = "fuyouping";
    private final boolean DEBUG = true;
    VerticalGridView mVercialGridVIew;
    Button mButton;
    HorizontalGridView mHorizontalGridView;
    ArrayObjectAdapter mCalendarArrayObjectAdapter;
    ArrayObjectAdapter mReminderArrayObjectAdapter;
    ArrayObjectAdapter mRowArrayObjectAdapter;
    HeaderItem mHeadItem1;
    HeaderItem mHeadItem2;
    private ItemBridgeAdapter mItemBridgeAdapter;
    ListRow mListRow;

    CalendarPresenter mCalendarPresenter;
    ReminderPresenter mReminderPresenter;
    ReminderPresenterSelector mReminderPresenterSelector;
    ClassPresenterSelector mClassPresenterSelector;
    CalendarListRowPresenter mCalendarListRowPresenter;
    ReminderListRowPresenter mReminderListRowPresenter;
    ReminderListRow mReminderListRow;
    CalendarRow mCalendarRow;
    ClassPresenterSelector mReminderClassPresenterSelector;
    NewReminderPresenter mNewReminderPresenter;
    FocusDrawer mFocusDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       //initUI();
        //loadRows();
        //initData();
        //init();
        //initUINew();initDataNew();
        test4();
    }

    private void test4() {
        Log.i(TAG, "test4");
        initUINewItemSelector();
        initDataNewItemSelector();
    }

    private void test3() {
        Log.i(TAG, "test3");
        initUINew();
        initDataNew();
    }


    private void test2() {
        Log.i(TAG, "test2");
        initUI();
        initData();
    }

    private void test1() {
        Log.i(TAG, "test1");
        //need to change R.layout.activity_main to R.layout.activity_main_hori
        initClassPresenterSelector();
    }

    private void init() {
        mHorizontalGridView = (HorizontalGridView) findViewById(R.id.list_view);
        mReminderPresenterSelector = new ReminderPresenterSelector(this);
        mRowArrayObjectAdapter = new ArrayObjectAdapter(mReminderPresenterSelector);
        mItemBridgeAdapter = new ItemBridgeAdapter(mRowArrayObjectAdapter);
        mHorizontalGridView.setAdapter(mItemBridgeAdapter);
    }

    private void initClassPresenterSelector() {
        Log.i(TAG, "initClassPresenterSelector");
        mHorizontalGridView = (HorizontalGridView) findViewById(R.id.list_view);

        mCalendarPresenter = new CalendarPresenter(this);
        mReminderPresenter = new ReminderPresenter(this);
        mClassPresenterSelector = new ClassPresenterSelector();
        mClassPresenterSelector.addClassPresenter(ReminderData.class, mReminderPresenter);
        mClassPresenterSelector.addClassPresenter(CalendarData.class, mCalendarPresenter);

        mRowArrayObjectAdapter = new ArrayObjectAdapter(mClassPresenterSelector);
        mItemBridgeAdapter = new ItemBridgeAdapter(mRowArrayObjectAdapter);
        mHorizontalGridView.setAdapter(mItemBridgeAdapter);
        initData();
    }

    private void initUI() {
        mVercialGridVIew = (VerticalGridView) findViewById(R.id.list_view);
        mCalendarPresenter = new CalendarPresenter(this);
        mReminderPresenter = new ReminderPresenter(this);

        mCalendarArrayObjectAdapter = new ArrayObjectAdapter(mCalendarPresenter);
        mReminderArrayObjectAdapter = new ArrayObjectAdapter(mReminderPresenter);

        mRowArrayObjectAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        mHeadItem1 = new HeaderItem("1111");
        mHeadItem2 = new HeaderItem("2222222222");
        mRowArrayObjectAdapter.add(new ListRow(mHeadItem1, mCalendarArrayObjectAdapter));
        mRowArrayObjectAdapter.add(new ListRow(mHeadItem2, mReminderArrayObjectAdapter));
        mItemBridgeAdapter = new ItemBridgeAdapter(mRowArrayObjectAdapter);
        mVercialGridVIew.setAdapter(mItemBridgeAdapter);

    }

    private void initUINew() {
        mVercialGridVIew = (VerticalGridView) findViewById(R.id.list_view);

        mClassPresenterSelector = new ClassPresenterSelector();
        mCalendarListRowPresenter = new CalendarListRowPresenter(this);
        mReminderListRowPresenter = new ReminderListRowPresenter(0, false);
        mHeadItem1 = new HeaderItem("1111");
        mHeadItem2 = new HeaderItem("2222222222");
       // mCalendarRow = new CalendarRow(mHeadItem1, new CalendarAdapter());
      //  mReminderListRow = new ReminderListRow(mHeadItem2, new ReminderAdapter());

        mClassPresenterSelector.addClassPresenter(CalendarRow.class, mCalendarListRowPresenter);
        mClassPresenterSelector.addClassPresenter(ReminderListRow.class, mReminderListRowPresenter);

        mCalendarPresenter = new CalendarPresenter(this);
        mReminderPresenter = new ReminderPresenter(this);

        mCalendarArrayObjectAdapter = new ArrayObjectAdapter(mCalendarPresenter);
        mReminderArrayObjectAdapter = new ArrayObjectAdapter(mReminderPresenter);

        mCalendarRow = new CalendarRow(mHeadItem1, mCalendarArrayObjectAdapter);
        mReminderListRow = new ReminderListRow(mHeadItem2, mReminderArrayObjectAdapter);

        mRowArrayObjectAdapter = new ArrayObjectAdapter(mClassPresenterSelector);

        mRowArrayObjectAdapter.add(mCalendarRow);
        mRowArrayObjectAdapter.add(mReminderListRow);

        mItemBridgeAdapter = new ItemBridgeAdapter(mRowArrayObjectAdapter);
        mVercialGridVIew.setAdapter(mItemBridgeAdapter);

    }

    private void initUINewItemSelector() {
        mVercialGridVIew = (VerticalGridView) findViewById(R.id.list_view);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCLick();
            }
        });

        //For all rows. There are two rows will be shown and they have different class
        //Need use classPresenterSelector as there are two different rows
        mClassPresenterSelector = new ClassPresenterSelector();

        //init two list ListRowPresenter
        mCalendarListRowPresenter = new CalendarListRowPresenter(this);
        mReminderListRowPresenter = new ReminderListRowPresenter(0, false);
        //set the title of rows
        mHeadItem1 = new HeaderItem("1111");
        mHeadItem2 = new HeaderItem("2222222222");

        mClassPresenterSelector.addClassPresenter(CalendarRow.class, mCalendarListRowPresenter);
        mClassPresenterSelector.addClassPresenter(ReminderListRow.class, mReminderListRowPresenter);

        //Init Item Presenter for first row.
        mCalendarPresenter = new CalendarPresenter(this);

        //Init Item Presenter for the second row, there are two types item in the row, so need use
        //classPresenterSelector
        mReminderClassPresenterSelector = new ClassPresenterSelector();
        mReminderPresenter = new ReminderPresenter(this);
        mNewReminderPresenter = new NewReminderPresenter(this);
        mReminderClassPresenterSelector.addClassPresenter(ReminderData.class, mReminderPresenter);
        mReminderClassPresenterSelector.addClassPresenter(NewReminderData.class, mNewReminderPresenter);

        //Init ArrayObjectAdapter, bind item data to view by presenter
        mCalendarArrayObjectAdapter = new ArrayObjectAdapter(mCalendarPresenter);
        mReminderArrayObjectAdapter = new ArrayObjectAdapter(mReminderClassPresenterSelector);

        //Init ListRow, set row data and head title.
        mCalendarRow = new CalendarRow(mHeadItem1, mCalendarArrayObjectAdapter);
        mReminderListRow = new ReminderListRow(mHeadItem2, mReminderArrayObjectAdapter);

        mRowArrayObjectAdapter = new ArrayObjectAdapter(mClassPresenterSelector);

        mRowArrayObjectAdapter.add(mCalendarRow);
        mRowArrayObjectAdapter.add(mReminderListRow);

        //set adapter for VerticalGridView
        mItemBridgeAdapter = new ItemBridgeAdapter(mRowArrayObjectAdapter);
        mVercialGridVIew.setAdapter(mItemBridgeAdapter);
        mVercialGridVIew.setWindowAlignmentOffsetPercent(30);
       // mVercialGridVIew.setItemMargin(66);
        //mVercialGridVIew.setHorizontalMargin(30);
       // mReminderListRowPresenter.ViewHolder.
       // disableClipOnParents(mVercialGridVIew);
        //ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
        //mVercialGridVIew.setFocusScrollStrategy(2);
    }


    private static View getRootView(Activity context) {
        return ((ViewGroup)context.findViewById(android.R.id.content)).getChildAt(0);
    }

    private void initViewTree() {
        ViewTreeObserver treeObserver = mVercialGridVIew.getViewTreeObserver();
        if (treeObserver.isAlive()) {
            treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    for (int i = 0; i < mVercialGridVIew.getChildCount(); i ++) {
                        Log.i(TAG, "fffffffff:" + mVercialGridVIew.getChildAt(i));
                        if (mVercialGridVIew.getChildAt(i) != null && mVercialGridVIew instanceof ViewGroup) {
                            Log.i(TAG, "fffffffff: +" + mVercialGridVIew.getChildAt(i));
                            ViewGroup vg = (ViewGroup) mVercialGridVIew.getChildAt(i);
                            //disableClipOnParents(vg);
                            vg.setClipToPadding(false);
                            vg.setClipChildren(false);
                        }
                    }
                }
            });
        } else {
            Log.w(TAG, " treeObserver isn't alive ");
        }
    }

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

    private void onCLick() {

        Log.i(TAG, "fffffffffffffffffff :" + mItemBridgeAdapter.getItemCount());
        Log.i(TAG, "fffffffffffffffffff :" + mRowArrayObjectAdapter.size());
        Log.i(TAG, "fffffffffffffffffff :" + mVercialGridVIew.getChildCount());


        Log.i(TAG, "presenter 00000 :" + mRowArrayObjectAdapter.getPresenter(mRowArrayObjectAdapter.get(0)));
        Log.i(TAG, "presenter 11111:" + mRowArrayObjectAdapter.getPresenter(mRowArrayObjectAdapter.get(1)));

        if (mRowArrayObjectAdapter.getPresenter(mRowArrayObjectAdapter.get(0)) instanceof CalendarListRowPresenter) {
            CalendarListRowPresenter listRowPresenter =
                    (CalendarListRowPresenter) mRowArrayObjectAdapter.getPresenter(mRowArrayObjectAdapter.get(0));
            listRowPresenter.setSelectedPosition(0);
        }

        if (mRowArrayObjectAdapter.getPresenter(mRowArrayObjectAdapter.get(1)) instanceof ReminderListRowPresenter) {
            ReminderListRowPresenter listRowPresenter =
                    (ReminderListRowPresenter) mRowArrayObjectAdapter.getPresenter(mRowArrayObjectAdapter.get(1));
            listRowPresenter.setSelectedPosition(2);
        }
    }

    private void initDataNewItemSelector() {
        Log.i(TAG, "fffffffff, initDataNew");
        ReminderData data1 =
                new ReminderData("1nnnn1111113333333333333");
        ReminderData data2 = new ReminderData("122nnnn1");
        ReminderData data3 = new ReminderData("133nnnnnn3");
        ReminderData data4 = new ReminderData("144nnnnnn3");
        ReminderData data5 = new ReminderData("155nnnnnn3");
        ReminderData data6 = new ReminderData("166nnnnnn3");
        ReminderData data7 = new ReminderData("177nnnnnn3");
        ReminderData data81 = new ReminderData("188nnnnnn3");
        ReminderData data9 = new ReminderData("199nnnnnn3");


        CalendarData data8 = new CalendarData("4444");
       // NewReminderData newData = new NewReminderData("success");
        mCalendarArrayObjectAdapter.add(data8);
        mCalendarArrayObjectAdapter.add(data8);
        mCalendarArrayObjectAdapter.add(data8);
        mCalendarArrayObjectAdapter.add(data8);
        mCalendarArrayObjectAdapter.add(data8);
        mCalendarArrayObjectAdapter.add(data8);
        mCalendarArrayObjectAdapter.add(data8);
        mCalendarArrayObjectAdapter.add(data8);
        mCalendarArrayObjectAdapter.add(data8);
        mCalendarArrayObjectAdapter.add(data8);

        mReminderArrayObjectAdapter.add(data1);
        mReminderArrayObjectAdapter.add(data2);
        mReminderArrayObjectAdapter.add(data3);
        mReminderArrayObjectAdapter.add(data4);
        mReminderArrayObjectAdapter.add(data5);
        mReminderArrayObjectAdapter.add(data6);
        mReminderArrayObjectAdapter.add(data7);
        mReminderArrayObjectAdapter.add(data9);
        mReminderArrayObjectAdapter.add(data9);
        mReminderArrayObjectAdapter.add(data9);
        mReminderArrayObjectAdapter.add(data9);
        mReminderArrayObjectAdapter.add(data9);



        // mReminderArrayObjectAdapter.add(newData);
        mItemBridgeAdapter.notifyDataSetChanged();
    }

    private void initDataNew() {
        Log.i(TAG, "initDataNew");
        ReminderData data1 = new ReminderData("1nnnn1");
        ReminderData data2 = new ReminderData("122nnnn1");
        ReminderData data3 = new ReminderData("133nnnnnn3");
        CalendarData data4 = new CalendarData("4444");
        mCalendarArrayObjectAdapter.add(data4);
        mReminderArrayObjectAdapter.add(data1);
        mReminderArrayObjectAdapter.add(data2);
        mReminderArrayObjectAdapter.add(data3);
        mItemBridgeAdapter.notifyDataSetChanged();
    }

    private void initData() {
        ReminderData data1 = new ReminderData("1nnnn1");
        ReminderData data2 = new ReminderData("122nnnn1");
        ReminderData data3 = new ReminderData("133nnnnnn3");
        CalendarData data4 = new CalendarData("4444");

        mRowArrayObjectAdapter.add(data1);
        mRowArrayObjectAdapter.add(data2);
        mRowArrayObjectAdapter.add(data3);
        mRowArrayObjectAdapter.add(data4);
        mItemBridgeAdapter.notifyDataSetChanged();
    }

    private void loadRows() {
        if (DEBUG) {
            Log.d(TAG, "in loadRows()");
        }
        // create a presenter selector for all types of rows we will support
        final ClassPresenterSelector rowsPresenterSelector = new ClassPresenterSelector();

        CalendarListRowPresenter listRowPresenter = new CalendarListRowPresenter(this);

        rowsPresenterSelector.addClassPresenter(ListRow.class, listRowPresenter);

        // create the Conversense row and start the loader to fill the data
        mCalendarPresenter = new CalendarPresenter(this);
        mReminderPresenter = new ReminderPresenter(this);
        mCalendarArrayObjectAdapter = new ArrayObjectAdapter(mCalendarPresenter);
        mReminderArrayObjectAdapter = new ArrayObjectAdapter(mReminderPresenter);
        mRowArrayObjectAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        mHeadItem1 = new HeaderItem("1111");
        mHeadItem2 = new HeaderItem("2222222222");
        mListRow = new ListRow(mHeadItem1, mCalendarArrayObjectAdapter);

        final ArrayObjectAdapter conversenseRowAdapter = new ArrayObjectAdapter(rowsPresenterSelector);
        conversenseRowAdapter.add(mListRow);
        //getLoaderManager().initLoader(CONVERSENSE_CURSOR_LOADER_ID, null, this);

        // If presenter selector is null, adapter ps will be used
        final ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(conversenseRowAdapter, rowsPresenterSelector);
       // itemBridgeAdapter.setAdapterListener(mBridgeAdapterListenerTiles);

        mVercialGridVIew.setAdapter(itemBridgeAdapter);
        mVercialGridVIew.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
            @Override
            public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
                super.onChildViewHolderSelected(parent, child, position, subposition);
                if (child != null) {
                    //setRowViewSelected((ItemBridgeAdapter.ViewHolder) child, true, false);
                    Log.i(TAG, "ttttttttttttttttttttttttttttt");
                }
            }
        });
        mVercialGridVIew.setSelectedPosition(0);

    }
}
