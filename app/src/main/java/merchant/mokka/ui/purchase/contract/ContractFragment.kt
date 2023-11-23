package merchant.mokka.ui.purchase.contract

import android.os.Bundle
import android.os.Handler
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.view.children
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_contract.*
import merchant.mokka.Event
import merchant.mokka.R
import merchant.mokka.common.BaseActivity
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.BaseRecyclerViewAdapter
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.DocumentData
import merchant.mokka.model.DocumentKind
import merchant.mokka.model.LoanData
import merchant.mokka.track
import merchant.mokka.utils.dpToPx
import merchant.mokka.utils.isBgLocale
import merchant.mokka.utils.isPlLocale
import merchant.mokka.utils.isRoLocale
import merchant.mokka.utils.toTextWithCurrency
import merchant.mokka.utils.toTimerText
import merchant.mokka.utils.visible
import merchant.mokka.widget.KeyboardWidget
import merchant.mokka.widget.PinState
import merchant.mokka.widget.PinWidget

class ContractFragment : BaseFragment(), ContractView {

    companion object {
        fun getInstance(loan: LoanData): ContractFragment {
            val fragment = ContractFragment()
            val args = Bundle()
            args.putSerializable(ExtrasKey.LOAN.name, loan)
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: ContractPresenter

    @ProvidePresenter
    fun providePresenter() = ContractPresenter(injector)

    override val layoutResId = R.layout.fragment_contract
    override val titleResId = R.string.contract_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.ACCENT

    private lateinit var adapter: BaseRecyclerViewAdapter<DocumentData>
    private lateinit var loan: LoanData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Event.LOAN_AGREE.track()
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        loan = arguments?.getSerializable(ExtrasKey.LOAN.name) as LoanData

        val tariff = loan.tariffs?.firstOrNull { it.term_id == loan.termId }

        contractRuSum.text =
            (tariff?.bnpl?.let { tariff.sumWithDiscount }
                ?: tariff?.totalOfPayments)?.toTextWithCurrency()
        contractRuOver.text =
            (tariff?.bnpl?.let { 0.0 } ?: tariff?.totalOverpayment)?.toTextWithCurrency()
        contractRuPayCount.text =
            (tariff?.bnpl?.let { 1 } ?: tariff?.schedule?.size ?: 0).toString()

        contractRuPayMonth.text =
            (tariff?.bnpl?.let { tariff.sumWithDiscount }
                ?: tariff?.monthlyPayment)?.toTextWithCurrency()

        setHasOptionsMenu(true)

        when {
            isPlLocale() -> fillPoland()
            isRoLocale() -> fillRomania()
            isBgLocale() -> fillBulgaria()
            else -> {}
        }

        val documents: MutableList<DocumentData>? = if (presenter.documents.orEmpty().size == 2)
            presenter.documents.orEmpty().sortedBy { it.checkable }.toMutableList()
        else presenter.documents

        with(view) {
            documents?.let {
                adapter = BaseRecyclerViewAdapter(
                    layout = R.layout.item_contract,
                    items = it,
                    holderFactory = { v ->
                        ContractHolder(
                            v,
                            client = loan.client
                        ) { validateChecks() }
                    },
                    onItemClick = { item, position ->
                        item.checked = true
                        adapter.notifyItemChanged(position)

                        val kind = loan.client?.let { c ->
                            val docKind = DocumentKind.values()
                                .firstOrNull { it.urlPart == item.kind.urlPart }
                                ?: DocumentKind.INDIVIDUAL

                            when {
                                c.isNewClient && docKind == DocumentKind.SECCI -> DocumentKind.SECCI_RCL_REGULAR.urlPart
                                c.isNewClient && docKind == DocumentKind.INDIVIDUAL -> DocumentKind.AGREEMENT_RCL_REGULAR.urlPart

                                c.isRepeated && c.rclAccepted && docKind == DocumentKind.SECCI -> DocumentKind.SECCI.urlPart
                                c.isRepeated && c.rclAccepted && docKind == DocumentKind.INDIVIDUAL -> DocumentKind.INDIVIDUAL_AGREEMENT.urlPart

                                c.isRepeated && !c.rclAccepted && docKind == DocumentKind.RCL -> DocumentKind.AGREEMENT_RCL.urlPart
                                c.isRepeated && !c.rclAccepted && docKind == DocumentKind.INDIVIDUAL_AGREEMENT -> DocumentKind.INDIVIDUAL_AGREEMENT.urlPart

                                else -> null
                            }

                        } ?: item.kind.urlPart

                        presenter.onItemClick(item.titleResId, loan, kind)
                    }
                )
                contractDocuments.layoutManager =
                    androidx.recyclerview.widget.LinearLayoutManager(context)
                contractDocuments.adapter = adapter
                contractDocuments.addItemDecoration(
                    androidx.recyclerview.widget.DividerItemDecoration(
                        context,
                        androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
                    )
                )

                contractDivider.visible(adapter.items.isNotEmpty())
            }

            for (i in 0..3) {
                contractCode.addView(PinWidget(context))
            }
            contractKeyboard.setPins(contractCode)
            contractKeyboard.setNextListener(nextListener)

            contractSendCodeAgain.setOnClickListener { presenter.sendCodeAgain(loan.token) }
        }

        validateChecks()

//        Скрыто в задаче #39
//        presenter.showInfoClientRepeatNoRcl(loan)
    }

    private fun fillRomania() {
        additionalTextView.visible(true)
        additionalTextView.setText(R.string.ro_confirm_3)

        if (loan.client?.rclAccepted != true && loan.tariff?.isRclProductKing == true) {
            presenter.documents = arrayListOf(
                DocumentData(
                    name = getString(R.string.ro_confirm_5),
                    titleResId = R.string.ro_confirm_5,
                    kind = DocumentKind.INDIVIDUAL_REGULAR_LOAN
                ),

                DocumentData(
                    name = getString(R.string.ro_confirm_6),
                    titleResId = R.string.ro_confirm_6,
                    kind = DocumentKind.AGREEMENT_RCL
                ),


                DocumentData(
                    name = getString(R.string.ro_confirm_1),
                    titleResId = R.string.confirm_read_loan_info_repeated_not_rcl_accepted_kind_rcl,
                    kind = DocumentKind.NONE,
                    isOptional = true
                ),

                DocumentData(
                    name = getString(R.string.ro_confirm_2),
                    titleResId = R.string.confirm_read_loan_info_rcl_accepted_checkable,
                    kind = DocumentKind.SECCI_RCL_REGULAR
                )
            )
        } else if (loan.client?.rclAccepted == true && loan.tariff?.isRclProductKing == true) {
            presenter.documents = arrayListOf(
                DocumentData(
                    name = getString(R.string.ro_confirm_6),
                    titleResId = R.string.ro_confirm_6,
                    kind = DocumentKind.AGREEMENT_RCL
                ),

                DocumentData(
                    name = getString(R.string.ro_confirm_2),
                    titleResId = R.string.ro_confirm_2,
                    kind = DocumentKind.SECCI_REGULAR_LOAN
                )
            )
        } else if (loan.tariff?.isFactoringProductKing == true) {
            presenter.documents = arrayListOf(
                DocumentData(
                    name = getString(R.string.ro_confirm_4),
                    titleResId = R.string.ro_confirm_4,
                    kind = DocumentKind.AGREEMENT_FACTORING,
                    checkable = true
                )
            )
        }
    }

    private fun fillBulgaria() {
        adminBgInfoView.visible(true)
        presenter.documents = arrayListOf(
            DocumentData(
                name = getString(R.string.contract_agree),
                titleResId = R.string.confirm_read_loan_info_repeated_not_rcl_accepted_kind_rcl,
                kind = DocumentKind.INDIVIDUAL_AGREEMENT
            ),
            DocumentData(
                name = getString(R.string.confirm_read_loan_info_rcl_accepted_checkable),
                titleResId = R.string.confirm_read_loan_info_rcl_accepted_checkable,
                kind = DocumentKind.PERSONAL_DATA
            )
        )
    }

    private fun fillPoland() {
        if (loan.client?.isRepeated == true && loan.client?.rclAccepted != true) {
            presenter.documents = arrayListOf(
                DocumentData(
                    name = getString(R.string.confirm_read_loan_info),
                    titleResId = R.string.confirm_read_loan_info,
                    kind = DocumentKind.SECCI_RCL_REGULAR,
                    checkable = false
                ),

                DocumentData(
                    name = getString(R.string.contract_agree),
                    titleResId = R.string.confirm_read_loan_info_repeated_not_rcl_accepted_kind_rcl,
                    kind = DocumentKind.RCL
                ),

                DocumentData(
                    name = getString(R.string.confirm_read_loan_info_rcl_accepted_checkable),
                    titleResId = R.string.confirm_read_loan_info_rcl_accepted_checkable,
                    kind = DocumentKind.INDIVIDUAL_AGREEMENT
                )
            )
        } else if (presenter.documents == null) {
            presenter.documents = arrayListOf(
                DocumentData(
                    name = getString(R.string.contract_agree),
                    titleResId = R.string.contract_agree_title,
                    kind = DocumentKind.INDIVIDUAL
                ),
                DocumentData(
                    name = getString(R.string.contract_secci),
                    titleResId = R.string.contract_secci_title,
                    kind = DocumentKind.SECCI,
                    checkable = false
                )
            )
        }
    }

    private fun validateChecks() {
        contractKeyboard.visible(isValid())
        if (isValid()) {
            contractScrollView.postDelayed(
                { contractScrollView.smoothScrollTo(0, contractContentLayout.height) },
                300L
            )
        }
    }

    private val nextListener = object : KeyboardWidget.OnNextListener {
        override fun next() {
            if (isValid())
                presenter.onNextClick(loan, contractKeyboard.getValue())
        }
    }

    private fun isValid(): Boolean {
        if (!::adapter.isInitialized) return false
        return with(adapter.items) {
            all { documentData -> documentData.checked || documentData.isOptional || !documentData.checkable }
        }
    }

    override fun setData(items: List<DocumentData>) {
        items.forEach {
            adapter.addItem(it)
        }
    }

    override fun showTimeInfo(time: Long?) {
        if (time != null) {
            contractSendCodeAgain.visibility = View.GONE
            contractTimerInfo.visibility = View.VISIBLE
            contractTimer.text = time.toTimerText()
        } else {
            contractSendCodeAgain.visibility = View.VISIBLE
            contractTimerInfo.visibility = View.GONE
        }
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
                openHelp(toolbarStyle, "help_contract")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setCodeValid(valid: Boolean) {
        contractCode.children.filterIsInstance<PinWidget>()
            .forEach { it.setState(if (valid) PinState.VALID else PinState.EMPTY) }
    }

    override fun showInfoClientRepeatNoRcl() {
        Handler().postDelayed({
            val view = TextView(requireActivity()).apply {
                val paddingVertical = requireActivity().dpToPx(16)
                val paddingHorizontal = requireActivity().dpToPx(24)

                text = HtmlCompat.fromHtml(
                    getString(R.string.loan_info_repeated_not_rcl_accepted_message),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                movementMethod = LinkMovementMethod.getInstance()

                setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, 0)
                setLineSpacing(requireActivity().dpToPx(2).toFloat(), 1.0f)
            }

            (requireActivity() as BaseActivity).alert(
                title = getString(R.string.loan_info_repeated_not_rcl_accepted_title),
                view = view,
                positiveButtonResId = R.string.button_close
            )
        }, 1_000)
    }

}