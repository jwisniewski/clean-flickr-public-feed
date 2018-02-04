package com.jw.flickrfeed.repository.flickr.rest;

import android.support.annotation.NonNull;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;

/**
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public interface FlickrApi {

    @GET("services/feeds/photos_public.gne")
    Single<PublicPhotosResponse> pullPublicPhotosFeed();

    static FlickrApi create(@NonNull String baseUrl, boolean verbose) {
        return new Retrofit.Builder()
                .client(FlickrHttpClientFactory.create(baseUrl, true))
                .addConverterFactory(FlickrJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .baseUrl(baseUrl)
                .build()
                .create(FlickrApi.class);
    }
}
