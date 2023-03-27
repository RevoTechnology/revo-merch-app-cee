package pl.revo.merchant.utils

import android.os.Bundle
import pl.revo.merchant.repository.LamodaRepository
import java.util.*

class SessionUtils {

    private var lastActivity: Long = 0
    private var logoutTimer: Timer? = null
    private var activity: ManagedActivity? = null
    private val lamodaRepository = LamodaRepository()

    fun setActivity(activity: ManagedActivity) {
        this.activity = activity
    }

    private fun tryLogout() {
        if (System.currentTimeMillis() - lastActivity > USER_INTERACTION_TIMEOUT && !lamodaRepository.tokenValid)
            activity?.logout()
    }

    fun onRestart() {
        tryLogout()
    }

    fun onResume() {
        logoutTimer = Timer()
        val logoutTask = object : TimerTask() {
            override fun run() {
                tryLogout()
            }
        }

        logoutTimer!!.schedule(logoutTask, INITIAL_DELAY_MILLIS, PERIOD_MILLIS)
    }

    fun onPause() {
        logoutTimer!!.cancel()
    }

    fun onUserInteraction() {
        lastActivity = System.currentTimeMillis()
    }

    fun onCreate(savedInstanceState: Bundle?, time: Long) {
        lastActivity = savedInstanceState?.getLong(EXTRA_LAST_ACTIVITY, 0) ?: time
    }

    fun onSaveInstanceState(outState: Bundle?) {
        outState?.putLong(EXTRA_LAST_ACTIVITY, lastActivity)
    }

    interface ManagedActivity {
        fun logout()
    }

    companion object {
        private const val EXTRA_LAST_ACTIVITY = "EXTRA_LAST_ACTIVITY"
        private const val PERIOD_MILLIS: Long = 10000                         // интервал проверки
        private const val INITIAL_DELAY_MILLIS: Long = 5000                   // начальная задержка таймера
        private const val USER_INTERACTION_TIMEOUT: Long = 15 * 60 * 1000     // интервал блокировки
    }
}