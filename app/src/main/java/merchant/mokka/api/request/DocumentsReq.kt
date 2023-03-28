package merchant.mokka.api.request

import com.squareup.moshi.JsonClass
import okhttp3.RequestBody

@JsonClass(generateAdapter = true)
data class DocumentsReq(
        val client: DocumentsData
)

@JsonClass(generateAdapter = true)
data class DocumentsData(
        val name: RequestBody? = null,
        val client_with_passport: RequestBody? = null
)