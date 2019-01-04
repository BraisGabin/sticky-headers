package com.a21buttons.stickyheaders

import androidx.recyclerview.widget.RecyclerView
import android.view.View

open class StickyHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
  var sectionId: Long = -1
}
