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

class Remove : AppCompatActivity() {
  companion object {
    fun getCallingIntent(context: Context) = Intent(context, Remove::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.recycler_view)

    val recyclerView: RecyclerView = findViewById(R.id.recyclerView) as RecyclerView

    recyclerView.layoutManager = StickyHeaderLayoutManager()
    recyclerView.adapter = Adapter(layoutInflater)
  }

  class Adapter(val inflater: LayoutInflater) : StickyHeaderAdapter<Adapter.ViewHolder>() {
    private val list: MutableList<Int> = MutableList(100, { it })

    override fun getItemCount() = list.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      return ViewHolder(inflater.inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, sectionId: Long) {
      holder.bind("Position ${list[position]}\tSection $sectionId", this)
    }

    override fun getSectionId(position: Int) = (list[position] / 2).toLong()

    override fun getHeaderPosition(sectionId: Long) = list.indexOf((sectionId * 2).toInt())

    class ViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
      fun bind(s: String, adapter: Adapter) {
        val view = v.findViewById(R.id.text1)
        if (view is TextView) {
          view.text = s
        }
        v.setOnClickListener {
          if (adapterPosition != NO_POSITION) {
            adapter.list.removeAt(adapterPosition)
            adapter.notifyItemRemoved(adapterPosition)
          }
        }
      }
    }
  }
}
