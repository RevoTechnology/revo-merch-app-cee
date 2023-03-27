package pl.revo.merchant.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddressData(
        @Json(name = "miejscowosc")
        val city: String?,

        @Json(name = "ulica")
        val street: String?
)