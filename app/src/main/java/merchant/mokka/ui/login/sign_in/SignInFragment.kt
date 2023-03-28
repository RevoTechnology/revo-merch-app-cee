package merchant.mokka.ui.login.sign_in

import android.os.Bundle
import android.text.InputType
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_sign_in.*
import merchant.mokka.BuildConfig
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.IDemoClickedView
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.AgentData
import merchant.mokka.ui.root.RootActivity
import merchant.mokka.utils.FormatTextWatcher
import merchant.mokka.utils.createPhoneMaskFormatWatcher
import merchant.mokka.utils.decoro.watchers.MaskFormatWatcher
import merchant.mokka.utils.isBgLocale
import merchant.mokka.utils.isPlLocale
import merchant.mokka.utils.isRoLocale
import merchant.mokka.utils.isValid
import merchant.mokka.utils.isValidAsPhoneOrLogin
import merchant.mokka.utils.isValidPinCode
import merchant.mokka.utils.toAlpha
import merchant.mokka.widget.EditTextValidator
import merchant.mokka.widget.attachValidator
import merchant.mokka.widget.detachValidator

class SignInFragment : BaseFragment(), SignInView, IDemoClickedView {

    companion object {
        fun getInstance() = SignInFragment()
    }

    @InjectPresenter
    lateinit var presenter: SignInPresenter

    @ProvidePresenter
    fun providePresenter() = SignInPresenter(injector)

    override val layoutResId = R.layout.fragment_sign_in
    override val titleResId = R.string.sign_in_title
    override val homeIconType = HomeIconType.NONE
    override val toolbarStyle = ToolbarStyle.LIGHT

    private lateinit var loginValidator: EditTextValidator
    private lateinit var pinValidator: EditTextValidator
    private lateinit var phoneFormatWatcher: MaskFormatWatcher
    private lateinit var phonePrefix: String

    override fun showDemo() = true

    override fun initView(view: View, savedInstanceState: Bundle?) {
        phonePrefix = getString(R.string.phone_prefix)


        if (isBgLocale()) {
            phoneFormatWatcher = createPhoneMaskFormatWatcher(signInLogin, getString(R.string.phone_mask))
        } else {
            loginValidator = EditTextValidator(
                validator = { text -> text.isValidAsPhoneOrLogin() },
                onChangedState = { validateModel() },
                errorText = getString(R.string.error_login),
                errorRequired = getString(R.string.error_field_required)
            )
        }
        pinValidator = EditTextValidator(
            validator = { text -> text.isValidPinCode() },
            onChangedState = { validateModel() },
            errorText = getString(R.string.error_pin_code),
            errorRequired = getString(R.string.error_field_required)
        )

        signInForgotBtn.setOnClickListener {
            presenter.showForgotScreen(signInLogin.text.toString())
        }

        signInBtn.setOnClickListener {
            if (isModelValid())
                presenter.signIn(signInLogin.text.toString(), signInPin.text.toString())
        }

        signInVer.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)

        presenter.create()
    }

    private fun isModelValid(): Boolean {
        return (((isPlLocale() || isRoLocale()) && loginValidator.isValid()) || (isBgLocale() && phoneFormatWatcher.mask.isValid())) && pinValidator.isValid()
    }

    private fun validateModel() {
        signInBtn.alpha = isModelValid().toAlpha()
    }

    override fun onResume() {
        super.onResume()

        if (isBgLocale()) {
            phoneFormatWatcher.setCallback(FormatTextWatcher {
                if (it.isEmpty() && isBgLocale()) signInLogin.setText(getString(R.string.phone_prefix))
                validateModel()
            })
            signInLogin.inputType = InputType.TYPE_CLASS_PHONE
            if (isBgLocale()) signInLogin.setText(getString(R.string.phone_prefix))
        } else {
            signInLogin.attachValidator(loginValidator, signInLoginLayout)
            signInLogin.inputType = InputType.TYPE_CLASS_TEXT
        }

        signInPin.attachValidator(pinValidator, signInPinLayout)

        (activity as RootActivity).setRootFrameVisibility(true)

        validateModel()
    }

    override fun onPause() {
        super.onPause()

        if (isBgLocale()) {
            phoneFormatWatcher.setCallback(null)
        } else {
            signInLogin.detachValidator(loginValidator)
        }

        signInPin.detachValidator(pinValidator)
    }

    override fun setAgentData(agentData: AgentData) {
        (activity as RootActivity).setAgentData(agentData, 0)
    }

    override fun confirmUpdate(version: String) {
        (activity as RootActivity).confirmUpdate(version)
    }

    override fun onDemoClick() {
        alert(
            title = "",
            message = getString(R.string.demo_alert),
            positiveButtonResId = R.string.button_accept,
            positive = { presenter.setDemo() },
            negativeButtonResId = R.string.button_back,
            negative = { }
        )
    }
}