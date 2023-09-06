package merchant.mokka.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable
import java.util.*

@JsonClass(generateAdapter = true)
data class ClientData(
    val id: Int? = null,

    @Json(name = "mobile_phone")
    var phone: String? = null,

    @Json(name = "first_name")
    var firstName: String? = null,

    @Json(name = "last_name")
    var lastName: String? = null,

    @Json(name = "middle_name")
    var middleName: String? = null,

    @Json(name = "birth_date")
    var birthDate: Date? = null,

    @Json(name = "id_documents")
    val idDocuments: IdDocuments? = null,

    @Json(name = "missing_documents")
    val missingDocuments: MutableList<String>? = null,

    var email: String? = null,
    val area: String? = null,
    val settlement: String? = null,
    val street: String? = null,
    val house: String? = null,
    val building: String? = null,
    val apartment: String? = null,

    @Json(name = "postal_code")
    val postalCode: String? = null,

    @Json(name = "black_mark")
    val blackMark: String? = null,

    @Json(name = "credit_decision")
    var creditDecision: String? = null,

    @Json(name = "credit_limit")
    var creditLimit: Double? = null,

    @Json(name = "decision_code")
    var decisionCode: Int? = null,

    @Json(name = "decision_message")
    var decisionMessage: String? = null,

    @Json(name = "client_type")
    val clientType: String? = null,

    @Json(name = "confirm_data")
    val confirmData: Boolean? = null,

    @Json(name = "kyc_passed")
    val isKycPassed: Boolean? = null,

    var technicalMessage: String? = null,

    @Json(name = "is_repeated") val _isRepeated: Boolean? = null,
    @Json(name = "rcl_accepted") val _rclAccepted: Boolean? = null

) : Serializable {
    val fullName: String
        get() = "$firstName $lastName"

    val fullNameWithPatronymic: String
        get() = "$firstName $middleName $lastName"

    val isRepeated
        get() = _isRepeated == true

    val rclAccepted
        get() = _rclAccepted == true

    fun isEmpty() = id == null && phone == null && firstName == null &&
            lastName == null && birthDate == null && idDocuments == null &&
            email == null && settlement == null && street == null &&
            house == null && apartment == null && postalCode == null

    val approved: Boolean
        get() = creditDecision == "approved"

    val address: String
        get() = "$area $settlement $street $house $building $apartment"

    val isNewClient: Boolean
        get() = !isRepeated //clientType.equals("new", ignoreCase = true)
}
@JsonClass(generateAdapter = true)
data class IdDocuments(
    @Json(name = "polish_id")
    var polishId: PolishId? = null,

    @Json(name = "polish_pesel")
    var polishPesel: PolishPesel? = null,

    @Json(name = "russian_passport")
    var russianPassport: RussianPassport? = null,

    @Json(name = "cnp")
    var romanianCnp: RomanianCnp? = null,

    //Bulgaria
    @Json(name = "id_card")
    var idCard: IdCard? = null,

    @Json(name = "egn")
    var egn: Egn? = null
) : Serializable
@JsonClass(generateAdapter = true)
data class RomanianCnp(@Json(name = "number") val number: String) : Serializable
@JsonClass(generateAdapter = true)
data class IdCard(@Json(name = "number") val number: String) : Serializable
@JsonClass(generateAdapter = true)
data class Egn(@Json(name = "number") val number: String) : Serializable
@JsonClass(generateAdapter = true)
data class PolishId(
    @Json(name = "expiry_date")
    var expiryDate: String? = null,

    var number: String? = null
) : Serializable
@JsonClass(generateAdapter = true)
data class PolishPesel(
    @Json(name = "expiry_date")
    var expiryDate: String? = null,

    var number: String? = null
) : Serializable
@JsonClass(generateAdapter = true)
data class RussianPassport(
    @Json(name = "number")
    var number: String? = null,

    @Json(name = "series")
    var series: String? = null,

    @Json(name = "expiry_date")
    var expiryDate: String? = null
) : Serializable