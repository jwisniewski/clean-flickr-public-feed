package com.jw.base.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.BuildConfig;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.jw.flickrfeed.R;

/**
 * Base class for activities operating on fragments. Contains some base utils for screen (fragment) management,
 * including a few nasty hacks dealing with known bugs of Android SDK and support libraries.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public abstract class AppFragmentActivity extends AppCompatActivity
        implements FragmentManager.OnBackStackChangedListener {

    private static final String TAG = AppFragmentActivity.class.getSimpleName();

    private ViewGroup container;

    private boolean resumed;

    private boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Allows layouts to occupy the whole screen. The first layout setting the fitSystemWindows to true will apply
        // padding required to don't draw on status and navigation bars.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.fragment_container);

        container = findViewById(R.id.container);

        int fragmentContainerId = getContentFragmentContainerId();
        if (fragmentContainerId != View.NO_ID) {

            // In case we're being restored from a previous state, fragments will be here already, nothing more to do!
            if (savedInstanceState != null) {
                return;
            }

            Fragment fragment = createStartupFragment(getIntent());
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                                           .add(fragmentContainerId, fragment)
                                           .commit();
            }
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        started = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;

        updateHomeAsUp();
        dumpFragmentManagerState();
    }

    @Override
    protected void onPause() {
        resumed = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        started = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }

    @Override
    public void onBackStackChanged() {
        dumpFragmentManagerState();
        updateHomeAsUp();
    }

    @Override
    public boolean onSupportNavigateUp() {
        //noinspection SimplifiableIfStatement
        if (!navigateFragmentsBack()) {
            // Perform default activity action only if contained fragments didn't consume the event.
            // Workaround:
            // Normally we would just call onSupportNavigateUp(), however it's bugged in support library v26
            // and don't navigate up when user came from other task, for example by clicking ABOUT in assist settings.
            final Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (upIntent != null) {
                if (NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot()) {
                    Log.v(TAG, "Recreating back stack");
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }

                return true;
            }
            //return super.onSupportNavigateUp();
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if (!navigateFragmentsBack()) {
            super.onBackPressed();
        }
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);

        updateHomeAsUp();
    }

    /**
     * Tests if the activity can do it's default back behaviour, calls {@link AppFragment#onNavigatingBack()}
     * on a currently contained fragment, allowing it's to consume the event and prevent the default behaviour.
     *
     * @return true if contained fragments consumed a back navigation event
     */
    protected boolean navigateFragmentsBack() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(getContentFragmentContainerId());
        //noinspection SimplifiableIfStatement
        if (fragment instanceof AppFragment) {
            // activity should navigate by itself only if children didn't consume an event
            return ((AppFragment) fragment).onNavigatingBack();
        } else {
            return false;
        }
    }

    /**
     * Returns true if the activity should display home as up button. Default implementation returns true if back stack
     * isn't empty.
     */
    public boolean shouldDisplayHomeAsUp() {
        return getSupportFragmentManager().getBackStackEntryCount() > 0;
    }

    public void updateHomeAsUp() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(shouldDisplayHomeAsUp());
        }
    }



    @Nullable
    public abstract Fragment createStartupFragment(@NonNull Intent intent);

    public boolean isScreenVisible(@NonNull String fragmentTag) {
        return getScreenIfVisible(fragmentTag) != null;
    }

    public AppFragment getScreen(@NonNull String fragmentTag) {
        getSupportFragmentManager().executePendingTransactions();

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment instanceof AppFragment) {
            return (AppFragment) fragment;
        } else {
            return null;
        }
    }

    public AppFragment getScreenIfVisible(@NonNull String fragmentTag) {
        AppFragment fragment = getScreen(fragmentTag);
        return fragment != null && fragment.isVisible() ? fragment : null;
    }

    public void showScreen(@NonNull Fragment fragment, @Nullable String tag) {
        showScreenPushingOnBackStack(fragment, tag, false);
    }

    public void showScreenPushingOnBackStack(@NonNull Fragment fragment, @Nullable String tag) {
        showScreenPushingOnBackStack(fragment, tag, true);
    }

    public void removeBackgroundScreens() {
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();

        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount - 1; i++) {
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(i);
            if (backEntry.getName() == null) {
                continue;
            }

            Fragment fragment = getSupportFragmentManager().findFragmentByTag(backEntry.getName());
            if (fragment == null) {
                continue;
            }

            tr.remove(fragment);
        }

        tr.setTransition(FragmentTransaction.TRANSIT_NONE);
        tr.commit();

        getSupportFragmentManager().executePendingTransactions();
    }

    public boolean popBackStackToScreen(@Nullable String tag) {
        if (isScreenOnBackStack(tag)) {
            getSupportFragmentManager().popBackStack(tag, 0);
            return true;
        } else {
            return false;
        }
    }

    public Fragment getLastScreenFromBackStack() {
        int entriesCount = getSupportFragmentManager().getBackStackEntryCount();
        if (entriesCount >= 1) {
            String tag = getSupportFragmentManager().getBackStackEntryAt(entriesCount - 1).getName();
            if (tag != null) {
                return getSupportFragmentManager().findFragmentByTag(tag);
            }
        }

        return null;
    }

    public boolean isScreenOnBackStack(@Nullable String tag) {
        if (tag == null) {
            return false;
        }

        int entries = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < entries; i++) {
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(i);
            if (tag.equals(backEntry.getName())) {
                return true;
            }
        }

        return false;
    }

    public boolean isActivityResumed() {
        return resumed;
    }

    public boolean isActivityStarted() {
        return started;
    }

    public View getRootView() {
        return findViewById(android.R.id.content);
    }

    private void showScreenPushingOnBackStack(@NonNull Fragment replacementFragment,
            @Nullable String replacementFragmentTag, boolean addToBackStack) {
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();

        tr.replace(getContentFragmentContainerId(), replacementFragment, replacementFragmentTag);

        if (addToBackStack) {
            tr.addToBackStack(replacementFragmentTag);
        }

        if (replacementFragment.getEnterTransition() == null) {
            tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }
        tr.commitAllowingStateLoss();
    }

    /**
     * Equivalent to {@link #showScreenPushingOnBackStack(Fragment, String)}, with a workaround implemented to a bug of
     * android platform (support lib): https://code.google.com/p/android/issues/detail?id=163384
     * <p/>
     * Newly added fragments are put behind the old fragments. When both fragments, the old one and the added one have
     * animations, the old fragment hides parts of the new fragment until fully hidden by an animation.
     * <p/>
     * In order to implement a fragment transition putting a new fragment in front of the old one, we are using two
     * containers here. Old fragment finishes it's life on a previous container, in the same time an alternative
     * fragment container is being putted in front of it, and new fragment is added to it.
     * <p/>
     * When a mentioned bug will be fixed in the support library, we should remove this method, including
     * the corresponding {@link #getContentFragmentContainerId()}, {@link #toggleContentFragmentContainer()}
     * and additional container added to the fragment_container layout. All usages of this method should be replaced
     * then by: {@link #showScreenPushingOnBackStack(Fragment, String, boolean)}.
     */
    public void showScreenInFront(@NonNull Fragment replacementFragment, @Nullable String replacementFragmentTag) {
        // We support only false, because adding to back stack would mess with implementation of this workaround which
        // switches dynamically between fragment containers. Back stack transitions could restore fragments in wrong
        // containers.
        final boolean addToBackStack = false;

        final FragmentManager fm = getSupportFragmentManager();
        final int fragmentContainerId = getContentFragmentContainerId();

        FragmentTransaction tr = fm.beginTransaction();

        // remove all fragments from current container
        Fragment fragmentById = fm.findFragmentById(fragmentContainerId);
        if (fragmentById != null) {
            tr.remove(fragmentById);
        }

        if (fm.getFragments() != null) {
            for (Fragment addedFragment : fm.getFragments()) {
                if (addedFragment != null && addedFragment != fragmentById
                        && addedFragment.getId() == fragmentContainerId) {
                    tr.remove(addedFragment);
                }
            }
        }

        // switch the active container
        toggleContentFragmentContainer();

        // add a new fragment to a new container
        tr.add(getContentFragmentContainerId(), replacementFragment, replacementFragmentTag);

        // noinspection ConstantConditions
        if (addToBackStack) {
            tr.addToBackStack(replacementFragmentTag);
        }

        if (replacementFragment.getEnterTransition() == null) {
            tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }

        tr.commitAllowingStateLoss();
    }

    /**
     * Dumps current stack trace, a fragment class currently on fragment_container, a list of fragments (by class)
     * added to the fragment manager.
     */
    private void dumpFragmentManagerState() {
        if (!BuildConfig.DEBUG) {
            return;
        }

        String s = "";

        FragmentManager fm = getSupportFragmentManager();

        Fragment containedFragment = fm.findFragmentById(getContentFragmentContainerId());
        s += "FragmentManagerState(Activity: " + (getClass().getSimpleName()) + ", R.id.fragment_container: "
                + (containedFragment != null ? containedFragment.getClass().getSimpleName() : null) + ")\n";

        s += "BackStack: ";
        int backStackElements = fm.getBackStackEntryCount();
        for (int i = 0; i < backStackElements; i++) {
            if (i != 0) {
                s += " <- ";
            }
            s += fm.getBackStackEntryAt(i).getName();
        }
        s += "\n";

        s += "Fragments: ";
        if (fm.getFragments() != null) {
            for (int i = 0; i < fm.getFragments().size(); i++) {
                if (i != 0) {
                    s += ", ";
                }

                Fragment fragment = fm.getFragments().get(i);
                s += (fragment != null ? fragment.getClass().getSimpleName() : null);
            }
        }

        Log.d(TAG, s);
    }

    @Nullable
    public View getContentView() {
        return findViewById(android.R.id.content);
    }

    @IdRes
    public int getContentFragmentContainerId() {
        ViewGroup currentContainer = getContentFragmentContainer();
        return currentContainer != null ? currentContainer.getId() : View.NO_ID;
    }

    @Nullable
    public ViewGroup getContentFragmentContainer() {
        if (container != null && container.getChildCount() > 0) {
            return (ViewGroup) container.getChildAt(container.getChildCount() - 1);
        } else {
            return null;
        }
    }

    private void toggleContentFragmentContainer() {
        if (container != null && container.getChildCount() > 1) {
            container.getChildAt(0).bringToFront();
        }
    }

    public static AppFragmentActivity from(Activity activity) {
        if (activity == null) {
            throw new Error("Activity shall not be null");
        }

        if (!(activity instanceof AppFragmentActivity)) {
            throw new Error("Activity should be of a class: " + AppFragmentActivity.class.getName());
        }

        return (AppFragmentActivity) activity;
    }
}
