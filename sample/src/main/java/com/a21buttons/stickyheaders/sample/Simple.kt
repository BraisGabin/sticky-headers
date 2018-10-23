package com.a21buttons.stickyheaders.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.a21buttons.stickyheaders.SimpleStickyHeaderAdapter
import com.a21buttons.stickyheaders.StickyHeaderLayoutManager
import com.a21buttons.stickyheaders.StickyHeaderViewHolder

class Simple : AppCompatActivity() {
  companion object {
    fun getCallingIntent(context: Context) = Intent(context, Simple::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.recycler_view)

    val recyclerView: RecyclerView = findViewById(R.id.recyclerView) as RecyclerView

    recyclerView.layoutManager = StickyHeaderLayoutManager()
    recyclerView.adapter = Adapter(layoutInflater)
  }

  class Adapter(val inflater: LayoutInflater) : SimpleStickyHeaderAdapter<Adapter.ViewHolder>() {
    override fun getSectionCount() = 50

    override fun getSectionItemCount(sectionId: Long): Int {
      return (sectionId.toInt() % 8) + 2
    }

    override fun hasHeader(sectionId: Long) = true

    override fun onCreateViewHolder2(parent: ViewGroup, viewType: Int): ViewHolder {
      return ViewHolder(inflater.inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, sectionId: Long) {
      holder.bind("Position $position\tSection $sectionId")
    }

    class ViewHolder(val v: View) : StickyHeaderViewHolder(v) {
      fun bind(s: String) {
        val view: View = v.findViewById(R.id.text1)
        if (view is TextView) {
          view.text = s
        }
      }
    }
  }
}
