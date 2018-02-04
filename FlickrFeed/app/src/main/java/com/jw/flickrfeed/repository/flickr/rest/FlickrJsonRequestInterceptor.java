package com.jw.flickrfeed.repository.flickr.rest;

import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
class FlickrJsonRequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request originalRequest = chain.request();
        final HttpUrl originalUrl = originalRequest.url();

        final HttpUrl url = originalUrl.newBuilder()
                                       .addQueryParameter("format", "json")
                                       .addQueryParameter("nojsoncallback", "1")
                                       .build();

        final Request request = originalRequest.newBuilder()
                                               .url(url)
                                               .build();

        return chain.proceed(request);
    }
}
