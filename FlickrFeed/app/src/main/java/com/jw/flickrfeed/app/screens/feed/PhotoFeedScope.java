package com.jw.flickrfeed.app.screens.feed;

import com.jw.flickrfeed.domain.FilterProfile;
import com.jw.flickrfeed.domain.PhotoFeed;
import com.jw.flickrfeed.presentation.PhotoFeedPresenter;

/**
 * Defines dependencies required by the {@link PhotoFeedFragment}.
 * <p>
 * Should be provided by the activity hosting this fragment.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public interface PhotoFeedScope {

    PhotoFeed photoFeed();

    FilterProfile filterProfile();

    PhotoFeedPresenter.Navigator photoFeedNavigator();
}
