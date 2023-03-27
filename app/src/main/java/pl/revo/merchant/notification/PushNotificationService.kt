package pl.revo.merchant.notification

import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.exponea.sdk.Exponea
import com.exponea.sdk.models.PropertiesList
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.protocol.Message
import pl.revo.merchant.BuildConfig
import pl.revo.merchant.R
import pl.revo.merchant.pref.Prefs
import pl.revo.merchant.ui.root.RootActivity
import java.net.URL

class PushNotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        sentryCapture(remoteMessage)

        val notify = remoteMessage.notification
        val title = notify?.title ?: remoteMessage.data["title"] ?: getString(R.string.app_name)
        val body = notify?.body ?: remoteMessage.data["message"]
        val url = remoteMessage.data["url"]
        val imageUrl = notify?.imageUrl?.toString() ?: remoteMessage.data["image"]

        sendNotification(
            title = title,
            body = body.orEmpty(),
            url = url,
            imageUrl = imageUrl)
    }

    private fun sendNotification(title: String, body: String, url: String? = null, imageUrl: String? = null) {
        val intent = if (url != null) Intent(Intent.ACTION_VIEW, Uri.parse(url))
        else Intent(this, RootActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
                this,
                101,
                intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setContentIntent(pendingIntent)

        imageUrl?.let {
            Single.just(imageUrl)
                    .map {
                        val input = URL(imageUrl).openStream()
                        BitmapFactory.decodeStream(input)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onSuccess = { bitmap ->
                                bitmap?.let {
                                    builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(body))
                                }

                                notify(builder)
                            },
                            onError = {
                                notify(builder)
                            }
                    )
        } ?:  notify(builder)
    }

    private fun notify(builder: NotificationCompat.Builder) = NotificationManagerCompat.from(this).notify(101, builder.build())
    override fun onNewToken(token: String) {
        Prefs.pushToken = token
        if (BuildConfig.FLAVOR == "stage")
            FirebaseMessaging.getInstance().subscribeToTopic("test_topic783").addOnSuccessListener { Log.i("PushNotificationService", "successfully subscribed") }

        Exponea.trackPushToken(token)
    }

    private fun sentryCapture(remoteMessage: RemoteMessage) {
        if (BuildConfig.IS_PROD) return

        val notification = remoteMessage.notification
        val data = hashMapOf<String, Any>(
                "hasData" to "${remoteMessage.data.isNotEmpty()}",
                "notification_title" to (notification?.title ?: "null"),
            "notification_body" to (notification?.body ?: "null"),
            "notification_clickAction" to (notification?.clickAction ?: "null"),
            "notification_link" to (notification?.link ?: "null"),
            "notification_tag" to (notification?.tag ?: "null"),
            "notification_ticker" to (notification?.ticker ?: "null")
        )

        remoteMessage.data.keys.forEach { data["data_${it}"] = remoteMessage.data[it].orEmpty() }

        // Sentry
        val sentryEvent = SentryEvent().apply {
            message = Message().apply {
                message =
                    "PushNotificationService.onMessageReceived data=${remoteMessage.notification != null}"
            }
            tags = mapOf("push_notification" to "onMessageReceived")
        }

        data.entries.forEach { sentryEvent.setExtra(it.key, it.value) }
        Sentry.captureEvent(sentryEvent)


        // Exponea
        Exponea.trackEvent(
            eventType = "push_notification_on_message_received",
            properties = PropertiesList(data)
        )


        Log.i(
            "PushNotificationService", "onMessageReceived message=${sentryEvent.message}\n" +
                    "Sentry.extras=${data.entries}"
        )
    }
}