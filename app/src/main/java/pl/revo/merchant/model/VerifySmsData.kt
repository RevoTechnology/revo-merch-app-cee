package pl.revo.merchant.model

import java.io.Serializable

data class VerifySmsData(
        val login: String,
        val confirmationCode: String
) : Serializable
