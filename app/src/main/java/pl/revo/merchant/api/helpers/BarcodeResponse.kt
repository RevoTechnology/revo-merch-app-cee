package pl.revo.merchant.api.helpers

import pl.revo.merchant.api.adapter.MoshiUtils
import pl.revo.merchant.api.error.InvalidFinalizationResponse
import pl.revo.merchant.api.response.BarcodeFixData
import pl.revo.merchant.api.response.FinalizationBarcodeStringResponse
import pl.revo.merchant.api.response.FinalizationOneBarcodeResponse
import pl.revo.merchant.api.response.FinalizationResponse
import pl.revo.merchant.api.response.LoanApplication
import pl.revo.merchant.model.BarcodeDto

class BarcodeResponse(private val body: String?) {
    private val moshi = MoshiUtils.moshi().build()
    val finalization
        get() = res() ?: fix() ?: oneRes() ?: empty()

    fun stringResponse() = try {
        val jsonAdapter = moshi.adapter(FinalizationBarcodeStringResponse::class.java)
        jsonAdapter.fromJson(body)
    } catch (e: Throwable) {
        null
    }

    private fun res(): FinalizationResponse? = try {
        val jsonAdapter = moshi.adapter(FinalizationResponse::class.java)
        jsonAdapter.fromJson(body)
            ?.apply { if (!isValid) throw InvalidFinalizationResponse() }
    } catch (e: Throwable) {
        null
    }

    private fun oneRes() = try {
        val jsonAdapter = moshi.adapter(FinalizationOneBarcodeResponse::class.java)
        val res = jsonAdapter.fromJson(body)
        res?.loanApplication?.barcodes ?: throw InvalidFinalizationResponse()

        FinalizationResponse(
            offerId = res.offerId,
            loanApplication = LoanApplication(listOf(res.loanApplication.barcodes))
        )
            .apply { if (!isValid) throw InvalidFinalizationResponse() }
    } catch (e: Throwable) {
        null
    }

    private fun fix() = try {
        val jsonAdapter = moshi.adapter(BarcodeFixData::class.java)
        val res = jsonAdapter.fromJson(body)
        val barcodeDto = res?.loanApplication
            ?.let {
                BarcodeDto(
                    number = res.loanApplication.barcodes.number,
                    image = res.loanApplication.barcodes.image,
                    text = res.loanApplication.barcodes.text
                )
            }
            ?: run { BarcodeDto() }

        FinalizationResponse(offerId = "", loanApplication = LoanApplication(listOf(barcodeDto)))
            .apply { if (!isValid) throw InvalidFinalizationResponse() }
    } catch (e: Throwable) {
        null
    }

    private fun empty() = FinalizationResponse(
        offerId = "",
        loanApplication = LoanApplication(listOf(BarcodeDto()))
    )
}