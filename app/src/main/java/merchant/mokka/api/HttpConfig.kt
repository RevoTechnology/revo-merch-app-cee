package merchant.mokka.api

import android.net.Uri
import merchant.mokka.BuildConfig
import merchant.mokka.utils.isBgLocale
import merchant.mokka.utils.isPlLocale
import merchant.mokka.utils.isRoLocale
import merchant.mokka.utils.isRuLocale

object HttpConfig {

    const val MAX_CONNECTION_TIMEOUT: Long = 60000
    const val MAX_READ_TIMEOUT: Long = 60000
    const val MAX_WRITE_TIMEOUT: Long = 60000

    const val ADDRESS_URL = "http://kodpocztowy.intami.pl/api/"
    const val APK_DESTINATION = "revo.apk"

    private val UPDATE_ENDPOINT: String
        get() = updateUrl()

    val CHECK_UPDATE_ENDPOINT: String
        get() = checkUpdateUrl()

    val DOCUMENT_URL: String
        get() = documentUrl()

    val POLICY_URL: String
        get() = policyUrl()

    val UPDATE_APK_TOKEN = if (BuildConfig.IS_PROD) {
        // merchant-app-auth:LcQ#!VKQRXBAO5@6BkQL -> toBase64
        "Basic bWVyY2hhbnQtYXBwLWF1dGg6TGNRIyFWS1FSWEJBTzVANkJrUUw="
    } else {
        // merchant-app-stage-auth:I4kV/v&8&yW08Z\J44cB -> toBase64
        "Basic bWVyY2hhbnQtYXBwLXN0YWdlLWF1dGg6STRrVi92JjgmeVcwOFpcSjQ0Y0I="
    }

    val VERSION_FILE_URL = "$UPDATE_ENDPOINT/${if(BuildConfig.SCREEN_SHOOT_DISABLED) "version-paytel.txt" else "version.txt"}"
    val UPDATE_APK_URL = "$UPDATE_ENDPOINT/revo.apk"

    private fun customUriBuilder(): Uri.Builder {
        val uriBuilder = Uri.parse(RevoInterceptor.baseUrl()).buildUpon()
        RevoInterceptor.apiEnvironment()?.let { uriBuilder.appendEncodedPath(it) }
        RevoInterceptor.countryCode()?.let { uriBuilder.appendEncodedPath(it) }
        return uriBuilder
    }

    private fun modifyCustomUriBuilder(uriBuilder: Uri.Builder,
                                       serviceName: Boolean = false) {

        if (serviceName)
            RevoInterceptor.serviceName()?.let { uriBuilder.appendEncodedPath(it) }

        when {
            isRuLocale() -> "api/loans/v1"
            isPlLocale() -> "api/loans/v1"
            isRoLocale() -> "api/loans/v1"
            isBgLocale() -> "api/loans/v1"
            else -> null
        }?.let { uriBuilder.appendEncodedPath(it) }

    }

    private fun checkUpdateUrl(): String {
        // https://backend.best.revoup.ru/staging/ru/updater/api/v1/devices/

        val uriBuilder = customUriBuilder()
        uriBuilder.appendEncodedPath("updater/api/v1/devices/%s")

        return uriBuilder.build().toString()
    }

    private fun documentUrl(): String {
        val uriBuilder = customUriBuilder()
        modifyCustomUriBuilder(uriBuilder = uriBuilder, serviceName = true)
        uriBuilder.appendEncodedPath("loan_requests/%s/documents/%s.html")
        return uriBuilder.build().toString()
    }

    private fun policyUrl(): String {
        val uriBuilder = customUriBuilder()
        modifyCustomUriBuilder(uriBuilder = uriBuilder, serviceName = true)

        uriBuilder.appendEncodedPath("loan_requests/%s/documents/%s.html")
        return uriBuilder.toString()
    }

    private fun updateUrl() = if (BuildConfig.IS_PROD) "https://merchant-app-updater.revoplus.pl"
    else "https://merchant-app-updater-stage.revoplus.pl"
}