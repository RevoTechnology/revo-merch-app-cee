package merchant.mokka.api

import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()
                .header("Accept-Language", Locale.getDefault().language)
                .header("User-Agent", "Revo Partner mobile")
                .build()
        return chain.proceed(request)
    }
}