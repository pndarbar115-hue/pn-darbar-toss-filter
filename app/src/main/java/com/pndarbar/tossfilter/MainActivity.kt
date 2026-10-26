package com.pndarbar.tossfilter

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var root: LinearLayout
    private lateinit var totalText: TextView
    private lateinit var teamList: LinearLayout

    private val gold = Color.rgb(255, 215, 0)
    private val bg = Color.rgb(18, 18, 18)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this)
        root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(22, 28, 22, 28)
        root.setBackgroundColor(bg)
        scroll.addView(root)

        val title = TextView(this)
        title.text = "🏏 PN DARBAR"
        title.textSize = 30f
        title.setTextColor(gold)
        title.gravity = Gravity.CENTER
        root.addView(title)

        val subtitle = TextView(this)
        subtitle.text = "GOLDEN TOSS FILTER"
        subtitle.textSize = 18f
        subtitle.setTextColor(Color.WHITE)
        subtitle.gravity = Gravity.CENTER
        subtitle.setPadding(0, 8, 0, 25)
        root.addView(subtitle)

        totalText = TextView(this)
        totalText.textSize = 23f
        totalText.setTextColor(gold)
        totalText.gravity = Gravity.CENTER
        totalText.setPadding(0, 15, 0, 20)
        root.addView(totalText)

        addButton("🔄 Refresh Toss Count") {
            refreshCounts()
        }

        addButton("🔔 Notification Access खोलें") {
            try {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            } catch (_: Exception) {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }
        }

        val heading = TextView(this)
        heading.text = "\n📊 TEAM-WISE TOSS COUNT"
        heading.textSize = 19f
        heading.setTextColor(gold)
        heading.setPadding(0, 20, 0, 12)
        root.addView(heading)

        teamList = LinearLayout(this)
        teamList.orientation = LinearLayout.VERTICAL
        root.addView(teamList)

        val note = TextView(this)
        note.text = "\nनोट: गिनती केवल उस समय से होगी जब Notification Access चालू होगा।"
        note.textSize = 13f
        note.setTextColor(Color.LTGRAY)
        note.setPadding(0, 20, 0, 10)
        root.addView(note)

        setContentView(scroll)
        refreshCounts()
    }

    private fun addButton(label: String, action: () -> Unit) {
        val button = Button(this)
        button.text = label
        button.setTextColor(Color.BLACK)
        button.setBackgroundColor(gold)
        button.textSize = 16f

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 8, 0, 8)
        root.addView(button, params)
        button.setOnClickListener { action() }
    }

    private fun refreshCounts() {
        val prefs = getSharedPreferences("pn_toss_data", MODE_PRIVATE)
        totalText.text = "कुल Toss Messages: ${prefs.getInt("total", 0)}"

        teamList.removeAllViews()

        val teamKeys = prefs.all.keys
            .filter { it.startsWith("team_") && it.endsWith("_name") }
            .sorted()

        if (teamKeys.isEmpty()) {
            val empty = TextView(this)
            empty.text = "अभी कोई Toss Message नहीं मिला।"
            empty.textSize = 16f
            empty.setTextColor(Color.WHITE)
            empty.setPadding(0, 8, 0, 8)
            teamList.addView(empty)
            return
        }

        for (nameKey in teamKeys) {
            val team = prefs.getString(nameKey, "Unknown") ?: "Unknown"
            val countKey = nameKey.removeSuffix("_name")
            val count = prefs.getInt(countKey, 0)

            val row = TextView(this)
            row.text = "🏏 $team  —  $count"
            row.textSize = 18f
            row.setTextColor(Color.WHITE)
            row.setPadding(12, 14, 12, 14)
            row.setBackgroundColor(Color.rgb(35, 31, 20))

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 5, 0, 5)
            teamList.addView(row, params)
        }
    }

    override fun onResume() {
        super.onResume()
        if (::totalText.isInitialized) refreshCounts()
    }
}
