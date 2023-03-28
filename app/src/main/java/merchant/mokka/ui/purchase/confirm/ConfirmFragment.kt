package merchant.mokka.ui.purchase.confirm

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_confirm.*
import merchant.mokka.R
import merchant.mokka.api.HttpConfig
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.LoanData
import merchant.mokka.pref.Prefs
import merchant.mokka.utils.RevoWebView

class ConfirmFragment : BaseFragment(), ConfirmView {

    companion object {
        fun getInstance(loan: LoanData): ConfirmFragment {
            val fragment = ConfirmFragment()
            val args = Bundle()
            args.putSerializable(ExtrasKey.LOAN.name, loan)
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: ConfirmPresenter

    @ProvidePresenter
    fun providePresenter() = ConfirmPresenter(injector)

    override val layoutResId = R.layout.fragment_confirm
    override val titleResId = R.string.confirm_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.ACCENT

    private lateinit var loan: LoanData

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView(view: View, savedInstanceState: Bundle?) {
        loan = arguments?.getSerializable(ExtrasKey.LOAN.name) as LoanData
        setHasOptionsMenu(true)

        RevoWebView.create(
                webView = confirmView,
                scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY,
                isScrollbarFadingEnabled = false
        )

        val url = String.format(HttpConfig.DOCUMENT_URL, loan.token, "secci")
        val extraHeaders = mapOf("Authorization" to Prefs.token)
        confirmView.loadUrl(url, extraHeaders)

        confirmNextBtn.setOnClickListener { presenter.onNextClick(loan) }
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
                openHelp(toolbarStyle, "help_confirm")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}