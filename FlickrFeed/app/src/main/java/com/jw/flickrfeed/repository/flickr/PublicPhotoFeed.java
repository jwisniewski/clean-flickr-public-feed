package com.jw.flickrfeed.repository.flickr;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * A representation of a response of the Flickr Public Feed: https://www.flickr.com/services/feeds/docs/photos_public.
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
@Accessors(chain = true)
class PublicPhotoFeed {

    @Value
    static class Item {

        @Value
        static class Media {

            @NonNull
            String m;

            Media(@NonNull @JsonProperty(value = "m", required = true) String m) {
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
        String dateTaken;

        @NonNull
        String description;

        @NonNull
        String published;

        @NonNull
        String author;

        @NonNull
        String authorId;

        @NonNull
        String tags;

        Item(
                @NonNull @JsonProperty(value = "title", required = true) String title,
                @NonNull @JsonProperty(value = "link", required = true) String link,
                @NonNull @JsonProperty(value = "media", required = true) Media media,
                @NonNull @JsonProperty(value = "date_taken", required = true) String dateTaken,
                @NonNull @JsonProperty(value = "description", required = true) String description,
                @NonNull @JsonProperty(value = "published", required = true) String published,
                @NonNull @JsonProperty(value = "author", required = true) String author,
                @NonNull @JsonProperty(value = "author_id", required = true) String authorId,
                @NonNull @JsonProperty(value = "tags", required = true) String tags) {
            this.title = title;
            this.link = link;
            this.media = media;
            this.dateTaken = dateTaken;
            this.description = description;
            this.published = published;
            this.author = author;
            this.authorId = authorId;
            this.tags = tags;
        }
    }

    @NonNull
    String title;

    @NonNull
    String link;

    @NonNull
    String description;

    @Nullable
    String modified;

    @Nullable
    String generator;

    @NonNull
    List<Item> items;

    public PublicPhotoFeed(
            @NonNull @JsonProperty(value = "title", required = true) String title,
            @NonNull @JsonProperty(value = "link", required = true) String link,
            @NonNull @JsonProperty(value = "description", required = true) String description,
            @Nullable @JsonProperty(value = "modified") String modified,
            @Nullable @JsonProperty(value = "generator") String generator,
            @NonNull @JsonProperty(value = "items", required = true) List<Item> items) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.modified = modified;
        this.generator = generator;
        this.items = items;
    }
}
