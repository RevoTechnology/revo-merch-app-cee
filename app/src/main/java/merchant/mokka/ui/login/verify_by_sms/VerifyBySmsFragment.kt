package merchant.mokka.ui.login.verify_by_sms

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_verify_by_sms.*
import kotlinx.android.synthetic.main.fragment_verify_by_sms.view.*
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.utils.toTimerText
import merchant.mokka.widget.KeyboardWidget
import merchant.mokka.widget.PinState
import merchant.mokka.widget.PinWidget

class VerifyBySmsFragment : BaseFragment(), VerifyBySmsView {

    companion object {
        fun getInstance(login: String) : VerifyBySmsFragment {
            val fragment = VerifyBySmsFragment()
            val bundle = Bundle()
            bundle.putString(ExtrasKey.LOGIN.name, login)
            fragment.arguments = bundle
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: VerifyBySmsPresenter

    @ProvidePresenter
    fun providePresenter() = VerifyBySmsPresenter(injector)

    override val layoutResId = R.layout.fragment_verify_by_sms
    override val titleResId = R.string.verify_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.LIGHT

    private lateinit var login: String

    override fun initView(view: View, savedInstanceState: Bundle?) {
        login = arguments?.getString(ExtrasKey.LOGIN.name).orEmpty()

        with(view) {
            for (i in 0..3) {
                verifyCode.addView(PinWidget(context))
            }
            verifyKeyboard.setPins(verifyCode)
            verifyKeyboard.setNextListener(nextListener)

            verifySendCodeAgain.setOnClickListener { presenter.sendCodeAgain(login) }
        }
    }

    private val nextListener = object : KeyboardWidget.OnNextListener{
        override fun next() {
            setValid()
            presenter.onNext(login, verifyKeyboard.getValue())
        }
    }

    private fun setValid() {
        (0..3).forEach { i ->
            val pinControl: PinWidget = verifyCode.getChildAt(i) as PinWidget
            pinControl.setState(PinState.VALID)
        }
    }

    override fun showTimeInfo(time: Long?) {
        if (time != null) {
            verifySendCodeAgain.visibility = View.INVISIBLE
            verifyTimerInfo.visibility = View.VISIBLE
            verifyTimer.text = time.toTimerText()
        } else {
            verifySendCodeAgain.visibility = View.VISIBLE
            verifyTimerInfo.visibility = View.INVISIBLE
        }
    }
}