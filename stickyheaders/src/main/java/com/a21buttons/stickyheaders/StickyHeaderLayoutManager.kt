package com.a21buttons.stickyheaders

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup

import java.lang.Math.max
import java.lang.Math.min

class StickyHeaderLayoutManager : RecyclerView.LayoutManager() {
  private val viewCache = SparseArray<View>()

  override fun onAdapterChanged(oldAdapter: RecyclerView.Adapter<*>?, newAdapter: RecyclerView.Adapter<*>?) {
    removeAllViews()
    if (newAdapter !is StickyHeaderAdapter?) {
      throw IllegalArgumentException("The adapter must extend com.a21buttons.stickyheaders.StickyHeaderAdapter")
    }
  }

  override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
    removeAndRecycleAllViews(recycler)
    val itemCount = itemCount

    var top = 0
    val totalHeight = height
    var i = 0

    while (top < totalHeight && i < itemCount) {
      val view = createView(recycler, i, childCount)
      layoutToBottom(view, top)
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
          topView = createView(recycler, topPosition, 0)
          layoutToTop(topView, 0)
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
          bottomView = createView(recycler, bottomPosition, childCount)
          layoutToBottom(bottomView, height)
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

  /**
   *
   * @param childPosition [0..childCount]
   */
  private fun createView(recycler: RecyclerView.Recycler, adapterPosition: Int, childPosition: Int): View {
    var view: View? = viewCache[adapterPosition]
    if (view != null) {
      viewCache.delete(adapterPosition)
      attachView(view, childPosition)
    } else {
      view = recycler.getViewForPosition(adapterPosition)!!
      addView(view, childPosition)
      measureChildWithMargins(view, 0, 0)
    }
    return view
  }

  private fun layoutToTop(view: View, bottom: Int) {
    val width = view.measuredWidth
    val height = view.measuredHeight
    layoutDecorated(view, 0, bottom - height, width, bottom)
  }

  private fun layoutToBottom(view: View, top: Int) {
    val width = view.measuredWidth
    val height = view.measuredHeight
    layoutDecorated(view, 0, top, width, top + height)
  }

  private fun getAdapterPosition(view: View): Int {
    return getLayoutParams(view).viewAdapterPosition
  }

  private fun getSectionId(view: View): Long {
    return getTag(view).sectionId
  }

  private fun getLayoutParams(view: View): LayoutParams {
    return view.layoutParams as LayoutParams
  }

  private fun getTag(view: View): StickyHeaderData {
    return view.getTag(R.id.com_a21buttons_stickyheaders_tag) as StickyHeaderData
  }

  override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
    return lp is LayoutParams
  }

  override fun generateDefaultLayoutParams(): LayoutParams {
    return LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)
  }

  override fun generateLayoutParams(c: Context, attrs: AttributeSet): LayoutParams {
    return LayoutParams(c, attrs)
  }

  override fun generateLayoutParams(lp: ViewGroup.LayoutParams): LayoutParams {
    return LayoutParams(lp)
  }

  class LayoutParams : RecyclerView.LayoutParams {

    constructor(lp: ViewGroup.LayoutParams) : super(lp)

    constructor(c: Context, attrs: AttributeSet) : super(c, attrs)

    constructor(width: Int, height: Int) : super(width, height)
  }
}
