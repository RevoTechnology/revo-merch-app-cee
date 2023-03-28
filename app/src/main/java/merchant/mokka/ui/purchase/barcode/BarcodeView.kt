package merchant.mokka.ui.purchase.barcode

import merchant.mokka.common.IBaseView

interface BarcodeView : IBaseView {
    fun lamodaResult(result: String)
}