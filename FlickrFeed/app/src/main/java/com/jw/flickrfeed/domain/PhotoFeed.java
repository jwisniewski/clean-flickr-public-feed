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
 * TODO implement me
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

        // TODO finish me
    }

    public void destroy() {
        photosSubject.onComplete();

        // TODO finish me
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
        throw new Error("Not implemented");
    }
}
