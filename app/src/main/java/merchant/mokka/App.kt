package merchant.mokka

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.exponea.sdk.Exponea
import com.exponea.sdk.models.ExponeaConfiguration
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.lazy
import com.google.android.gms.security.ProviderInstaller
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import merchant.mokka.di.dependencies
import merchant.mokka.notification.NotificationsChannels
import merchant.mokka.pref.Prefs
import merchant.mokka.utils.CrashReportingTree
import timber.log.Timber

class App : Application(), KodeinAware {

    override val kodein by Kodein.lazy {
        import(dependencies())
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Prefs.init(this)

        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        else Timber.plant(CrashReportingTree())

        runCatching {
            ProviderInstaller.installIfNeeded(this)
        }

        NotificationsChannels(this).create()

        Exponea.init(
            this,
            ExponeaConfiguration().apply {
                authorization = getString(R.string.exponea_auth)
                projectToken = getString(R.string.exponea_project_token)
                baseURL = getString(R.string.exponea_base_url)
                pushChannelId = getString(R.string.default_notification_channel_id)
                httpLoggingLevel = ExponeaConfiguration.HttpLoggingLevel.BODY
                tokenTrackFrequency = ExponeaConfiguration.TokenFrequency.EVERY_LAUNCH
            }
        )
        if (Prefs.pushToken.isNotEmpty()) Exponea.trackPushToken(Prefs.pushToken)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        lateinit var instance: App
    }
}