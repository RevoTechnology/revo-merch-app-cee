package pl.revo.merchant.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pl.revo.merchant.utils.toDayMonth
import java.util.*

@JsonClass(generateAdapter = true)
data class ReportData(
        @Json(name = "start_at")
        val startAt: Date?,

        @Json(name = "end_at")
        val endAt: Date?,

        @Json(name = "loans_count")
        val loansCount: Int?,

        val amount: Double?
) {
    val periodName: String?
        get() {
            return if (startAt != null && endAt != null) {
                startAt.toDayMonth() + " - " + endAt.toDayMonth()
            } else {
                null
            }
        }
}