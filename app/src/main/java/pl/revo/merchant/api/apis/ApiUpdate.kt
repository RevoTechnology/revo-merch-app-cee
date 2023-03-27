package pl.revo.merchant.api.apis

import io.reactivex.Single
import okhttp3.ResponseBody
import pl.revo.merchant.api.response.DeviceSpecificRes
import pl.revo.merchant.api.response.UpdateRes
import retrofit2.Response
import retrofit2.http.*

interface ApiUpdate {
    @GET
    fun downloadVersionFile(
            @Url url: String,
            @Header("Authorization") versionToken: String
    ) : Single<Response<ResponseBody>>

    @Streaming
    @GET
    fun downloadApkFile(
            @Url url: String,
            @Header("Authorization") versionToken: String
    ) : Single<Response<ResponseBody>>

    @GET
    fun getDevice(@Url devicePath: String) : Single<Response<DeviceSpecificRes>>

    @FormUrlEncoded
    @POST
    fun deviceLogs(
            @Url updatePath: String,
            @Field("event") event: String?,
            @Field("store_id") storeId: Int?,
            @Field("device_mac_addr") macAddress: String? = null,
            @Field("phone_number") phoneNumber: String? = null,
            @Field("device_model") deviceModel: String? = null,
            @Field("device_os_version") osVersion: String? = null,
            @Field("current_app_version") appVersion: String? = null
    ) : Single<Response<ResponseBody>>

    @GET
    fun getDeviceUpdate(@Url devicePath: String) : Single<Response<UpdateRes>>

    @Streaming
    @GET
    fun downloadNatashaApkFile(
            @Url url: String
    ) : Single<Response<ResponseBody>>
}