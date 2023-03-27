package pl.revo.merchant.ui.purchase.barcode

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_barcode.*
import pl.revo.merchant.BuildConfig
import pl.revo.merchant.R
import pl.revo.merchant.common.ExtrasKey
import pl.revo.merchant.common.RouteFrom
import pl.revo.merchant.model.BarcodeDto
import pl.revo.merchant.utils.Constants
import pl.revo.merchant.utils.barcode.BarcodeBitmapGenerator
import pl.revo.merchant.utils.barcode.BarcodeFormat
import pl.revo.merchant.utils.barcode.BarcodeRequest
import pl.revo.merchant.utils.base64ToImage

class BarcodeActivity : AppCompatActivity() {

    private lateinit var routeFrom: RouteFrom

    override fun onCreate(savedInstanceState: Bundle?) {
        if(BuildConfig.SCREEN_SHOOT_DISABLED) window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)

        val barcode = intent?.extras?.get(ExtrasKey.BARCODE.name) as BarcodeDto
        routeFrom = intent?.extras?.get(ExtrasKey.ROUTE_FROM.name) as RouteFrom

        if (barcode.image?.isNotEmpty() == true && barcode.text?.isNotEmpty() == true) {
            val bitmap = barcode.image.base64ToImage()
            barcodeImg.setImageBitmap(bitmap)
            barcodeValue.text = String.format(getString(R.string.barcode_value), barcode.text)
        } else if (barcode.number?.isNotEmpty() == true) {
            createBarcodeFromString(barcode.number.orEmpty())
        }

        initToolbar()
    }

    private fun createBarcodeFromString(barcode: String) {
        val metrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(metrics)
        val height = resources.getDimensionPixelSize(R.dimen.barcode_height)

        @Suppress("DEPRECATION")
        val barcodeRequest = BarcodeRequest.BarcodeRequestBuilder()
                .barcodeText(barcode)
                .barcodeFormat(BarcodeFormat.CODE_128)
                .width(metrics.widthPixels)
                .backgroundColor(Constants.BARCODE_BACKGROUND)
                .height(height)
                .build()
        val bitmap = BarcodeBitmapGenerator().generateCode128(barcodeRequest)
        barcodeImg.setImageBitmap(bitmap)
        barcodeValue.text = String.format(getString(R.string.barcode_value), barcode)
    }

    private fun initToolbar() {
        barcodeToolbar.setBackgroundColor(
                ContextCompat.getColor(this,
                        if (routeFrom == RouteFrom.PURCHASE) R.color.colorAccent else R.color.brown
                )
        )
        setSupportActionBar(barcodeToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            val icon = ContextCompat.getDrawable(this, R.drawable.ic_clear)
            it.setHomeAsUpIndicator(icon)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}