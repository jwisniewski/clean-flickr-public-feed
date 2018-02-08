package com.jw.flickrfeed.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import java.util.Collection;
import java.util.List;

/**
 * The feed of latest photos provided by the {@link PhotoRepository}, filtered by a current {@link Filter}.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
@SuppressWarnings("Convert2MethodRef")
public class PhotoFeed {

    /**
     * Data source of the latest photos matching given collection of tags.
     */
    public interface PhotoRepository {

        @NonNull
        Single<List<Photo>> loadLatestPhotos(@NonNull Collection<String> tags);
    }

    @NonNull
    private final PhotoRepository photoRepository;

    @NonNull
    private final BehaviorSubject<List<Photo>> photosSubject = BehaviorSubject.create();

    @Nullable
    private Disposable observedFilterDisposable;

    @NonNull
    private Filter filter = Filter.EMPTY;

    public PhotoFeed(@NonNull PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    /**
     * Finishes with this instance, it shouldn't be used anymore.
     */
    public void destroy() {
        photosSubject.onComplete();

        if (observedFilterDisposable != null) {
            observedFilterDisposable.dispose();
        }
    }

    /**
     * Obtains latest photos from the feed according the current filter.
     *
     * @return a {@link Completable} reporting success if the refresh succeeded.
     */
    @NonNull
    public Completable refresh() {
        return photoRepository.loadLatestPhotos(filter.tags())
                              .doOnSuccess(photos -> photosSubject.onNext(photos))
                              .toCompletable();
    }

    /**
     * Obtains latest photos from the feed matching a new filter.
     *
     * @param newFilter a new filter.
     * @return a {@link Completable} reporting success if the filtering succeeded.
     */
    @NonNull
    public Completable filter(@NonNull Filter newFilter) {
        filter = newFilter;
        return refresh();
    }

    /**
     * Observes a given filter and refreshes automatically given the filter changes.
     *
     * @param observableFilter the filter to observe.
     */
    public void observe(@Nullable Observable<Filter> observableFilter) {
        if (observedFilterDisposable != null) {
            observedFilterDisposable.dispose();
        }

        if (observableFilter != null) {
            observedFilterDisposable = observableFilter.subscribe(filter -> filter(filter).subscribe());
        }
    }

    /**
     * Provides an observable stream of photos, matching the latest filtering criteria.
     *
     * @return an observable stream of photos.
     */
    @NonNull
    public Observable<List<Photo>> observablePhotos() {
        return photosSubject;
    }
}
