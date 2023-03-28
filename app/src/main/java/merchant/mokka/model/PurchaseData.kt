package merchant.mokka.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable
@JsonClass(generateAdapter = true)
data class PurchaseData(
        @Json(name = "first_name")
        val firstName: String?,

        @Json(name = "middle_name")
        val middleName: String?,

        @Json(name = "last_name")
        val lastName: String?,

        val credit_decision: String?,
        val decision: String?,

        @Json(name = "credit_limit")
        val creditLimit: Double?,

        @Json(name = "decision_code")
        val decisionCode: Int,

        @Json(name = "decision_message")
        val decisionMessage: String,

        @Json(name = "kyc_passed")
        val kycPassed: Boolean? = null

) : Serializable {
        val approved: Boolean
                get() = credit_decision == "approved" || decision == "approved"

        fun isEmpty() = firstName == null && lastName == null && credit_decision == null &&
                decision == null && creditLimit == null
}