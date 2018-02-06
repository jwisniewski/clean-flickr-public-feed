package com.jw.flickrfeed.presentation;

import android.support.annotation.NonNull;

/**
 * TODO implement me
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public interface Navigator {

    /**
     * Navigates to the external web browser.
     *
     * @param url the URL to display
     */
    boolean openWebPage(@NonNull String url);
}
