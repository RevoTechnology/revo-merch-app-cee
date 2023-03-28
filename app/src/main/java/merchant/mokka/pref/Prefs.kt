package merchant.mokka.pref

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.util.*

object Prefs{

    private lateinit var sp: SharedPreferences

    fun init(context: Context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context)
        if (uuid.isEmpty())
            sp.edit().putString(UUID_KEY, UUID.randomUUID().toString()).apply()
    }

    private const val TOKEN_KEY = "token"
    private const val PHONE_KEY = "phone"
    private const val CURRENT_STORE_IDX_KEY = "currentStoreIdx"
    private const val CURRENT_STORE_ID_KEY = "currentStoreId"
    private const val TARIFF_MIN_KEY = "tariffMin"
    private const val TARIFF_MAX_KEY = "tariffMax"
    private const val LOCALE_KEY = "locale"
    private const val PUSH_TOKEN_KEY = "pushToken"
    private const val UUID_KEY = "uuid"
    private const val LIVETEX_TOKEN = "LIVETEX_TOKEN"

    var token: String
        get() = sp.getString(TOKEN_KEY, "").orEmpty()
        set(value) { sp.edit().putString(TOKEN_KEY, value).apply() }

    var phone: String
        get() = sp.getString(PHONE_KEY, "").orEmpty()
        set(value) { sp.edit().putString(PHONE_KEY, value).apply() }

    var currentStoreIdx: Int
        get() = sp.getInt(CURRENT_STORE_IDX_KEY, 0)
        set(value) { sp.edit().putInt(CURRENT_STORE_IDX_KEY, value).apply() }

    var currentStoreId: Int
        get() = sp.getInt(CURRENT_STORE_ID_KEY, 0)
        set(value) { sp.edit().putInt(CURRENT_STORE_ID_KEY, value).apply() }

    var tariffMin: Double
        get() = sp.getInt(TARIFF_MIN_KEY, 0).toDouble() / 100.0
        set(value) { sp.edit().putInt(TARIFF_MIN_KEY, (value * 100).toInt()) .apply() }

    var tariffMax: Double
        get() = sp.getInt(TARIFF_MAX_KEY, 0).toDouble() / 100.0
        set(value) { sp.edit().putInt(TARIFF_MAX_KEY, (value * 100).toInt()).apply() }

    var locale: String
        get() = sp.getString(LOCALE_KEY, "") ?: ""
        set(value) { sp.edit().putString(LOCALE_KEY, value).apply() }

    var pushToken: String
        get() = sp.getString(PUSH_TOKEN_KEY, "") ?: ""
        set(value) { sp.edit().putString(PUSH_TOKEN_KEY, value).apply() }

    val uuid: String
        get() = sp.getString(UUID_KEY, "") ?: ""

    var livetexToken: String?
        get()  = sp.getString(LIVETEX_TOKEN, null)
        set(value) { sp.edit().putString(LIVETEX_TOKEN, value).apply() }

}