package com.a21buttons.stickyheaders.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.a21buttons.stickyheaders.StickyHeaderAdapter
import com.a21buttons.stickyheaders.StickyHeaderLayoutManager
import com.a21buttons.stickyheaders.StickyHeaderViewHolder

class ChangeHeader : AppCompatActivity() {
  companion object {
    fun getCallingIntent(context: Context) = Intent(context, ChangeHeader::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.recycler_view)

    val recyclerView: RecyclerView = findViewById(R.id.recyclerView) as RecyclerView

    recyclerView.layoutManager = StickyHeaderLayoutManager()
    recyclerView.adapter = Adapter(layoutInflater)
  }

  class Adapter(val inflater: LayoutInflater) : StickyHeaderAdapter<Adapter.ViewHolder>() {
    var count = 1

    override fun getItemCount() = 100

    override fun onCreateViewHolder2(parent: ViewGroup, viewType: Int): ViewHolder {
      return ViewHolder(inflater.inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, sectionId: Long) {
      holder.bind("Position ${position % count}\tSection $sectionId", this)
    }

    override fun getSectionId(position: Int) = (position / count).toLong()

    override fun getHeaderPosition(sectionId: Long) = (sectionId * count).toInt()

    fun update() {
      count = (count % 10) + 1
      notifyItemRangeChanged(1, 99)
    }

    class ViewHolder(val v: View) : StickyHeaderViewHolder(v) {
      fun bind(s: String, adapter: Adapter) {
        val view: View = v.findViewById(R.id.text1)
        if (view is TextView) {
          view.text = s
        }
        v.setOnClickListener {
          if (adapterPosition != NO_POSITION) {
            adapter.update()
          }
        }
      }
    }
  }
}
