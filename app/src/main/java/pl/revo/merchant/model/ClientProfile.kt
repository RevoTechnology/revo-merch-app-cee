package pl.revo.merchant.model

import java.io.Serializable
import java.util.*

data class ClientProfile(
        var phone: String = "",
        var firstName: String = "",
        var lastName: String = "",
        var birthDay: Date? = null,
        var passport: String = "",
        var expiryDate: Date? = null,
        var pesel: String = "",
        var street: String = "",
        var house: String = "",
        var flat: String = "",
        var postalCode: String = "",
        var city: String = "",
        var email: String = "",
        var warning: Boolean = false
) : Serializable {

    val fullName: String
        get() {
            return "$firstName $lastName"
        }
}