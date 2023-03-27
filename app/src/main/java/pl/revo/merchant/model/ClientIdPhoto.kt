package pl.revo.merchant.model

import java.io.File
import java.io.Serializable

data class ClientIdPhoto(
        var nameImage: File? = null,
        var clientWithPassportImage: File? = null,
        var livingAddressImage: File? = null
) : Serializable