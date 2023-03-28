package merchant.mokka.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApprovedReq(
        @Json(name = "term_id")
        val termId: Int,
        @Json(name = "client")
        val client: ApprovedClientReq
)

@JsonClass(generateAdapter = true)
data class ApprovedClientReq(
        @Json(name = "sms_info")
        val smsInfo: ApprovedClientSmsInfoReq
)

@JsonClass(generateAdapter = true)
data class ApprovedClientSmsInfoReq(
        @Json(name = "subscribed")
        val subscribed: Boolean
)