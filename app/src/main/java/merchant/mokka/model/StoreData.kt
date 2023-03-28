package merchant.mokka.model

import com.squareup.moshi.Json

data class StoreData(
        @Json(name = "id") val id: Int,
        @Json(name = "name") val name: String,
        @Json(name = "address") val address: String,
        @Json(name = "name_by_trader") val traderName: String,
        @Json(name = "tariff_min") val tariffMin: Double,
        @Json(name = "tariff_max") val tariffMax: Double,
        @Json(name = "sms_info") val smsInfo: Boolean?,
        @Json(name = "sms_info_price") val smsInfoPrice: Int?
)

data class ChainData(
        @Json(name = "id") val id: Int,
        @Json(name = "name") val name: String
)
