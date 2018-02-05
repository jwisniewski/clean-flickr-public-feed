package com.jw.flickrfeed.repository.flickr;

import android.support.annotation.NonNull;
import com.jw.flickrfeed.domain.Photo;
import com.jw.flickrfeed.domain.PhotoFeed;
import com.jw.flickrfeed.repository.flickr.api.FlickrApi;
import com.jw.flickrfeed.repository.flickr.api.FlickrPublicPhotos;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.Collection;
import java.util.List;

/**
 * Provides a list of latest {@link Photo}s from the Flickr Public Feed
 * (https://www.flickr.com/services/feeds/docs/photos_public).
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class FlickrPhotoRepository implements PhotoFeed.PhotoRepository {

    private static final String TAG_SEPARATOR = ",";

    @NonNull
    private final FlickrApi api;

    public FlickrPhotoRepository(@NonNull FlickrApi api) {
        this.api = api;
    }

    @NonNull
    @Override
    public Single<List<Photo>> pullLatestPhotos(@NonNull Collection<String> tags) {
        return api.pullPublicPhotos(joinTags(tags))
                  .map(FlickrPublicPhotos::items)
                  .flatMap(items -> Observable.fromIterable(items)
                                              .map(this::itemToPhoto)
                                              .toList());
    }

    @NonNull
    Photo itemToPhoto(@NonNull FlickrPublicPhotos.Item item) {
        return Photo.builder()
                    .author(item.author())
                    .publishedAt(item.datePublished())
                    .url(item.media().m())
                    .build();
    }

    String joinTags(@NonNull Collection<String> tags) {
        StringBuilder sb = new StringBuilder();
        for (String tag : tags) {
            if (sb.length() > 0) {
                sb.append(TAG_SEPARATOR);
            }
            sb.append(tag);
        }
        return sb.toString();
    }
}
