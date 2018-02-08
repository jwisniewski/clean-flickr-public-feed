package com.jw.flickrfeed.domain;

import android.support.annotation.NonNull;
import java.util.Collections;
import java.util.List;
import lombok.Value;
import lombok.experimental.Accessors;

import static java.util.Collections.emptyList;

/**
 * Immutable representation of filtering criteria.
 * <p>
 * At the time being a list of preferred tags is supported.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
@Value
@Accessors(fluent = true)
public class Filter {

    public static final Filter EMPTY = new Filter(emptyList());

    @NonNull
    private List<String> tags;
}
