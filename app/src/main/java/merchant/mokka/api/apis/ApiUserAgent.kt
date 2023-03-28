package merchant.mokka.api.apis

import io.reactivex.Single
import okhttp3.ResponseBody
import merchant.mokka.api.request.UserReq
import merchant.mokka.api.response.TokenRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiUserAgent {
    @POST("api/loans/v1/sessions")
    fun userAgentSession(
            @Body user: UserReq
    ): Single<Response<TokenRes>>

    @DELETE("api/loans/v1/sessions")
    fun userAgentDelete(): Single<Response<ResponseBody>>

    @POST("api/loans/v1/passwords")
    fun userAgentPassword(@Body user: UserReq): Single<Response<ResponseBody>>

    @PUT("api/loans/v1/passwords")
    fun userAgentNewPassword(@Body user: UserReq): Single<Response<TokenRes>>
}