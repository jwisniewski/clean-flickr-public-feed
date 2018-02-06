package com.jw.flickrfeed.repository.flickr.api;

import com.jw.flickrfeed.AppTest;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.jw.flickrfeed.utils.TestResources.readFile;
import static fi.iki.elonen.NanoHTTPD.Response.Status.OK;
import static org.junit.Assert.assertEquals;

/**
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class FlickrApiTest extends AppTest {

    private FlickrApi sut;

    private TestFlickrServer flickrServer;

    @Before
    public void setUp() throws IOException {
        sut = new FlickrApiFactory().verbose(true).create(TestFlickrServer.URL);

        flickrServer = new TestFlickrServer();
        flickrServer.start();
    }

    @After
    public void tearDown() {
        flickrServer.stop();
    }

    @Test
    public void validPublicPhotosFeedShouldReturnWithSuccess() throws IOException {
        flickrServer.setPublicPhotosResponse(OK, "application/json; charset=utf-8",
                readFile("/flickr/api/services/feeds/photos_public_with_one_valid_item.json", "utf-8"));

        FlickrPublicPhotos response = sut.pullPublicPhotos(null).blockingGet();

        assertEquals(1, response.items().size());
    }
}
