package merchant.mokka.ui.purchase.purchase

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_purchase.*
import merchant.mokka.BuildConfig
import merchant.mokka.Event
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.track
import merchant.mokka.utils.FormatTextWatcher
import merchant.mokka.utils.clearPhone
import merchant.mokka.utils.createPhoneMaskFormatWatcher
import merchant.mokka.utils.decoro.watchers.MaskFormatWatcher
import merchant.mokka.utils.enable
import merchant.mokka.utils.isBgLocale
import merchant.mokka.utils.isPlLocale
import merchant.mokka.utils.isRuLocale
import merchant.mokka.utils.isValid
import merchant.mokka.utils.setTextWithLinkSupport
import merchant.mokka.utils.toAlpha
import merchant.mokka.utils.visible

class PurchaseFragment : BaseFragment(), PurchaseView {

    companion object {
        fun getInstance() = PurchaseFragment()
    }

    @InjectPresenter
    lateinit var presenter: PurchasePresenter

    @ProvidePresenter
    fun providePresenter() = PurchasePresenter(injector)

    override val layoutResId = R.layout.fragment_purchase
    override val titleResId = R.string.purchase_title
    override val homeIconType = HomeIconType.MENU
    override val toolbarStyle = ToolbarStyle.ACCENT

    private lateinit var phoneFormatWatcher: MaskFormatWatcher
    private lateinit var phonePrefix: String
    private lateinit var phoneMask: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Event.LIMIT_CHECK.track()
        presenter.create()
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        phoneMask = getString(R.string.phone_mask)

        phoneFormatWatcher = createPhoneMaskFormatWatcher(purchasePhone, phoneMask)
        phonePrefix = getString(
            if(BuildConfig.ENV == "production") R.string.phone_prefix
            else R.string.phone_prefix_staging
        )

        when {
            presenter.phone != null -> purchasePhone.setText(presenter.phone)
            presenter.phone == null && BuildConfig.ENV == "production" -> purchasePhone.setText(R.string.phone_empty)
            presenter.phone == null && BuildConfig.ENV == "staging" -> purchasePhone.setText(R.string.phone_empty_staging)
        }

        validate(false)

        purchaseNextBtn.setOnClickListener { submit() }
        purchaseInfo.setOnClickListener {
            if (isPlLocale() || isBgLocale())
                openHelp(toolbarStyle, "article_inform", getString(R.string.purchase_help_title))
        }

        purchaseSelfRegistration.visible(isRuLocale())
        purchaseSelfRegistration.setOnClickListener {
            presenter.showSelfRegistration(purchasePhone.text.toString().clearPhone())
        }

        if (BuildConfig.DEBUG) {
            when {
                isPlLocale() -> "+48333999112"
                else -> null
            }?.let {
                purchasePhone.setText(it)
                presenter.phone = it
                validate(true)
            }

            purchaseNextBtn.enable(true)
        }

        puchaseInfoView.movementMethod = LinkMovementMethod.getInstance()

        puchaseInfoView.setTextWithLinkSupport(getString(R.string.purchase_info)){
            presenter.clickOnLink(it)
        }
    }

    override fun onResume() {
        super.onResume()
        phoneFormatWatcher.setCallback(FormatTextWatcher { text ->
            if (text.isEmpty() && isBgLocale()) purchasePhone.setText(getString(R.string.phone_prefix))
            presenter.phone = text
            validate(true)
        })
    }

    override fun onPause() {
        super.onPause()

        phoneFormatWatcher.setCallback(null)
    }

    private fun validate(showErrors: Boolean) {
        if (presenter.phone.isNullOrEmpty() || presenter.phone?.clearPhone() == phonePrefix) {
            presenter.valid = false
            purchasePhoneLayout.error = if (showErrors) getString(R.string.error_field_required) else null
        } else {
            presenter.valid = presenter.isDemo() || phoneFormatWatcher.mask.isValid()
            purchasePhoneLayout.error = if (presenter.valid) null else if (showErrors) getString(R.string.error_phone) else null
        }
        purchaseNextBtn.alpha = presenter.valid.toAlpha()
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
                openHelp(toolbarStyle, "help_purchase")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed(): Boolean {
        confirmShowDashboard { presenter.showDashboardScreen() }
        return true
    }

    private fun submit() {
        if (presenter.valid)
            presenter.createLoan(purchasePhone.text.toString().clearPhone())
    }
}