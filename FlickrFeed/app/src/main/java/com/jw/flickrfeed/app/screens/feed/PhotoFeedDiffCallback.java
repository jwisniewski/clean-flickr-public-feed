package com.jw.flickrfeed.app.screens.feed;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import com.jw.flickrfeed.domain.Photo;
import java.util.List;
import java.util.Objects;

/**
 * A Callback class used by {@link DiffUtil} while calculating the difference between two lists of {@link Photo}s.
 *
 * @author Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class PhotoFeedDiffCallback extends DiffUtil.Callback {

    @NonNull
    private final List<Photo> oldPhotos;

    @NonNull
    private final List<Photo> newPhotos;

    public PhotoFeedDiffCallback(@NonNull List<Photo> oldPhotos, @NonNull List<Photo> newPhotos) {
        this.oldPhotos = oldPhotos;
        this.newPhotos = newPhotos;
    }

    @Override
    public int getOldListSize() {
        return oldPhotos.size();
    }

    @Override
    public int getNewListSize() {
        return newPhotos.size();
    }

    @Override
    public boolean areItemsTheSame(int oldPosition, int newPosition) {
        return Objects.equals(oldPhotos.get(oldPosition).url(), newPhotos.get(newPosition).url());
    }

    @Override
    public boolean areContentsTheSame(int oldPosition, int newPosition) {
        return Objects.equals(oldPhotos.get(oldPosition), newPhotos.get(newPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return null;
    }
}
