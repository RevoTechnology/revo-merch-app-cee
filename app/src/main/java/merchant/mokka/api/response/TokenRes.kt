package merchant.mokka.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenRes(
        @Json(name = "user")
        val user: TokenData
)

@JsonClass(generateAdapter = true)
data class TokenData(
        @Json(name = "authentication_token")
        val token: String
)