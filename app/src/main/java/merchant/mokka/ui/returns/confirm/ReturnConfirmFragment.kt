package merchant.mokka.ui.returns.confirm

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_return_confirm.*
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.ReturnData
import merchant.mokka.utils.toTimerText
import merchant.mokka.widget.KeyboardWidget
import merchant.mokka.widget.PinState
import merchant.mokka.widget.PinWidget

class ReturnConfirmFragment : BaseFragment(), ReturnConfirmView {

    companion object {
        fun getInstance(returnData: ReturnData) : ReturnConfirmFragment {
            val fragment = ReturnConfirmFragment()
            fragment.setArguments(ExtrasKey.RETURN, returnData)
            return fragment
        }
    }

    override val layoutResId = R.layout.fragment_return_confirm
    override val titleResId = R.string.return_confirm_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.DARK

    @InjectPresenter
    lateinit var presenter : ReturnConfirmPresenter

    @ProvidePresenter
    fun providePresenter() = ReturnConfirmPresenter(injector)

    private lateinit var data : ReturnData

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        data = arguments?.getSerializable(ExtrasKey.RETURN.name) as ReturnData

        with(view) {
            returnConfirmClientName.text = data.clientName

            for (i in 0..3) {
                returnConfirmCode.addView(PinWidget(context))
            }

            returnConfirmKeyboard.setPins(returnConfirmCode)
            returnConfirmKeyboard.setNextListener(nextListener)

            returnConfirmSendCodeAgain.setOnClickListener {
                presenter.sendConfirmCodeAgain(data.orderId)
            }
        }
    }

    private val nextListener = object : KeyboardWidget.OnNextListener{
        override fun next() {
            presenter.onNextClick(data, returnConfirmKeyboard.getValue())
        }
    }

    override fun showTimeInfo(time: Long?) {
        if (time != null) {
            returnConfirmSendCodeAgain.visibility = View.GONE
            returnConfirmTimerInfo.visibility = View.VISIBLE
            returnConfirmTimer.text = time.toTimerText()
        } else {
            returnConfirmSendCodeAgain.visibility = View.VISIBLE
            returnConfirmTimerInfo.visibility = View.GONE
        }
    }

    override fun setCodeValid(valid: Boolean) {
        (0..3).forEach { i ->
            val pinControl: PinWidget = returnConfirmCode.getChildAt(i) as PinWidget
            pinControl.setState(if (valid) PinState.VALID else PinState.EMPTY)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_help, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.item_help -> {
                openHelp(toolbarStyle, "help_return_sms")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showTechnicalError() {
        alert(
                title = "",
                message = getString(R.string.error_do_support),
                positive = { presenter.exitToDashboard() },
                canceledOnTouchOutside = false
        )
    }
}