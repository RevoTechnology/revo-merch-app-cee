package merchant.mokka.api.response

import com.squareup.moshi.Json
import merchant.mokka.model.ClientData

data class ClientRes(
      @Json(name = "client") val client: ClientData?,
      @Json(name = "meta") val meta: MetaRes?
)