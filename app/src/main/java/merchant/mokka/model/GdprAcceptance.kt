package merchant.mokka.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable
@JsonClass(generateAdapter = true)
data class GdprAcceptance(
        // Common
        @Json(name = "regulations") var regulations: String? = null,

        // Poland version
        @Json(name = "personal_data") var personalData: String? = null,
        @Json(name = "credit_bureaus") var creditBureaus: String? = null,
        @Json(name = "personal_data_marketing_email") var marketingEmail: String? = null,
        @Json(name = "personal_data_marketing_phone") var marketingPhone: String? = null,


        // Romanian version
        @Json(name = "tax_office") var taxOffice: String? = null,
        @Json(name = "personal_data_marketing_all") var personalDataMarketingAll: String? = null
) : Serializable