package com.a21buttons.stickyheaders.sample.smooth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.a21buttons.stickyheaders.StickyHeaderLayoutManager
import com.a21buttons.stickyheaders.sample.R

class SmoothScrollActivity : AppCompatActivity() {

  companion object {
    fun getCallingIntent(context: Context) = Intent(context, SmoothScrollActivity::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_smooth_scroll)

    val recyclerView = findViewById(R.id.recyclerView) as RecyclerView

    recyclerView.layoutManager = StickyHeaderLayoutManager()

    val items = listOf(
            SmoothScrollItem("Red", android.R.color.holo_red_light),
            SmoothScrollItem("Blue", android.R.color.holo_blue_light),
            SmoothScrollItem("Green", android.R.color.holo_green_light),
            SmoothScrollItem("Orange", android.R.color.holo_orange_light),
            SmoothScrollItem("Purple", android.R.color.holo_purple),
            SmoothScrollItem("White", android.R.color.white),
            SmoothScrollItem("Black", android.R.color.black))

    recyclerView.adapter = SmoothScrollAdapter(layoutInflater, items)

    findViewById(R.id.button).setOnClickListener({recyclerView.smoothScrollToPosition(0)})
  }

}