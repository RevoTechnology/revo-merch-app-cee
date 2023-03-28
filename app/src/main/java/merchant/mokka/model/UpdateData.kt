package merchant.mokka.model

import java.io.Serializable

data class UpdateData(
        val apkUrl: String,
        val installationMessage: String
) : Serializable