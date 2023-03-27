package pl.revo.merchant.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import pl.revo.merchant.api.request.LamodaCredentialsRes
import pl.revo.merchant.api.request.LamodaLoanPayloadRes
import pl.revo.merchant.model.BarcodeDto
import pl.revo.merchant.model.FinalizeInputDto

@JsonClass(generateAdapter = true)
data class LoanApplicationOne(
        @Json(name = "barcodes") val barcodes: BarcodeDto,
        @Json(name = "no_input") val noInput: FinalizationInputResponse? = null,
        @Json(name = "input") val input: FinalizationInputResponse? = null
)

@JsonClass(generateAdapter = true)
data class LoanApplication(
        @Json(name = "barcodes") val barcodes: List<BarcodeDto>?,
        @Json(name = "no_input") val noInput: FinalizationInputResponse? = null,
        @Json(name = "input") val input: FinalizationInputResponse? = null
) {
    val finalizeInput
        get() = FinalizeInputDto(
                text = noInput?.text ?: input?.text,
                type = when {
                    input != null -> FinalizeInputDto.Type.INPUT
                    noInput != null -> FinalizeInputDto.Type.NO_INPUT
                    !barcodes?.filter { !it.isEmpty() }.isNullOrEmpty() -> FinalizeInputDto.Type.BARCODE
                    else -> FinalizeInputDto.Type.NO_INPUT
                }
        )
}

@JsonClass(generateAdapter = true)
data class BarcodeFixData(
        @Json(name = "offer_id") val offerId: String?,
        @Json(name = "loan_application") val loanApplication: LoanApplicationOne?
)

@JsonClass(generateAdapter = true)
data class FinalizationResponse(
        @Json(name = "offer_id") val offerId: String? = null,
        @Json(name = "loan_application") val loanApplication: LoanApplication?,

        // Specific fields for Lamoda
        @Json(name = "credentials") val credentialsLamoda: LamodaCredentialsRes? = null,
        @Json(name = "payload") val payloadLamoda: LamodaLoanPayloadRes? = null) {

    val isValid
        get() = (loanApplication != null && offerId != null)
                || (credentialsLamoda != null && payloadLamoda != null)

    val isLamoda
        get() = credentialsLamoda != null && payloadLamoda != null
}

data class FinalizationOneBarcodeResponse(
        @Json(name = "offer_id") val offerId: String? = null,
        @Json(name = "loan_application") val loanApplication: LoanApplicationOne
)

data class FinalizationInputResponse(
        @Json(name = "screen_text") val text: String
)

data class FinalizationBarcodeStringResponse(@Json(name = "loan_application") val loanApplication: LoanApplicationBarcodeString?)
data class LoanApplicationBarcodeString(@Json(name = "barcode") val barcode: String?)