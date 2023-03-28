package merchant.mokka.ui.purchase.calculator


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_calculator.*
import merchant.mokka.Event
import merchant.mokka.R
import merchant.mokka.api.response.TariffClientSmsInfoData
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.LoanData
import merchant.mokka.model.TariffData
import merchant.mokka.pref.Prefs
import merchant.mokka.track
import merchant.mokka.utils.*
import merchant.mokka.widget.EditSumValidator
import merchant.mokka.widget.attachSumValidator
import merchant.mokka.widget.detachSumValidator
import merchant.mokka.utils.*

class CalculatorFragment : BaseFragment(), CalculatorView {

    companion object {
        fun getInstance(loan: LoanData): CalculatorFragment {
            val fragment = CalculatorFragment()
            val args = Bundle()
            args.putSerializable(ExtrasKey.LOAN.name, loan)
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: CalculatorPresenter

    @ProvidePresenter
    fun providePresenter() = CalculatorPresenter(injector)

    override val layoutResId = R.layout.fragment_calculator
    override val titleResId = R.string.calc_title
    override val homeIconType = HomeIconType.NONE
    override val toolbarStyle = ToolbarStyle.ACCENT

    private lateinit var adapter: CalculatorAdapter
    private lateinit var loan: LoanData
    private lateinit var sumValidator: EditSumValidator

    private var expandedItem: Int? = null
    private var isStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Event.LIMIT_SCREEN.track()
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        loan = arguments?.getSerializable(ExtrasKey.LOAN.name) as LoanData
        presenter.initialSum = loan.sum

        setHasOptionsMenu(true)

        sumValidator = EditSumValidator(
                validator = { sum ->
                    validate()
                    validateSum(sum)
                },
                onChangedState = { validate() },
                errorText = getString(R.string.error_sum)
        )

        adapter = CalculatorAdapter(
                items = mutableListOf(),
                inflater = layoutInflater,
                onItemClick = { item, position ->
                    expandedItem = position

                    adapter.items.forEach {
                        if (it != item) {
                            it.expanded = false
                            it.selected = false
                        }
                    }

                    adapter.notifyDataSetChanged()
                    validate()
                }
        )

        calcList.adapter = adapter
        calcList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        calcNextBtn.setOnClickListener {
            if (isValidModel()) {
                val selected = adapter.items.first { loan -> loan.selected }
                presenter.onNextClick(loan, selected.term_id, calcSmsSwh.isChecked)
            }
        }

        calcList.adapter = adapter
        calcList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        calcNextBtn.setOnClickListener {
            if (isValidModel()) {
                val selected = adapter.items.first { loan -> loan.selected }
                presenter.onNextClick(loan, selected.term_id, calcSmsSwh.isChecked)
            }
        }

        calcLimit.text = loan.client?.creditLimit.toTextWithCent()
        calcSum.setText(loan.sum.toText())
        calcSumText.text = loan.sum.toTextWithCurrency()
        calcRefresh.setOnClickListener { refreshSum() }
        insuranceSwitcher.setOnCheckedChangeListener { _, _ -> refreshSum() }

        with(loan.client) {
            if (!isPlLocale() && !isBgLocale()) return@with null
            when {
                this == null -> null
                else -> getString(R.string.calc_limit)
            }
        }?.let { titleView.text = it }

        validate()

        // since fragment can be reinstantiate by system because of fragmentManager.popBack()
        // can cause call fragment.oncreateview again
        // we have to keep isStarted state
        if (!isStarted) {
            presenter.start(loan)
            isStarted = true
        }

        helperTextView.addImage(
            atText = getString(R.string.calc_input_sum),
            imgRes = R.drawable.ic_refresh_small,
            imgWidth = requireContext().dpToPx(16),
            imgHeight = requireContext().dpToPx(16)
        )

        calcSmsInfoView.setOnClickListener {
            alert(
                message = getString(R.string.calc_enable_sms_inform_info)
            )
        }
    }

    override fun setData(tariffData: List<TariffData>?, clientSmsInfoData: TariffClientSmsInfoData?) {
        if (loan.sum == 0.0) {
            calcPeriodHint.visible(false)
            helperTextView.visible(true)

            adapter.items.clear()
        } else {
            if (adapter.items.isEmpty()) Event.LOAN_CALC.track()

            calcPeriodHint.visible(true)
            helperTextView.visible(visible = false, isInvisible = true)

            adapter.items.clear()
            tariffData?.let { list ->
                val expandedPosition = when {
                    expandedItem != null && expandedItem.orZero() < list.size -> expandedItem
                    list.size == 1 -> 0
                    else -> null
                }

                expandedPosition?.let {
                    try {
                        list[it].selected = true
                        list[it].expanded = true
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                list.forEach {
                    it.currentSum = getSum()
                    adapter.addItem(it)
                }
            }
        }

        val hideSmsLayout = !clientSmsInfoData?.available.orFalse() || clientSmsInfoData?.subscribed.orFalse()
        calcSmsLayout.visible(!hideSmsLayout)
        calcSmsView.text = getString(R.string.calc_enable_sms_inform, clientSmsInfoData?.price?.toTextWithCent().orEmpty())
        calcSmsSwh.isChecked = clientSmsInfoData?.subscribed.orFalse()

        val insuranceVisible = isRuLocale() && loan.sum > 0 && loan.insuranceAvailable
        calcInsuranceLayout.visible(insuranceVisible)

        validate()
    }

    override fun refreshSum() {
        if (isValidSum(getSum()) == 0)
            presenter
                    .refreshSum(loan = loan,
                            sum = getSum(),
                            agreeInsurance = if (!calcInsuranceLayout.isVisible()) null else insuranceSwitcher.isChecked)
    }

    override fun lockSumAndRefresh() {
        calcSum.enable(false)
        calcSumText.enable(false)
        calcRefresh.enable(false)

        refreshSum()
    }

    private fun isValidModel(): Boolean {
        helperTextView.visible(getSum() == 0.0, isInvisible = true)
        return adapter.items.any { loan -> loan.selected } &&
                validateSum(getSum()) &&
                presenter.initialSum == getSum()
    }

    private fun getMax(): Double {
        var max = if (loan.client != null) loan.client?.creditLimit else loan.client?.creditLimit
        val storeMax = Prefs.tariffMax
        if (max == null || max > storeMax)
            max = storeMax
        return max
    }

    private fun isValidSum(sum: Double) =
            when {
                sum > getMax() -> 1
                sum < Prefs.tariffMin -> -1
                else -> 0
            }

    private fun validateSum(sum: Double): Boolean {
        val validValue = isValidSum(sum)

        calcSumError.text = when (validValue) {
            1 -> getString(R.string.error_purchase_exceeds, getMax().toTextWithCent())
            -1 -> getString(R.string.error_purchase_small)
            else -> ""
        }

        adapter.items.forEach { it.currentSum = sum }
        adapter.notifyDataSetChanged()

        return validValue == 0
    }

    private fun validate() {
        val color = if (validateSum(getSum())) R.color.colorAccent else R.color.gray_d8dbe1

        calcNextBtn.alpha = isValidModel().toAlpha()
        calcRefresh.setBackgroundColor(ContextCompat.getColor(requireContext(), color))
    }

    override fun onResume() {
        super.onResume()
        calcSum.attachSumValidator(sumValidator, null, calcSumText)
    }

    override fun onPause() {
        super.onPause()
        calcSum.detachSumValidator(sumValidator)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_help_close, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_close -> {
                confirmShowDashboard { presenter.showDashboardScreen() }
                true
            }
            R.id.item_help -> {
                openHelp(toolbarStyle, "help_calculator")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getSum() = calcSum.text.toString().parse()
    override fun onBackPressed(): Boolean {
        confirmShowDashboard { presenter.showDashboardScreen() }
        return true
    }
}