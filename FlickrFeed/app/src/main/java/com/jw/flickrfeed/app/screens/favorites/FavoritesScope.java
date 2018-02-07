package com.jw.flickrfeed.app.screens.favorites;

import com.jw.flickrfeed.domain.FilterProfile;
import com.jw.flickrfeed.presentation.FavoritesPresenter;

/**
 * Defines dependencies required by the {@link FavoritesDialogFragment}.
 * <p>
 * Should be provided by the activity hosting this fragment.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public interface FavoritesScope {

    FilterProfile filterProfile();

    FavoritesPresenter.Navigator favouritesNavigator();
}
