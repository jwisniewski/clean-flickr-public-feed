package com.jw.flickrfeed.repository.flickr.api;

import android.support.annotation.Nullable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public interface FlickrApi {

    @GET("services/feeds/photos_public.gne")
    Single<FlickrPublicPhotos> pullPublicPhotos(@Query(value = "tags", encoded = true) @Nullable String tags);
}
