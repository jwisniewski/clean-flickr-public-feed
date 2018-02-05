package com.jw.flickrfeed.repository.flickr.api;

import android.support.annotation.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

/**
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
interface FlickrHttpClientFactory {

    static OkHttpClient create(@NonNull String baseUrl, boolean verbose) {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new FlickrJsonRequestInterceptor());

        if (verbose) {
            builder.addInterceptor(new HttpLoggingInterceptor()
                    .setLevel(BODY));
        }

        return builder.build();
    }
}
