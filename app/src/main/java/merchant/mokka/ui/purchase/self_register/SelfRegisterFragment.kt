package merchant.mokka.ui.purchase.self_register

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_self_register.*
import merchant.mokka.Event
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.track
import merchant.mokka.utils.FormatTextWatcher
import merchant.mokka.utils.clearPhone
import merchant.mokka.utils.createPhoneMaskFormatWatcher
import merchant.mokka.utils.isBgLocale
import merchant.mokka.utils.isValid
import merchant.mokka.utils.toAlpha

class SelfRegisterFragment : BaseFragment(), SelfRegisterView {

    companion object {
        fun getInstance(phone: String) : SelfRegisterFragment {
            val fragment = SelfRegisterFragment()
            val args = Bundle()
            args.putSerializable(ExtrasKey.PHONE.name, phone)
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: SelfRegisterPresenter

    @ProvidePresenter
    fun providePresenter() = SelfRegisterPresenter(injector)

    override val layoutResId = R.layout.fragment_self_register
    override val titleResId = R.string.purchase_title
    override val homeIconType = HomeIconType.MENU
    override val toolbarStyle = ToolbarStyle.ACCENT

    private val phoneFormatWatcher by lazy { createPhoneMaskFormatWatcher(purchasePhone, getString(R.string.phone_mask)) }

    private lateinit var phonePrefix : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Event.SELF_REG_MAIN.track()
        presenter.phone = arguments?.getString(ExtrasKey.PHONE.name)
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        phonePrefix = getString(R.string.phone_prefix)

        purchaseNextBtn.setOnClickListener {
            if (presenter.valid) {
                presenter.createLoan()
            }
        }

        if (presenter.isDemo())
            purchaseDemo.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        phoneFormatWatcher.setCallback(FormatTextWatcher { text ->
            if (text.isEmpty() && isBgLocale()) purchasePhone.setText(getString(R.string.phone_prefix))
            presenter.phone = text
            validate(true)
        })

        if (presenter.phone.isNullOrEmpty() || presenter.phone == phonePrefix)
            purchasePhone.setText(R.string.phone_empty)
        else
            purchasePhone.setText(presenter.phone)
        validate(false)
    }

    override fun onPause() {
        super.onPause()

        phoneFormatWatcher.setCallback(null)
    }

    private fun validate(showErrors: Boolean) {
        if (presenter.phone.isNullOrEmpty() || presenter.phone?.clearPhone() == phonePrefix) {
            presenter.valid = false
            if (showErrors)
                purchasePhoneLayout.error = getString(R.string.error_field_required)
            else
                purchasePhoneLayout.error = null
        } else {
            presenter.valid = presenter.isDemo() || phoneFormatWatcher.mask.isValid()
            if (presenter.valid) {
                purchasePhoneLayout.error = null
            } else {
                if (showErrors)
                    purchasePhoneLayout.error = getString(R.string.error_phone)
                else
                    purchasePhoneLayout.error = null
            }
        }
        purchaseNextBtn.alpha = presenter.valid.toAlpha()
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
                openHelp(toolbarStyle, "help_selfregistry")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed(): Boolean {
        confirmShowDashboard { presenter.showDashboardScreen() }
        return true
    }

    override fun onSuccess() {
        alert(
                title = getString(R.string.self_register_success_title),
                message = getString(R.string.self_register_success_message),
                positive = {  presenter.showDashboardScreen() }
        )
    }

    override fun onRetryClient() {
        alert(
                title = getString(R.string.self_register_retry_client_title),
                message = getString(R.string.self_register_retry_client_message),
                positive = {
                    presenter.showConfirmScreen()
                },
                cancelable = false
        )
    }
}