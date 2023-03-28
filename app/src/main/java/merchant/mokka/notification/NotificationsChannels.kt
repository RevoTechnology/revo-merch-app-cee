package merchant.mokka.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import merchant.mokka.R

class NotificationsChannels(private val context: Context) {
    private val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    fun create() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(defaultChannel())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun defaultChannel(): NotificationChannel {
        return NotificationChannel(context.getString(R.string.default_notification_channel_id),
                "Revo Notifications",
                NotificationManager.IMPORTANCE_DEFAULT)
                .apply {
                    enableLights(true)
                    lightColor = Color.BLUE
                    enableVibration(true)
                    setSound(defaultSound, null)
                    vibrationPattern = longArrayOf(100L, 200L)
                }
    }

}