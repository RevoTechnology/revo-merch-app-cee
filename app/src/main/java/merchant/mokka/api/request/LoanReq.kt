package merchant.mokka.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoanReq(
        @Json(name = "loan_request")
        val loanRequest: LoanReqData
)

@JsonClass(generateAdapter = true)
data class LoanReqData(
        @Json(name = "store_id")
        val storeId: Int? = null,

        val token: String? = null,

        @Json(name = "mobile_phone")
        val phone: String? = null,

        @Json(name = "amount")
        val amount: String? = null,

        @Json(name = "insurance_available")
        var insuranceAvailable: Boolean? = null,

        @Json(name = "agree_insurance")
        var insuranceAgree: Boolean? = null,

        @Json(name = "kind")
        var kind: String = "merch_app"
)