package merchant.mokka.ui.returns.barcode

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_return_barcode.*
import merchant.mokka.common.*
import merchant.mokka.R
import merchant.mokka.common.*
import merchant.mokka.model.ReturnData
import merchant.mokka.ui.purchase.barcode.BarcodeActivity
import merchant.mokka.utils.*
import merchant.mokka.utils.*
import merchant.mokka.utils.barcode.BarcodeBitmapGenerator
import merchant.mokka.utils.barcode.BarcodeFormat
import merchant.mokka.utils.barcode.BarcodeRequest

class BarcodeReturnFragment : BaseFragment(), BarcodeReturnView {

    companion object {
        fun getInstance(returnData: ReturnData): BarcodeReturnFragment {
            val fragment = BarcodeReturnFragment()
            val args = Bundle()
            args.putSerializable(ExtrasKey.RETURN.name, returnData)
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: BarcodeReturnPresenter

    @ProvidePresenter
    fun providePresenter() = BarcodeReturnPresenter(injector)

    override val layoutResId = R.layout.fragment_return_barcode
    override val titleResId = R.string.barcode_title
    override val homeIconType = HomeIconType.MENU
    override val toolbarStyle = ToolbarStyle.DARK

    private lateinit var data: ReturnData

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        data = arguments?.getSerializable(ExtrasKey.RETURN.name) as ReturnData

        val purchase = data.purchaseSum.toTextWithCent()
        val returnSum = data.returnSum.toTextWithCent()
        barcodeTotalSum.text = String.format(
                getString(R.string.barcode_return_sum), purchase, returnSum
        )

        if (data.barcode?.image?.isNotEmpty() == true && data.barcode?.text?.isNotEmpty() == true) {
            createBarcodeFromImage(data.barcode?.image.orEmpty(), data.barcode?.text.orEmpty())
        } else if (data.barcode?.number?.isNotEmpty() == true) {
            createBarcodeFromString(data.barcode?.number.orEmpty())
        }

        barcodeNextBtn.setOnClickListener { presenter.confirmReturn(data) }
        barcodeCancelBtn.setOnClickListener { presenter.cancelReturn(data) }

        barcodeImg.setOnClickListener {
            data.barcode.let { barcode ->
                val intent = Intent(context, BarcodeActivity::class.java)
                intent.putExtra(ExtrasKey.BARCODE.name, barcode)
                intent.putExtra(ExtrasKey.ROUTE_FROM.name, RouteFrom.RETURN)
                startActivity(intent)
            }
        }

        titleview.visible(data.barcode?.isEmpty() == false)
    }

    private fun createBarcodeFromImage(image: String, text: String) {
        val bitmap = image.base64ToImage()
        barcodeImg.setImageBitmap(bitmap)
        barcodeValue.text = String.format(getString(R.string.barcode_value), text.barCodeFormat())
    }

    private fun createBarcodeFromString(barcode: String) {
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_help, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.item_help -> {
                openHelp(toolbarStyle, "help_barcode_return")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}