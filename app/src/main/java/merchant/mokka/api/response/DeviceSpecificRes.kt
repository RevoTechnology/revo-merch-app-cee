package merchant.mokka.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeviceSpecificRes(
        @Json(name = "current_package_name")
        val currentPackageName: String,

        @Json(name = "installation_message")
        val installationMessage: String,

        @Json(name = "redirect_message")
        val redirectMessage: String
)