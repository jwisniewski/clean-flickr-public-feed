package com.jw.flickrfeed.app.screens.favorites;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.google.android.flexbox.FlexboxLayout;
import com.jw.flickrfeed.R;
import com.jw.flickrfeed.app.screens.feed.PhotoFeedScope;
import com.jw.flickrfeed.domain.FilterProfile;
import com.jw.flickrfeed.presentation.FavoritesPresenter;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import fisk.chipcloud.ChipDeletedListener;
import java.util.List;

/**
 * A bottom sheet dialog with favorite tags (basing on the {@link FilterProfile}.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class FavoritesDialogFragment extends BottomSheetDialogFragment
        implements FavoritesPresenter.View, ChipDeletedListener {

    public static final String TAG = "FavouritesDialogFragment";

    @BindView(R.id.favoritesContent)
    ViewGroup favoritesContent;

    @BindView(R.id.tagsFlexboxLayout)
    FlexboxLayout tagsFlexboxLayout;

    @BindView(R.id.clearFavoritesButton)
    ImageButton clearFavoritesButton;

    Unbinder unbinder;

    ChipCloud tagsCloud;

    FavoritesPresenter presenter;

    public static FavoritesDialogFragment newInstance() {
        return new FavoritesDialogFragment();
    }

    @NonNull
    private FavoritesScope injectScope(@Nullable Activity activity) {
        if (activity instanceof PhotoFeedScope) {
            return (FavoritesScope) activity;
        } else {
            throw new IllegalStateException("Fragment used within a wrong scope");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final Context themedContext = new ContextThemeWrapper(getActivity(), R.style.ThemeOverlay_AppCompat_Dark);
        final LayoutInflater themedInflater = inflater.cloneInContext(themedContext);
        final View view = themedInflater.inflate(R.layout.favorites_fragment, container, false);

        unbinder = ButterKnife.bind(this, view);

        final ChipCloudConfig tagsCloudConfig = new ChipCloudConfig()
                .selectMode(ChipCloud.SelectMode.none)
                .useInsetPadding(true)
                .uncheckedChipColor(ResourcesCompat.getColor(getResources(), R.color.colorCardBackground, null))
                .showClose(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null),
                        getResources().getInteger(android.R.integer.config_shortAnimTime));

        tagsCloud = new ChipCloud(getContext(), tagsFlexboxLayout, tagsCloudConfig);
        tagsCloud.setDeleteListener(this);

        final FavoritesScope scope = injectScope(getActivity());
        presenter = new FavoritesPresenter(this, scope.favouritesNavigator(), scope.filterProfile());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
        unbinder = null;

        tagsCloud.setDeleteListener(null);
        tagsCloud = null;
    }

    @OnClick(R.id.clearFavoritesButton)
    public void onClearFavoritesClicked() {
        presenter.clearFavouritePhotos();
    }

    @Override
    public void chipDeleted(int index, String string) {
        presenter.clearFavoriteTag(string);
    }

    @Override
    public void showFavoriteTags(@NonNull List<String> tags) {
        tagsFlexboxLayout.setVisibility(View.VISIBLE);
        tagsCloud.addChips(tags);
    }

    @Override
    public void hideFavoriteTags() {
        tagsFlexboxLayout.setVisibility(View.GONE);
    }

    @Override
    public void showClearFavoriteTagsButton(boolean show) {
        clearFavoritesButton.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
