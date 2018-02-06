package com.jw.flickrfeed.app;

import android.app.Application;
import android.content.Context;
import com.jw.flickrfeed.BuildConfig;
import com.jw.flickrfeed.domain.PhotoFeed;
import com.jw.flickrfeed.repository.flickr.FlickrPhotoRepository;
import com.jw.flickrfeed.repository.flickr.api.FlickrApi;
import com.jw.flickrfeed.repository.flickr.api.FlickrApiFactory;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Android application class, due to lack of more sophisticated dependency injection solution for this project,
 * we use this class to assemble our domain objects with lifespan equal to the application process.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
@Accessors(fluent = true)
public class FlickrFeedApplication extends Application {

    private static final String FLICKR_API_BASE_URL = "https://api.flickr.com";

    /**
     * Domain representative with a lifespan equal to the application process.
     */
    @Getter
    private PhotoFeed photoFeed;

    /**
     * Application creation callback used to assemble everything together.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        final FlickrApi flickrApi = new FlickrApiFactory().verbose(BuildConfig.DEBUG).create(FLICKR_API_BASE_URL);
        final FlickrPhotoRepository flickrPhotoRepository = new FlickrPhotoRepository(flickrApi);

        photoFeed = new PhotoFeed(flickrPhotoRepository, null); // TODO fixme
    }

    /**
     * Casts an application context of this package to the {@link FlickrFeedApplication} class.
     *
     * @param context the context of this application.
     * @return the application context mapped to {@link FlickrFeedApplication}.
     */
    public static FlickrFeedApplication instance(Context context) {
        return (FlickrFeedApplication) context.getApplicationContext();
    }
}
