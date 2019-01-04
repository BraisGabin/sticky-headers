package com.a21buttons.stickyheaders.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.a21buttons.stickyheaders.StickyHeaderAdapter
import com.a21buttons.stickyheaders.StickyHeaderLayoutManager
import com.a21buttons.stickyheaders.StickyHeaderViewHolder

class Remove : AppCompatActivity() {
  companion object {
    fun getCallingIntent(context: Context) = Intent(context, Remove::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.recycler_view)

    val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

    recyclerView.layoutManager = StickyHeaderLayoutManager()
    recyclerView.adapter = Adapter(layoutInflater)
  }

  class Adapter(val inflater: LayoutInflater) : StickyHeaderAdapter<Adapter.ViewHolder>() {
    private val list: MutableList<Int> = MutableList(100, { it })

    override fun getItemCount() = list.count()

    override fun onCreateViewHolder2(parent: ViewGroup, viewType: Int): ViewHolder {
      return ViewHolder(inflater.inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, sectionId: Long) {
      holder.bind("Position ${list[position]}\tSection $sectionId", this)
    }

    override fun getSectionId(position: Int) = (list[position] / 2).toLong()

    override fun getHeaderPosition(sectionId: Long) = list.indexOf((sectionId * 2).toInt())

    class ViewHolder(val v: View) : StickyHeaderViewHolder(v) {
      fun bind(s: String, adapter: Adapter) {
        val view: View = v.findViewById(R.id.text1)
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
