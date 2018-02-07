package com.jw.flickrfeed.app.screens.feed;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.jw.base.ui.activities.AppFragment;
import com.jw.base.ui.views.GridSpacingItemDecoration;
import com.jw.flickrfeed.R;
import com.jw.flickrfeed.domain.Photo;
import com.jw.flickrfeed.presentation.PhotoFeedPresenter;
import java.util.List;

/**
 * Presents a photo feed, an implementation of {@link PhotoFeedPresenter.View}.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class PhotoFeedFragment extends AppFragment implements PhotoFeedPresenter.View,
        PhotoFeedAdapter.PhotoIntegrationListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.photosSwipeRefreshLayout)
    SwipeRefreshLayout photosSwipeRefreshLayout;

    @BindView(R.id.photosRecyclerView)
    RecyclerView photosRecyclerView;

    PhotoFeedAdapter photosAdapter;

    Unbinder unbinder;

    PhotoFeedPresenter presenter;

    @NonNull
    public static PhotoFeedFragment newInstance() {
        return new PhotoFeedFragment();
    }

    @NonNull
    private PhotoFeedScope injectScope(@Nullable Activity activity) {
        if (activity instanceof PhotoFeedScope) {
            return (PhotoFeedScope) activity;
        } else {
            throw new IllegalStateException("Fragment used within a wrong scope");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_feed_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        final int spanCount = getResources().getInteger(R.integer.photo_feed_span_count);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);
        final GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(getContext(), layoutManager,
                R.dimen.grid_0_5x);

        photosRecyclerView.setHasFixedSize(true);
        photosRecyclerView.setLayoutManager(layoutManager);
        photosRecyclerView.addItemDecoration(itemDecoration);
        photosRecyclerView.setAdapter(photosAdapter = new PhotoFeedAdapter());
        photosRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            final int newSpanCount = getResources().getInteger(R.integer.photo_feed_span_count);
            layoutManager.setSpanCount(newSpanCount);
            photosRecyclerView.invalidateItemDecorations();
        });

        final PhotoFeedScope scope = injectScope(getActivity());

        presenter = new PhotoFeedPresenter(this, scope.photoFeedNavigator(), scope.photoFeed(), scope.filterProfile());

        photosAdapter.setPhotoIntegrationListener(this);
        photosSwipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.destroy();
        presenter = null;

        unbinder.unbind();
        unbinder = null;
    }

    @Override
    public void onRefresh() {
        presenter.refreshPhotos();
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter.refreshPhotos();
    }

    @Override
    public void onPhotoSelected(@NonNull Photo photo) {
        presenter.selectPhoto(photo);
    }

    @Override
    public void onPhotoDetailsRequested(@NonNull Photo photo) {
        presenter.presentPhotoDetails(photo);
    }

    @OnClick(R.id.favoritesFab)
    public void onFavoritesButtonClicked() {
        presenter.presentFavorites();
    }

    @Override
    public void showRefreshing(boolean refreshing) {
        photosSwipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void showPhotos(@NonNull List<Photo> photos) {
        photosAdapter.updateItems(photos);
    }

    @Override
    public void showTryLaterHint() {
        Snackbar.make(photosRecyclerView, R.string.connection_issue_try_later, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showSelectedPhotoNotTaggedHint() {
        Snackbar.make(photosRecyclerView, "Photo not tagged :(", Snackbar.LENGTH_LONG).show();
    }
}
