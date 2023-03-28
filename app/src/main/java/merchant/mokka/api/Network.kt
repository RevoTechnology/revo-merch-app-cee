@file:Suppress("UNCHECKED_CAST")

package merchant.mokka.api

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import io.reactivex.Single
import io.sentry.Sentry
import okhttp3.ResponseBody
import merchant.mokka.api.adapter.MoshiUtils
import merchant.mokka.api.error.ApiErr
import merchant.mokka.api.error.ApiNotImplementErr
import merchant.mokka.api.error.ErrorParser
import merchant.mokka.api.error.LargeDataError
import merchant.mokka.api.error.NetworkAvailableErr
import merchant.mokka.api.error.ServerException
import merchant.mokka.api.error.UnAuthorizedErr
import merchant.mokka.api.helpers.BarcodeResponse
import merchant.mokka.api.helpers.RefundBarcodeResponse
import merchant.mokka.api.response.ClientRes
import merchant.mokka.api.response.FinalizationResponse
import merchant.mokka.api.response.LoanApplication
import merchant.mokka.api.response.ReturnRes
import merchant.mokka.model.BarcodeDto
import merchant.mokka.utils.isValidJson
import retrofit2.Response

/**
 * @param error is passed if the error message is using in the other places such as throwables etc.
 */
private fun <T> Response<T>.logError(error: String? = errorBody()?.string()): String? {
    val exception = Exception("--> ${raw().request.url}\n<-- $error")
    Log.e("network_error", exception.message.orEmpty())
    Sentry.captureException(exception)
    return error
}

fun Context.isNetworkAvailable(): Boolean? {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = cm.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnectedOrConnecting
}

fun <T> Single<T>.mapToNetworkException(context: Context?): Single<T> {
    val networkAvailable = context?.isNetworkAvailable() ?: false
    return if (networkAvailable) this
    else Single.error(NetworkAvailableErr())
}

fun <T> Single<Response<T>>.transform(): Single<T> {
    return this.flatMap { response ->
        when (response.code()) {
            200, 201 -> Single.just(response.body())
            401 -> {
                response.logError()
                Single.error(UnAuthorizedErr())
            }
            404 -> {
                val errorResponse = response.logError()
                val throwable = if (!errorResponse.isValidJson()) ApiNotImplementErr()
                else Throwable(errorResponse)

                Single.error(throwable)
            }
            413 -> {
                response.logError()
                Single.error(LargeDataError())
            }
            452 -> {
                val errorBody = response.errorBody()
                val error = errorBody?.string()
                response.logError(error)

                val result: T? = try {
                    error.toClientData() as T
                } catch (e: Exception) {
                    null
                }

                result?.let { Single.just(it) } ?: Single.error<T>(Throwable(error))
            }
            422 -> {
                val errorBody = response.errorBody()
                val error = errorBody?.string()
                response.logError(error)

                try {
                    val errorArray = ErrorParser(error).errors()
                    Single.error<T>(ApiErr(errorArray))
                } catch (e: Exception) {
                    Single.error<T>(Throwable(error))
                }
            }
            500 -> {
                response.logError()
                Single.error(ServerException())
            }
            else -> {
                val err = response.errorBody()?.string()
                response.logError(err)
                Single.error(Throwable(err))
            }
        }
    }
}

fun Single<Response<ResponseBody>>.transformToBarcode(): Single<FinalizationResponse> {
    return this.flatMap { response ->
        when (response.code()) {
            200 -> {
                val body = response.body()?.string()
                val result = BarcodeResponse(body).stringResponse()
                val finalizationResponse = FinalizationResponse(
                    loanApplication = LoanApplication(
                        barcodes = listOf(
                            BarcodeDto(number = result?.loanApplication?.barcode)
                        )
                    )
                )
                Single.just(finalizationResponse)
            }
            404 -> {
                val errorResponse = response.logError()
                val throwable = if (!errorResponse.isValidJson()) ApiNotImplementErr()
                else Throwable(errorResponse)

                Single.error(throwable)
            }
            422 -> {
                val errorBody = response.errorBody()
                val error = errorBody?.string()
                response.logError(error)
                try {
                    val errorArray = ErrorParser(error).errors()
                    Single.error(ApiErr(errorArray))
                } catch (e: Exception) {
                    Single.error(Throwable(error))
                }
            }
            500 -> {
                response.logError()
                Single.error(ServerException())
            }
            else -> {
                val err = response.errorBody().toString()
                response.logError(err)
                Single.error(Throwable(err))
            }
        }
    }
}

fun Single<Response<ResponseBody>>.transformToReturnBarcode(): Single<ReturnRes> {
    return this.flatMap { response ->

        when (response.code()) {
            200 -> {
                val body = response.body()?.string()
                val result = RefundBarcodeResponse(body).result()

                Single.just(result)
            }
            404 -> {
                val errorResponse = response.logError()
                val throwable = if (!errorResponse.isValidJson()) ApiNotImplementErr()
                else Throwable(errorResponse)

                Single.error(throwable)
            }
            422 -> {
                val errorBody = response.errorBody()
                val error = errorBody?.string()
                response.logError(error)
                try {
                    val errorArray = ErrorParser(error).errors()
                    Single.error(ApiErr(errorArray))
                } catch (e: Exception) {
                    Single.error(Throwable(error))
                }
            }
            500 -> {
                response.logError()
                Single.error(ServerException())
            }
            else -> {
                val err = response.errorBody()?.string()
                response.logError(err)
                Single.error(Throwable(err))
            }
        }
    }
}

fun String?.toClientData(): ClientRes? {
    val moshi = MoshiUtils.moshi().build()
    val jsonAdapter = moshi.adapter(ClientRes::class.java)
    return jsonAdapter.fromJson(this.orEmpty())
}
