package com.jw.flickrfeed.repository.flickr;

import android.support.annotation.NonNull;
import com.jw.flickrfeed.domain.Filter;
import com.jw.flickrfeed.domain.PhotoFeed;
import java.util.concurrent.Future;

/**
 * TODO implement me
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class FlickrPhotoRepository implements PhotoFeed.PhotoRepository {

    @NonNull
    @Override
    public Future<Filter> pullLatestPhotos() {
        throw new Error("Not implemented");
    }
}
