package pl.revo.merchant.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable
import java.util.*

@JsonClass(generateAdapter = true)
data class SearchData(
	val id: Int,

	@Json(name = "client_name")
	val client: String,

	@Json(name = "mobile_phone")
	val phone: String,

	val barcode: String?,

	@Json(name = "created_at")
	val date: Date,

	@Json(name = "amount")
	val amount: Double,

	@Json(name = "remaining_amount")
	val remainingAmount: Double,

	@Json(name = "guid") val guid: String,

	var returnSum: Double = 0.0
) : Serializable