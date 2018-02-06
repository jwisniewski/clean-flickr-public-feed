package com.jw.flickrfeed.presentation;

import android.support.annotation.NonNull;
import com.jw.flickrfeed.domain.Photo;
import com.jw.flickrfeed.domain.PhotoFeed;
import io.reactivex.disposables.CompositeDisposable;
import java.util.List;

/**
 * TODO implement me
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class PhotoFeedPresenter {

    public interface View {

        void showRefreshing(boolean show);

        void showPhotos(@NonNull List<Photo> photos);

        void showTryLaterHint();
    }

    @NonNull
    private final Navigator navigator;

    @NonNull
    private final View view;

    @NonNull
    private final PhotoFeed photoFeed;

    @NonNull
    private final CompositeDisposable disposables = new CompositeDisposable();

    public PhotoFeedPresenter(@NonNull Navigator navigator, @NonNull View view, @NonNull PhotoFeed photoFeed) {
        this.navigator = navigator;
        this.view = view;
        this.photoFeed = photoFeed;

        disposables.add(photoFeed.observablePhotos()
                                 .subscribe(view::showPhotos));

        refreshPhotos();
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
        // TODO
    }

    public void requestPhotoDetails(@NonNull Photo photo) {
        navigator.openWebPage(photo.detailsUrl());
    }
}
