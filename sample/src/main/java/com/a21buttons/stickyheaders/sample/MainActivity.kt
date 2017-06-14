package com.a21buttons.stickyheaders.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById(R.id.button1).setOnClickListener({ startActivity(Simple.getCallingIntent(this)) })
    findViewById(R.id.button2).setOnClickListener({ startActivity(Decoration.getCallingIntent(this)) })
    findViewById(R.id.button3).setOnClickListener({ startActivity(Remove.getCallingIntent(this)) })
  }
}
