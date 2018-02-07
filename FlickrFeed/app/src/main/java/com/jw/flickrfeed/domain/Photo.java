package com.jw.flickrfeed.domain;

import android.support.annotation.NonNull;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Immutable representation of a photo taken by a concrete author at concrete time.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
@Value
@Builder
@Accessors(fluent = true)
public class Photo {

    @NonNull
    private String thumbnailUrl;

    @NonNull
    private String detailsUrl;

    @NonNull
    private Date publishedAt;

    @NonNull
    private List<String> tags;

    @NonNull
    private String author;
}
