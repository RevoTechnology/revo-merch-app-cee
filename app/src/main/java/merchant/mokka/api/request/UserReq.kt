package merchant.mokka.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserReq(
        val user: UserData
)
@JsonClass(generateAdapter = true)
data class UserData(
        val login: String,

        @Json(name = "confirmation_code")
        val smsCode: String? = null,

        val password: String? = null,

        @Json(name = "password_confirmation")
        val confirmation: String? = null
)

