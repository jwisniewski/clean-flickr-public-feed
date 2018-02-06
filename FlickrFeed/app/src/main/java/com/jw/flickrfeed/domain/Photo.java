package com.jw.flickrfeed.domain;

import android.support.annotation.NonNull;
import java.util.Date;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Immutable representation of a photography taken by a concrete author at concrete time.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
@Value
@Builder
@Accessors(fluent = true)
public class Photo {

    @NonNull
    String url;

    @NonNull
    Date publishedAt;

    @NonNull
    String author;
}
