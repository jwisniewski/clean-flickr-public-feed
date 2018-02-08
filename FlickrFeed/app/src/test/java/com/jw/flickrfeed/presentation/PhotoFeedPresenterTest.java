package com.jw.flickrfeed.presentation;

import com.jw.flickrfeed.AppTest;
import com.jw.flickrfeed.domain.FilterProfile;
import com.jw.flickrfeed.domain.Photo;
import com.jw.flickrfeed.domain.PhotoFeed;
import io.reactivex.Observable;
import io.reactivex.subjects.CompletableSubject;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the presenter: {@link PhotoFeedPresenter}.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class PhotoFeedPresenterTest extends AppTest {

    @Mock
    private PhotoFeed photoFeed;

    @Mock
    private Observable<List<Photo>> observablePhotos;

    @Mock
    private FilterProfile filterProfile;

    @Mock
    private PhotoFeedPresenter.View view;

    @Mock
    private PhotoFeedPresenter.Navigator navigator;

    private CompletableSubject refreshCompletable;

    private PhotoFeedPresenter sut;

    @Before
    public void setUp() throws Exception {
        refreshCompletable = CompletableSubject.create();

        when(photoFeed.observablePhotos()).thenReturn(observablePhotos);
        when(photoFeed.refresh()).thenReturn(refreshCompletable);

        sut = new PhotoFeedPresenter(view, navigator, photoFeed, filterProfile);
    }

    @After
    public void tearDown() {
        sut.destroy();
    }

    @Test
    public void refreshPhotosOnCreated() {

        verify(photoFeed).refresh();
    }

    @Test
    public void showRefreshIndicatorGivenUserRequestedRefresh() {
        consumeInitialRefresh();

        sut.refreshPhotos();

        verify(view).showRefreshing(false);
    }

    @Test
    public void hideRefreshIndicatorGivenRefreshCompleted() {
        consumeInitialRefresh();

        sut.refreshPhotos();

        refreshCompletable.onComplete();

        verify(view).showRefreshing(true);
        verify(view).showRefreshing(false);
    }

    @Test
    public void trainFilterGiverUserSelectedPhoto() {
        final Photo givenPhoto = Photo.builder()
                                      .author("author")
                                      .tags(Arrays.asList("tag1", "tag2"))
                                      .publishedAt(new Date(0))
                                      .thumbnailUrl("media")
                                      .detailsUrl("link")
                                      .build();

        final List<String> expectedTags = Arrays.asList("tag1", "tag2");

        sut.selectPhoto(givenPhoto);

        verify(filterProfile).trainFilter(expectedTags);
    }

    private void consumeInitialRefresh() {
        refreshCompletable.onComplete();
        reset(view);
    }
}
