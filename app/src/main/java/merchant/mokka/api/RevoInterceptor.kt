package merchant.mokka.api

import android.net.Uri
import okhttp3.Interceptor
import okhttp3.Response
import merchant.mokka.BuildConfig
import merchant.mokka.pref.Prefs
import merchant.mokka.utils.isBgLocale
import merchant.mokka.utils.isPlLocale
import merchant.mokka.utils.isRoLocale
import merchant.mokka.utils.isRuLocale

class RevoInterceptor(val useRevoAuth: Boolean, val usePathConstructor: Boolean) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()

        val url = with(originalRequest.url) {
            val uriBuilder = Uri.Builder()
                    .scheme(scheme)
                    .authority(host)

            if (usePathConstructor) {
                apiEnvironment()?.let { uriBuilder.appendEncodedPath(it) }
                countryCode()?.let { uriBuilder.appendEncodedPath(it) }
                serviceName()?.let { uriBuilder.appendEncodedPath(it) }
            }

            // url path
            pathSegments.forEach { uriBuilder.appendEncodedPath(it) }

            // queries
            for (i in 0 until querySize) {
                uriBuilder.appendQueryParameter(queryParameterName(i), queryParameterValue(i))
            }

            uriBuilder.build().toString()
        }

        if (Prefs.token.isNotEmpty() && useRevoAuth) builder.addHeader("Authorization", Prefs.token)

        val request = builder.url(url).build()
        return chain.proceed(request)
    }


    companion object {
        fun baseUrl() = when {
            isPlLocale() -> BuildConfig.BASE_PL_URL
            isRuLocale() -> BuildConfig.BASE_URL
            isRoLocale() -> BuildConfig.BASE_RO_URL
            isBgLocale() -> BuildConfig.BASE_BG_URL

            else -> BuildConfig.BASE_URL
        }

        fun apiEnvironment() = when {
            isPlLocale() -> null
            isRoLocale() -> null
            isBgLocale() -> null
            else -> BuildConfig.ENV
        }

        fun countryCode() = when {
            isPlLocale() -> null
            isRoLocale() -> null
            isBgLocale() -> null
            else -> Prefs.locale
        }

        fun serviceName() = when {
            isPlLocale() -> null
            isRoLocale() -> null
            isBgLocale() -> null
            else -> "appmodule"
        }
    }

}