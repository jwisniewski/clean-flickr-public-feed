package com.jw.flickrfeed.presentation;

import android.support.annotation.NonNull;
import com.jw.flickrfeed.domain.FilterProfile;
import com.jw.flickrfeed.domain.Photo;
import com.jw.flickrfeed.domain.PhotoFeed;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import java.util.List;

/**
 * Presents a photo feed. Supports refreshing. Supports filtering based on the {@link FilterProfile} which collects
 * tags of recently chosen photos.
 * <p>
 * Integrates a presentation logic with an interface of {@link Navigator} switching between presenters
 * and an interface of {@link View} responsible for actual rendering of the content.
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

    @NonNull
    private final CompletableObserver refreshCompletionObserver = new CompletableObserver() {
        @Override
        public void onSubscribe(Disposable d) {
            disposables.add(d);
        }

        @Override
        public void onComplete() {
            view.showRefreshing(false);
        }

        @Override
        public void onError(Throwable e) {
            view.showRefreshing(false);
            view.showTryLaterHint();
        }
    };

    public PhotoFeedPresenter(@NonNull View view, @NonNull Navigator navigator, @NonNull PhotoFeed photoFeed,
            @NonNull FilterProfile filterProfile) {
        this.navigator = navigator;
        this.view = view;
        this.photoFeed = photoFeed;
        this.filterProfile = filterProfile;

        disposables.add(photoFeed.observablePhotos()
                                 .subscribe(view::showPhotos));

        refreshPhotos();
    }

    public void destroy() {
        disposables.clear();
    }

    public void refreshPhotos() {
        view.showRefreshing(true);
        photoFeed.refresh()
                 .subscribe(refreshCompletionObserver);
    }

    public void selectPhoto(@NonNull Photo photo) {
        if (photo.tags().isEmpty()) {
            view.showSelectedPhotoNotTaggedHint();
        } else {
            filterProfile.trainFilter(photo.tags());
        }
    }

    public void presentPhotoDetails(@NonNull Photo photo) {
        navigator.navigateToWebPage(photo.detailsUrl());
    }

    public void presentFavorites() {
        navigator.navigateToFavourites();
    }
}
