package com.jw.flickrfeed.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The feed of latest photos provided by the {@link PhotoRepository}, filtered by the {@link FilterRepository}.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class PhotoFeed {

    public interface PhotoRepository {

        @NonNull
        Single<List<Photo>> pullLatestPhotos(@NonNull Collection<String> tags);
    }

    public interface FilterRepository {

        @NonNull
        Single<Filter> pullLatestFilter();
    }

    @NonNull
    private final BehaviorSubject<List<Photo>> photosSubject = BehaviorSubject.create();

    @NonNull
    private final PhotoRepository photoRepository;

    @NonNull
    private final FilterRepository filterRepository;

    public PhotoFeed(@NonNull PhotoRepository photoRepository, @NonNull FilterRepository filterRepository) {
        this.photoRepository = photoRepository;
        this.filterRepository = filterRepository;
    }

    public void destroy() {
        photosSubject.onComplete();
    }

    @NonNull
    public Completable refresh() {
        return photoRepository.pullLatestPhotos(Collections.emptyList())    // TODO support tags filtering
                              .doOnSuccess(photosSubject::onNext)
                              .toCompletable();
    }

    public Observable<List<Photo>> observablePhotos() {
        return photosSubject;
    }

    public void setFilter(@Nullable Filter filter) {
        throw new Error("Not implemented"); // TODO implement me
    }
}
