package com.a21buttons.stickyheaders

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup

import java.lang.Math.max
import java.lang.Math.min

class StickyHeaderLayoutManager : RecyclerView.LayoutManager() {
  private val viewCache = SparseArray<View>()
  private var stickyHeader: HeaderLookup = HeaderLookupPlaceholder
  private var firstVisibleAdapterPosition: Int = 0
  private var firstVisibleTop: Int = 0
  private var pendingSavedState: SavedState? = null

  override fun onAdapterChanged(oldAdapter: RecyclerView.Adapter<*>?, newAdapter: RecyclerView.Adapter<*>?) {
    removeAllViews()
    firstVisibleAdapterPosition = 0
    firstVisibleTop = 0
    if (newAdapter !is StickyHeaderAdapter?) {
      throw IllegalArgumentException("The adapter must extend com.a21buttons.stickyheaders.StickyHeaderAdapter")
    } else if (newAdapter != null) {
      stickyHeader = newAdapter
    } else {
      stickyHeader = HeaderLookupPlaceholder
    }
  }

  override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
    if (itemCount <= 0) {
      removeAndRecycleAllViews(recycler)
      firstVisibleAdapterPosition = 0
      firstVisibleTop = 0
      return
    }

    val pending = pendingSavedState
    if (pending != null) {
      firstVisibleAdapterPosition = pending.firstVisibleAdapterPosition
      firstVisibleTop = pending.firstVisibleTop
    }

    val adapterPosition = firstVisibleAdapterPosition
    val top = firstVisibleTop

    detachAndScrapAttachedViews(recycler)

    val (_, filledDownTo) = fillFromTop(recycler, adapterPosition, top)
    if (filledDownTo < height) {
      detachAndScrapAttachedViews(recycler)
      val (position, filledUpTo) = fillFromBottom(recycler, itemCount - 1, height)
      if (filledUpTo > 0) {
        offsetChildrenVertical(-filledUpTo)
        firstVisibleAdapterPosition = 0
        firstVisibleTop = 0
      } else {
        firstVisibleAdapterPosition = position
        firstVisibleTop = filledUpTo
      }
    }

    addHeaders(recycler)
  }

  override fun onLayoutCompleted(state: RecyclerView.State) {
    pendingSavedState = null
  }

  override fun canScrollVertically(): Boolean {
    return true
  }

  private fun fillFromTop(recycler: RecyclerView.Recycler, initialAdapterPosition: Int, initialTop: Int): FillFrom {
    val itemCount = itemCount
    val totalHeight = height

    var top = initialTop
    var i = initialAdapterPosition
    while (top < totalHeight && i < itemCount) {
      val view = createView(recycler, i, childCount)
      layoutToBottom(view, top)
      top += getDecoratedMeasuredHeight(view)
      i++
    }
    return FillFrom(max(initialAdapterPosition, i - 1), top)
  }

  private fun fillFromBottom(recycler: RecyclerView.Recycler, initialAdapterPosition: Int, initialBottom: Int): FillFrom {
    var bottom = initialBottom
    var i = initialAdapterPosition
    while (bottom > 0 && i >= 0) {
      val view = createView(recycler, i, 0)
      layoutToTop(view, bottom)
      bottom -= getDecoratedMeasuredHeight(view)
      i--
    }
    return FillFrom(min(initialAdapterPosition, i + 1), bottom)
  }

  data class FillFrom(val adapterPosition: Int, val filledTo: Int)

  override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
    if (childCount == 0 || dy == 0) {
      return 0
    }

    removeHeaders()

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

    firstVisibleAdapterPosition = getAdapterPosition(topView)
    firstVisibleTop = getDecoratedTop(topView)

    addHeaders(recycler)
    recycleUnusedViews(recycler)

    return scrolled
  }

  override fun scrollToPosition(position: Int) {
    firstVisibleAdapterPosition = position
    firstVisibleTop = 0

    pendingSavedState = null
    requestLayout()
  }

  override fun onSaveInstanceState(): Parcelable {
    return pendingSavedState ?: SavedState(firstVisibleAdapterPosition, firstVisibleTop)
  }

  override fun onRestoreInstanceState(state: Parcelable?) {
    pendingSavedState = state as SavedState?
    requestLayout()
  }

  fun findFirstVisibleItemPosition(): Int {
    return if (childCount <= 0) NO_POSITION else firstVisibleAdapterPosition
  }

  fun findLastVisibleItemPosition(): Int {
    val needOffset = getDecoratedTop(getChildAt(0)) > 0
    var offset = 0
    for (i in childCount - 1 downTo 0) {
      val view = getChildAt(i)
      if (!isStickyHeader(view)) {
        return firstVisibleAdapterPosition + i + offset
      } else if (needOffset) {
        offset++
      }
    }
    return NO_POSITION
  }

  private fun removeHeaders() {
    for (i in childCount - 1 downTo 0) {
      val view = getChildAt(i)
      if (isStickyHeader(view)) {
        detachView(view)
        setStickyHeader(view, false)
        val bottom = getDecoratedTop(getChildAt(0))
        if (bottom > 0) {
          attachView(view, 0)
          layoutToTop(view, bottom)
        } else {
          viewCache.put(getAdapterPosition(view), view)
        }
      } else {
        break
      }
    }
  }

  private fun addHeaders(recycler: RecyclerView.Recycler) {
    val topView = getTopView()
    val topSectionId = getSectionId(topView)
    val headerAdapterPosition = stickyHeader.getHeaderPosition(topSectionId)
    if (headerAdapterPosition >= 0) {
      if (headerAdapterPosition == getAdapterPosition(topView)) {
        if (getDecoratedTop(topView) < 0 && getDecoratedBottom(topView) < height) {
          val bottom = calculateSpace(getDecoratedMeasuredHeight(topView), topSectionId)
          detachView(topView)
          attachView(topView)
          setStickyHeader(topView, true)
          layoutToTop(topView, bottom)
        }
      } else {
        val header = createView(recycler, headerAdapterPosition, childCount)
        setStickyHeader(header, true)
        layoutToTop(header, calculateSpace(getDecoratedMeasuredHeight(header), topSectionId))
      }
    }
  }

  private fun recycleUnusedViews(recycler: RecyclerView.Recycler) {
    for (i in 0..viewCache.size() - 1) {
      recycler.recycleView(viewCache.valueAt(i))
    }
    viewCache.clear()
  }

  private fun calculateSpace(headerHeight: Int, sectionId: Long): Int {
    var space = 0
    for (i in 0..childCount - 1) {
      val view = getTopView(i)
      if (getSectionId(view) == sectionId) {
        space = min(getDecoratedBottom(view), headerHeight)
        if (space >= headerHeight) {
          break
        }
      } else {
        break
      }
    }
    return min(space, height)
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
    getLayoutParams(view).stickyHeader = false
    return view
  }

  private fun layoutToTop(view: View, bottom: Int) {
    val width = getDecoratedMeasuredWidth(view)
    val height = getDecoratedMeasuredHeight(view)
    layoutDecorated(view, 0, bottom - height, width, bottom)
  }

  private fun layoutToBottom(view: View, top: Int) {
    val width = getDecoratedMeasuredWidth(view)
    val height = getDecoratedMeasuredHeight(view)
    layoutDecorated(view, 0, top, width, top + height)
  }

  private fun getAdapterPosition(view: View): Int {
    return getLayoutParams(view).viewAdapterPosition
  }

  private fun getSectionId(view: View): Long {
    return getViewHolder(view).sectionId
  }

  private fun isStickyHeader(view: View): Boolean {
    return getLayoutParams(view).stickyHeader
  }

  private fun setStickyHeader(view: View, stickyHeader: Boolean) {
    getLayoutParams(view).stickyHeader = stickyHeader
  }

  private fun getLayoutParams(view: View): LayoutParams {
    return view.layoutParams as LayoutParams
  }

  private fun getViewHolder(view: View): StickyHeaderViewHolder {
    return view.getTag(R.id.com_a21buttons_stickyheaders_holder) as StickyHeaderViewHolder
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

  data class SavedState(val firstVisibleAdapterPosition: Int, val firstVisibleTop: Int) : Parcelable {
    companion object {
      @JvmField val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
        override fun createFromParcel(source: Parcel): SavedState = SavedState(source)
        override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
      }
    }

    constructor(source: Parcel) : this(
        source.readInt(),
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
      dest.writeInt(firstVisibleAdapterPosition)
      dest.writeInt(firstVisibleTop)
    }
  }

  class LayoutParams : RecyclerView.LayoutParams {
    var stickyHeader: Boolean = false

    constructor(lp: ViewGroup.LayoutParams) : super(lp)

    constructor(c: Context, attrs: AttributeSet) : super(c, attrs)

    constructor(width: Int, height: Int) : super(width, height)
  }

  interface HeaderLookup {
    fun getHeaderPosition(sectionId: Long): Int
  }

  object HeaderLookupPlaceholder : HeaderLookup {
    override fun getHeaderPosition(sectionId: Long): Int {
      throw IllegalStateException("Please report this exception with a reproducible example")
    }
  }
}
