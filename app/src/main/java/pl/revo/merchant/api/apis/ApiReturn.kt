package pl.revo.merchant.api.apis

import io.reactivex.Single
import okhttp3.ResponseBody
import pl.revo.merchant.api.response.SearchRes
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiReturn {
    @GET("api/loans/v1/orders.json")
    fun getOrderList(
        @Query("store_id") storeId: Int,
        @Query("filters[mobile_phone]") phone: String?,
        @Query("filters[id_document_no]") documentId: String?,
        @Query("filters[order_id]") orderId: Int?,
        @Query("filters[guid]") guid: String?
    ) : Single<Response<SearchRes>>

    @POST("api/loans/v1/orders/{id}/send_return_confirmation_code")
    fun sendReturnConfirmationCode(
            @Path("id") orderId: Int
    ) : Single<Response<ResponseBody>>

    @FormUrlEncoded
    @POST("api/loans/v1/returns")
    fun createReturn(
            @Field("return[order_id]") orderId: Int,
            @Field("return[confirmation_code]") confirmCode: String,
            @Field("return[amount]") amount: String,
            @Field("return[store_id]") storeId: Int
    ) : Single<Response<ResponseBody>>

    @POST("api/loans/v1/returns/{id}/confirm")
    fun confirmReturn(
            @Path("id") returnId: Int
    ) : Single<Response<ResponseBody>>

    @POST("api/loans/v1/returns/{id}/cancel")
    fun cancelReturn(
            @Path("id") returnId: Int
    ) : Single<Response<ResponseBody>>
}