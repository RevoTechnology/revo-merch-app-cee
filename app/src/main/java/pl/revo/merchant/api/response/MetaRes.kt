package pl.revo.merchant.api.response

import com.squareup.moshi.Json

data class MetaRes(@Json(name = "screen_content") val screenContent: ScreenContentRes?,
                   @Json(name = "main_text") val mainText: String?
) {
    val text: String?
        get() = mainText ?: screenContent?.mainText
}

data class ScreenContentRes(@Json(name = "main_text") val mainText: String?)