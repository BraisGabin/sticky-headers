package com.a21buttons.stickyheaders.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById<View>(R.id.button1).setOnClickListener({ startActivity(Simple.getCallingIntent(this)) })
    findViewById<View>(R.id.button2).setOnClickListener({ startActivity(Decoration.getCallingIntent(this)) })
    findViewById<View>(R.id.button3).setOnClickListener({ startActivity(Remove.getCallingIntent(this)) })
    findViewById<View>(R.id.button4).setOnClickListener({ startActivity(Modify.getCallingIntent(this)) })
    findViewById<View>(R.id.button5).setOnClickListener({ startActivity(Insert.getCallingIntent(this)) })
    findViewById<View>(R.id.button6).setOnClickListener({ startActivity(ChangeHeader.getCallingIntent(this)) })
    findViewById<View>(R.id.button7).setOnClickListener({ startActivity(DataSetChanged.getCallingIntent(this)) })
  }
}
