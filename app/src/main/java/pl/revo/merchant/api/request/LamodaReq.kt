package pl.revo.merchant.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LamodaRes(
        @Json(name = "credentials") val credentials: LamodaCredentialsRes,
        @Json(name = "payload") val payload: LamodaPayloadRes
)
@JsonClass(generateAdapter = true)
data class LamodaCredentialsRes(
        @Json(name = "store_id") val storeId: Int? = null,
        @Json(name = "auth_type") val authType: String = "sha-1-sorted",
        @Json(name = "signature") var signature: String = ""
)
@JsonClass(generateAdapter = true)
data class LamodaPayloadRes(
        @Json(name = "agent_phone") val agentPhone: String,
        @Json(name = "merchant_agent_id") val merchantAgentId: String,
        @Json(name = "order_id") val orderId: String,
        @Json(name = "amount") val amount: Float,
        @Json(name = "phone") val phone: String
)

@JsonClass(generateAdapter = true)
data class LamodaLoanRes(
        @Json(name = "credentials") val credentials: LamodaCredentialsRes,
        @Json(name = "payload") val payload: LamodaLoanPayloadRes
) {
        val isValid: Boolean
                get() = payload.amount != null && payload.decision != null
}
@JsonClass(generateAdapter = true)
data class LamodaLoanPayloadRes(
        @Json(name = "order_id") val orderId: String? = null,
        @Json(name = "decision") val decision: String? = null,
        @Json(name = "amount") val amount: Float?,
        @Json(name = "discount_amount") val discountAmount: Float?,
        @Json(name = "term") val term: Int?,
        @Json(name = "phone") val phone: String
)