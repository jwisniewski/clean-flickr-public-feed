package com.jw.flickrfeed.repository.flickr.api;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * A response of the Flickr Public Feed (https://www.flickr.com/services/feeds/docs/photos_public).
 * <p>
 * Implementation highlights:
 * <ul>
 * <li>Thread safe due to it's immutability.</li>
 * <li>Designed to properly handle nullable/nonnull annotations in order to reduce risk of null-related crashes.</li>
 * <li>Follows the rule: "don't allow creation of invalid objects", which helps to avoid "fragile" code (always when we
 * allow for creation of invalid objects, we can't easily escape the risk of future bugs - when other developers
 * wil need to deal which our 'not quite safe' objects.</li>
 * <li>Can be parsed from JSON via Jackson library or just built traditionally via constructors.</li>
 * </ul>
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
@Keep
@Value
@Accessors(fluent = true)
public class FlickrPublicPhotos {

    @Value
    @Accessors(fluent = true)
    public static class Item {

        private static final String PUBLISHED_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

        @Value
        @Accessors(fluent = true)
        public static class Media {

            @NonNull
            String m;

            public Media(@NonNull @JsonProperty(value = "m", required = true) String m) {
                this.m = m;
            }
        }

        @NonNull
        String title;

        @NonNull
        String link;

        @NonNull
        Media media;

        @NonNull
        String description;

        @NonNull
        Date datePublished;

        @NonNull
        String author;

        @NonNull
        String authorId;

        @NonNull
        String tags;

        public Item(
                @NonNull @JsonProperty(value = "title", required = true) String title,
                @NonNull @JsonProperty(value = "link", required = true) String link,
                @NonNull @JsonProperty(value = "media", required = true) Media media,
                @NonNull @JsonProperty(value = "description", required = true) String description,
                @NonNull @JsonProperty(value = "published", required = true) @JsonFormat(
                        shape = JsonFormat.Shape.STRING, pattern = PUBLISHED_DATE_PATTERN) Date datePublished,
                @NonNull @JsonProperty(value = "author", required = true) String author,
                @NonNull @JsonProperty(value = "author_id", required = true) String authorId,
                @NonNull @JsonProperty(value = "tags", required = true) String tags) {
            this.title = title;
            this.link = link;
            this.media = media;
            this.description = description;
            this.datePublished = datePublished;
            this.author = author;
            this.authorId = authorId;
            this.tags = tags;
        }
    }

    @NonNull
    List<Item> items;

    public FlickrPublicPhotos(
            @NonNull @JsonProperty(value = "items", required = true) List<Item> items) {
        this.items = items;
    }
}
