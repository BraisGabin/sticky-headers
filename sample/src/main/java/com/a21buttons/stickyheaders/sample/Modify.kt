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
import com.a21buttons.stickyheaders.StickyHeaderAdapter
import com.a21buttons.stickyheaders.StickyHeaderLayoutManager
import com.a21buttons.stickyheaders.StickyHeaderViewHolder

class Modify : AppCompatActivity() {
  companion object {
    fun getCallingIntent(context: Context) = Intent(context, Modify::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.recycler_view)

    val recyclerView: RecyclerView = findViewById(R.id.recyclerView) as RecyclerView

    recyclerView.layoutManager = StickyHeaderLayoutManager()
    recyclerView.adapter = Adapter(layoutInflater)
  }

  class Adapter(val inflater: LayoutInflater) : StickyHeaderAdapter<Adapter.ViewHolder>() {
    private var list: MutableList<Int> = MutableList(100, { 0 })

    override fun getItemCount() = list.count()

    override fun onCreateViewHolder2(parent: ViewGroup, viewType: Int): ViewHolder {
      return ViewHolder(inflater.inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, sectionId: Long) {
      holder.bind("Update ${list[position]}\tPosition $position\tSection $sectionId", this)
    }

    override fun getSectionId(position: Int) = (position / 2).toLong()

    override fun getHeaderPosition(sectionId: Long) = (sectionId * 2).toInt()

    fun update() {
      list = list.map { it + 1 }
          .toMutableList()
      notifyItemRangeChanged(0, list.count())
    }

    class ViewHolder(val v: View) : StickyHeaderViewHolder(v) {
      fun bind(s: String, adapter: Adapter) {
        val view: View = v.findViewById(R.id.text1)
        if (view is TextView) {
          view.text = s
        }
        v.setOnClickListener {
          adapter.update()
        }
      }
    }
  }
}
