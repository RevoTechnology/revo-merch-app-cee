package pl.revo.merchant.api

import android.content.Context
import com.squareup.moshi.Moshi
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.revo.merchant.api.adapter.MoshiUtils
import pl.revo.merchant.api.apis.ApiUpdate
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class HttpClient(
        private val backgroundScheduler: Scheduler = Schedulers.io(),
        private val resultScheduler: Scheduler = AndroidSchedulers.mainThread()
) {

    private fun <S> createHttpClientInstance(serviceClass: Class<S>,
                                             logLevel: HttpLoggingInterceptor.Level): OkHttpClient {

        val useRevoAuth = serviceClass != ApiUpdate::class.java
        val usePathConstructor = serviceClass != ApiUpdate::class.java

        val loggerInterceptor = HttpLoggingInterceptor {
            Timber.tag(serviceClass.simpleName).i(it)
        }.setLevel(logLevel)

        val httpClient = OkHttpClient.Builder()
                .addInterceptor(RevoInterceptor(useRevoAuth = useRevoAuth, usePathConstructor = usePathConstructor))
                .addInterceptor(loggerInterceptor)
                .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
                .connectTimeout(HttpConfig.MAX_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(HttpConfig.MAX_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(HttpConfig.MAX_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)

        return httpClient.build()
    }

    private fun <S> createRetrofitInstance(serviceClass: Class<S>, logLevel: HttpLoggingInterceptor.Level): Retrofit {
        val httpClient = createHttpClientInstance(serviceClass = serviceClass, logLevel = logLevel)

        return Retrofit.Builder()
                .baseUrl(RevoInterceptor.baseUrl())
                .addConverterFactory(createConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build()
    }

    private fun createConverterFactory(): Converter.Factory {
        return MoshiConverterFactory.create(MoshiUtils.moshi().build())
    }

    fun <S> createService(serviceClass: Class<S>, logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY): S {
        val retrofit = createRetrofitInstance(serviceClass = serviceClass, logLevel = logLevel)
        return retrofit.create(serviceClass)
    }

    fun <T> compose(single: Single<out T>, context: Context): Single<out T> {
        return single
                .subscribeOn(backgroundScheduler)
                .observeOn(resultScheduler)
                .mapToNetworkException(context)
    }
}