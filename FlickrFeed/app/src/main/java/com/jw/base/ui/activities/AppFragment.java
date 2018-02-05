package com.jw.base.ui.activities;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import java.util.List;

/**
 * Base class of fragments integrating with the {@link AppFragmentActivity}.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public abstract class AppFragment extends Fragment {

    @Nullable
    public AppFragmentActivity getAppActivity() {
        Activity activity = getActivity();

        if (activity != null && activity instanceof AppFragmentActivity) {
            return (AppFragmentActivity) activity;
        } else {
            return null;
        }
    }

    /**
     * Called when user taps back or home up. Default implementation passes the event to all visible child fragments,
     * allowing them to consume it.
     *
     * @return true if an event has been consumed and handled by the fragment, false otherwise
     */
    public boolean onNavigatingBack() {
        boolean consumed = false;

        // Propagates the event to active child fragments, allowing one of them to consume it.
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment == this) {
                    // Shouldn't happen, child fragment manager shouldn't contain our fragment.
                    continue;
                }

                if (!(fragment instanceof AppFragment)) {
                    continue;
                }

                AppFragment appFragment = (AppFragment) fragment;
                if (!appFragment.isVisible()) {
                    continue;
                }

                // We allow all matching child fragments to process a back event, otherwise we could end with
                // unexpected behaviour in view pagers for example, view pagers keep all fragments in visible state,
                // other fragment that the current one could process the event and user would perceive it as
                // unresponsiveness.
                if (appFragment.onNavigatingBack()) {
                    consumed = true;
                }
            }
        }

        return consumed;
    }
}
