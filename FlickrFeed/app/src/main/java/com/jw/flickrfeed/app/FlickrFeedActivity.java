package com.jw.flickrfeed.app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.jw.base.ui.activities.AppFragmentActivity;
import com.jw.flickrfeed.app.screens.feed.PhotoFeedFragment;
import com.jw.flickrfeed.presentation.Navigator;

/**
 * TODO finish me
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class FlickrFeedActivity extends AppFragmentActivity implements Navigator {

    @Nullable
    @Override
    public Fragment createStartupFragment(@NonNull Intent intent) {
        return PhotoFeedFragment.newInstance();
    }
}
