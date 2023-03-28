package merchant.mokka.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateRes(
        @Json(name = "apk_version")
        val apkVersion: String,

        @Json(name = "apk_url")
        val apkUrl: String
)