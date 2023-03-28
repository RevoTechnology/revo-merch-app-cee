package merchant.mokka.ui.purchase.protection

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_protection_program.*
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.LoanData
import merchant.mokka.pref.Prefs
import merchant.mokka.utils.RevoWebView

class ProtectionProgramFragment : BaseFragment(), ProtectionProgramView {
    companion object {
        fun getInstance(loan: LoanData): ProtectionProgramFragment {
            val fragment = ProtectionProgramFragment()
            val bundle = Bundle()
            bundle.putSerializable(ExtrasKey.LOAN.name, loan)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val layoutResId = R.layout.fragment_protection_program
    override val titleResId = R.string.protection_program_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.ACCENT

    @InjectPresenter
    lateinit var presenter: ProtectionProgramPresenter

    @ProvidePresenter
    fun providePresenter() = ProtectionProgramPresenter(injector)

    private val loan by lazy { arguments?.getSerializable(ExtrasKey.LOAN.name) as LoanData }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        RevoWebView.create(webView = webView,
                onLoadResource = { showProgress() },
                onPageFinished = { hideProgress() }
        )

        val url = ""
//        if (isRuLocale())
//            String.format(HttpConfig.DOCUMENT_URL, HttpConfig.ENV, Prefs.locale, loan.token, "insurance")
//        else
//            String.format(HttpConfig.DOCUMENT_URL, loan.token, "insurance")

        val extraHeaders = mapOf("Authorization" to Prefs.token)
        webView.loadUrl(url, extraHeaders)
    }
}