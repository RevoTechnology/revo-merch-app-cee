package pl.revo.merchant.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BillLoanRequest(@Json(name = "loan") val loan: BillRequest)
@JsonClass(generateAdapter = true)
data class BillRequest(@Json(name = "input_text") val code: String) {
    fun loanRequest() = BillLoanRequest(loan = this)
}

