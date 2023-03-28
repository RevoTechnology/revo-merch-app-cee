package merchant.mokka.ui.login.sign_up

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*
import merchant.mokka.common.*
import merchant.mokka.R
import merchant.mokka.common.*
import merchant.mokka.model.AgentData
import merchant.mokka.model.PinRegistrationMode
import merchant.mokka.model.VerifySmsData
import merchant.mokka.ui.root.RootActivity
import merchant.mokka.utils.Constants
import merchant.mokka.utils.isValidPinCode
import merchant.mokka.widget.KeyboardWidget
import merchant.mokka.widget.PinState
import merchant.mokka.widget.PinWidget

class SignUpFragment : BaseFragment(), SignUpView {

    companion object {
        fun getInstance(data: VerifySmsData) : SignUpFragment {
            val fragment = SignUpFragment()
            val bundle = Bundle()
            bundle.putSerializable(ExtrasKey.LOGIN.name, data)
            fragment.arguments = bundle
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: SignUpPresenter

    @ProvidePresenter
    fun providePresenter() = SignUpPresenter(injector)

    override val layoutResId = R.layout.fragment_sign_up
    override val titleResId = R.string.sign_up_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle =  ToolbarStyle.LIGHT

    private lateinit var data: VerifySmsData
    private lateinit var pin: String
    private lateinit var simplePins: Array<String>

    private var mode: PinRegistrationMode = PinRegistrationMode.REGISTRATION
    private var retryError: Boolean = false

    override fun initView(view: View, savedInstanceState: Bundle?) {
        data = arguments?.getSerializable(ExtrasKey.LOGIN.name) as VerifySmsData

        simplePins = requireActivity().resources.getStringArray(R.array.simple_pins)

        with(view) {
            for (i in 0 until Constants.PIN_CODE_LENGTH) {
                signUpPin.addView(PinWidget(context))
                signUpRepeat.addView(PinWidget(context))
            }
            signUpKeyboard.setPins(signUpPin)
            signUpKeyboard.setNextListener(nextListener)
            signUpKeyboard.setBackListener(backListener)
            signUpKeyboard.setPinAppendListener(appendListener)
        }
    }

    private val nextListener = object : KeyboardWidget.OnNextListener{
        override fun next() {
            if (mode == PinRegistrationMode.REGISTRATION) {
                pin = signUpKeyboard.getValue()

                for (i in 0 until simplePins.size) {
                    if (pin == simplePins[i]) {
                        simplePin()
                        return
                    }
                }

                nextPin()
            } else {
                val repeatPin = signUpKeyboard.getValue()
                if (repeatPin.isValidPinCode()){
                    if (repeatPin == pin) {
                        setValid(true)
                        presenter.signUpNewPin(data, pin)
                    } else {
                        setValid(false)
                        onErrorWithBack()
                    }
                }
            }
        }
    }

    private fun onErrorWithBack() {
        (activity as BaseActivity).alert(
                title = getString(R.string.error_title),
                message = getString(R.string.error_pin_not_match),
                positive = {},
                positiveButtonResId = R.string.button_back
        )
    }

    private fun simplePin() {
        val builder = AlertDialog.Builder(requireContext())
                .setTitle("")
                .setMessage(getString(R.string.sign_up_simple))
                .setPositiveButton(R.string.sign_up_simple_change) {
                    dialog, _ -> retypePin()
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.sign_up_simple_next) {
                    dialog, _ -> nextPin()
                    dialog.dismiss()
                }

        builder.show()
    }

    private fun retypePin() {
        mode = PinRegistrationMode.REGISTRATION

        pin = ""
        (0..3).forEach { i ->
            val pinControl: PinWidget = signUpPin.getChildAt(i) as PinWidget
            pinControl.setState(PinState.EMPTY)

            val pinControl2: PinWidget = signUpRepeat.getChildAt(i) as PinWidget
            pinControl2.setState(PinState.EMPTY)
        }

        signUpKeyboard.setPins(signUpPin)
    }

    private fun nextPin() {
        pin = signUpKeyboard.getValue()
        if (pin.isValidPinCode()) {

            signUpKeyboard.setPins(signUpRepeat)
            mode = PinRegistrationMode.CONFIRMATION
        }
    }

    private fun setValid(isValid: Boolean) {
        if (!isValid) {
            mode = PinRegistrationMode.REGISTRATION
            pin = ""
            retryError = true
        }
        (0 until Constants.PIN_CODE_LENGTH).forEach { i ->
            val pinControl: PinWidget = signUpPin.getChildAt(i) as PinWidget
            pinControl.setState(if (isValid) PinState.VALID else PinState.ERROR)
            val pinControl2: PinWidget = signUpRepeat.getChildAt(i) as PinWidget
            pinControl2.setState(if (isValid) PinState.VALID else PinState.ERROR)
        }
        if (!isValid)
            signUpKeyboard.setPins(signUpPin)
    }

    private val backListener = object : KeyboardWidget.OnBackListener{
        override fun back() {
            if (mode == PinRegistrationMode.CONFIRMATION) {
                val value = signUpKeyboard.getValue()
                if (value.isEmpty()) {
                    pin = ""
                    mode = PinRegistrationMode.REGISTRATION
                    signUpKeyboard.setPins(signUpPin)

                    val pinControl: PinWidget = signUpPin.getChildAt(Constants.PIN_CODE_LENGTH - 1) as PinWidget
                    pinControl.setState(PinState.EMPTY)
                    pinControl.tag = ""
                }
            }
        }
    }

    private val appendListener = object : KeyboardWidget.OnPinAppendListener {
        override fun onAppend() {
            if (retryError) {
                (0 until Constants.PIN_CODE_LENGTH).forEach { i ->
                    val pinControl: PinWidget = signUpPin.getChildAt(i) as PinWidget
                    pinControl.setState(PinState.EMPTY)

                    val pinControl2: PinWidget = signUpRepeat.getChildAt(i) as PinWidget
                    pinControl2.setState(PinState.EMPTY)
                }
                retryError = false
            }
        }
    }

    override fun setAgentData(agentData: AgentData) {
        (activity as RootActivity).setAgentData(agentData, 0)
    }
}