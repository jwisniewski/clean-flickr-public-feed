package com.jw.flickrfeed.repository.sharedprefs;

import android.support.annotation.NonNull;
import com.jw.flickrfeed.domain.Filter;
import com.jw.flickrfeed.domain.PhotoFeed;
import io.reactivex.Single;

/**
 * TODO implement me
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class SharedPrefsFilterRepository implements PhotoFeed.FilterRepository {

    @NonNull
    @Override
    public Single<Filter> pullLatestFilter() {
        throw new Error("Not implemented");
    }
}
