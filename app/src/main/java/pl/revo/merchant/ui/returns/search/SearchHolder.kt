package pl.revo.merchant.ui.returns.search

import android.view.View
import androidx.core.view.doOnLayout
import kotlinx.android.synthetic.main.item_search.view.*
import pl.revo.merchant.R
import pl.revo.merchant.common.BaseRecyclerViewHolder
import pl.revo.merchant.model.SearchData
import pl.revo.merchant.utils.*
import pl.revo.merchant.utils.barcode.BarcodeBitmapGenerator
import pl.revo.merchant.utils.barcode.BarcodeFormat
import pl.revo.merchant.utils.barcode.BarcodeRequest

class SearchHolder(
        val view: View
) : BaseRecyclerViewHolder<SearchData>(view) {

    override fun bindView(data: SearchData, onItemClick: ((item: SearchData, position: Int) -> Unit)?) {
        with(view) {
            searchItemClient.text = data.client
            searchItemPhone.text = if(isBgLocale()) (context.getString(R.string.phone_empty)+data.phone).replace(" ", "") else data.phone
            searchItemContract.text = data.guid
            searchItemDate.text = data.date.toText(DateFormats.SIMPLE_FORMAT)
            searchItemSum.text = data.amount.toTextWithCurrency()

            try {
                searchItemBarcodeView.visible(false)
                if (!data.barcode.isNullOrEmpty()) {
                    barcodeView.visible(true)
                    barcodeView.text = context.getString(R.string.barcode_value, data.barcode)
                    barcodeImageView.visible(true)
                    barcodeImageView.doOnLayout {
                        val barcodeRequest = BarcodeRequest.BarcodeRequestBuilder()
                            .barcodeText(data.barcode)
                            .barcodeFormat(BarcodeFormat.CODE_128)
                            .backgroundColor(Constants.BARCODE_BACKGROUND)
                            .height(barcodeImageView.height)
                            .width(barcodeImageView.width)
                                .build()

                        val bitmap = BarcodeBitmapGenerator().generateCode128(barcodeRequest)
                        barcodeImageView.setImageBitmap(bitmap)
                    }
                }
            } catch (e: Throwable) {
                barcodeImageView.visible(false)
                barcodeView.visible(false)
                searchItemBarcode.text = data.barcode
                searchItemBarcodeView.visible(true)
            }

            searchItemMakeReturn.setOnClickListener { onItemClick?.invoke(data, adapterPosition) }
        }
    }
}