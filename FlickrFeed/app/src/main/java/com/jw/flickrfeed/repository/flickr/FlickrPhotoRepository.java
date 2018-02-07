package com.jw.flickrfeed.repository.flickr;

import android.support.annotation.NonNull;
import com.jw.flickrfeed.domain.Photo;
import com.jw.flickrfeed.domain.PhotoFeed;
import com.jw.flickrfeed.repository.flickr.api.FlickrApi;
import com.jw.flickrfeed.repository.flickr.api.FlickrPublicPhotos;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a list of latest {@link Photo}s from the Flickr Public Feed
 * (https://www.flickr.com/services/feeds/docs/photos_public).
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class FlickrPhotoRepository implements PhotoFeed.PhotoRepository {

    /**
     * Separator of tags string provided as an input to {@link FlickrApi#pullPublicPhotos(String)}.
     */
    private static final String TAGS_JOIN_SEPARATOR = ",";

    private static final String TAGS_SPLIT_SEPARATOR = " ";

    /**
     * Pattern matching a quoted not empty string. Decoded version: "(.+?)".
     */
    private static final Pattern AUTHOR_NAME_PATTERN = Pattern.compile("\"(.+?)\"");

    @NonNull
    private final FlickrApi api;

    public FlickrPhotoRepository(@NonNull FlickrApi api) {
        this.api = api;
    }

    @NonNull
    @Override
    public Single<List<Photo>> loadLatestPhotos(@NonNull Collection<String> tags) {
        return api.pullPublicPhotos(joinTags(tags))
                  .observeOn(AndroidSchedulers.mainThread())
                  .map(FlickrPublicPhotos::items)
                  .flatMap(items -> Observable.fromIterable(items)
                                              .map(this::itemToPhoto)
                                              .toList());
    }

    @NonNull
    Photo itemToPhoto(@NonNull FlickrPublicPhotos.Item item) {
        return Photo.builder()
                    .author(extractQuotedAuthorName(item.author()))
                    .tags(splitTags(item.tags()))
                    .publishedAt(item.datePublished())
                    .thumbnailUrl(item.media().m())
                    .detailsUrl(item.link())
                    .build();
    }

    @NonNull
    String joinTags(@NonNull Collection<String> tags) {
        StringBuilder sb = new StringBuilder();
        for (String tag : tags) {
            if (sb.length() > 0) {
                sb.append(TAGS_JOIN_SEPARATOR);
            }
            sb.append(tag);
        }
        return sb.toString();
    }

    @NonNull
    List<String> splitTags(@NonNull String tags) {
        return Collections.unmodifiableList(Observable.fromArray(tags.split(TAGS_SPLIT_SEPARATOR))
                                                      .filter(tag -> !tag.isEmpty())
                                                      .toList()
                                                      .blockingGet());
    }

    @NonNull
    String extractQuotedAuthorName(@NonNull String flickrFeedAuthor) {
        Matcher matcher = AUTHOR_NAME_PATTERN.matcher(flickrFeedAuthor);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return flickrFeedAuthor;
        }
    }
}
