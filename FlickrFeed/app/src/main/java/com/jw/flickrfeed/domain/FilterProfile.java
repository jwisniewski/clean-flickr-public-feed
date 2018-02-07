package com.jw.flickrfeed.domain;

import android.support.annotation.NonNull;
import io.reactivex.Observable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * A profile of user choices, generating the {@link Filter} automatically.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
@Accessors(fluent = true)
public class FilterProfile {

    @Accessors(fluent = true)
    public static class ScoredTag implements Comparable<ScoredTag> {

        @Getter
        @NonNull
        final String tag;

        @Getter
        float score;

        ScoredTag(@NonNull String tag) {
            this.tag = tag;
        }

        @Override
        public int compareTo(@NonNull ScoredTag other) {
            return Float.compare(score, other.score);
        }

        @Override
        public String toString() {
            return tag;
        }
    }

    @NonNull
    private final Map<String, ScoredTag> scoredTags = new HashMap<>();

    /**
     * Amount of score evaporating on every train session by a multiplication with the accumulated score.
     */
    @Getter
    private float decay = 0.1f;

    /**
     * Amount of score added every time the tag has been available in a wining set.
     */
    @Getter
    private float reward = 1.0f;

    @Getter
    private float favouriteThreshold = 0.5f;

    @Getter
    int favouriteLimit = 20;

    public void setDecay(float decay) {
        this.decay = Math.max(decay, 0.0f);
    }

    public void setReward(float reward) {
        this.reward = Math.max(reward, 0.0f);
    }

    public void setFavouriteThreshold(float threshold) {
        this.favouriteThreshold = Math.max(threshold, 0.0f);
    }

    public void setFavouriteLimit(int limit) {
        this.favouriteLimit = Math.max(limit, 0);
    }

    public void train(@NonNull Collection<String> winningTags) {
        if (!winningTags.isEmpty()) {
            decayAllTags(decay);
            rewardWinningTags(winningTags, reward);
        }
    }

    public void clear() {
        scoredTags.clear();
    }

    public void removeTag(String tag) {
        scoredTags.remove(tag);
    }

    @NonNull
    public Filter buildFilter() {
        final List<String> filterTags = Observable.fromIterable(countFavoriteTags())
                                                  .map(scoredTag -> scoredTag.tag)
                                                  .toList()
                                                  .blockingGet();

        return new Filter(filterTags);
    }

    public List<ScoredTag> countFavoriteTags() {
        return Observable.fromIterable(scoredTags.values())
                         .filter(sortedTag -> sortedTag.score >= favouriteThreshold)
                         .sorted()
                         .take(favouriteLimit)
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
