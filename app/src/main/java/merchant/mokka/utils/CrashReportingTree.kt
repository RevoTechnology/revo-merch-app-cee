package merchant.mokka.utils

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashReportingTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) return

        val exception = throwable?.let { Exception(it) } ?: Exception(message)
        val errorMessage = throwable?.message ?: message

        FirebaseCrashlytics.getInstance().apply {
            if (priority == Log.ERROR) recordException(exception)
            else if (priority == Log.WARN && errorMessage.isNotEmpty()) log(errorMessage)
        }
    }
}