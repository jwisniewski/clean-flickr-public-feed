package com.jw.flickrfeed.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

/**
 * TODO implement me
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class PhotoFeed {

    public interface Listener {

        void onPhotoFeedUpdate(@Nullable List<Photo> photos);
    }

    public interface PhotoRepository {

        @NonNull
        Future<Filter> pullLatestPhotos();
    }

    public interface FilterRepository {

        @NonNull
        Future<Filter> pullLatestFilter();
    }

    private final CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();

    private final PhotoRepository photoRepository;

    private final FilterRepository filterRepository;

    public PhotoFeed(@NonNull PhotoRepository photoRepository, @NonNull FilterRepository filterRepository) {
        this.photoRepository = photoRepository;
        this.filterRepository = filterRepository;

        // TODO finish me
    }

    public void destroy() {
        listeners.clear();

        // TODO finish me
    }

    public void setFilter(@Nullable Filter filter) {
        throw new Error("Not implemented");
    }

    public boolean addListener(@NonNull Listener listener) {
        return listeners.addIfAbsent(listener);
    }

    public boolean removeListener(@Nullable Listener listener) {
        return listeners.remove(listener);
    }
}
