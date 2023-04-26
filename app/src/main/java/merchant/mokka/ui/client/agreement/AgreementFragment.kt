package merchant.mokka.ui.client.agreement

import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_agreement.*
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.LoanData
import merchant.mokka.utils.isBgLocale
import merchant.mokka.utils.isPlLocale
import merchant.mokka.utils.isRoLocale
import merchant.mokka.utils.toAlpha
import merchant.mokka.utils.toText
import merchant.mokka.utils.visible

class AgreementFragment : BaseFragment(), AgreementView {

    companion object {
        fun getInstance(loan: LoanData): AgreementFragment {
            val fragment = AgreementFragment()
            val bundle = Bundle()
            bundle.putSerializable(ExtrasKey.LOAN.name, loan)
            fragment.arguments = bundle
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: AgreementPresenter

    @ProvidePresenter
    fun providePresenter() = AgreementPresenter(injector)

    override val layoutResId = R.layout.fragment_agreement
    override val titleResId = R.string.agreement_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.ACCENT

    private lateinit var loan: LoanData

    override fun initView(view: View, savedInstanceState: Bundle?) {
        loan = arguments?.getSerializable(ExtrasKey.LOAN.name) as LoanData
        if (presenter.agrees == null)
            presenter.agrees = loan.agrees

        setHasOptionsMenu(true)

        when {
            isPlLocale() -> {
                agreementCreditBureausCheckBox.isChecked = presenter.agrees?.creditBureaus == "1"
                agreementMarketingEmailCheckBox.isChecked = presenter.agrees?.marketingEmail == "1"
            }
            isRoLocale() -> {
                agreementCreditBureausCheckBox.isChecked = presenter.agrees?.creditBureaus == "1"
                agreementMarketingEmailCheckBox.isChecked =
                    presenter.agrees?.personalDataMarketingAll == "1"
            }
            isBgLocale() -> {
                dividerView.visible(false)
                bgCheckboxLayout.visible(true)
                agreementCreditBureausCheckBox.setTypeface(null, Typeface.NORMAL)
                agreementCreditBureausCheckBox.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.steel
                    )
                )
                agreementCreditBureausCheckBox.isChecked = presenter.agrees?.creditBureaus == "1"
                agreementMarketingEmailCheckBox.isChecked = presenter.agrees?.marketingEmail == "1"
            }
        }

        agreementAll.setOnClickListener {
            agreementAllCheckBox.isChecked = !agreementAllCheckBox.isChecked
            setAllChecked(agreementAllCheckBox.isChecked)
        }

        agreementAllCheckBox.setOnClickListener {
            setAllChecked(agreementAllCheckBox.isChecked)
        }

        agreementCreditBureausCheckBox.setOnClickListener {
            if (isBgLocale()) {
                presenter.agrees?.personalData = agreementCreditBureausCheckBox.isChecked.toText()
            } else {
                presenter.agrees?.creditBureaus = agreementCreditBureausCheckBox.isChecked.toText()
            }
            validateChecks()
        }

        agreementCreditBureausImageView.setOnClickListener {
            val kind = if (isBgLocale()) "regulations" else "personal_data_marketing_all"
            presenter.onItemClick(R.string.agreement_credit_bureaus_title, loan, kind)
        }

        agreementMarketingEmailCheckBox.setOnClickListener {
            if (isBgLocale()) {
                presenter.agrees?.regulations = agreementMarketingEmailCheckBox.isChecked.toText()
            } else {
                presenter.agrees?.marketingEmail = agreementMarketingEmailCheckBox.isChecked.toText()
            }
            validateChecks()
        }
        agreementMarketingEmailImageView.setOnClickListener {
            val kind = if (isBgLocale()) "personal_data" else "credit_bureaus"
            presenter.onItemClick(R.string.agreement_marketing_email_title, loan, kind)
        }

        agreementPersonalDataCheckBox.setOnClickListener {
            presenter.agrees?.personalDataMarketingAll = agreementPersonalDataCheckBox.isChecked.toText()
            validateChecks()
        }

        agreementPersonalDataImageView.setOnClickListener {
            presenter.onItemClick(
                R.string.agreement_SMS_title,
                loan,
                "personal_data_marketing_all"
            )
        }

        agreementNextBtn.setOnClickListener {
            if (isModelValid()) {
                with(loan.agrees) {
                    when {
                        isPlLocale() -> {
                            creditBureaus = agreementCreditBureausCheckBox.isChecked.toText()
                            marketingEmail = agreementMarketingEmailCheckBox.isChecked.toText()
                        }
                        isRoLocale() -> {
                            creditBureaus = agreementCreditBureausCheckBox.isChecked.toText()
                            personalDataMarketingAll =
                                agreementMarketingEmailCheckBox.isChecked.toText()
                        }
                        isBgLocale() -> {
                            creditBureaus = agreementCreditBureausCheckBox.isChecked.toText()
                            marketingEmail = agreementMarketingEmailCheckBox.isChecked.toText()
                        }
                    }
                }

                presenter.onNextClick(loan)
            }
        }

        validateChecks()
    }

    private fun setAllChecked(checked: Boolean) {
        when {
            isPlLocale() -> {
                presenter.agrees?.regulations = checked.toText()
                presenter.agrees?.personalData = checked.toText()
                presenter.agrees?.creditBureaus = checked.toText()
                presenter.agrees?.marketingEmail = checked.toText()
            }
            isRoLocale() -> {
                presenter.agrees?.regulations = checked.toText()
                presenter.agrees?.taxOffice = checked.toText()
                presenter.agrees?.creditBureaus = checked.toText()
                presenter.agrees?.personalDataMarketingAll = checked.toText()
            }
            isBgLocale() -> {
                presenter.agrees?.personalData = checked.toText()
                presenter.agrees?.regulations = checked.toText()
                presenter.agrees?.personalDataMarketingAll = checked.toText()
            }
        }

        agreementAllCheckBox.isChecked = checked
        agreementCreditBureausCheckBox.isChecked = checked
        agreementMarketingEmailCheckBox.isChecked = checked
        agreementPersonalDataCheckBox.isChecked = checked
        agreementNextBtn.alpha = checked.toAlpha()
    }

    private fun isModelValid(): Boolean = if (isBgLocale()) {
        agreementCreditBureausCheckBox.isChecked && agreementMarketingEmailCheckBox.isChecked
    } else agreementCreditBureausCheckBox.isChecked

    private fun validateChecks() {
        agreementNextBtn.alpha = isModelValid().toAlpha()
        validateAgreementAllCheckBox()
    }

    private fun validateAgreementAllCheckBox() {
        agreementAllCheckBox.isChecked =  if (isBgLocale()) {
            agreementCreditBureausCheckBox.isChecked && agreementMarketingEmailCheckBox.isChecked && agreementPersonalDataCheckBox.isChecked
        } else agreementCreditBureausCheckBox.isChecked && agreementMarketingEmailCheckBox.isChecked
    }

    override fun onResume() {
        super.onResume()
        validateChecks()
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
                openHelp(toolbarStyle, "help_consents")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}