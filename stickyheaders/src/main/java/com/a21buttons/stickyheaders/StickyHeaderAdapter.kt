package com.a21buttons.stickyheaders

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

abstract class StickyHeaderAdapter<VH : StickyHeaderViewHolder> : RecyclerView.Adapter<VH>(), StickyHeaderLayoutManager.HeaderLookup {

  final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val holder = onCreateViewHolder2(parent, viewType)
    holder.itemView.setTag(R.id.com_a21buttons_stickyheaders_holder, holder)
    return holder
  }

  abstract fun onCreateViewHolder2(parent: ViewGroup, viewType: Int): VH

  final override fun onBindViewHolder(holder: VH, position: Int) {
    val sectionId = getSectionId(position)
    onBindViewHolder(holder, position, sectionId)
    holder.sectionId = sectionId
  }

  abstract fun onBindViewHolder(holder: VH, position: Int, sectionId: Long)

  abstract fun getSectionId(position: Int): Long
}
