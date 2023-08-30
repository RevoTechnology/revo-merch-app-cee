package merchant.mokka.utils

import android.content.Context
import android.content.Intent
import android.content.res.Configuration

import android.view.LayoutInflater
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import merchant.mokka.R
import merchant.mokka.pref.Prefs
import java.util.Locale

const val LOCALE_RO = "ro"
const val LOCALE_BG = "bg"
const val LOCALE_PL = "pl"

fun Context.checkLocaleUpdates(
    appCompatContext: AppCompatActivity,
    onFinished: () -> Unit
) {
    if (isNeedSetNewLocale()) {
        showLocaleDialog {
            setNewLocale(it)
            onFinished()
            val intent = appCompatContext.intent
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }else {
        setLocale()
        onFinished()
    }
}

fun Context.setLocale(): Context {
    val locale = Prefs.locale.ifBlank {
        val language = getCurrentLocale(this)?.language.orEmpty()
        Prefs.locale = language
        language
    }
    return updateResources(this, locale)
}

fun Context.setNewLocale(language: String): Context {
    Prefs.locale = language
    return updateResources(this, language)
}

private fun Context.isNeedSetNewLocale(): Boolean {
    val prefsLocale = Prefs.locale
    val prefsLocaleEmpty = prefsLocale.isEmpty()
    val locale = if (prefsLocaleEmpty) getCurrentLocale(this)?.language else prefsLocale
    return  locale != LOCALE_RO && locale != LOCALE_BG && locale != LOCALE_PL
}

private fun Context.showLocaleDialog(onSelect:(locale: String) -> Unit) {
    val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_select_locale, null, false)
    val selectButtonPl = dialogView.findViewById<RadioButton>(R.id.selectButtonPl)
    val selectButtonRo = dialogView.findViewById<RadioButton>(R.id.selectButtonRo)
    val selectButtonBg = dialogView.findViewById<RadioButton>(R.id.selectButtonBg)
    AlertDialog.Builder(this)
        .setView(dialogView)
        .setCancelable(false)
        .setPositiveButton(R.string.button_ok) { _, _ ->
            when {
                selectButtonPl.isChecked -> Prefs.locale = LOCALE_PL
                selectButtonRo.isChecked -> Prefs.locale = LOCALE_RO
                selectButtonBg.isChecked -> Prefs.locale = LOCALE_BG
            }
            onSelect(Prefs.locale)
        }.create().apply {
            setCanceledOnTouchOutside(false)
        }.show()

}

private fun updateResources(context: Context, language: String): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val res = context.resources
    val config = Configuration(res.configuration)
    config.setLocale(locale)
    return context.createConfigurationContext(config)
}