package pl.revo.merchant.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pl.revo.merchant.model.BarcodeDto

@JsonClass(generateAdapter = true)
data class ReturnRes(
        @Json(name = "return")
        val returnId: ReturnId
)

@JsonClass(generateAdapter = true)
data class ReturnId(
        val id: Int,
        val barcode: BarcodeDto
)

@JsonClass(generateAdapter = true)
data class ReturnOldRes(
        @Json(name = "return")
        val returnId: ReturnOldId
)

@JsonClass(generateAdapter = true)
data class ReturnOldId(
        val id: Int,
        val barcode: String
)