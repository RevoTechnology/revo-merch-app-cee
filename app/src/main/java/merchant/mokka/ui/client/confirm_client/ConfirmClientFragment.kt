package merchant.mokka.ui.client.confirm_client

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.children
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_confirm_client.*
import merchant.mokka.Event
import merchant.mokka.R
import merchant.mokka.api.error.ApiErr
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.ClientData
import merchant.mokka.model.LoanData
import merchant.mokka.track
import merchant.mokka.ui.root.RootActivity
import merchant.mokka.utils.toTimerText
import merchant.mokka.widget.KeyboardWidget
import merchant.mokka.widget.PinState
import merchant.mokka.widget.PinWidget

class ConfirmClientFragment : BaseFragment(), ConfirmClientView {

    companion object {
        fun getInstance(loan: LoanData): ConfirmClientFragment {
            val fragment = ConfirmClientFragment()
            val args = Bundle()
            args.putSerializable(ExtrasKey.LOAN.name, loan)
            fragment.arguments = args
            return fragment
        }
    }

    override val layoutResId = R.layout.fragment_confirm_client
    override val titleResId = R.string.confirm_client_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.ACCENT

    @InjectPresenter
    lateinit var presenter: ConfirmClientPresenter

    @ProvidePresenter
    fun providePresenter() = ConfirmClientPresenter(injector)

    private lateinit var loan: LoanData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Event.SMS_CONFIRM.track()
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        loan = arguments?.getSerializable(ExtrasKey.LOAN.name) as LoanData

        with(view) {
            if (loan.isNewClient) {
                confirmSmsHint.text = getString(R.string.confirm_new_client_hint)
                (activity as RootActivity).setTitle(R.string.agreement_title)
            } else {
                confirmSmsHint.text = getString(R.string.confirm_client_hint)
                (activity as RootActivity).setTitle(R.string.confirm_client_title)
            }

            confirmClient.text = loan.client?.fullNameWithPatronymic.orEmpty()

            for (i in 0..3) {
                confirmCode.addView(PinWidget(context))
            }

            confirmKeyboard.setPins(confirmCode)
            confirmKeyboard.setNextListener(nextListener)
            confirmSendCodeAgain.setOnClickListener { presenter.sendCodeAgain(loan.token) }

            if (presenter.isDemo())
                confirmDemo.visibility = View.VISIBLE
        }
    }

    private val nextListener = object : KeyboardWidget.OnNextListener {
        override fun next() {
            presenter.onNextClick(loan, confirmKeyboard.getValue())
        }
    }

    override fun showTimeInfo(time: Long?) {
        if (time != null) {
            confirmSendCodeAgain.visibility = View.GONE
            confirmTimerInfo.visibility = View.VISIBLE
            confirmTimer.text = time.toTimerText()
        } else {
            confirmSendCodeAgain.visibility = View.VISIBLE
            confirmTimerInfo.visibility = View.GONE
        }
    }

    override fun setCodeValid(valid: Boolean) {
        (0..3).forEach { i ->
            val pinControl: PinWidget = confirmCode.getChildAt(i) as PinWidget
            pinControl.setState(if (valid) PinState.VALID else PinState.EMPTY)
        }
    }

    override fun showClientInfo(client: ClientData?) {
        client?.technicalMessage ?: return

        client.technicalMessage?.let { message -> alert(message = message) }
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
                openHelp(toolbarStyle, "help_consents_sms")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onError(error: String) {
        super.onError(error)
        confirmCode.children.filterIsInstance<PinWidget>().forEach { it.setState(PinState.EMPTY) }
    }

    override fun onError(throwable: Throwable) {
        confirmCode.children.filterIsInstance<PinWidget>().forEach { it.setState(PinState.EMPTY) }

        if (throwable is ApiErr) {
            val message = when {
                throwable.errorArray.keys.size == 1 && !throwable.message.isNullOrEmpty() -> throwable.message
                throwable.invalidClientData() -> getString(R.string.error_client_data)
                throwable.invalidClientDocument() -> getString(R.string.error_id_number)
                else -> null
            }

            if (message != null)
                alert(title = "",
                    message = throwable.message.orEmpty(),
                    positive = {
                        when {
                            throwable.invalidClientData() -> presenter.showClientProfile(loan)
                            throwable.invalidClientDocument() -> presenter.showDocuments(loan)
                            else -> activity?.onBackPressed()
                        }
                    })
            else super.onError(throwable)
        } else
            super.onError(throwable)
    }
}