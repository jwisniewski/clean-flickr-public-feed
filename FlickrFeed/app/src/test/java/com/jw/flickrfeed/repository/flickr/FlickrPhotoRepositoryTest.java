package com.jw.flickrfeed.repository.flickr;

import com.jw.flickrfeed.AppTest;
import com.jw.flickrfeed.domain.Photo;
import com.jw.flickrfeed.domain.PhotoFeed;
import com.jw.flickrfeed.repository.flickr.api.FlickrApi;
import com.jw.flickrfeed.repository.flickr.api.FlickrPublicPhotos;
import io.reactivex.Single;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests of Flickr implementation of {@link PhotoFeed.PhotoRepository}.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class FlickrPhotoRepositoryTest extends AppTest {

    @Mock
    private FlickrApi flickrApi;

    private FlickrPhotoRepository sut;

    @Before
    public void setUp() {
        sut = new FlickrPhotoRepository(flickrApi);

        when(flickrApi.pullPublicPhotos(anyString())).thenReturn(
                Single.just(new FlickrPublicPhotos(Collections.emptyList())));
    }

    @Test
    public void loadLatestPhotosShouldQueryFlickrApi() {
        sut.loadLatestPhotos(Collections.emptyList());

        verify(flickrApi, times(1)).pullPublicPhotos(anyString());
    }

    @Test
    public void loadLatestPhotosShouldJoinTags() {
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");

        sut.loadLatestPhotos(tags);

        verify(flickrApi, times(1)).pullPublicPhotos("tag1,tag2,tag3");
    }

    @Test
    public void itemShouldBeMappedToPhoto() {
        final FlickrPublicPhotos.Item givenItem = new FlickrPublicPhotos.Item(
                "title",
                "link",
                new FlickrPublicPhotos.Item.Media("media"),
                "description",
                new Date(0),
                "author",
                "authorId",
                "tag1 tag2 tag3"
        );

        final Photo expectedPhoto = Photo.builder()
                                         .author("author")
                                         .tags(Arrays.asList("tag1", "tag2", "tag3"))
                                         .publishedAt(new Date(0))
                                         .thumbnailUrl("media")
                                         .detailsUrl("link")
                                         .build();

        Photo photo = sut.mapItemToPhoto(givenItem);

        assertEquals(expectedPhoto, photo);
    }

    @Test
    public void extractQuotedAuthorNameShouldExtractQuotedString() {
        String flickrFeedAuthor = "nobody@flickr.com (\"Dyler.com - Classic Cars for Sale\")";

        String authorName = sut.extractQuotedAuthorName(flickrFeedAuthor);

        assertEquals("Dyler.com - Classic Cars for Sale", authorName);
    }

    @Test
    public void extractQuotedAuthorNameShouldFallbackToInputString() {
        String flickrFeedAuthor = "nobody@flickr.com (\"Dyler.com - Classic Cars for Sale";

        String authorName = sut.extractQuotedAuthorName(flickrFeedAuthor);

        assertEquals(flickrFeedAuthor, authorName);
    }
}
