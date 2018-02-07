package com.jw.flickrfeed.app.screens.feed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.jw.base.ui.activities.AppFragment;
import com.jw.base.ui.views.GridSpacingItemDecoration;
import com.jw.flickrfeed.R;
import com.jw.flickrfeed.app.FlickrFeedApplication;
import com.jw.flickrfeed.domain.Photo;
import com.jw.flickrfeed.presentation.Navigator;
import com.jw.flickrfeed.presentation.PhotoFeedPresenter;
import java.util.List;

/**
 * Fragment presenting a photo feed, an implementation of {@link PhotoFeedPresenter.View}.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class PhotoFeedFragment extends AppFragment implements PhotoFeedPresenter.View,
        PhotoFeedAdapter.PhotoIntegrationListener, SwipeRefreshLayout.OnRefreshListener {

    static final String TAG = PhotoFeedFragment.class.getSimpleName();

    @BindView(R.id.photosSwipeRefreshLayout)
    SwipeRefreshLayout photosSwipeRefreshLayout;

    @BindView(R.id.photosRecyclerView)
    RecyclerView photosRecyclerView;

    PhotoFeedAdapter photosAdapter;

    Unbinder unbinder;

    PhotoFeedPresenter presenter;

    Navigator navigator;

    @NonNull
    public static PhotoFeedFragment newInstance() {
        return new PhotoFeedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_feed_screen, container, false);
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

        final FlickrFeedApplication app = FlickrFeedApplication.instance(getContext());
        presenter = new PhotoFeedPresenter(navigator, this, app.photoFeed(), app.filterProfile());

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
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Navigator) {
            navigator = (Navigator) context;
        } else {
            throw new IllegalStateException("Fragment expects attaching activity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        navigator = null;
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh");

        presenter.refreshPhotos();
    }

    @Override
    public void onPhotoSelected(@NonNull Photo photo) {
        Log.d(TAG, "onPhotoSelected: " + photo);

        presenter.selectPhoto(photo);
    }

    @Override
    public void onPhotoDetailsRequested(@NonNull Photo photo) {
        Log.d(TAG, "onPhotoDetailsRequested: " + photo);

        presenter.requestPhotoDetails(photo);
    }

    @Override
    public void showRefreshing(boolean refreshing) {
        Log.d(TAG, "showRefreshing: " + refreshing);

        photosSwipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void showPhotos(@NonNull List<Photo> photos) {
        Log.d(TAG, "showPhotos: " + photos);

        photosAdapter.updateItems(photos);
    }

    @Override
    public void showTryLaterHint() {
        Log.d(TAG, "showTryLaterHint");

        Snackbar.make(photosRecyclerView, R.string.connection_issue_try_later, Snackbar.LENGTH_LONG).show();
    }
}
