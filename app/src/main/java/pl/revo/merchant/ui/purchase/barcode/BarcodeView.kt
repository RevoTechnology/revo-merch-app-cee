package pl.revo.merchant.ui.purchase.barcode

import pl.revo.merchant.common.IBaseView

interface BarcodeView : IBaseView {
    fun lamodaResult(result: String)
}