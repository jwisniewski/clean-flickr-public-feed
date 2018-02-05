package com.jw.flickrfeed.app.screens.feed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.jw.base.ui.activities.AppFragment;
import com.jw.flickrfeed.R;
import com.jw.flickrfeed.app.FlickrFeedApplication;
import com.jw.flickrfeed.domain.Photo;
import com.jw.flickrfeed.presentation.PhotoFeedPresenter;
import java.util.List;

/**
 * Fragment presenting a photo feed, an implementation of {@link PhotoFeedPresenter.View}.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class PhotoFeedFragment extends AppFragment implements PhotoFeedPresenter.View {

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_feed_screen, container, false);
        unbinder = ButterKnife.bind(this, view);

        final LinearLayoutManager photosLayoutManager = new LinearLayoutManager(getContext());
        photosRecyclerView.setHasFixedSize(true);
        photosRecyclerView.setLayoutManager(photosLayoutManager);
        photosRecyclerView.setAdapter(photosAdapter = new PhotoFeedAdapter());

        presenter = new PhotoFeedPresenter(this, FlickrFeedApplication.instance(getContext()).photoFeed());

        photosSwipeRefreshLayout.setOnRefreshListener(() -> presenter.refreshPhotos());

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
    public void showRefreshing(boolean refreshing) {
        photosSwipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void showPhotos(@NonNull List<Photo> photos) {
        photosAdapter.updateItems(photos);
    }

    @Override
    public void showTryLaterHint() {
        Snackbar.make(photosRecyclerView, R.string.connection_issue_try_later, Snackbar.LENGTH_LONG);
    }
}
