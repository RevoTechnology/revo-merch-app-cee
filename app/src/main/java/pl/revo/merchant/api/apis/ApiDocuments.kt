package pl.revo.merchant.api.apis

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiDocuments {
    @Multipart
    @PATCH("api/loans/v1/loan_requests/{token}/client")
    fun loanUpdateClientDocuments(
            @Path("token") loanToken: String,
            @Part phoneName: MultipartBody.Part?,
            @Part photoLivingAddress: MultipartBody.Part?,
            @Part photoClientWithPassport: MultipartBody.Part?
    ) : Single<Response<ResponseBody>>
}