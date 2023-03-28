package merchant.mokka.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FinalizeReq(
        val loan: FinalizeData
)

@JsonClass(generateAdapter = true)
data class FinalizeData(
        @Json(name = "agree_processing")
        val agreeProcessing: String,

        @Json(name = "agree_sms_info")
        val agreeSmsInfo: String,

        @Json(name = "confirmation_code")
        val confirmationCode: String
)