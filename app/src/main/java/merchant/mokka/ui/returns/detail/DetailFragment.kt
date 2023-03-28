package merchant.mokka.ui.returns.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_detail.*
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.ReturnData
import merchant.mokka.model.SearchData
import merchant.mokka.utils.*
import merchant.mokka.widget.EditSumValidator
import merchant.mokka.widget.attachSumValidator
import merchant.mokka.widget.detachSumValidator
import merchant.mokka.utils.*

class DetailFragment : BaseFragment(), DetailView {

    companion object {
        fun getInstance(data: SearchData) : DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putSerializable(ExtrasKey.SEARCH.name, data)
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter : DetailPresenter

    @ProvidePresenter
    fun providePresenter() = DetailPresenter(injector)

    override val layoutResId = R.layout.fragment_detail
    override val titleResId = R.string.detail_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.DARK

    private lateinit var data: SearchData
    private lateinit var sumValidator: EditSumValidator

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        data = arguments?.getSerializable(ExtrasKey.SEARCH.name) as SearchData

        detailClient.text = data.client
        detailPhone.text = if(isBgLocale()) "+"+getString(R.string.phone_prefix)+data.phone else data.phone
        detailTotalSum.text = data.amount.toTextWithCent()
        detailRemainingSum.text = (data.remainingAmount - data.returnSum).toTextWithCent()

        sumValidator = EditSumValidator(
                validator = {
                    data.returnSum = getSum()
                    detailFullReturn.isChecked = it == data.remainingAmount
                    val remaining = data.remainingAmount - it
                    detailRemainingSum.text = (if (remaining < 0.0) 0.0 else remaining).toTextWithCent()
                    isSumValid(it)
                },
                onChangedState = { validate() },
                errorText = getString(R.string.error_sum)
        )

        detailFullReturn.setOnClickListener {
            if (detailFullReturn.isChecked) {
                detailReturnSum.setText(data.remainingAmount.toText())
            } else {
                detailReturnSum.text.clear()
            }
        }

        detailNextBtn.setOnClickListener {
            if (isSumValid(getSum())) {
                data.returnSum = getSum()
                presenter.sendConfirmCode(
                        ReturnData(
                                clientName = data.client,
                                purchaseSum = data.amount,
                                returnSum = getSum(),
                                orderId = data.id
                        )
                )
            }
        }
    }

    private fun getSum() = detailReturnSum.text.toString().parse()

    private fun isSumValid(sum: Double) : Boolean {
        return sum > 0.0 && sum <= data.remainingAmount
    }

    private fun validate() {
        val sum = getSum()
        if (sum > data.remainingAmount)
            detailSumError.visibility = View.VISIBLE
        else
            detailSumError.visibility = View.GONE
        detailNextBtn.alpha = isSumValid(sum).toAlpha()

    }

    override fun onResume() {
        super.onResume()
        detailReturnSum.attachSumValidator(sumValidator, null, detailReturnText)
        detailReturnSum.setText(data.returnSum.toTextWithCurrency())
        detailReturnText.text = data.returnSum.toTextWithCurrency()
        validate()
    }

    override fun onPause() {
        super.onPause()
        detailReturnSum.detachSumValidator(sumValidator)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_help_close, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.item_close -> {
                alert(
                        title = "",
                        message = requireContext().getString(R.string.dashboard_confirm_return),
                        positive = { presenter.showDashboardScreen() },
                        negative = { }
                )
                true
            }
            R.id.item_help -> {
                openHelp(toolbarStyle, "help_detail")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}