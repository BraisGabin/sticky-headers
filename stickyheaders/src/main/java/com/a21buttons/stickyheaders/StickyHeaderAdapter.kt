package com.a21buttons.stickyheaders

import android.support.v7.widget.RecyclerView

abstract class StickyHeaderAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>(), StickyHeaderLayoutManager.HeaderLookup {

  final override fun onBindViewHolder(holder: VH, position: Int) {
    val sectionId = getSectionId(position)
    onBindViewHolder(holder, position, sectionId)
    holder.itemView.setTag(R.id.com_a21buttons_stickyheaders_tag, StickyHeaderData(sectionId))
  }

  abstract fun onBindViewHolder(holder: VH, position: Int, sectionId: Long)

  abstract fun getSectionId(position: Int): Long
}
