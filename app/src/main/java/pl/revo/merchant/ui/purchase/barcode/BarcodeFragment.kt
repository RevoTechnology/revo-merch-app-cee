package pl.revo.merchant.ui.purchase.barcode

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_barcode.*
import pl.revo.merchant.Event
import pl.revo.merchant.R
import pl.revo.merchant.common.*
import pl.revo.merchant.model.FinalizeDto
import pl.revo.merchant.model.FinalizeInputDto
import pl.revo.merchant.track
import pl.revo.merchant.utils.*

class BarcodeFragment : BaseFragment(), BarcodeView {

    companion object {
        fun getInstance(finalize: FinalizeDto): BarcodeFragment {
            val fragment = BarcodeFragment()
            fragment.setArguments(ExtrasKey.BARCODE, finalize)
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: BarcodePresenter

    @ProvidePresenter
    fun providePresenter() = BarcodePresenter(injector)

    override val layoutResId = R.layout.fragment_barcode
    override val titleResId = R.string.barcode_title
    override val homeIconType = HomeIconType.MENU
    override val toolbarStyle = ToolbarStyle.ACCENT

    private val finalize by lazy { arguments?.getSerializable(ExtrasKey.BARCODE.name) as FinalizeDto }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Event.FINALIZE.track()
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        barcodeTotalSum.text = getString(R.string.barcode_sum, finalize.loan.sum.toTextWithCent())

        when (finalize.input.type) {
            FinalizeInputDto.Type.INPUT -> createFinalizeInput(title = finalize.input.text)
            FinalizeInputDto.Type.NO_INPUT -> createFinalizeNoInput(title = finalize.input.text)
            else -> createBarcodes()
        }

        barcodeNextBtn.setOnClickListener {
            presenter.finalize(
                    loan = finalize.loan,
                    type = finalize.input.type,
                    code = input.text.toString(),
                    credentials = finalize.credentialsLamoda,
                    payload = finalize.payloadLamoda
            )
        }
        barcodeDemo.visible(presenter.isDemo())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_help, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_help -> {
                openHelp(toolbarStyle, "help_barcode")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed(): Boolean {
        presenter.onFinish()
        return true
    }

    private fun createFinalizeInput(title: String?) {
        textInputLayout.visible(true)
        titleview.text = catchAll(
                action = { String.format(getString(R.string.barcode_result_success), title) },
                onError = { title })



        input.setOnEditorActionListener { _, actionId, event ->
            val isEnter: Boolean = actionId == EditorInfo.IME_ACTION_DONE
            if (isEnter) {
                activity.hideSoftKeyboard()
                barcodeNextBtn.performClick()
            }

            true
        }


        input.postDelayed({
            input.requestFocus()
            activity?.showKeyboard()
        }, 1_000)
    }

    private fun createFinalizeNoInput(title: String?) {
        textInputLayout.visible(false)
        titleview.text = catchAll(
                action = { String.format(getString(R.string.barcode_result_success), title ?: "") },
                onError = { title })
    }

    private fun createBarcodes() {
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        barcodePager.adapter = BarcodePageAdapter(
                items = finalize.barcode,
                barcodeWidth = metrics.widthPixels,
                barcodeHeight = resources.getDimensionPixelSize(R.dimen.barcode_height),
                onItemClick = {
                    val intent = Intent(context, BarcodeActivity::class.java)
                    intent.putExtra(ExtrasKey.BARCODE.name, it)
                    intent.putExtra(ExtrasKey.ROUTE_FROM.name, RouteFrom.PURCHASE)
                    startActivity(intent)
                }
        )
        barcodeTabLayout.setupWithViewPager(barcodePager)
        barcodeTabLayout.visible(finalize.barcode.size > 1)
        barcodePager.visible(true)
        titleview.visible(finalize.barcode.any { !it.isEmpty() })
        textInputLayout.visible(false)
    }

    override fun lamodaResult(result: String) {
        val intent = Intent().putExtra("json_data", result)
        activity?.setResult(RESULT_OK, intent)
        activity?.finish()
    }
}