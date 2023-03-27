package pl.revo.merchant.api.response

import com.squareup.moshi.Json
import pl.revo.merchant.model.TariffData

data class TariffRes(
        @Json(name = "loan_request")
        val tariffs: List<TariffData>,
        @Json(name = "client")
        val client: TariffClientData?
)

data class TariffClientData(
        @Json(name = "sms_info")
        val smsInfo: TariffClientSmsInfoData?
)

data class TariffClientSmsInfoData(
        @Json(name = "available")
        val available: Boolean? = null,
        @Json(name = "subscribed")
        val subscribed: Boolean? = null,
        @Json(name = "price")
        val price: Double? = null

)