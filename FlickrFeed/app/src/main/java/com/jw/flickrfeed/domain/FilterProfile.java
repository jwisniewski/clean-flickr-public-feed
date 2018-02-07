package com.jw.flickrfeed.domain;

import android.support.annotation.NonNull;
import io.reactivex.Observable;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * A profile of user choices. Generates a {@link Filter} automatically, collects the most frequently used tags
 * and builds a filter with them.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
@Accessors(fluent = true)
public class FilterProfile {

    static class ScoredTag {

        static final Comparator<ScoredTag> DESC_SCORE_COMPARATOR = (first, second) ->
                Float.compare(second.score, first.score);

        @NonNull
        final String tag;

        float score;

        ScoredTag(@NonNull String tag) {
            this.tag = tag;
        }

        @Override
        public String toString() {
            return tag;
        }
    }

    @NonNull
    private final Map<String, ScoredTag> scoredTags = new HashMap<>();

    @Getter
    private float decay = 0.2f;

    @Getter
    private float reward = 1.0f;

    @Getter
    private float favouriteThreshold = 0.5f;

    @Getter
    int favouriteLimit = 10;

    /**
     * Amount of score evaporating on every train session by a multiplication with the accumulated score.
     */
    public void setDecay(float decay) {
        this.decay = Math.max(decay, 0.0f);
    }

    /**
     * Amount of score added every time the tag has been available in a wining set.
     */
    public void setReward(float reward) {
        this.reward = Math.max(reward, 0.0f);
    }

    /**
     * A threshold of accumulated rewards necessary to accept a given tag as a favourite and put it into the
     * {@link Filter}.
     */
    public void setFavouriteThreshold(float threshold) {
        this.favouriteThreshold = Math.max(threshold, 0.0f);
    }

    /**
     * Maximum number of tags which can be used to build a resulting {@link Filter}.
     */
    public void setFavouriteLimit(int limit) {
        this.favouriteLimit = Math.max(limit, 0);
    }

    public void train(@NonNull Collection<String> winningTags) {
        if (!winningTags.isEmpty()) {
            decayAllTags(decay);
            rewardWinningTags(winningTags, reward);
        }
    }

    /**
     * Removes all the collected tags.
     */
    public void reset() {
        scoredTags.clear();
    }

    /**
     * Removes a selected tag.
     *
     * @param tag the tag to forget about.
     */
    public void removeTag(@NonNull String tag) {
        scoredTags.remove(tag);
    }

    /**
     * Builds a {@link Filter} with a list of favorite tags.
     *
     * @return the filter.
     */
    @NonNull
    public Filter buildFilter() {
        return new Filter(countFavoriteTags());
    }

    /**
     * Returns a list of favorite tags.
     *
     * @return the list of tags.
     */
    @SuppressWarnings("Convert2MethodRef")
    public List<String> countFavoriteTags() {
        return Observable.fromIterable(scoredTags.values())
                         .filter(scoredTag -> scoredTag.score >= favouriteThreshold)
                         .sorted(ScoredTag.DESC_SCORE_COMPARATOR)
                         .take(favouriteLimit)
                         .map(scoredTag -> scoredTag.toString())
                         .toList()
                         .blockingGet();
    }

    private void decayAllTags(float decay) {
        float remainingPart = Math.max(1.0f - decay, 0.0f);
        for (ScoredTag scoredTag : scoredTags.values()) {
            scoredTag.score *= remainingPart;
        }
    }

    private void rewardWinningTags(@NonNull Collection<String> winningTags, float reward) {
        for (String winningTag : winningTags) {
            ScoredTag scoredTag = obtainScoredTag(scoredTags, winningTag);
            scoredTag.score += reward;
        }
    }

    @NonNull
    private ScoredTag obtainScoredTag(@NonNull Map<String, ScoredTag> scoredTags, @NonNull String tag) {
        ScoredTag scoredTag = scoredTags.get(tag);
        if (scoredTag == null) {
            scoredTags.put(tag, scoredTag = new ScoredTag(tag));
        }
        return scoredTag;
    }
}
