package com.a21buttons.stickyheaders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class StickyHeaderLayoutManager extends RecyclerView.LayoutManager {

  @Override
  public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
    removeAllViews();
  }

  @Override
  public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
    removeAndRecycleAllViews(recycler);
    final int itemCount = getItemCount();

    int top = 0;
    int totalHeight = getHeight();
    int i = 0;

    while (top < totalHeight && i < itemCount) {
      final View view = getView(recycler, i);
      addViewToBottom(view, top);
      top += view.getHeight();
      i++;
    }
  }

  @Override
  public boolean canScrollVertically() {
    return true;
  }

  @Override
  public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
    if (getChildCount() == 0 || dy == 0) {
      return 0;
    }

    final int height = getHeight();

    int scrolled = 0;
    View topView = getTopView(0);
    View bottomView = getBottomView(0);
    if (dy < 0) { // scroll up
      int topPosition = getAdapterPosition(topView);
      while (scrolled > dy) {
        final int top = getDecoratedTop(topView);
        final int scroll = max(min(top, 0), dy - scrolled);
        offsetChildrenVertical(-scroll);
        scrolled += scroll;
        while (getDecoratedTop(bottomView) > height) {
          removeAndRecycleView(bottomView, recycler);
          bottomView = getBottomView(0);
        }
        if (scrolled > dy && topPosition > 0) {
          topPosition--;
          topView = getView(recycler, topPosition);
          addViewToTop(topView, 0);
        } else if (scroll == 0) {
          break;
        }
      }
    } else { // scroll down
      final int itemCount = getItemCount();
      int bottomPosition = getAdapterPosition(bottomView);
      while (scrolled < dy) {
        final int bottom = getDecoratedBottom(bottomView);
        final int scroll = min(max(bottom - height, 0), dy - scrolled);
        offsetChildrenVertical(-scroll);
        scrolled += scroll;
        while (getDecoratedBottom(topView) < 0) {
          removeAndRecycleView(topView, recycler);
          topView = getTopView(0);
        }
        if (scrolled < dy && bottomPosition + 1 < itemCount) {
          bottomPosition++;
          bottomView = getView(recycler, bottomPosition);
          addViewToBottom(bottomView, height);
        } else if (scroll == 0) {
          break;
        }
      }
    }

    return scrolled;
  }

  private View getTopView(int position) {
    return getChildAt(position);
  }

  private View getBottomView(int position) {
    return getChildAt(getChildCount() - 1 - position);
  }

  private void addViewToTop(View view, int bottom) {
    addView(view, 0);
    measureChildWithMargins(view, 0, 0);
    final int width = view.getMeasuredWidth();
    final int height = view.getMeasuredHeight();
    layoutDecorated(view, 0, bottom - height, width, bottom);
  }

  private void addViewToBottom(View view, int top) {
    addView(view);
    measureChildWithMargins(view, 0, 0);
    final int width = view.getMeasuredWidth();
    final int height = view.getMeasuredHeight();
    layoutDecorated(view, 0, top, width, top + height);
  }

  private View getView(RecyclerView.Recycler recycler, int position) {
    return recycler.getViewForPosition(position);
  }

  private int getAdapterPosition(View view) {
    return getViewViewHolder(view).getViewAdapterPosition();
  }

  private RecyclerView.LayoutParams getViewViewHolder(View view) {
    return (RecyclerView.LayoutParams) view.getLayoutParams();
  }

  @Override
  public RecyclerView.LayoutParams generateDefaultLayoutParams() {
    return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
  }
}
