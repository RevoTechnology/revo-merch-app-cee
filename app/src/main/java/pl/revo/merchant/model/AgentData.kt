package pl.revo.merchant.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AgentData(
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    @Json(name = "avatar") val avatar: String,
    @Json(name = "stores") val stores: MutableList<StoreDto>,
    @Json(name = "chains") val chains: List<ChainDto>? = null,
    @Json(name = "id") val id: Int = 0
) {
    val fullName
        get() = "$firstName $lastName".trim()
}

@JsonClass(generateAdapter = true)
data class StoreDto(val store: StoreData)
@JsonClass(generateAdapter = true)
data class ChainDto(@Json(name = "chain") val chain: ChainData)