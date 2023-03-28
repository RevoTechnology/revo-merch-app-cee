package merchant.mokka.api.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CodeReq(
        val code: String
)