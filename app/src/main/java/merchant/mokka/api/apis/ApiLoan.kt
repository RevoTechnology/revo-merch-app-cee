package merchant.mokka.api.apis

import io.reactivex.Single
import okhttp3.ResponseBody
import merchant.mokka.api.request.ApprovedReq
import merchant.mokka.api.request.BillLoanRequest
import merchant.mokka.api.request.FinalizeReq
import merchant.mokka.api.request.LoanReq
import merchant.mokka.api.response.TariffRes
import retrofit2.Response
import retrofit2.http.*

interface ApiLoan {
    @POST("api/loans/v1/loan_requests")
    fun loanCreateRequest(
            @Body loanRequest: LoanReq
    ): Single<Response<LoanReq>>

    @PUT("api/loans/v1/loan_requests/{token}")
    fun loanUpdateRequest(
            @Path("token") loanToken: String,
            @Body loanRequest: LoanReq
    ): Single<Response<ResponseBody>>

    @GET("api/loans/v1/loan_requests/{token}")
    fun getTariffInformation(
            @Path("token") loanToken: String
    ): Single<Response<TariffRes>>

    @POST("api/loans/v1/loan_requests/{token}/loan")
    fun createApprovedLoan(
            @Path("token") loanToken: String,
            @Body term: ApprovedReq
    ): Single<Response<ResponseBody>>

    @POST("api/loans/v1/loan_requests/{token}/loan/finalization")
    fun finalizeLoan(
            @Path("token") loanToken: String,
            @Body data: FinalizeReq
    ): Single<Response<ResponseBody>>

    @GET("api/loans/v1/loan_requests/{token}/documents/{urlPart}.html")
    fun getDocuments(
            @Path("token") loanToken: String,
            @Path("urlPart") kind: String
    ): Single<Response<ResponseBody>>

    @POST("api/loans/v1/loan_requests/{token}/client/self_registration")
    fun selfRegistration(
            @Path("token") loanToken: String,
            @Query("mobile_phone") phone: String
    ): Single<Response<ResponseBody>>

    @PUT("api/loans/v1/loan_requests/{token}/loan/sales_reciept")
    fun bill(
            @Path("token") loanToken: String,
            @Body data: BillLoanRequest
    ): Single<Response<ResponseBody>>
}