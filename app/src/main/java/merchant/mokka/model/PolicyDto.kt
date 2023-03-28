package merchant.mokka.model

import java.io.Serializable

data class PolicyDto(
        val titleResId: Int,
        val loanToken: String,
        val kind: String,
        val data: String? = null
) : Serializable