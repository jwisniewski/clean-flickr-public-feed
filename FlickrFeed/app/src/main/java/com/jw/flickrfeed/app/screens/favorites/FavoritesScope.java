package com.jw.flickrfeed.app.screens.favorites;

import com.jw.flickrfeed.domain.FilterProfile;
import com.jw.flickrfeed.presentation.FavoritesPresenter;

/**
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public interface FavoritesScope {

    FilterProfile filterProfile();

    FavoritesPresenter.Navigator favouritesNavigator();
}
