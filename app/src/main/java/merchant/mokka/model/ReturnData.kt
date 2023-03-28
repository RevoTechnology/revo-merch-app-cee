package merchant.mokka.model

import java.io.Serializable

data class ReturnData(
        val clientName: String,
        val purchaseSum: Double,
        val returnSum: Double,
        val orderId: Int,
        var returnId: Int? = null,
        var barcode: BarcodeDto? = null
) : Serializable