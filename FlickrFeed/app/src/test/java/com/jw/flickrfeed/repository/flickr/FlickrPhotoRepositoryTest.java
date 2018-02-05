package com.jw.flickrfeed.repository.flickr;

import com.jw.flickrfeed.domain.Photo;
import com.jw.flickrfeed.repository.flickr.api.FlickrApi;
import com.jw.flickrfeed.repository.flickr.api.FlickrPublicPhotos;
import io.reactivex.Single;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
@RunWith(MockitoJUnitRunner.class)
public class FlickrPhotoRepositoryTest {

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
    public void pullPhotosShouldQueryFlickrApi() {
        sut.pullLatestPhotos(Collections.emptyList());

        verify(flickrApi, times(1)).pullPublicPhotos(anyString());
    }

    @Test
    public void itemShouldBeMappedToPhoto() {
        final FlickrPublicPhotos.Item givenItem = new FlickrPublicPhotos.Item(
                "title",
                "link",
                new FlickrPublicPhotos.Item.Media("mediaUrl"),
                "description",
                new Date(0),
                "author",
                "authorId",
                "tags"
        );

        final Photo expectedPhoto = Photo.builder().author("author")
                                         .publishedAt(new Date(0))
                                         .url("mediaUrl")
                                         .build();

        Photo photo = sut.itemToPhoto(givenItem);

        assertEquals(expectedPhoto, photo);
    }

    @Test
    public void tagsShouldBeJoined() {
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");

        String joinedTags = sut.joinTags(tags);

        assertEquals("tag1,tag2,tag3", joinedTags);
    }
}
