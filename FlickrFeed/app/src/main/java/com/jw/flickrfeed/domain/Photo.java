package com.jw.flickrfeed.domain;

import android.support.annotation.NonNull;
import java.util.Date;
import lombok.Builder;
import lombok.Value;

/**
 * TODO implement me
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
@Value
@Builder
public class Photo {

    @NonNull
    String url;

    @NonNull
    Date publishedAt;

    @NonNull
    String author;
}
