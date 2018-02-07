package com.jw.flickrfeed.repository.flickr.api;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

/**
 * Factory assembling the Flickr API representation configured with {@literal json} format.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
@Accessors(chain = true, fluent = true)
public class FlickrApiFactory {

    /**
     * Logging will be enabled if {@literal true}, disabled otherwise.
     */
    @Setter
    @Getter
    private boolean verbose = false;

    /**
     * Assembles the Flickr API.
     *
     * @param baseUrl a base URL to connect the API with
     * @return the implementation of {@link FlickrApi} connected to the given base url.
     */
    public FlickrApi create(@NonNull String baseUrl) {
        return new Retrofit.Builder()
                .client(createHttpClient(baseUrl, verbose))
                .addConverterFactory(createRetrofitConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .baseUrl(baseUrl)
                .build()
                .create(FlickrApi.class);
    }

    private static OkHttpClient createHttpClient(@NonNull String baseUrl, boolean verbose) {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new FlickrJsonRequestInterceptor());

        if (verbose) {
            builder.addInterceptor(new HttpLoggingInterceptor()
                    .setLevel(BODY));
        }

        return builder.build();
    }

    private static Converter.Factory createRetrofitConverterFactory() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);

        return JacksonConverterFactory.create(mapper);
    }

    /**
     * Intercepts requests and configures them to request pure {@literal json} format of the response.
     */
    private static class FlickrJsonRequestInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            final Request originalRequest = chain.request();
            final HttpUrl originalUrl = originalRequest.url();

            final HttpUrl url = originalUrl.newBuilder()
                                           .addQueryParameter("format", "json")
                                           .addQueryParameter("nojsoncallback", "1")
                                           .addQueryParameter("tagmode", "any")
                                           .build();

            final Request request = originalRequest.newBuilder()
                                                   .url(url)
                                                   .build();

            return chain.proceed(request);
        }
    }
}
