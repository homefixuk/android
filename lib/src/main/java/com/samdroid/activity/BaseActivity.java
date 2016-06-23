package com.samdroid.activity;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
//import com.balysv.materialmenu.MaterialMenuDrawable;
//import com.balysv.materialmenu.MaterialMenuIconToolbar;
import com.melnykov.fab.FloatingActionButton;
import com.samdroid.R;
import com.samdroid.fragment.BaseFragment;
import com.samdroid.layout.MyFrameLayout;
import com.samdroid.listener.ActionBarItemOnTouchListener;
import com.samdroid.math.Vector;
import com.samdroid.network.NetworkManager;
import com.samdroid.string.Strings;
import com.samdroid.view.ScreenUtils;
import com.samdroid.view.SelectableRoundedImageView;

/**
 * Base Activity
 *
 * @author samuel
 */
public abstract class BaseActivity extends AppCompatActivity {

    // TAG used for logging
    protected String TAG = "BaseActivity";

    // Context for the activity
    protected Context mContext;

    // whether the activity is trying to be closing by our code
    protected boolean closingActivity = false;

    // store if the app is open
    public static boolean isAppOpen = false;

    // store the screen info
    protected static int mScreenSize = -1;
    protected static ScreenUtils.ScreenDensity mScreenDensity = null;

    // the layout template used for the activity
    protected int mainLayoutId = -1;

    // FragmentManager to control the fragments displayed
    protected FragmentManager mFragmentManager;

    // base fragment to be used by any implementing activity
    protected BaseFragment mFragment;

    protected boolean
            isNavDrawerOpen = false, // store if the navigation drawer menu is open or closed, default to closed
            isUsingMaterialMenu, // true if using the material menu, false if using custom action bar
            isUsingFab, // stores if there is a main action button for the activity
            showToolbarShadow = false; // whether or not to show the activity top bar shadow

    // the title to show in the action bar
    protected String mActionbarTitle = "";

    // the Toolbar used as the action bar
    protected Toolbar mToolbar;

    // the content FrameLayout ID
    protected int mContentFrameResId = R.id.content;

    // the content FrameLayout used to display fragments in
    protected MyFrameLayout mContentFrame;

    // drawer menu layout
    protected DrawerLayout mDrawerLayout;

    // the drawer menu views
    protected ImageView mDrawerCoverImage;
    protected SelectableRoundedImageView mDrawerProfileImage;
    protected TextView mDrawerUserName, mDrawerUserUsername;

    // the floating action button
    protected FloatingActionButton mFab;

    // the top bar shadow
    protected View mTopShadow;

    // the last time we displayed a toast
    protected Long mLastToastTime = null;

    // the device width and height
    protected int mWidth, mHeight;

    // the bundle to give to the profile fragment with variables in
    final protected Bundle mFragmentBundle = new Bundle();

//     material tool bar menu
//    protected MaterialMenuIconToolbar mMaterialMenu;

    // the view in the action bar indicating the back action
    // if not using the material menu
    protected View mActionBarBack, mActionBarBackCaret;

    // the action bar title and sub-title text views from a custom action bar view
    protected TextView mActionBarTitleTxt, mActionBarSubTitleTxt;

    // dialog that can be used in by any extending class
    protected MaterialDialog mDialog;

    /**
     * All the pages in the app
     */
    public enum Activities {
        HOME, UNKNOWN;

        public String safelyGetName() {
            return this != null ? this.name() : UNKNOWN.name();
        }
    }

    // what this activity is and where this activity came from
    protected Activities thisActivity = Activities.UNKNOWN, fromActivity = Activities.UNKNOWN;
    final public static String fromActivityId = "fromActivityId";

    /**
     * Empty constructor so Activities can be recreated after being destroyed in background
     */
    protected BaseActivity() {
    }

    /**
     * Constructor giving a specific layout.
     * <p>
     * Needs to have:
     * - a toolbar with the id "toolbar"
     * - a drawer layout with the id "drawer_layout"
     * - a FrameLayout with the id "content"
     * - a main action button with the id "main_action_view"
     *
     * @param TAG
     * @param layoutId
     */
    protected BaseActivity(String TAG, int layoutId, boolean useCustomActionbar, boolean showToolBarShadow, Activities thisActivity) {
        this();
        this.TAG = TAG;
        this.mainLayoutId = layoutId;
        this.thisActivity = thisActivity;
        this.isUsingMaterialMenu = !useCustomActionbar;
        this.showToolbarShadow = showToolBarShadow;
    }

    /**
     * Constructor to specify to use a profile or non-profile main layout.
     *
     * @param TAG
     * @param profile
     */
    public BaseActivity(
            String TAG,
            boolean profile,
            boolean useCustomActionbar,
            boolean showToolBarShadow,
            Activities thisActivity) {
        this(TAG,
                profile ? R.layout.profile_main_layout : R.layout.non_profile_main_layout,
                useCustomActionbar,
                showToolBarShadow,
                thisActivity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_PROGRESS);

        // set the content view
        if (mainLayoutId > 0) setContentView(mainLayoutId);

        mContext = this;

        //		// Device model
        //		String PhoneModel = android.os.Build.MODEL;
        //
        //		// Android version
        //		String AndroidVersion = android.os.Build.VERSION.RELEASE;
        //
        //		// Get the SDK API level
        //		String sdkAPI = "" + android.os.Build.VERSION.SDK_INT;

        // get the screen size
        if (mScreenSize == -1) mScreenSize = ScreenUtils.getScreenSize(this);

        // get the screen density
        if (mScreenDensity == null) mScreenDensity = ScreenUtils.getScreenDensityType(this);

        //		MyLog.i(TAG, "Screen size: " + mScreenSize);
        //		MyLog.i(TAG, "Screen density: " + mScreenDensity.name());

        // get the screen dimensions
        Vector dims = ScreenUtils.getScreenDimensions(this);
        mWidth = (int) dims.getX();
        mHeight = (int) dims.getY();

        // get the intent
        Intent i = getIntent();

        // if it has a fromActivity
        if (i != null && i.hasExtra(fromActivityId) && i.getStringExtra(fromActivityId) != null) {
            // set the fromActivity
            try {
                fromActivity = Activities.valueOf(i.getStringExtra(fromActivityId));
            } catch (Exception e) {
                fromActivity = Activities.UNKNOWN;
            }

        } else {
            // else set the from activity to unknown
            fromActivity = Activities.UNKNOWN;
        }

        // get the fragment manager
        mFragmentManager = getFragmentManager();

        // get the toolbar layout and set it to be the action bar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        // set the title colour to white
        mToolbar.setTitleTextColor(Color.WHITE);

        // set the toolbar as the support action bar
        setSupportActionBar(mToolbar);

        // set the default click listener
        setNavigationToolbarClickListener();

        // set the default title
        mActionbarTitle = getString(R.string.app_name);

        // get the top bar shadow
        mTopShadow = findViewById(R.id.activity_frame_top_shadow);

        // hide the top shadow if we are not to show it
        if (mTopShadow != null && !showToolbarShadow) mTopShadow.setVisibility(View.GONE);

        // setup the drawer layout
        mDrawerLayout = ((DrawerLayout) findViewById(R.id.drawer_layout));

        // set the colour to cover the main content with
        mDrawerLayout.setScrimColor(getResources().getColor(R.color.drawer_scrim));

        // set the drawer open/close/slide listener to call the activity functions
        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                onNavDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                onNavDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                onNavDrawerClosed(drawerView);
            }
        });

        // get the navigation menu views //

        // get the cover and profile image views
        mDrawerCoverImage = (ImageView) mDrawerLayout.findViewById(R.id.menu_cover_image);
        mDrawerProfileImage = (SelectableRoundedImageView) mDrawerLayout.findViewById(R.id.profile_image);

        // get the user name and username views
        mDrawerUserName = (TextView) mDrawerLayout.findViewById(R.id.profile_name_text);
        mDrawerUserUsername = (TextView) mDrawerLayout.findViewById(R.id.sub_profile_name_text);

        // Get each Drawer menu layout

        // Get each menu option row text

        // add touch and click listener to each navigation menu row

        // set the click listener for the cover image to go to the current users profile
//        mDrawerCoverImage.setOnClickListener(new MenuItemClickListener(Activities.HOME));

        // get the main frame content
        mContentFrame = (MyFrameLayout) findViewById(R.id.content);

        // get the floating action button view
        mFab = (FloatingActionButton) findViewById(R.id.floating_action_button);

        // get the click listener for the main action
        OnClickListener mainActionListener = getMainActionClickListener();

        // if no click listener is provided, hide the button
        if (mainActionListener == null) {
            // hide the fab
            mFab.setVisibility(View.GONE);
            hideFab();

            isUsingFab = false;

        } else {
            // else show the button and set its click listener //
            isUsingFab = true;

            mFab.setVisibility(View.VISIBLE);
            mFab.setOnClickListener(mainActionListener);
        }

        // setup the action bar
        setupActionBar();

        // set the users names
        mDrawerUserName.setText("Simpoll");

        // Disable navigation drawer if desired
//        disableSlideNavDrawer();
    }

    /**
     * Set the click listener for the home icon in the action bar
     */
    protected void setNavigationToolbarClickListener() {
        if (mToolbar != null) mToolbar.setNavigationOnClickListener(getNavClickListener());
    }

    /**
     * Can be Overridden to change its listener depending on other modes.
     *
     * @return the default navigation icon click listener
     */
    protected OnClickListener getNavClickListener() {
        return toggleMenuListener;
    }

    /**
     * Setup the action bar with any other specific content
     */
    @SuppressLint("InflateParams")
    public void setupActionBar() {
        // get the action bar
        ActionBar actionBar = getSupportActionBar();

        // if using the material menu icon
        if (isUsingMaterialMenu) {
            // setup the material menu bar
//            mMaterialMenu = new MaterialMenuIconToolbar(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN) {
//
//                @Override
//                public int getToolbarViewId() {
//                    return R.id.toolbar;
//                }
//
//            };
//            mMaterialMenu.setNeverDrawTouch(true);

            // set the default title
            actionBar.setTitle(mActionbarTitle);

        } else {
            // else using a simple custom view //

            // disable the home icon, title and logo
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);

            // enable the use of a custom layout
            actionBar.setDisplayShowCustomEnabled(true);

            // inflate the custom layout
            LayoutInflater linflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            mActionBarBack = linflater.inflate(R.layout.custom_back_icon_title_toolbar, null);

            // set the click listener for the back layout to be
            // whatever the inheriting activity implements
            mActionBarBack.setOnClickListener(getOnBackClickListener());

            // set the touch listener to the back layout
            mActionBarBack.setOnTouchListener(new ActionBarItemOnTouchListener(this));

            // get the action bar title text view
            mActionBarTitleTxt = (TextView) mActionBarBack.findViewById(R.id.toolbar_title_text);
            mActionBarSubTitleTxt = (TextView) mActionBarBack.findViewById(R.id.toolbar_sub_title_text);

            // if the screen size is large, set the action bar title to be bold
            if (mScreenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || mScreenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                mActionBarTitleTxt.setTextAppearance(mContext, R.style.SingleLineActionBarTheme_Bold);
            }

            // get the back caret
            mActionBarBackCaret = mActionBarBack.findViewById(R.id.toolbar_back_caret);

            // set the custom layout for the action bar
            actionBar.setCustomView(mActionBarBack);
        }
    }

    /**
     * Set the action bar title
     */
    public void setActionBarTitle(String title) {
        mActionbarTitle = Strings.returnSafely(title);

        // get the action bar
        ActionBar actionBar = getSupportActionBar();

        // if using the material menu
        if (isUsingMaterialMenu) {
            // set the default title
            actionBar.setTitle(mActionbarTitle);

        } else {
            // else using a custom action bar //

            if (mActionBarTitleTxt == null && mActionBarBack != null) {
                // get the action bar title text view
                mActionBarTitleTxt = (TextView) mActionBarBack.findViewById(R.id.toolbar_title_text);
            }

            if (mActionBarTitleTxt != null) {
                // set the title in the stored action bar title view
                mActionBarTitleTxt.setText(mActionbarTitle);

                mActionBarTitleTxt.invalidate();
                mActionBarTitleTxt.requestLayout();
            }

            // set the base action bar title to be empty
            actionBar.setTitle("");
        }
    }

    /**
     * Set the action bar title from a resource string
     */
    public void setActionBarTitle(int stringResId) {
        setActionBarTitle(getString(stringResId));
    }

    /**
     * Set the action bar sub title text
     *
     * @param subTitle
     */
    public void setActionBarSubTitle(String subTitle) {
        if (mActionBarSubTitleTxt == null) return;

        if (!Strings.isEmpty(subTitle)) {
            mActionBarSubTitleTxt.setVisibility(View.VISIBLE);
            mActionBarSubTitleTxt.setText(subTitle);

        } else {
            mActionBarSubTitleTxt.setVisibility(View.GONE);
        }
    }

    /**
     * Set the action bar sub title text
     *
     * @param stringResId
     */
    public void setActionBarSubTitle(int stringResId) {
        setActionBarSubTitle(getString(stringResId));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

//        if (isUsingMaterialMenu) mMaterialMenu.syncState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        if (isUsingMaterialMenu) mMaterialMenu.onSaveInstanceState(outState);

        outState.putString("TAG", TAG);
        outState.putString("thisActivity", thisActivity.name());
        outState.putString("fromActivity", fromActivity.name());
        outState.putBoolean("materialMenu", isUsingMaterialMenu);
        outState.putBoolean("toolbarShadow", showToolbarShadow);
        outState.putInt("mainLayoutId", mainLayoutId);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        TAG = savedInstanceState.getString("TAG");
        try {
            thisActivity = Activities.valueOf(savedInstanceState.getString("thisActivity"));
        } catch (Exception e) {
        }
        try {
            fromActivity = Activities.valueOf(savedInstanceState.getString("fromActivity"));
        } catch (Exception e) {
        }
        isUsingMaterialMenu = savedInstanceState.getBoolean("materialMenu", false);
        showToolbarShadow = savedInstanceState.getBoolean("toolbarShadow", false);
        mainLayoutId = savedInstanceState.getInt("mainLayoutId");
    }

    /**
     * Function returns the listener run when the back view is clicked in the action bar
     * <p>
     * <b>Default:</b> just finishes the current activity
     *
     * @return the click listener to run when the back view is pressed in the action bar
     */
    protected OnClickListener getOnBackClickListener() {
        return finishActivityListener;
    }

    /**
     * Click listener to toggle the navigation menu open/closed
     */
    protected OnClickListener toggleMenuListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // if the menu is open, close it
            if (isNavDrawerOpen) closeDrawer();
                // else open the menu
            else openDrawer();
        }

    };

    /**
     * Click listener to open the navigation menu
     */
    protected OnClickListener openMenuListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // open the drawer
            openDrawer();
        }

    };

    /**
     * Click listener to close the navigation menu
     */
    protected OnClickListener closeMenuListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // close the drawer
            closeDrawer();
        }

    };

    /**
     * Open the navigation menu
     */
    public void openDrawer() {
        if (mDrawerLayout != null) mDrawerLayout.openDrawer(GravityCompat.START);

        mDrawerLayout.requestFocus();
    }

    /**
     * Close the navigation menu
     */
    public void closeDrawer() {
        if (mDrawerLayout != null) mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Hide the keyboard if a view has focus until the user clicks an EditText view
     */
    public void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

            // get the current focused view
            View view = this.getCurrentFocus();

            if (view == null) {
                view = getWindow().getCurrentFocus();
            }

            // if there is one
            if (view != null) {
                // hide the keyboard from it
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            } else if (inputManager.isActive()) {
                // else try another method to close the keyboard //
                inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            } else {
                // else try a third method
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }

        } catch (Exception e) {
        }
    }

    /**
     * Show the keyboard and set the focus to the edit text, if we have one.
     *
     * @param view
     */
    public static void showKeyboard(Context context, View view) {
        // if we have an edit text
        if (view != null) {
            // show the keyboard for it
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            view.requestFocus();
            return;
        }

        // else just force show the keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    /**
     * Force show the keyboard.
     */
    public void showKeyboard() {
        showKeyboard(this, null);
    }

    /**
     * Called when the drawer is sliding
     *
     * @param drawerView
     * @param slideOffset
     */
    protected void onNavDrawerSlide(View drawerView, float slideOffset) {
        if (isUsingMaterialMenu) {
            // animate the menu/arrow icon when the menu slides
//            mMaterialMenu.setTransformationOffset(
//                    MaterialMenuDrawable.AnimationState.BURGER_ARROW,
//                    isNavDrawerOpen ? 2 - slideOffset : slideOffset
//            );
        }
    }

    /**
     * Called when the drawer is opened
     *
     * @param drawerView
     */
    protected void onNavDrawerOpened(View drawerView) {
        // set the navigation menu to be open
        isNavDrawerOpen = true;

        // also ensure the keyboard hides
        hideKeyboard();
    }

    /**
     * Called when the drawer is closed
     *
     * @param drawerView
     */
    protected void onNavDrawerClosed(View drawerView) {
        // set the navigation menu to be closed
        isNavDrawerOpen = false;
    }

    /**
     * Click listener to finish the current activity
     */
    protected OnClickListener finishActivityListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // simply finish the current activity
            finish();
        }

    };

    /**
     * Disable sliding the navigation drawer open by locking it closed
     */
    public void disableSlideNavDrawer() {
        if (mDrawerLayout != null)
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        closeDrawer();
    }

    /**
     * Enable sliding the navigation drawer open, by unlocking it
     */
    public void enableSlideNavDrawer() {
        if (mDrawerLayout != null) mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        closeDrawer();
    }

    /**
     * Function to return the listener that is called when the main action button is clicked.
     * It returns <b>null</b> by default to indicate that it is to be hidden.
     * <p>
     * So to handle its clicks, override this function to return what you want it to do.
     *
     * @return
     */
    protected OnClickListener getMainActionClickListener() {
        return null;
    }

    /**
     * Clear any overlays on the activity, at base it closes the drawer menu.
     * Can be extended to hide search, etc.
     */
    protected void clearActivityOverlays() {
        // as the base case, close the menu
        closeDrawer();
    }

    /**
     * Hide the floating action bar
     */
    public void hideFab() {
        if (mFab == null) return;

        // if not using the button, return
        if (!isUsingFab) return;

        mFab.hide(true);
    }

    /**
     * Allow the fab become visible from now on with any subsequent calls to showFab
     */
    public void makeFabVisible() {
        isUsingFab = true;

        if (mFab != null) mFab.setVisibility(View.VISIBLE);
    }

    /**
     * Show the floating action bar
     */
    public void showFab() {
        if (mFab == null) return;

        // if not using the button, return
        if (!isUsingFab) return;

        // if it is already showing, return
//        if (mFab.isShowing()) return;

        // else show it
        mFab.setVisibility(View.VISIBLE);
        mFab.hide(false);
    }

    /**
     * @return if the FAB is being shown on the activity
     */
    public boolean isShowingFab() {
        if (mFab == null) return false;

        return mFab.isShown();
    }

    @Override
    protected void onStart() {
        // Register BroadcastReceivers to receive event from our com.samdroid.andi.service
//        mProjectFundedReceiver = new ProjectFundingBroadcastReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ProjectFundedNotificationService.ACTION);
//        registerReceiver(mProjectFundedReceiver, intentFilter);

        super.onStart();
    }

    @Override
    protected void onStop() {
        // Unregister BroadcastReceivers
//        unregisterReceiver(mProjectFundedReceiver);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        // set that the app is open
        isAppOpen = true;
    }

    @Override
    public void onPause() {
        super.onPause();

        // set that the app is not open
        isAppOpen = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // if it has a fromActivity
        if (intent != null && intent.hasExtra(fromActivityId)) {
            // set the fromActivity
            try {
                fromActivity = Activities.valueOf(intent.getStringExtra(fromActivityId));
            } catch (Exception e) {
                fromActivity = Activities.UNKNOWN;
            }

        } else {
            // else set the from activity to unknown
            fromActivity = Activities.UNKNOWN;
        }
    }

    /**
     * @return this activity enum
     */
    public Activities getThisActivity() {
        return thisActivity != null ? thisActivity : Activities.UNKNOWN;
    }

    /**
     * @return from activity enum
     */
    public Activities getFromActivity() {
        return fromActivity != null ? fromActivity : Activities.UNKNOWN;
    }

    /**
     * Set the icon for the floating action button.
     *
     * @param drawableId
     */
    public void setFabIcon(int drawableId) {
        // if the fab is null or we are not to be using the fab, return
        if (mFab == null || !isUsingFab) return;

        // else set the fab drawable
        mFab.setImageResource(drawableId);
    }

    /**
     * Update the FAB image resource
     *
     * @param resId resource ID of the drawable to change to
     */
    public void updateFabIcon(int resId) {
        if (mFab == null) return;

        mFab.setImageResource(resId);
    }

    private static final long TOAST_INTERVAL_TIME = 30 * 1000;

    /**
     * Try and show the no network connection toast if we have not for at least 30 seconds.
     *
     * @return if this function handled showing a toast or message to the user
     */
    public boolean tryShowNoNetworkConnectionToast() {
        // if we have a network connection, return this function did not handle the toast
        if (NetworkManager.hasConnection(this)) return false;

        // get the current time
        long currentTime = System.currentTimeMillis();
        long diff = TOAST_INTERVAL_TIME;

        // get the time difference
        if (mLastToastTime != null) diff = currentTime - mLastToastTime;

        // if it has been at least 30 seconds since the last no network connection toast
        if (diff >= TOAST_INTERVAL_TIME) {
            // update the last toast time
            mLastToastTime = currentTime;

            // show the no connection toast
            Toast.makeText(this, "Sorry, no network connection found.", Toast.LENGTH_SHORT).show();
        }

        // return true that this function handled showing the toast
        return true;
    }

    /**
     * Show a toast, forcing the last toast time to reset.
     *
     * @param message
     */
    public void tryShowToast(String message) {
        tryShowToast(message, false);
    }

    /**
     * Try and show the a toast if we have not for at least 30 seconds.
     *
     * @return if this function showed a toast
     */
    public boolean tryShowToast(String message, boolean forceNew) {
        // get the current time
        long currentTime = System.currentTimeMillis();
        long diff = TOAST_INTERVAL_TIME;

        // get the time difference
        if (mLastToastTime != null) diff = currentTime - mLastToastTime;

        // if we are forcing a new toast OR it has been at least 30 seconds since the last no network connection toast
        if (forceNew || diff >= TOAST_INTERVAL_TIME) {
            // update the last toast time
            mLastToastTime = currentTime;

            // show the toast
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            // return true that this showed a toast
            return true;
        }

        // return false because we showed no toast
        return false;
    }

    /**
     * Update the drawer menu images after the user changes them from their profile.
     */
    public void updateDrawerImages() {
    }

    /**
     * Close this activity by sliding out right and expanding in the activity returning to
     * with an empty intent and RESULTS_OK
     */
    public void finishActivityWithAnimation() {
        finishActivityWithAnimation(new Intent());
    }

    /**
     * Close this activity by sliding out right and expanding in the activity returning to
     * with a specific intent and RESULTS_OK
     */
    public void finishActivityWithAnimation(Intent intent) {
        if (intent == null) intent = new Intent();

        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.expand_in_from_partial, R.anim.right_slide_out);
    }

    /**
     * Show a confirm dialog with a message and finish the activity with Cancelled results
     * when the confirm is selected
     *
     * @param message
     * @param withAnimation
     */
    protected void showFinishActivityWithErrorConfirm(final String message, final boolean withAnimation) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                new AlertDialogWrapper.Builder(mContext)
//                        .setTitle(message)
//                        .setPositiveButton("OK", new ButtonCallback() {
//
//                    @Override
//                    public void onPositive(MaterialDialog dialog) {
//                        // close the activity
//                        setResult(RESULT_CANCELED, new Intent());
//                        finish();
//                        if (withAnimation)
//                            overridePendingTransition(R.anim.expand_in_from_partial, R.anim.right_slide_out);
//                    }
//
//                }).show();
            }
        });
    }

    /**
     * Hide and disable the action bar back view (if using and showing it)
     */
    protected void disableActionBarBack(boolean removeBackCaret) {
        // if using the material menu icon, return
        if (isUsingMaterialMenu) return;

        if (mActionBarBack == null) return;

        // if showing the back view, clear the listeners
        if (mActionBarBack.getVisibility() == View.VISIBLE) {
            mActionBarBack.setOnClickListener(null);
            mActionBarBack.setOnTouchListener(null);

            // also hide the back caret
            if (mActionBarBackCaret != null)
                mActionBarBackCaret.setVisibility(removeBackCaret ? View.GONE : View.INVISIBLE);
        }
    }

    /**
     * Show and enable the action bar back view (if using and showing it)
     */
    protected void enableActionBarBack() {
        // if using the material menu, return
        if (isUsingMaterialMenu) return;

        // if showing the back view, set the listeners
        if (mActionBarBack.getVisibility() == View.VISIBLE) {
            mActionBarBack.setOnClickListener(getOnBackClickListener());
            mActionBarBack.setOnTouchListener(new ActionBarItemOnTouchListener(this));

            // make sure the back caret is now showing
            if (mActionBarBackCaret != null) mActionBarBackCaret.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @return the screen size as a Configuration.screen_size
     */
    public static int getScreenSize() {
        return mScreenSize;
    }

    /**
     * @return the screen density
     */
    public static ScreenUtils.ScreenDensity getScreenDensity() {
        return mScreenDensity;
    }

    @Override
    public void onBackPressed() {
        // if the menu is open, close it
        if (isNavDrawerOpen) {
            closeDrawer();
            return;
        }

        super.onBackPressed();
    }

    /**
     * @return if the activity is closing
     */
    public boolean isActivityClosing() {
        return closingActivity;
    }

}
