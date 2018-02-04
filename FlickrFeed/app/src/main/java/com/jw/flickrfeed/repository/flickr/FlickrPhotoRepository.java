package com.jw.flickrfeed.repository.flickr;

import android.support.annotation.NonNull;
import com.jw.flickrfeed.domain.Photo;
import com.jw.flickrfeed.domain.PhotoFeed;
import com.jw.flickrfeed.repository.flickr.rest.FlickrApi;
import com.jw.flickrfeed.repository.flickr.rest.PublicPhotosResponse;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.List;

/**
 * TODO implement me
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class FlickrPhotoRepository implements PhotoFeed.PhotoRepository {

    @NonNull
    private final FlickrApi api;

    public FlickrPhotoRepository(@NonNull String baseUrl, boolean verbose) {
        api = FlickrApi.create(baseUrl, verbose);
    }

    @NonNull
    @Override
    public Single<List<Photo>> pullLatestPhotos() {
        return api.pullPublicPhotosFeed()
                  .map(PublicPhotosResponse::items)
                  .flatMap(items ->
                          Observable.fromIterable(items)
                                    .map(item ->
                                            Photo.builder()
                                                 .author(item.author())
                                                 .publishedAt(item.datePublished())
                                                 .url(item.media().m())
                                                 .build())
                                    .toList());
    }
}
