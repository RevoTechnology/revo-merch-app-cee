package merchant.mokka.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.core.text.HtmlCompat
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import merchant.mokka.api.request.DeviceInfoReq
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.DeviceInfoData
import merchant.mokka.pref.Prefs
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.*


fun Int?.orZero() = this ?: 0

fun Double?.orZero() = this ?: 0.0

fun Boolean?.orFalse() = this ?: false

fun Activity.getHomeIcon(@DrawableRes iconRes: Int?, toolbarStyle: ToolbarStyle) =
        if (iconRes != null) {
            val icon = ContextCompat.getDrawable(this, iconRes)
            icon?.setColorFilter(
                    ContextCompat.getColor(this, toolbarStyle.titleColorRes),
                    PorterDuff.Mode.SRC_ATOP
            )
            icon
        } else {
            null
        }

fun androidx.fragment.app.FragmentActivity?.granted(permission: String): Boolean {
    val result = ContextCompat.checkSelfPermission(this as AppCompatActivity, permission)
    return result == PackageManager.PERMISSION_GRANTED
}

fun androidx.fragment.app.FragmentActivity.showToast(text: String) {
    runOnUiThread { Toast.makeText(this, text, Toast.LENGTH_SHORT).show() }
}

fun Boolean?.toAlpha() = if (this == true) 1.0f else 0.5f

@Suppress("unused")
fun Context.updateBaseContextLocale(context: Context?): Context? {
    val codeLocale = Prefs.locale
    val locale = if (codeLocale.isEmpty()) {
        getCurrentLocale(context)
    } else {
        Locale(codeLocale)
    }

    Locale.setDefault(locale)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return updateResourcesLocale(context, locale)
    }

    return updateResourcesLocaleLegacy(context, locale)
}

fun Activity?.showKeyboard() {
    this ?: return
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInputFromWindow(currentFocus?.windowToken,
            InputMethodManager.SHOW_FORCED, 0)
}

fun Activity?.hideSoftKeyboard() {
    this ?: return
    (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(findViewById<View>(android.R.id.content)?.windowToken, 0)
}

@Suppress("DEPRECATION")
fun getCurrentLocale(context: Context?): Locale? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        ConfigurationCompat.getLocales(context?.resources?.configuration!!)[0]
    } else {
        context?.resources?.configuration?.locale
    }
}

@TargetApi(Build.VERSION_CODES.N)
private fun updateResourcesLocale(context: Context?, locale: Locale?): Context? {
    val configuration = context?.resources?.configuration
    configuration?.setLocale(locale)
    return configuration?.let { context.createConfigurationContext(configuration) } ?: context
}

@Suppress("DEPRECATION")
private fun updateResourcesLocaleLegacy(context: Context?, locale: Locale?): Context? {
    val resources = context?.resources
    val configuration = resources?.configuration
    configuration?.locale = locale
    resources?.updateConfiguration(configuration, resources.displayMetrics)
    return context
}

fun isRuLocale() = Prefs.locale == Constants.LOCALE_RU
fun isPlLocale() = Prefs.locale == Constants.LOCALE_PL
fun isRoLocale() = Prefs.locale == Constants.LOCALE_RO
fun isBgLocale() = Prefs.locale == Constants.LOCALE_BG

fun Context?.getDeviceInfo(logStep: String): DeviceInfoData? {
    this ?: return null

    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    val osVersion = Build.VERSION.SDK_INT

    var currentVersion = ""
    try {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        currentVersion = pInfo.versionName + "." + pInfo.versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    return DeviceInfoData(
            uuid = Prefs.uuid,
            info = DeviceInfoReq(
                    event = logStep,
                    store_id = Prefs.currentStoreId,
                    device_model = manufacturer + model,
                    device_os_version = osVersion.toString(),
                    current_app_version = currentVersion,
                    phone_number = Prefs.phone
            )
    )
}

fun View.visible(visible: Boolean, isInvisible: Boolean = false) {
    visibility = when {
        visible -> View.VISIBLE
        isInvisible -> View.INVISIBLE
        else -> View.GONE
    }
}

fun View.isVisible() = visibility == View.VISIBLE

fun Toolbar.setToolbarTextViewsMarquee() {
    (0 until childCount).forEach { (getChildAt(it) as? TextView)?.setMarquee() }
}

fun TextView.setMarquee() {
    ellipsize = TextUtils.TruncateAt.MARQUEE
    isSelected = true
    marqueeRepeatLimit = 1
}

fun <T> catchAll(action: () -> T, onError: ((Throwable) -> T)? = null) = try {
    action()
} catch (t: Throwable) {
    onError?.invoke(t)
}

fun String?.isValidJson(): Boolean {
    this ?: return false
    return try {
        JSONObject(this)
        true
    } catch (e: JSONException) {
        try {
            JSONArray(this)
            true
        } catch (ex1: JSONException) {
            false
        }
    }
}

fun String?.sha1(): String {
    this ?: return ""
    val md = MessageDigest.getInstance("SHA-1")
    val digest = md.digest(toByteArray())
    return digest.fold("", { str, it -> str + "%02x".format(it) })
}

fun View.enable(enabled: Boolean, alpha: Float = .5f) = apply {
    this.alpha = if (enabled) 1f else alpha
    isEnabled = enabled
}

fun Bitmap.scaleToMaxSize(scaleUp: Boolean = false, maxSize: Int = 1024): Bitmap {
    val scale = (maxSize.toFloat() / width).coerceAtLeast(maxSize.toFloat() / height)
    if (scale > 1 && !scaleUp) return this.copy(Bitmap.Config.ARGB_8888, true)

    val matrix = Matrix()
    matrix.postScale(scale, scale)

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.save(file: File) {
    if (file.parentFile?.exists() != true) file.parentFile?.mkdirs()
    if (!file.exists()) file.createNewFile()

    var fileOutputStream: FileOutputStream? = null
    try {
        fileOutputStream = FileOutputStream(file)
        compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        fileOutputStream?.close()
    }
}

fun Context.dpToPx(dpVal: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal.toFloat(), resources.displayMetrics).toInt()
}

fun TextView.setTextWithLinkSupport(
        fullText: String,
        callback: (String) -> Unit
) {
    val linkRegex = "<a[\\s\\S]*?href=\"([^\"]+)\"[\\s\\S]*?>".toRegex()
    val spannable = SpannableString(HtmlCompat.fromHtml(fullText, HtmlCompat.FROM_HTML_MODE_LEGACY).toString())

    "<a.*?>(.*?)<\\/a>".toRegex().findAll(fullText).toList().forEach { match ->
        val text = match.groupValues.getOrNull(1)
        val data = match.groupValues.firstOrNull()

        if (data != null && text != null) {
            linkRegex.find(data)?.groupValues?.getOrNull(1)?.also { link ->
                val start = spannable.indexOf(text)
                val end = start + text.length

                spannable.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        callback(link)
                    }
                }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    this.text = spannable
    movementMethod = LinkMovementMethod.getInstance() // Make link clickable
}

fun TextView.addImage(atText: String, @DrawableRes imgRes: Int, imgWidth: Int, imgHeight: Int) {
    val ssb = SpannableStringBuilder("$atText\u00A0\u00A0")

    val drawable = ContextCompat.getDrawable(this.context, imgRes) ?: return
    drawable.mutate()
    drawable.setBounds(0, 0, imgWidth, imgHeight)
    ssb.setSpan(VerticalImageSpan(drawable), ssb.length-1, ssb.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    this.setText(ssb, TextView.BufferType.SPANNABLE)
}