package com.a21buttons.stickyheaders

import android.support.v7.widget.RecyclerView
import android.view.View

import java.lang.Math.max
import java.lang.Math.min

class StickyHeaderLayoutManager : RecyclerView.LayoutManager() {

  override fun onAdapterChanged(oldAdapter: RecyclerView.Adapter<*>?, newAdapter: RecyclerView.Adapter<*>?) {
    removeAllViews()
  }

  override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
    removeAndRecycleAllViews(recycler)
    val itemCount = itemCount

    var top = 0
    val totalHeight = height
    var i = 0

    while (top < totalHeight && i < itemCount) {
      val view = getView(recycler, i)
      addViewToBottom(view, top)
      top += view.height
      i++
    }
  }

  override fun canScrollVertically(): Boolean {
    return true
  }

  override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
    if (childCount == 0 || dy == 0) {
      return 0
    }

    val height = height

    var scrolled = 0
    var topView = getTopView()
    var bottomView = getBottomView()
    if (dy < 0) { // scroll up
      var topPosition = getAdapterPosition(topView)
      while (scrolled > dy) {
        val top = getDecoratedTop(topView)
        val scroll = max(min(top, 0), dy - scrolled)
        offsetChildrenVertical(-scroll)
        scrolled += scroll
        while (getDecoratedTop(bottomView) > height) {
          removeAndRecycleView(bottomView, recycler)
          bottomView = getBottomView()
        }
        if (scrolled > dy && topPosition > 0) {
          topPosition--
          topView = getView(recycler, topPosition)
          addViewToTop(topView, 0)
        } else if (scroll == 0) {
          break
        }
      }
    } else { // scroll down
      val itemCount = itemCount
      var bottomPosition = getAdapterPosition(bottomView)
      while (scrolled < dy) {
        val bottom = getDecoratedBottom(bottomView)
        val scroll = min(max(bottom - height, 0), dy - scrolled)
        offsetChildrenVertical(-scroll)
        scrolled += scroll
        while (getDecoratedBottom(topView) < 0) {
          removeAndRecycleView(topView, recycler)
          topView = getTopView()
        }
        if (scrolled < dy && bottomPosition + 1 < itemCount) {
          bottomPosition++
          bottomView = getView(recycler, bottomPosition)
          addViewToBottom(bottomView, height)
        } else if (scroll == 0) {
          break
        }
      }
    }

    return scrolled
  }

  private fun getTopView(position: Int = 0): View {
    return getChildAt(position)
  }

  private fun getBottomView(position: Int = 0): View {
    return getChildAt(childCount - 1 - position)
  }

  private fun addViewToTop(view: View, bottom: Int) {
    addView(view, 0)
    measureChildWithMargins(view, 0, 0)
    val width = view.measuredWidth
    val height = view.measuredHeight
    layoutDecorated(view, 0, bottom - height, width, bottom)
  }

  private fun addViewToBottom(view: View, top: Int) {
    addView(view)
    measureChildWithMargins(view, 0, 0)
    val width = view.measuredWidth
    val height = view.measuredHeight
    layoutDecorated(view, 0, top, width, top + height)
  }

  private fun getView(recycler: RecyclerView.Recycler, position: Int): View {
    return recycler.getViewForPosition(position)
  }

  private fun getAdapterPosition(view: View): Int {
    return getViewHolder(view).viewAdapterPosition
  }

  private fun getViewHolder(view: View): RecyclerView.LayoutParams {
    return view.layoutParams!! as RecyclerView.LayoutParams
  }

  override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
    return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)
  }
}
