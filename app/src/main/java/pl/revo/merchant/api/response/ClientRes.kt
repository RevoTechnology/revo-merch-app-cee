package pl.revo.merchant.api.response

import com.squareup.moshi.Json
import pl.revo.merchant.model.ClientData

data class ClientRes(
      @Json(name = "client") val client: ClientData?,
      @Json(name = "meta") val meta: MetaRes?
)