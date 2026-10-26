package com.pndarbar.tossfilter

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.security.MessageDigest

class NotificationTossListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val pkg = sbn.packageName ?: return

        if (pkg !in listOf(
                "org.telegram.messenger",
                "org.telegram.messenger.web",
                "org.thunderdog.challegram"
            )
        ) return

        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)
            ?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
            ?.toString()
            ?.takeIf { it.isNotBlank() }
            ?: extras.getCharSequence(Notification.EXTRA_TEXT)
                ?.toString().orEmpty()

        val message = "$title $text".trim()
        if (message.isBlank()) return

        val lower = message.lowercase()
        if (listOf(
                "toss", "toss winner", "won the toss",
                "toss prediction", "toss update", "टॉस"
            ).none { lower.contains(it) }
        ) return

        val prefs = getSharedPreferences("pn_toss_data", MODE_PRIVATE)
        val signature = sha256("$pkg|$title|$text")
        val now = System.currentTimeMillis()
        val previous = prefs.getLong("seen_$signature", 0L)

        if (previous > 0 && now - previous < 10 * 60 * 1000L) return

        val team = findTeam(message) ?: "Team Unknown"
        val teamKey = "team_" + sha256(team.lowercase())

        prefs.edit()
            .putLong("seen_$signature", now)
            .putString(teamKey + "_name", team)
            .putInt(teamKey, prefs.getInt(teamKey, 0) + 1)
            .putInt("total", prefs.getInt("total", 0) + 1)
            .putString("last_message", message)
            .putString("last_team", team)
            .apply()
    }

    private fun findTeam(message: String): String? {
        val patterns = listOf(
            Regex("""(?i)\b([A-Za-z][A-Za-z .&'-]{0,30}?)\s+won\s+the\s+toss\b"""),
            Regex("""(?i)\btoss(?:\s+winner)?\s*[:=\-]\s*([A-Za-z][A-Za-z .&'-]{0,30})""")
        )

        for (pattern in patterns) {
            val match = pattern.find(message) ?: continue
            val team = match.groupValues[1].trim().trimEnd('.', ',', ':', '-')
            if (team.isNotBlank()) return team
        }
        return null
    }

    private fun sha256(value: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
