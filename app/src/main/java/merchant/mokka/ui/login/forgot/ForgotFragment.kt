package merchant.mokka.ui.login.forgot

import android.os.Bundle
import android.text.InputType
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_forgot.*
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.utils.*
import merchant.mokka.utils.decoro.watchers.MaskFormatWatcher
import merchant.mokka.utils.*
import merchant.mokka.widget.EditTextValidator
import merchant.mokka.widget.attachValidator
import merchant.mokka.widget.detachValidator

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

        loginValidator = EditTextValidator(
            validator = { text -> text.isValidAsPhoneOrLogin() },
            onChangedState = { isValid ->
                forgotSendCodeBtn.alpha = isValid.toAlpha()
            },
            errorText = getString(R.string.error_login),
            errorRequired = getString(R.string.error_field_required)
        )

        val login = arguments?.getString(ExtrasKey.LOGIN.name).orEmpty()
        forgotLogin.setText(login)

        forgotSendCodeBtn.setOnClickListener {
            if ((isPlLocale() || isBgLocale())&& loginValidator.isValid()) {
                presenter.requestCode(forgotLogin.text.toString())
            }
        }
    }

    private fun validate() {
        val valid = forgotLogin.text.toString().isValidAsPhoneOrLogin()
        forgotSendCodeBtn.alpha = valid.toAlpha()
    }

    override fun onResume() {
        super.onResume()
        forgotLogin.attachValidator(loginValidator, forgotLoginLayout)
        forgotLogin.inputType = InputType.TYPE_CLASS_TEXT
        validate()
    }

    override fun onPause() {
        super.onPause()
        forgotLogin.detachValidator(loginValidator)
    }
}