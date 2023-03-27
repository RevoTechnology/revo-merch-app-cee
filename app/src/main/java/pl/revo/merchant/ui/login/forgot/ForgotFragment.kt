package pl.revo.merchant.ui.login.forgot

import android.os.Bundle
import android.text.InputType
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_forgot.*
import pl.revo.merchant.R
import pl.revo.merchant.common.BaseFragment
import pl.revo.merchant.common.ExtrasKey
import pl.revo.merchant.common.HomeIconType
import pl.revo.merchant.common.ToolbarStyle
import pl.revo.merchant.utils.*
import pl.revo.merchant.utils.decoro.watchers.MaskFormatWatcher
import pl.revo.merchant.widget.EditTextValidator
import pl.revo.merchant.widget.attachValidator
import pl.revo.merchant.widget.detachValidator

class ForgotFragment : BaseFragment(), ForgotView {

    companion object {
        fun getInstance(login: String) : ForgotFragment {
            val fragment = ForgotFragment()
            val bundle = Bundle()
            bundle.putString(ExtrasKey.LOGIN.name, login)
            fragment.arguments = bundle
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: ForgotPresenter

    @ProvidePresenter
    fun providePresenter() = ForgotPresenter(injector)

    override val layoutResId = R.layout.fragment_forgot
    override val titleResId = R.string.forgot_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.LIGHT

    private lateinit var loginValidator: EditTextValidator
    private lateinit var phoneFormatWatcher: MaskFormatWatcher
    private lateinit var phonePrefix : String

    override fun initView(view: View, savedInstanceState: Bundle?) {
        phonePrefix = getString(R.string.phone_prefix)

        if (isRuLocale()) {
            phoneFormatWatcher = createPhoneMaskFormatWatcher(forgotLogin, getString(R.string.phone_mask))
        } else {
            loginValidator = EditTextValidator(
                        validator = { text -> text.isValidAsPhoneOrLogin() },
                        onChangedState = { isValid ->
                            forgotSendCodeBtn.alpha = isValid.toAlpha()
                        },
                        errorText = getString(R.string.error_login),
                        errorRequired = getString(R.string.error_field_required)
                )
        }
        val login = arguments?.getString(ExtrasKey.LOGIN.name).orEmpty()
        forgotLogin.setText(login)

        forgotSendCodeBtn.setOnClickListener {
            if (isRuLocale() && phoneFormatWatcher.mask.isValid()) {
                    presenter.requestCode(forgotLogin.text.toString())
            } else if ((isPlLocale() || isBgLocale())&& loginValidator.isValid()) {
                    presenter.requestCode(forgotLogin.text.toString())
            }
        }
    }

    private fun validate() {
        val valid = if (isRuLocale()) {
            phoneFormatWatcher.mask.isValid()
        } else {
            forgotLogin.text.toString().isValidAsPhoneOrLogin()
        }
        forgotSendCodeBtn.alpha = valid.toAlpha()
    }

    override fun onResume() {
        super.onResume()
        if (isRuLocale()) {
            phoneFormatWatcher.setCallback(FormatTextWatcher {
                forgotSendCodeBtn.alpha = phoneFormatWatcher.mask.isValid().toAlpha()
            })
            forgotLogin.inputType = InputType.TYPE_CLASS_PHONE
        } else {
            forgotLogin.attachValidator(loginValidator, forgotLoginLayout)
            forgotLogin.inputType = InputType.TYPE_CLASS_TEXT
        }
        validate()
    }

    override fun onPause() {
        super.onPause()
        if (isRuLocale()) {
            phoneFormatWatcher.setCallback(null)
        } else {
            forgotLogin.detachValidator(loginValidator)
        }
    }
}