package merchant.mokka.ui.purchase.contract_ru

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_contract_ru.*
import merchant.mokka.Event
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.DocumentKind
import merchant.mokka.model.LoanData
import merchant.mokka.track
import merchant.mokka.utils.toTextWithCurrency
import merchant.mokka.utils.toTimerText
import merchant.mokka.utils.visible
import merchant.mokka.widget.KeyboardWidget
import merchant.mokka.widget.PinState
import merchant.mokka.widget.PinWidget

class ContractRuFragment : BaseFragment(), ContractRuView {

    companion object {
        fun getInstance(loan: LoanData) : ContractRuFragment {
            val fragment = ContractRuFragment()
            val bundle = Bundle()
            bundle.putSerializable(ExtrasKey.LOAN.name, loan)
            fragment.arguments = bundle
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: ContractRuPresenter

    @ProvidePresenter
    fun providePresenter() = ContractRuPresenter(injector)

    override val layoutResId = R.layout.fragment_contract_ru
    override val titleResId = R.string.contract_ru_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.ACCENT

    private lateinit var loan: LoanData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Event.LOAN_AGREE.track()
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        loan = arguments?.getSerializable(ExtrasKey.LOAN.name) as LoanData

        val tariff = loan.tariffs?.firstOrNull{ it.term_id == loan.termId }

        contractRuSum.text = tariff?.totalOfPayments?.toTextWithCurrency()
        contractRuOver.text = tariff?.totalOverpayment?.toTextWithCurrency()
        contractRuPayCount.text = (tariff?.schedule?.size ?: 0).toString()
        contractRuPayMonth.text = tariff?.monthlyPayment?.toTextWithCurrency()

        contractRuAgreeCheck.setOnClickListener { presenter.setCodeLayoutVisibility(contractRuAgreeCheck.isChecked) }

        for (i in 0..3) {
            contractRuCode.addView(PinWidget(context))
        }

        contractRuKeyboard.setPins(contractRuCode)
        contractRuKeyboard.setNextListener(nextListener)
        contractRuSendCodeAgain.setOnClickListener { presenter.sendCodeAgain(loan.token) }

        contractOfferButton.setOnClickListener {
            presenter.onOfferClick(loan, DocumentKind.OFFER.urlPart)
        }

        insuranceContainer.visible(false)
        insuranceContainer.setOnClickListener { presenter.onInsuranceInfoClick(loan) }
    }

    private val nextListener = object : KeyboardWidget.OnNextListener{
        override fun next() {
            presenter.onNextClick(loan, contractRuKeyboard.getValue())
        }
    }

    override fun setCodeLayoutVisibility(visibility: Boolean) {
        contractRuCodeLayout.visible(visibility)
        if (visibility) {
            contractRuScrollView.postDelayed({
                contractRuScrollView.smoothScrollTo(0, contractRuLayout.height)
            }, 300L)
        }
    }

    override fun showTimeInfo(time: Long?) {
        if (time != null) {
            contractRuSendCodeAgain.visibility = View.GONE
            contractRuTimerInfo.visibility = View.VISIBLE
            contractRuTimer.text = time.toTimerText()
        } else {
            contractRuSendCodeAgain.visibility = View.VISIBLE
            contractRuTimerInfo.visibility = View.GONE
        }
    }

    override fun setCodeValid(valid: Boolean) {
        (0..3).forEach { i ->
            val pinControl: PinWidget = contractRuCode.getChildAt(i) as PinWidget
            pinControl.setState(if (valid) PinState.VALID else PinState.ERROR)
        }
    }

    override fun clearCode() {
        (0..3).forEach { i ->
            val pinControl: PinWidget = contractRuCode.getChildAt(i) as PinWidget
            pinControl.setState(PinState.EMPTY)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_help_close, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.item_close -> {
                confirmShowDashboard { presenter.showDashboardScreen() }
                true
            }
            R.id.item_help -> {
                openHelp(toolbarStyle, "help_contract")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}