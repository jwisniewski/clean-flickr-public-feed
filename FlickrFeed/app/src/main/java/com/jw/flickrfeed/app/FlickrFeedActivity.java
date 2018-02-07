package com.jw.flickrfeed.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.jw.base.ui.activities.AppFragmentActivity;
import com.jw.flickrfeed.BuildConfig;
import com.jw.flickrfeed.app.screens.favorites.FavoritesDialogFragment;
import com.jw.flickrfeed.app.screens.favorites.FavoritesScope;
import com.jw.flickrfeed.app.screens.feed.PhotoFeedFragment;
import com.jw.flickrfeed.app.screens.feed.PhotoFeedScope;
import com.jw.flickrfeed.domain.FilterProfile;
import com.jw.flickrfeed.domain.PhotoFeed;
import com.jw.flickrfeed.presentation.FavoritesPresenter;
import com.jw.flickrfeed.presentation.PhotoFeedPresenter;
import com.jw.flickrfeed.repository.flickr.FlickrPhotoRepository;
import com.jw.flickrfeed.repository.flickr.api.FlickrApi;
import com.jw.flickrfeed.repository.flickr.api.FlickrApiFactory;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Main activity, an entry point of the app, inviting a user with a fresh list of photos downloaded from Flickr.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
@Accessors(fluent = true)
public class FlickrFeedActivity extends AppFragmentActivity implements PhotoFeedScope, FavoritesScope {

    private static final String FLICKR_API_BASE_URL = "https://api.flickr.com";

    @Getter
    private PhotoFeed photoFeed;

    @Getter
    private FilterProfile filterProfile;

    @Getter
    private final PhotoFeedPresenter.Navigator photoFeedNavigator = new PhotoFeedPresenter.Navigator() {

        @Override
        public void navigateToWebPage(@NonNull String url) {
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }

        @Override
        public void navigateToFavourites() {
            final FavoritesDialogFragment fragment = FavoritesDialogFragment.newInstance();
            fragment.show(getSupportFragmentManager(), FavoritesDialogFragment.TAG);
        }
    };

    @Getter
    private final FavoritesPresenter.Navigator favouritesNavigator = new FavoritesPresenter.Navigator() {

        @Override
        public void navigateBack() {
            final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FavoritesDialogFragment.TAG);
            if (fragment instanceof FavoritesDialogFragment) {
                ((FavoritesDialogFragment) fragment).dismiss();
            }
        }
    };

    @Nullable
    @Override
    public Fragment createStartupFragment(@NonNull Intent intent) {
        return PhotoFeedFragment.newInstance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // TODO simplify AppFragmentActivity
        final FlickrApi flickrApi = new FlickrApiFactory().verbose(BuildConfig.DEBUG).create(FLICKR_API_BASE_URL);
        final FlickrPhotoRepository flickrPhotoRepository = new FlickrPhotoRepository(flickrApi);

        photoFeed = new PhotoFeed(flickrPhotoRepository);
        filterProfile = new FilterProfile();

        super.onCreate(savedInstanceState);
    }
}
