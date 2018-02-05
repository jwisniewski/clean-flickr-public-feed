package com.jw.flickrfeed.app.screens.feed;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jw.flickrfeed.R;
import com.jw.flickrfeed.domain.Photo;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapts a list of {@link Photo} as the {@link RecyclerView}s data source.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class PhotoFeedAdapter extends RecyclerView.Adapter<PhotoFeedAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photoImageView)
        ImageView photoImageView;

        @NonNull
        public static ViewHolder create(@NonNull ViewGroup parent) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                                                .inflate(R.layout.photo_feed_item, parent, false));
        }

        private ViewHolder(@NonNull View view) {
            super(view);

            ButterKnife.bind(this, view);
        }

        public void bind(@NonNull Photo photo) {
            Picasso.with(itemView.getContext())
                   .load(photo.url())
                   .fit()
                   .centerCrop()
                   .into(photoImageView);

            /* TODO decide: An alternative solution, enforcing we won't scale up loaded images.
            itemView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    Picasso.with(itemView.getContext())
                           .load(photo.url())
                           .resize(photoImageView.getWidth(), photoImageView.getHeight())
                           .onlyScaleDown()
                           .centerCrop()
                           .into(photoImageView);

                    itemView.removeOnLayoutChangeListener(this);
                }
            });
            */
        }
    }

    @NonNull
    private final List<Photo> photos = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return ViewHolder.create(viewGroup);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.bind(photos.get(i));
    }

    public void updateItems(@NonNull List<Photo> newPhotos) {
        List<Photo> oldPhotos = new ArrayList<>(photos);
        photos.clear();
        photos.addAll(newPhotos);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PhotoFeedDiffCallback(oldPhotos, newPhotos));
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }
}
