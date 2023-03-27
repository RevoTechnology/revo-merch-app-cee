package pl.revo.merchant.api.helpers

import pl.revo.merchant.api.adapter.MoshiUtils
import pl.revo.merchant.api.response.ReturnId
import pl.revo.merchant.api.response.ReturnOldRes
import pl.revo.merchant.api.response.ReturnRes
import pl.revo.merchant.model.BarcodeDto

class RefundBarcodeResponse(private val body: String?) {
    private val moshi = MoshiUtils.moshi().build()

    fun result() = modernResult() ?: oldResult()
    private fun oldResult() = ReturnRes(returnId = oldReturnId())

    private fun modernResult(): ReturnRes? = try {
        val jsonAdapter = moshi.adapter<ReturnRes>(ReturnRes::class.java)
        jsonAdapter.fromJson(body)
    } catch (e: Exception) {
        null
    }

    private fun oldReturnId() = try {
        val jsonAdapter = moshi.adapter(ReturnOldRes::class.java)
        val res = jsonAdapter.fromJson(body) ?: throw Exception()
        ReturnId(id = res.returnId.id, barcode = BarcodeDto(number = res.returnId.barcode))
    } catch (e: Exception) {
        ReturnId(id = 0, barcode = BarcodeDto())
    }
}