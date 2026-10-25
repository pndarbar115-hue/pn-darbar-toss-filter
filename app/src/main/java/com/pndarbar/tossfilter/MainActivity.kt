package com.pndarbar.tossfilter

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)

    val layout = LinearLayout(this)
    layout.orientation = LinearLayout.VERTICAL
    layout.gravity = Gravity.CENTER
    layout.setBackgroundColor(Color.rgb(18, 18, 18))
    layout.setPadding(24, 24, 24, 24)

    val title = TextView(this)
    title.text = "🏏 PN DARBAR"
    title.textSize = 30f
    title.setTextColor(Color.rgb(255, 215, 0))
    title.gravity = Gravity.CENTER

    val subtitle = TextView(this)
    subtitle.text = "Toss Filter"
    subtitle.textSize = 20f
    subtitle.setTextColor(Color.WHITE)
    subtitle.gravity = Gravity.CENTER

    layout.addView(title)
    layout.addView(subtitle)
    setContentView(layout)
}

}
