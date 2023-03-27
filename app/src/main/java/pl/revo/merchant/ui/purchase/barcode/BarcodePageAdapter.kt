package pl.revo.merchant.ui.purchase.barcode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.page_barcode.view.*
import pl.revo.merchant.R
import pl.revo.merchant.model.BarcodeDto
import pl.revo.merchant.utils.Constants
import pl.revo.merchant.utils.barcode.BarcodeBitmapGenerator
import pl.revo.merchant.utils.barcode.BarcodeFormat
import pl.revo.merchant.utils.barcode.BarcodeRequest
import pl.revo.merchant.utils.base64ToImage

class BarcodePageAdapter(
        private var items: List<BarcodeDto>,
        private val barcodeWidth: Int,
        private val barcodeHeight: Int,
        val onItemClick: ((barcode: BarcodeDto) -> Unit)? = null
) : androidx.viewpager.widget.PagerAdapter() {

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view == any
    }

    override fun getCount() = items.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
                .inflate(R.layout.page_barcode, container, false)

        with(view) {
            val item = items[position]

            if (!item.image.isNullOrEmpty() && !item.text.isNullOrEmpty()) {
                val bitmap = item.image.base64ToImage()
                barcodeImg.setImageBitmap(bitmap)
                barcodeValue.text = context.getString(R.string.barcode_value, item.text)
            } else
            if (item.number?.isNotEmpty() == true) {
                val barcodeRequest = BarcodeRequest.BarcodeRequestBuilder()
                        .barcodeText(item.number.orEmpty())
                        .barcodeFormat(BarcodeFormat.CODE_128)
                        .width(barcodeWidth)
                        .backgroundColor(Constants.BARCODE_BACKGROUND)
                        .height(barcodeHeight)
                        .build()
                val bitmap = BarcodeBitmapGenerator().generateCode128(barcodeRequest)
                barcodeImg.setImageBitmap(bitmap)
                barcodeValue.text = context.getString(R.string.barcode_value, item.number)
            }

            setOnClickListener { onItemClick?.invoke(item) }
        }

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View)
    }
}