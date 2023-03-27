package pl.revo.merchant.api.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeviceInfoReq(
        val event: String? = null,
        val store_id: Int? = null,
        val device_mac_addr: String? = null,
        val phone_number: String? = null,
        val device_model: String? = null,
        val device_os_version: String? = null,
        val current_app_version: String? = null
)