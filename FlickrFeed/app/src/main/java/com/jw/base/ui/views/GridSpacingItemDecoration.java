package com.jw.base.ui.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Item decorator adding spacings between items of {@link android.support.v7.widget.GridLayoutManager}.
 * <p>
 * Based on: https://gist.github.com/liangzhitao/e57df3c3232ee446d464
 *
 * @author Ailurus, ailurus@foxmail.com, modified by Jaroslaw Wisniewski, j.wisniewski@appsisle.com
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private GridLayoutManager layoutManager;

    private int spacing;

    public GridSpacingItemDecoration(Context context, GridLayoutManager layoutManager, @DimenRes int spacingRes) {
        this.layoutManager = layoutManager;
        this.spacing = context.getResources().getDimensionPixelOffset(spacingRes);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int spanCount = layoutManager.getSpanCount();

        if (position >= 0) {
            int column = position % spanCount;

            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;
            if (position >= spanCount) {
                outRect.top = spacing;
            }
        } else {
            outRect.setEmpty();
        }
    }
}
