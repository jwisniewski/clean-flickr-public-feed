package com.jw.flickrfeed.presentation;

import android.support.annotation.NonNull;
import com.jw.flickrfeed.domain.FilterProfile;
import com.jw.flickrfeed.domain.Photo;
import com.jw.flickrfeed.domain.PhotoFeed;
import io.reactivex.disposables.CompositeDisposable;
import java.util.List;

/**
 * Presents manually refreshable and filterable photo feed.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class PhotoFeedPresenter {

    public interface Navigator {

        void navigateToWebPage(@NonNull String url);

        void navigateToFavourites();
    }

    public interface View {

        void showRefreshing(boolean show);

        void showPhotos(@NonNull List<Photo> photos);

        void showTryLaterHint();

        void showSelectedPhotoNotTaggedHint();
    }

    @NonNull
    private final Navigator navigator;

    @NonNull
    private final View view;

    @NonNull
    private final PhotoFeed photoFeed;

    @NonNull
    private final FilterProfile filterProfile;

    @NonNull
    private final CompositeDisposable disposables = new CompositeDisposable();

    public PhotoFeedPresenter(@NonNull View view, @NonNull Navigator navigator, @NonNull PhotoFeed photoFeed,
            @NonNull FilterProfile filterProfile) {
        this.navigator = navigator;
        this.view = view;
        this.photoFeed = photoFeed;
        this.filterProfile = filterProfile;

        disposables.add(photoFeed.observablePhotos()
                                 .subscribe(view::showPhotos));
    }

    public void destroy() {
        disposables.clear();
    }

    public void refreshPhotos() {
        view.showRefreshing(true);
        photoFeed.refresh().subscribe(() -> {
            view.showRefreshing(false);
        }, throwable -> {
            view.showRefreshing(false);
            view.showTryLaterHint();
        });
    }

    public void selectPhoto(@NonNull Photo photo) {
        if (photo.tags().isEmpty()) {
            view.showSelectedPhotoNotTaggedHint();
        } else {
            filterProfile.train(photo.tags());

            // TODO code duplication (see refreshPhotos())
            view.showRefreshing(true);
            photoFeed.filter(filterProfile.buildFilter()).subscribe(() -> {
                view.showRefreshing(false);
            }, throwable -> {
                view.showRefreshing(false);
                view.showTryLaterHint();
            });
        }
    }

    public void presentPhotoDetails(@NonNull Photo photo) {
        navigator.navigateToWebPage(photo.detailsUrl());
    }

    public void presentFavorites() {
        navigator.navigateToFavourites();
    }
}
