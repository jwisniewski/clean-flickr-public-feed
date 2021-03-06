package com.jw.flickrfeed.repository.flickr.api;

import android.support.annotation.NonNull;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;

/**
 * Simple HTTP server simulating communication with the Flickr API.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
@SuppressWarnings("WeakerAccess")
public class TestFlickrServer extends NanoHTTPD {

    public static final int PORT = 9876;

    public static final String URL = "http://localhost:" + PORT;

    private Response publicPhotosResponse;

    public TestFlickrServer() {
        super(PORT);
    }

    public void setPublicPhotosResponse(@NonNull Response.IStatus httpStatus, @NonNull String mimeType,
            @NonNull String body) {
        publicPhotosResponse = newFixedLengthResponse(httpStatus, mimeType, body);
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            if (session.getMethod() == Method.GET &&
                    session.getUri().equals("/services/feeds/photos_public.gne") &&
                    session.getQueryParameterString().contains("format=json") &&
                    session.getQueryParameterString().contains("nojsoncallback=1")) {
                return servePublicPhotosFeed();
            }

            return serveNotFound();
        } catch (Throwable t) {
            return serveInternalError(t);
        }
    }

    private Response servePublicPhotosFeed() throws IOException {
        return publicPhotosResponse;
    }

    private Response serveNotFound() {
        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/html", "");
    }

    private Response serveInternalError(Throwable throwable) {
        throwable.printStackTrace();

        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/html", "");
    }
}
