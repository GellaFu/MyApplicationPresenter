package com.widget.looprecycleview.myapplicationpresenter.shadow;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.util.Log;
import android.util.Property;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.widget.looprecycleview.myapplicationpresenter.R;

import java.util.Arrays;

/**
 * Utility class that provides ability for drawing focus image above the focused
 * views.
 *
 * @author brlmgalk
 */
public class FocusDrawer implements OnPreDrawListener, TypeEvaluator<ViewGroup.LayoutParams> {

    private static final String TAG = FocusDrawer.class.getSimpleName();
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_ANIMATION = false;

    private static final String STATUS_BAR_PERMISSION = "android.permission.STATUS_BAR_SERVICE";
    private static final String ALERT_WINDOW_PERMISSION = "android.permission.SYSTEM_ALERT_WINDOW";

    private static final long FOCUS_CHANGE_MAX_ANIMATION_DURATION = 100; // ms
    private static final long FOCUS_CHANGE_MIN_ANIMATION_DURATION = 50; // ms
    private static final long FOCUS_CHANGE_VELOCITY_CONSTANT = 2; // pixels/ms

    private static final int LEVEL_SELECTED = 0;
    private static final int LEVEL_FOCUSED = 1;
    private static final int LEVEL_PRESSED = 2;

    private static final int DIR_LTR = 0;
    private static final int DIR_RTL = 1;
    private static final int MIN_LEFT_CARD_FOCUS = 113;
    private static final int MAX_RIGHT_CARD_FOCUS = 1805;

    // Default scale factor for reduce focus padding
    private static final float EDGE_SCREEN_FOCUS_PADDING_SCALE = 0.5f;
    private static boolean sAnimationEnabled = true;
    private static boolean sFixedPosition = false;
    private static Rect sFocusedRect = null;
    private static OnAnimationFinishListener sOnAnimationFinishListener;
    private final Rect mDrawingRect = new Rect();
    private final Rect mLastFocusedRect = new Rect();
    private final Point mScreenSize = new Point();
    private final Point mBigView = new Point();
    /**
     * {@link Rect} that contains the paddings of focus {@link Drawable}
     */
    private final Rect mFocusPaddingsRect = new Rect();
    private final Rect mEdgeScreenFocusPaddingsRect = new Rect();
    private final int[] mFocusedViewLocation = new int[2];
    /**
     * View for drawing focus image
     */
    private View mFocusView;
    private View mFocusParentView;
    private View mWindowFocusObserverView;
    private View mSourceViewForWindowFocusObserverView;
    private Rect mSourceViewForWindowFocusObserverViewRect = new Rect();
    /**
     * View that contains focusable views.
     */
    private View mMainView;
    private boolean mUseRootView;
    /**
     * Decorator View for drawing focus at.
     */
    private ViewGroup mDecorView;
    private ViewGroup mFrameView;
    private View mLastFocused;
    private ViewGroup.MarginLayoutParams mPrevEndValue;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWinParams;
    private int mDisplayWidth = 0;
    /**
     * {@link Drawable} with focus image
     */
    private Drawable mFocusDrawable;
    private boolean mDestroyed = true;
    private boolean mDrawBigFocus = false;
    private ObjectAnimator mFocusChangeAnimator;
    private boolean mIsForceHidden = false;
    // in 4k2k mode of PhotoViewerActivity, FocusDrawer
    // need to be specified the ViewGroup to add mFrameView
    // this logic is only for MTK 5508 and 5329 -- change begin
    private ViewGroup mFocusViewContainer = null;
    private int[] mPrevStates;

    /**
     * Constructor
     */
    public FocusDrawer(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param decorView ViewGroup used for determining padding and movement
     *                  of focus drawer.  Can be null to specify that the mainView's root
     *                  view be used for this purpose.
     */
    public FocusDrawer(Context context, ViewGroup decorView) {
        if (DEBUG) Log.d(TAG, "Creating FocusDrawer");
        if (context != null) {
            mFocusDrawable =
                    context.getResources().getDrawable(R.drawable.image);
            mFocusDrawable.getPadding(mFocusPaddingsRect);
            setEdgeScreenFocusPaddings(EDGE_SCREEN_FOCUS_PADDING_SCALE);
        }

        if (decorView != null) {
            mDecorView = decorView;
        }
    }

    /**
     * whether animation is Enables/Disables
     *
     * @return animationEnabled true/false
     */
    public static boolean isAnimationEnabled() {
        return sAnimationEnabled;
    }

    /**
     * Enables/Disables animation
     *
     * @param animationEnabled true to enable animation
     */
    public static void setAnimationEnabled(boolean animationEnabled) {
        sAnimationEnabled = animationEnabled;
    }

    public static void setFixedPosition(boolean fixedPosition) {
        sFixedPosition = fixedPosition;
    }

    /**
     * Sets rect for drawing focus on it
     *
     * @param focusedRect rect to draw focus on it or null for default behavior
     */
    public static void setFocusedRect(Rect focusedRect) {
        sFocusedRect = focusedRect;
    }

    @Override
    public boolean onPreDraw() {
        if (DEBUG) {
            Log.d(TAG, "onPreDraw");
        }
        boolean willDraw = drawFocusInt();
        if (DEBUG) {
            Log.d(TAG, "onPreDraw: " + willDraw);
        }
        return willDraw;
    }

    /**
     * Sets reduced padding. It is used when focused view is located at the edge of the screen.
     *
     * @param scale Scale factor
     */
    private void setEdgeScreenFocusPaddings(float scale) {
        mEdgeScreenFocusPaddingsRect.top = (int) (mFocusPaddingsRect.top * scale);
        mEdgeScreenFocusPaddingsRect.bottom = (int) (mFocusPaddingsRect.bottom * scale);
        mEdgeScreenFocusPaddingsRect.left = (int) (mFocusPaddingsRect.left * scale);
        mEdgeScreenFocusPaddingsRect.right = (int) (mFocusPaddingsRect.right * scale);
    }

    private void setWinParams() {
        int windowType = WindowManager.LayoutParams.TYPE_APPLICATION;
        try {
            if (DEBUG) {
                Log.d(TAG, "Package: " + mMainView.getContext().getPackageName());
            }
            PackageInfo info = mMainView
                    .getContext()
                    .getPackageManager()
                    .getPackageInfo(mMainView.getContext().getPackageName(),
                            PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                for (String permision : info.requestedPermissions) {
                    if (DEBUG) {
                        Log.d(TAG, "Uses permision: " + permision);
                    }
                    if (STATUS_BAR_PERMISSION.equals(permision)) {
                        // for System UI
                        windowType = WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL;
                        break;
                    }
                    if (ALERT_WINDOW_PERMISSION.equals(permision)) {
                        // for TV Settings window
                        windowType = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                        break;
                    }
                }
            }
        } catch (NameNotFoundException e) {
            if (DEBUG) {
                Log.d(TAG, "setWinParams: NameNotFound");
            }
            // Do nothing
        }
        mWinParams = new WindowManager.LayoutParams(windowType);
        mWinParams.gravity = Gravity.TOP | Gravity.LEFT;

        mWinParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWinParams.format = PixelFormat.TRANSLUCENT;
        mWinParams.windowAnimations = 0;
        mWinParams.setTitle(TAG);
    }

    private Context prepareAsync() {
        setWinParams();
        final Context context = mMainView.getContext();

        // if we don't have a decor view specified
        // get the main view's root view
        if (mDecorView == null) {
            if (mUseRootView) {
                // If we are instructed to use the RootView, do so.
                // Also assume the MainView contains the focused item.
                mDecorView = (ViewGroup) mMainView.getRootView();
            } else {
                // If we are instructed to use the ParentView, do so
                // We assume the MainView does not contain the focused view
                // so change the MainView to be the same as its parent (DecorView)
                mDecorView = (ViewGroup) mMainView.getParent();
                // We still need mMainView to point to the root of view tree
                // to find the proper focused item.
                if (mMainView.getContext() instanceof Activity) {
                    mMainView = ((Activity) mMainView.getContext()).getWindow().getDecorView();
                } else {
                    mMainView = mDecorView;
                }
            }
        }

        Context ctx = mDecorView.getContext();
        if (ctx.getApplicationContext() != null) {
            mWindowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            mWindowManager.getDefaultDisplay().getSize(mScreenSize);
            mDisplayWidth = mScreenSize.x;
        }

        mBigView.set((int) (mScreenSize.x * 0.9), (int) (mScreenSize.y * 0.95));

        if (DEBUG) {
            Log.d(TAG, "Big sizes: " + mScreenSize + ", " + mBigView);
        }

        return context;
    }

    public void init(final View mainView) {
        init(mainView, true);
    }

    /**
     * Initializes the FocusDrawer.
     *
     * @param mainView    The entry-point view to the view-hierarchy.
     * @param useRootView If true, the root of mainView will be used to attach the
     *                    FocusDrawer, also the mainView will be searched to find the
     *                    focused view. If false, the parent view of mainView will be
     *                    used to attach the FocusDrawer and the search for the focused
     *                    view will be performed on the parent of mainView as well.
     */
    public void init(final View mainView, boolean useRootView) {
        if (!mDestroyed) {
            mUseRootView = useRootView;
            return;
        }
        mDestroyed = false;

        mMainView = mainView;
        sAnimationEnabled = true;
        sFixedPosition = false;
        sFocusedRect = null;
        mUseRootView = useRootView;
        mDisplayWidth = 0;

        Thread thread = new Thread() {
            @Override
            public void run() {
                final Context context = prepareAsync();
                mMainView.post(new Runnable() {
                    @Override
                    public void run() {
                        completeInit(context);
                    }
                });
            }
        };
        thread.start();
    }

    /**
     * set the ViewGroup to contain focus drawer in 4k2k mode of PhotoViewerActivity
     *
     * @param container ViewGroup that used to add focus drawer
     */
    public void setViewGroupForFocusView(ViewGroup container) {
        mFocusViewContainer = container;
    }
    // change end

    private void completeInit(Context context) {
        if (mDestroyed) {
            Log.d(TAG, "completeInit: already released");
            return;
        }
        if (context != null && context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity != null && activity.isFinishing()) {
                Log.w(TAG, "The activity is finished, do not need draw focus just return");
                return;
            }
        }
        mFocusView = mWindowManager != null ? new View(context) : new FocusView(context);
        mFocusView.setBackground(mFocusDrawable);
        mFocusView.setVisibility(View.INVISIBLE);

        if (mWindowManager != null) {
            mFrameView = new FrameLayout(mMainView.getContext());
            // this logic is only for MTK 5508 and 5329, in other platform
            // we just use mWindowManager to add mFrameView -- change begin
            if (mFocusViewContainer != null) {
                mFocusViewContainer.addView(mFrameView);
            } else {
                mWindowManager.addView(mFrameView, mWinParams);
            }
            // change end
            mFrameView.addView(mFocusView);
            mWindowFocusObserverView = new FocusView(context);
            mWindowFocusObserverView.setVisibility(View.GONE);
            mSourceViewForWindowFocusObserverView = null;
            mWindowFocusObserverView.setBackgroundResource(R.drawable.image);
            if (mUseRootView) {
                mDecorView.addView(mWindowFocusObserverView, 0);
            } else {
                mDecorView.addView(mWindowFocusObserverView);
            }
        } else {
            mWindowFocusObserverView = mFocusView;
            mDecorView.addView(mFocusView);
        }

        mDecorView.getViewTreeObserver().addOnPreDrawListener(this);
        mFocusParentView = (View) mFocusView.getParent();
        if (mFocusParentView != null) {
            mFocusParentView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Show static selection frame at current focus position if something focused
     *
     * @param show true to show and false to hide
     */
    public void setSecondaryFocus(boolean show) {
        if (mWindowFocusObserverView == null) {
            return;
        }
        if (show) {
            if (mFocusView.getVisibility() == View.VISIBLE) {
                mWindowFocusObserverView.setLayoutParams(mFocusView.getLayoutParams());
                mWindowFocusObserverView.setVisibility(View.VISIBLE);
                View focused = mMainView == null ? null : mMainView.findFocus();
                if (focused instanceof AbsListView) {
                    focused = ((AbsListView) focused).getSelectedView();
                } else if (focused instanceof android.widget.AbsListView) {
                    focused = ((android.widget.AbsListView) focused).getSelectedView();
                }

                if (focused instanceof FocusDrawerFocusable) {
                    focused = ((FocusDrawerFocusable) focused).getFocusView();
                }
                mSourceViewForWindowFocusObserverView = focused;
                if (mSourceViewForWindowFocusObserverView != null) {
                    mSourceViewForWindowFocusObserverView.getGlobalVisibleRect(mSourceViewForWindowFocusObserverViewRect);
                }
            }
        } else {
            mWindowFocusObserverView.setVisibility(View.GONE);
            mSourceViewForWindowFocusObserverView = null;
        }
    }

    private void initFocusAnimator(ViewGroup.LayoutParams... values) {
        if (DEBUG) {
            Log.d(TAG, "Initializing animator: " + Arrays.toString(values));
        }
        mFocusChangeAnimator = ObjectAnimator.ofObject(mFocusView,
                Property.of(View.class, ViewGroup.LayoutParams.class, "layoutParams"),
                this, values);
        mFocusChangeAnimator.setDuration(FOCUS_CHANGE_MAX_ANIMATION_DURATION);
        mFocusChangeAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                if (DEBUG_ANIMATION) {
                    Log.d(TAG, "onAnimationStart");
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (DEBUG_ANIMATION) {
                    Log.d(TAG, "onAnimationEnd");
                }
                FrameLayout.LayoutParams lp
                        = (FrameLayout.LayoutParams) mFocusChangeAnimator.getAnimatedValue();
                if (mFocusView != null) {
                    mFocusView.setLayoutParams(lp);
                }
//                if (TvConfiguration.DEBUG_PV_PERFORMANCE) {
//                    Log.d(TAG,
//                            "["
//                                    + SystemClock.uptimeMillis()
//                                    + "]: "
//                                    + "the next source/tile thumbnail finishes rendering in place");
//                }
                if (sOnAnimationFinishListener != null) {
                    sOnAnimationFinishListener.OnAnimationFinish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    public void registerAnimationFinishListener(OnAnimationFinishListener listener) {
        sOnAnimationFinishListener = listener;
    }

    public void unRegisterAnimationFinishListener() {
        sOnAnimationFinishListener = null;
    }

    public OnAnimationFinishListener getAnimationFinishListener() {
        return sOnAnimationFinishListener;
    }

    @Override
    public ViewGroup.LayoutParams evaluate(float fraction, ViewGroup.LayoutParams startValue,
                                           ViewGroup.LayoutParams endValue) {
        FrameLayout.LayoutParams msv = (FrameLayout.LayoutParams) startValue;
        FrameLayout.LayoutParams mev = (FrameLayout.LayoutParams) endValue;
        msv.setMargins(
                (int) (msv.leftMargin + fraction * (mev.leftMargin - msv.leftMargin)),
                (int) (msv.topMargin + fraction * (mev.topMargin - msv.topMargin)),
                (int) (msv.rightMargin + fraction * (mev.rightMargin - msv.rightMargin)),
                (int) (msv.bottomMargin + fraction * (mev.bottomMargin - msv.bottomMargin))
        );
        return msv;
    }

    public void destroy() {
        if (DEBUG) Log.d(TAG, "destroy");
        if (mDestroyed) {
            return;
        }
        mDestroyed = true;
        if (mFocusView == null) {
            if (DEBUG) Log.d(TAG, "destroy: init not completed yet");
            return;
        }

        if (mWindowManager != null) {
            // this logic is only for MTK 5508 and 5329, in other platform
            // we just use mWindowManager to add mFrameView -- change begin
            if (mFocusViewContainer != null) {
                mFocusViewContainer.removeView(mFrameView);
                mFocusViewContainer = null;
            } else {
                mWindowManager.removeViewImmediate(mFrameView);
            }
            // change end
            mDecorView.removeView(mWindowFocusObserverView);
        }

        if (mFocusParentView != null) {
            mFocusParentView.setVisibility(View.GONE);
        }
        mFocusView.setVisibility(View.GONE);
        mDecorView.getViewTreeObserver().removeOnPreDrawListener(this);
        mDecorView.removeView(mFocusView);
        mFocusView = null;
        mDecorView = null;
        mLastFocused = null;
        mLastFocusedRect.set(0, 0, 0, 0);
        mPrevEndValue = null;
        mMainView = null;
    }

    /**
     * Draws the focus
     *
     * @return true if drawing is required, false otherwise
     */
    private boolean drawFocusInt() {
        if (sFixedPosition) {
            if (DEBUG) Log.d(TAG, "sFixedPosition");
            return true;
        }

        boolean willDraw = true;

        // avoid doing anything after destroy
        if (mDestroyed) {
            if (DEBUG) Log.d(TAG, "DRAW: no-op (destroyed)");
            return true;
        }

        View focused = mMainView == null ? null : mMainView.findFocus();

        if (focused instanceof AbsListView) {
            focused = ((AbsListView) focused).getSelectedView();
        } else if (focused instanceof android.widget.AbsListView) {
            focused = ((android.widget.AbsListView) focused).getSelectedView();
        }

        if (focused instanceof FocusDrawerFocusable) {
            focused = ((FocusDrawerFocusable) focused).getFocusView();
        }

        if (mSourceViewForWindowFocusObserverView != null && mWindowFocusObserverView != null && mWindowFocusObserverView.getVisibility() == View.VISIBLE) {
            if (mSourceViewForWindowFocusObserverView.getVisibility() != View.VISIBLE || !mSourceViewForWindowFocusObserverView.isAttachedToWindow()) {
                mWindowFocusObserverView.setVisibility(View.GONE);
                mSourceViewForWindowFocusObserverView = null;
            } else {
                Rect sourceRect = new Rect();
                mSourceViewForWindowFocusObserverView.getGlobalVisibleRect(sourceRect);
                if (!sourceRect.equals(mSourceViewForWindowFocusObserverViewRect)) {
                    mWindowFocusObserverView.setVisibility(View.GONE);
                    mSourceViewForWindowFocusObserverView = null;
                }
            }
        }

        if (focused != null
                && mFocusView != null
                && focused != mMainView
                && focused.getParent() != null
                && focused.isShown()
                && !isInAnimation(focused)) {

            Rect fixedCardFocus = null;
            if (focused.getParent() instanceof HorizontalGridView) {
                final HorizontalGridView hgv = (HorizontalGridView) focused.getParent();
                final Object lastPositionObj = hgv.getTag(R.id.horizontal_grid_view_tag_last_pos);
                final Object prevDirectionObj = hgv.getTag(R.id.horizontal_grid_view_tag_direction);
                final int currentPosition = hgv.getSelectedPosition();
                hgv.setTag(R.id.horizontal_grid_view_tag_last_pos, currentPosition);
                final int prevDir = prevDirectionObj != null ? (Integer) prevDirectionObj : -1;
                int currentDir = -1;
                if (lastPositionObj != null) {
                    final int lastPosition = (Integer) lastPositionObj;
                    if (currentPosition > lastPosition) {
                        currentDir = DIR_LTR;
                        hgv.setTag(R.id.horizontal_grid_view_tag_direction, currentDir);
                    } else if (currentPosition < lastPosition) {
                        currentDir = DIR_RTL;
                        hgv.setTag(R.id.horizontal_grid_view_tag_direction, currentDir);
                    }
                }
                if (hgv.getScrollState() == 2) {
                    if (currentDir == DIR_LTR) {
                        if (mFocusView.getRight() > MAX_RIGHT_CARD_FOCUS
                                && prevDir == DIR_LTR) {
                            fixedCardFocus = new Rect(mDrawingRect);
                            fixedCardFocus.offsetTo(MAX_RIGHT_CARD_FOCUS - fixedCardFocus.width(), fixedCardFocus.top);
                        } else {
                            fixedCardFocus = null;
                        }
                    } else if (currentDir == DIR_RTL) {
                        if (mFocusView.getLeft() < MIN_LEFT_CARD_FOCUS && prevDir == DIR_RTL) {
                            fixedCardFocus = new Rect(mDrawingRect);
                            fixedCardFocus.offsetTo(MIN_LEFT_CARD_FOCUS, fixedCardFocus.top);
                        } else {
                            fixedCardFocus = null;
                        }
                    }
                    if (fixedCardFocus == null) {
                        return true;
                    }
                } else {
                    fixedCardFocus = null;
                }
            } else {
                fixedCardFocus = null;
            }

            if (sFocusedRect == null) {
                if (fixedCardFocus != null) {
                    mDrawingRect.set(fixedCardFocus);
                } else {
                    focused.getGlobalVisibleRect(mDrawingRect);
                    if (mDisplayWidth > 0) {
                        // We have the correct display/window
                        if ((focused.getLeft() <= 0 && focused.getRight() <= 0)
                                || (focused.getLeft() >= mDisplayWidth && focused.getRight() >= mDisplayWidth)) {
                            // The rect is wrong , Use the last focus rect"
                            mDrawingRect.set(mLastFocusedRect);
                        }
                    }
                }
            } else {
                mDrawingRect.set(sFocusedRect);
            }

            if (DEBUG) {
                Log.d(TAG, "mDrawingRect: " + mDrawingRect + ", prev: " + mLastFocusedRect);
            }

            if (mDrawingRect.width() >= mBigView.x && mDrawingRect.height() >= mBigView.y / 2
                    || mDrawingRect.height() >= mBigView.y && mDrawingRect.width() >= mBigView.x / 2) {
                // mDrawBigFocus is false, skip to draw big focus.
                if (!mDrawBigFocus) {
                    if (DEBUG) Log.d(TAG, "Focus is too big. Skipping... " + mDrawingRect);
                    if (mFocusView.getVisibility() != View.GONE) {
                        if (mFocusParentView != null) {
                            mFocusParentView.setVisibility(View.GONE);
                        }
                        mFocusView.setVisibility(View.GONE);
                        return false;
                    }
                    return true;
                }
            } else if (mDrawingRect.width() <= 0 && mDrawingRect.height() <= 0) {
                // we need this log to debug
                Log.i(TAG, "Focus too small. Skipping... " + mDrawingRect);
                if (mFocusView.getVisibility() != View.GONE) {
                    if (mFocusParentView != null) {
                        mFocusParentView.setVisibility(View.GONE);
                    }
                    mFocusView.setVisibility(View.GONE);
                    return false;
                }
                return true;
            }

            int states[] = focused.getDrawableState();
            if (DEBUG) Log.d(TAG, "states: " + Arrays.toString(states));
            states = updateStates(states);
            if (DEBUG) Log.d(TAG, "new states: " + Arrays.toString(states));

            boolean sameRect = mDrawingRect.equals(mLastFocusedRect);
            if (mLastFocused == focused && sameRect && Arrays.equals(mPrevStates, states)) {
                if (DEBUG) Log.d(TAG, "Still the same focus view: aborting");
                if (!sAnimationEnabled && mFocusChangeAnimator != null && mFocusChangeAnimator.isRunning()) {
                    mFocusChangeAnimator.cancel();
                    if (mPrevEndValue != null) {
                        mFocusView.setLayoutParams(mPrevEndValue);
                        if (DEBUG) Log.d(TAG, "return false");
                        return false;
                    }
                } else {
                    if (DEBUG) Log.d(TAG, "return true");
                    return true;
                }
            }

            mLastFocused = focused;

            updateBg(states);
            mPrevStates = states;

            if (DEBUG) Log.d(TAG, "DRAW: FOCUSED");

            ViewGroup.LayoutParams lp = mFocusView.getLayoutParams();
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams startValue = (ViewGroup.MarginLayoutParams) lp;
                ViewGroup.MarginLayoutParams endValue = new FrameLayout.LayoutParams(startValue);

                int decorPaddingTop = mDecorView.getPaddingTop();
                int decorPaddingLeft = mDecorView.getPaddingLeft();

                mFocusedViewLocation[0] = mDrawingRect.left;
                mFocusedViewLocation[1] = mDrawingRect.top;

                int left = mFocusedViewLocation[0] - mFocusPaddingsRect.left - decorPaddingLeft;
                left = left < -mEdgeScreenFocusPaddingsRect.left ? -mEdgeScreenFocusPaddingsRect.left : left;
                int right = mFocusedViewLocation[0] + mDrawingRect.width() + mFocusPaddingsRect.right
                        - decorPaddingLeft;
                right = right > mScreenSize.x + mEdgeScreenFocusPaddingsRect.right
                        ? mScreenSize.x + mEdgeScreenFocusPaddingsRect.right
                        : right;
                endValue.leftMargin = left;
                endValue.rightMargin = mScreenSize.x - right;

                int top = mFocusedViewLocation[1] - mFocusPaddingsRect.top
                        - decorPaddingTop;
                top = top < -mEdgeScreenFocusPaddingsRect.top ? -mEdgeScreenFocusPaddingsRect.top : top;
                int bottom = mFocusedViewLocation[1] + mDrawingRect.height() + mFocusPaddingsRect.bottom
                        - decorPaddingTop;
                bottom = bottom > mScreenSize.y + mEdgeScreenFocusPaddingsRect.bottom
                        ? mScreenSize.y + mEdgeScreenFocusPaddingsRect.bottom
                        : bottom;
                endValue.topMargin = top;
                endValue.bottomMargin = mScreenSize.y - bottom;
                if (DEBUG) {
                    Log.d(TAG, "Margins: " + endValue.leftMargin + ", " + endValue.topMargin + ", "
                            + endValue.rightMargin + ", " + endValue.bottomMargin);
                }
                if (mFocusChangeAnimator != null) {
                    // focus rect will not animate from invisible states
                    if (!mFocusView.isShown()) {
                        mFocusView.setLayoutParams(endValue);
                        willDraw = false;
                    } else {
                        if (mFocusChangeAnimator.isRunning()) {
                            mFocusChangeAnimator.cancel();
                            if (mPrevEndValue != null) {
                                mFocusView.setLayoutParams(mPrevEndValue);
                                willDraw = true;
                            }
                            if (DEBUG_ANIMATION) Log.d(TAG, "Animator restarts");
                        }
                        if (sAnimationEnabled && !sameRect) {
                            if (DEBUG) Log.d(TAG, "Animator starts");
                            // get animation duration based on change of focus rects
                            final long duration = getAnimationDuration(mLastFocusedRect, mDrawingRect);
                            mFocusChangeAnimator.setDuration(duration);
                            mFocusChangeAnimator.setObjectValues(endValue);
                            mFocusChangeAnimator.start();
                        } else if (!sameRect) {
                            if (DEBUG) Log.d(TAG, "setLayoutParams");
                            mFocusView.setLayoutParams(endValue);
                            willDraw = false;
                        } else {
                            willDraw = true;
                        }
                    }
                } else {
                    // first time no need to animate, just init animator and draw first time focus
                    if (sAnimationEnabled) {
                        initFocusAnimator(endValue);
                    }
                    mFocusView.setLayoutParams(endValue);
                    willDraw = true;
                }
                mPrevEndValue = new FrameLayout.LayoutParams(endValue);
            } else {
                Log.e(TAG, "drawFocus requires that the focus view use MarginLayoutParams");
            }
            if (!mIsForceHidden && mFocusView.getVisibility() != View.VISIBLE) {
                if (mFocusParentView != null) {
                    mFocusParentView.setVisibility(View.VISIBLE);
                }
                mFocusView.setVisibility(View.VISIBLE);
                willDraw = false;
            }
            mLastFocusedRect.set(mDrawingRect);
        } else if (mFocusView != null) {
            if (DEBUG) Log.d(TAG, "DRAW: NOT FOCUSED");
            if (mFocusChangeAnimator != null && mFocusChangeAnimator.isRunning()) {
                mFocusChangeAnimator.cancel();
                willDraw = false;
            }

            if (mFocusView.getVisibility() != View.GONE) {
                if (mFocusParentView != null) {
                    mFocusParentView.setVisibility(View.GONE);
                }
                mFocusView.setVisibility(View.GONE);
                willDraw = false;
            }
            mPrevStates = null;
            mLastFocused = null;
            mLastFocusedRect.setEmpty();
        }

        if (DEBUG) Log.d(TAG, "will draw: " + willDraw);
        return willDraw;
    }

    private int[] updateStates(int states[]) {
        int observerStates[] = mWindowFocusObserverView.getDrawableState();
        if (DEBUG) Log.d(TAG, "observer states: " + Arrays.toString(observerStates));
        if (Arrays.binarySearch(observerStates, android.R.attr.state_window_focused) < 0
                && Arrays.binarySearch(states, android.R.attr.state_window_focused) >= 0) {
            int origStates[] = states;
            states = new int[origStates.length - 1];
            for (int i = 0, j = 0; i < states.length; i++, j++) {
                if (origStates[j] == android.R.attr.state_window_focused) {
                    j++;
                }
                states[i] = origStates[j];
            }
        } else if (Arrays.binarySearch(observerStates, android.R.attr.state_window_focused) >= 0
                && Arrays.binarySearch(states, android.R.attr.state_window_focused) < 0) {
            int origStates[] = states;
            states = new int[origStates.length + 1];
            System.arraycopy(origStates, 0, states, 0, origStates.length);
            states[origStates.length] = android.R.attr.state_window_focused;
            Arrays.sort(states);
        }
        return states;
    }

    private boolean updateBg(int states[]) {
        boolean winFocus = Arrays.binarySearch(states, android.R.attr.state_window_focused) >= 0;
        int level = LEVEL_SELECTED;
        if (winFocus) {
            boolean pressed = Arrays.binarySearch(states, android.R.attr.state_pressed) >= 0;
            level = pressed ? LEVEL_PRESSED : LEVEL_FOCUSED;
        }
        if (DEBUG) Log.d(TAG, "updateBg: " + level);
        return mFocusDrawable.setLevel(level);
    }

    private long getAnimationDuration(Rect start, Rect end) {
        // calculate the animation duration between the min and max
        // durations based on the distance the focus indicator has to move
        long duration = FOCUS_CHANGE_MAX_ANIMATION_DURATION;
        final double dx = end.left - start.left;
        final double dy = end.top - start.top;
        final double dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        final long time = Math.round(dist / FOCUS_CHANGE_VELOCITY_CONSTANT);
        if (time < duration) {
            duration = (time > FOCUS_CHANGE_MIN_ANIMATION_DURATION
                    ? time : FOCUS_CHANGE_MIN_ANIMATION_DURATION);
        }
        if (DEBUG_ANIMATION) Log.d(TAG, "animation distance: " + dist + ", duration: " + duration);
        return duration;
    }

    private boolean isInAnimation(View focused) {
        View view = focused;
        while (view != null) {
            if (view.getTranslationX() != 0 || view.getTranslationY() != 0) {
                return true;
            }
            ViewParent parent = view.getParent();
            view = (parent instanceof View) ? (View) parent : null;
        }
        return false;
    }

    /**
     * set mDrawBigFocus to be true can make FocusDrawer draw big focus.
     */
    public void drawBigFocus() {
        mDrawBigFocus = true;
    }

    /**
     * Force the focus view to be hidden or restored to normal operation.
     *
     * @param hide true to hide the view, false to restore normal operation
     */
    public void forceHidden(boolean hide) {
        if (hide && mIsForceHidden != hide) {
            if (mFocusParentView != null) {
                mFocusParentView.setVisibility(View.INVISIBLE);
            }
            if (mFocusView != null) {
                mFocusView.setVisibility(View.INVISIBLE);
            }
        }
        mIsForceHidden = hide;
    }

    /**
     * Interface to provide view to show focus frame for it instead of focused/selected view.
     */
    public interface FocusDrawerFocusable {
        /**
         * Returns View to should focus frame around it or null to hide focus
         *
         * @return View to should focus frame around it or null to hide focus
         */
        View getFocusView();
    }

    /**
     * This interface be used to listen the Animation End.
     * The method OnAnimationFinish while be invoke when Animation End change.
     */
    public interface OnAnimationFinishListener {
        void OnAnimationFinish();
    }

    /**
     * Special view which is used for drawing the focus
     */
    private class FocusView extends View {

        public FocusView(Context context) {
            super(context);
        }

        @Override
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);

            if (DEBUG) Log.d(TAG, "focus changed, now: " + hasWindowFocus);

            // draw focus each time the window lose or obtain the focus
            drawFocusInt();
        }
    }
}
