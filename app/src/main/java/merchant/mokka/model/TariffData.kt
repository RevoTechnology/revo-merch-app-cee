package merchant.mokka.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable
import java.util.*
@JsonClass(generateAdapter = true)
data class TariffData(
        val term: Int,

        val term_id: Int,

        @Json(name = "monthly_payment")
        val monthlyPayment: Double,

        @Json(name = "total_of_payments")
        val totalOfPayments: Double,

        @Json(name = "sum_with_discount")
        val sumWithDiscount: Double,

        @Json(name = "total_overpayment")
        val totalOverpayment: Double,

        @Json(name = "min_amount")
        val minAmount: Double,

        @Json(name = "max_amount")
        val maxAmount: Double,

        @Json(name = "sms_info")
        val smsInfo: Double?,

        @Json(name = "tariff_product_kind")
        val tariffProductKind: String?,

        val schedule: List<Schedule>,

        var selected: Boolean = false,
        var expanded: Boolean = false,
        var currentSum: Double = 0.0,

        @Json(name = "bnpl") var bnpl: BnplData? = null
) : Serializable {
    val isRclProductKing
        get() = tariffProductKind == "rcl"

    val isFactoringProductKing
        get() = tariffProductKind == "factoring"
}

@JsonClass(generateAdapter = true)
data class Schedule(
        val date: Date,
        val amount: Double
) : Serializable

@JsonClass(generateAdapter = true)
data class BnplData(
        @SerializedName("term") val term: Int,
        @SerializedName("date_of_first_payment") val dateFirstPayment: Date?,
        @SerializedName("commission") val commission: Double?
) : Serializable