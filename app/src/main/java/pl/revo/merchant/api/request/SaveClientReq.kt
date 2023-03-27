package pl.revo.merchant.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pl.revo.merchant.model.GdprAcceptance
import pl.revo.merchant.model.IdDocuments
@JsonClass(generateAdapter = true)
data class SaveClientReq(
        val client: SaveClientData
)
@JsonClass(generateAdapter = true)
data class SaveClientData(
        @Json(name = "mobile_phone")
        var mobilePhone: String,

        @Json(name = "first_name")
        var firstName: String,

        @Json(name = "middle_name")
        var middleName: String? = null,

        @Json(name = "last_name")
        var lastName: String,

        @Json(name = "birth_date")
        var birthDate: String,

        @Json(name = "id_documents")
        val idDocuments: IdDocuments? = null,

        @Json(name = "missing_documents")
        val missingDocuments: MutableList<String>? = null,

        var email: String,
        val area: String? = null,
        val settlement: String,
        val street: String,
        val house: String,
        val building: String? = null,
        val apartment: String? = null,

        @Json(name = "postal_code")
        val postalCode: String,

        @Json(name = "black_mark")
        val blackMark: String,

        @Json(name = "confirmation_code")
        var confirmationCode: String,

        @Json(name = "gdpr_acceptance")
        val gdprAcceptance: GdprAcceptance
)