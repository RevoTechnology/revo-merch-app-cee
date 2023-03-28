package merchant.mokka.api.apis

import io.reactivex.Single
import okhttp3.ResponseBody
import merchant.mokka.api.request.CodeReq
import merchant.mokka.api.request.SaveClientReq
import merchant.mokka.api.response.ClientRes
import merchant.mokka.api.response.PurchaseRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiClient {
    @GET("api/loans/v1/loan_requests/{token}/client")
    fun loanClientInfo(
            @Path("token") loanToken: String
    ) : Single<Response<ClientRes>>

    @POST("api/loans/v1/loan_requests/{token}/client")
    fun loanCreateClient(
            @Path("token") loanToken: String,
            @Body client: SaveClientReq
    ) : Single<Response<ClientRes>>

    @POST("api/loans/v1/loan_requests/{token}/confirmation")
    fun loanConfirmClient(
            @Path("token") loanToken: String,
            @Body code: CodeReq
    ) : Single<Response<PurchaseRes>>

    @POST("api/loans/v1/loan_requests/{token}/client/confirmation")
    fun loanConfirm(
            @Path("token") loanToken: String
    ) : Single<Response<ResponseBody>>
}