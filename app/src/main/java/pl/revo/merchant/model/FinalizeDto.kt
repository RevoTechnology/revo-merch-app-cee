package pl.revo.merchant.model

import com.squareup.moshi.JsonClass
import pl.revo.merchant.api.request.LamodaCredentialsRes
import pl.revo.merchant.api.request.LamodaLoanPayloadRes
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class FinalizeDto(
        val offerId: String,
        val barcode: List<BarcodeDto>,
        val input: FinalizeInputDto,
        val loan: LoanData,

        // specific fields for Lamoda
        val credentialsLamoda: LamodaCredentialsRes? = null,
        val payloadLamoda: LamodaLoanPayloadRes? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class BarcodeDto(
        val image: String? = null,
        val text: String? = null,
        val number: String? = null
) : Serializable {

    fun isEmpty() = image.isNullOrEmpty() && text.isNullOrEmpty() && number.isNullOrEmpty()
    override fun toString() = "BarcodeDto(image=${image?.length}, text=$text, number=$number)"
}

@JsonClass(generateAdapter = true)
data class FinalizeInputDto(val text: String? = null, var type: Type = Type.BARCODE) : Serializable {
    enum class Type {
        BARCODE, INPUT, NO_INPUT
    }
}