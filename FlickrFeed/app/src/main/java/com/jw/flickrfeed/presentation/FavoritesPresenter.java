package com.jw.flickrfeed.presentation;

import android.support.annotation.NonNull;
import com.jw.flickrfeed.domain.FilterProfile;
import java.util.List;

/**
 * Presents an editable list of favorite tags collected by the {@link FilterProfile}.
 * <p>
 * Integrates a presentation logic with an interface of {@link Navigator} switching between presenters
 * and an interface of {@link View} responsible for actual rendering of the content.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class FavoritesPresenter {

    public interface Navigator {

        void navigateBack();
    }

    public interface View {

        void showFavoriteTags(@NonNull List<String> tags);

        void hideFavoriteTags();

        void showClearFavoriteTagsButton(boolean show);
    }

    @NonNull
    private final Navigator navigator;

    @NonNull
    private final View view;

    @NonNull
    private final FilterProfile filterProfile;

    public FavoritesPresenter(@NonNull View view, @NonNull Navigator navigator, @NonNull FilterProfile filterProfile) {
        this.navigator = navigator;
        this.view = view;
        this.filterProfile = filterProfile;

        List<String> favouriteTags = filterProfile.countFavoriteTags();
        if (favouriteTags.isEmpty()) {
            view.hideFavoriteTags();
            view.showClearFavoriteTagsButton(false);
        } else {
            view.showFavoriteTags(favouriteTags);
            view.showClearFavoriteTagsButton(true);
        }
    }

    public void clearFavouritePhotos() {
        filterProfile.reset();

        view.hideFavoriteTags();
        view.showClearFavoriteTagsButton(false);

        navigator.navigateBack();
    }

    public void clearFavoriteTag(@NonNull String tag) {
        filterProfile.removeTag(tag);

        if (filterProfile.countFavoriteTags().isEmpty()) {
            navigator.navigateBack();
        }
    }
}
