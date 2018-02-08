package com.jw.flickrfeed.domain;

import com.jw.flickrfeed.AppTest;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link FilterProfile}.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class FilterProfileTest extends AppTest {

    private FilterProfile sut;

    @Before
    public void setUp() {
        sut = new FilterProfile();
    }

    @Test
    public void returnEmptyFilterByDefault() {

        assertEquals(Filter.EMPTY, sut.getFilter());
    }

    @Test
    public void firstTrainIterationReturnsExactWinningTagsGivenRewardIsLargerThanThreshold() {
        final List<String> winningTags = asList("tag1", "tag2");
        final List<String> expectedTags = asList("tag1", "tag2");

        sut.setReward(1.0f);
        sut.setFavoriteThreshold(0.5f);
        sut.trainFilter(winningTags);

        assertEquals(expectedTags, sut.getFilter().tags());
    }

    @Test
    public void firstTrainIterationReturnsEmptyFilterGivenRewardIsSmallerThanThreshold() {
        final List<String> winningTags = asList("tag1", "tag2");

        sut.setReward(0.5f);
        sut.setFavoriteThreshold(1.0f);
        sut.trainFilter(winningTags);

        assertEquals(Filter.EMPTY, sut.getFilter());
    }

    @Test
    public void removeTagRemovesSelectedTag() {
        final List<String> winningTags = asList("tag1", "tag2");
        final String tagToRemove = "tag2";
        final List<String> expectedTags = singletonList("tag1");

        sut.setReward(1.0f);
        sut.setFavoriteThreshold(0.5f);
        sut.trainFilter(winningTags);
        sut.removeTag(tagToRemove);

        assertEquals(expectedTags, sut.getFilter().tags());
    }

    @Test
    public void resetClearsTheFilter() {
        final List<String> winningTags = asList("tag1", "tag2");

        sut.setReward(1.0f);
        sut.setFavoriteThreshold(0.5f);
        sut.trainFilter(winningTags);
        sut.reset();

        assertEquals(Filter.EMPTY, sut.getFilter());
    }

    @Test
    public void decayHelpsToForgetTagsWinningInPast() {
        final List<String> winningTags1 = asList("tag1", "tag2");
        final List<String> winningTags2 = singletonList("tag1");
        final List<String> winningTags3 = singletonList("tag1");
        final List<String> expectedTags = singletonList("tag1");

        sut.setReward(1.0f);
        sut.setFavoriteThreshold(0.9f);
        sut.setDecay(0.1f);
        sut.trainFilter(winningTags1);
        sut.trainFilter(winningTags2);
        sut.trainFilter(winningTags3);

        assertEquals(expectedTags, sut.getFilter().tags());
    }
}
