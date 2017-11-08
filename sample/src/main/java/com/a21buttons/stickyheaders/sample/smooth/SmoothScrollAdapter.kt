package com.a21buttons.stickyheaders.sample.smooth

import android.support.annotation.ColorRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.a21buttons.stickyheaders.StickyHeaderAdapter
import com.a21buttons.stickyheaders.StickyHeaderViewHolder
import com.a21buttons.stickyheaders.sample.R

class SmoothScrollAdapter(private val inflater: LayoutInflater,
                          private val items: List<SmoothScrollItem>) : StickyHeaderAdapter<StickyHeaderViewHolder>() {


  override fun onCreateViewHolder2(parent: ViewGroup, viewType: Int): StickyHeaderViewHolder {
    return when (viewType) {
      0 -> HeaderViewHolder(inflater.inflate(R.layout.smooth_header, parent, false))
      1 -> ItemViewHolder(inflater.inflate(R.layout.smooth_item, parent, false))
      else -> throw IllegalArgumentException("wtf?")
    }
  }

  override fun onBindViewHolder(holder: StickyHeaderViewHolder, position: Int, sectionId: Long) {
    if (holder is HeaderViewHolder) {
      holder.bind(items[sectionId.toInt()].name)
    } else if (holder is ItemViewHolder) {
      holder.bind(items[sectionId.toInt()].colorRes)
    }
  }

  override fun getSectionId(position: Int): Long {
    return (position / 2).toLong()
  }

  override fun getItemCount(): Int {
    return items.size * 2
  }

  override fun getHeaderPosition(sectionId: Long): Int {
    return if (sectionId >= items.size * 2) {
      -1
    } else {
      sectionId.toInt() * 2
    }
  }

  override fun getItemViewType(position: Int): Int {
    return if (position >= items.size * 2) {
      2
    } else {
      position % 2
    }
  }

  class HeaderViewHolder(view: View) : StickyHeaderViewHolder(view) {

    private val textView = view.findViewById(R.id.title) as TextView

    fun bind(text: String) {
      textView.text = text
    }
  }

  class ItemViewHolder(view: View) : StickyHeaderViewHolder(view) {

    private val imageView = view as ImageView

    fun bind(@ColorRes colorRes: Int) {
      imageView.setImageResource(colorRes)
    }

  }
}