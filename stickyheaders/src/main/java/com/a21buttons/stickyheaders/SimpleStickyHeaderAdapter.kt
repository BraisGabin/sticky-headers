package com.a21buttons.stickyheaders

import android.support.v7.widget.RecyclerView

abstract class SimpleStickyHeaderAdapter<VH : StickyHeaderViewHolder> : StickyHeaderAdapter<VH>() {
  private var sectionList: List<Int>? = null

  init {
    registerAdapterDataObserver(SimpleAdapterDataObserver(this))
  }

  abstract fun getSectionCount(): Int

  abstract fun getSectionItemCount(sectionId: Long): Int

  abstract fun hasHeader(sectionId: Long): Boolean

  override fun getItemCount(): Int {
    val sectionList = sectionList ?: createSectionList()
    return sectionList[sectionList.size - 1]
  }

  override fun getSectionId(position: Int): Long {
    return toSection(position).sectionId
  }

  override fun getHeaderPosition(sectionId: Long): Int {
    if (hasHeader(sectionId)) {
      val sectionList = sectionList ?: createSectionList()
      return if (sectionId == 0.toLong()) 0 else sectionList[sectionId.toInt() - 1]
    } else {
      return -1
    }
  }

  override fun getItemViewType(position: Int): Int {
    val (sectionId, sectionPosition) = toSection(position)
    return getItemViewType(position, sectionId, sectionPosition)
  }

  open fun getItemViewType(position: Int, sectionId: Long, sectionPosition: Int): Int {
    return super.getItemViewType(position)
  }

  open fun toSection(position: Int): SectionInfo {
    val sectionList = sectionList ?: createSectionList()
    val sectionId = findNearestButGreater(sectionList, position)
    val sectionPosition = if (sectionId == 0) position else position - sectionList[sectionId - 1]
    return SectionInfo(sectionId.toLong(), sectionPosition)
  }

  fun createSectionList(): List<Int> {
    val list = mutableListOf<Int>()
    var count = 0
    for (i in 0..getSectionCount() - 1) {
      count += getSectionItemCount(i.toLong())
      list.add(count)
    }
    sectionList = list
    return list
  }

  data class SectionInfo(val sectionId: Long, val sectionPosition: Int)

  class SimpleAdapterDataObserver(val simpleAdapter: SimpleStickyHeaderAdapter<*>) : RecyclerView.AdapterDataObserver() {

    override fun onChanged() {
      simpleAdapter.sectionList = null
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
      simpleAdapter.sectionList = null
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any) {
      simpleAdapter.sectionList = null
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
      simpleAdapter.sectionList = null
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
      simpleAdapter.sectionList = null
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
      simpleAdapter.sectionList = null
    }
  }
}

internal fun findNearestButGreater(list: List<Int>, value: Int, firstIndex: Int = 0, lastIndex: Int = list.size - 1): Int {
  if (firstIndex == lastIndex) {
    return firstIndex
  } else {
    val i = (lastIndex - firstIndex) / 2 + firstIndex
    if (list[i] > value) {
      return findNearestButGreater(list, value, firstIndex, i)
    } else {
      return findNearestButGreater(list, value, i + 1, lastIndex)
    }
  }
}
