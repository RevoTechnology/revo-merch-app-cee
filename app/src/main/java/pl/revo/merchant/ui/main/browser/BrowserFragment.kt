package pl.revo.merchant.ui.main.browser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import kotlinx.android.synthetic.main.fragment_browser.*
import pl.revo.merchant.R
import pl.revo.merchant.common.BaseFragment
import pl.revo.merchant.common.ExtrasKey
import pl.revo.merchant.common.HomeIconType
import pl.revo.merchant.common.ToolbarStyle
import pl.revo.merchant.pref.Prefs
import pl.revo.merchant.utils.RevoWebView
import timber.log.Timber

class BrowserFragment : BaseFragment() {
    companion object {
        fun getInstance(url: String): BrowserFragment {
            val fragment = BrowserFragment()
            val bundle = Bundle()
            bundle.putSerializable(ExtrasKey.URL.name, url)
            fragment.arguments = bundle
            return fragment
        }
    }


    override val layoutResId: Int = R.layout.fragment_browser
    override val titleResId: Int = R.string.empty
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.ACCENT

    private lateinit var url: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        url = arguments?.getString(ExtrasKey.URL.name).orEmpty()
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun initView(view: View, savedInstanceState: Bundle?) {
        RevoWebView.create(webView = policyWebView,
                onPageFinished = { hideProgress() },
                onLoadResource = { showProgress() },
                scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY,
                isScrollbarFadingEnabled = false)

        Timber.i("load $url")

        val extraHeaders = mapOf("Authorization" to Prefs.token)
        policyWebView.loadUrl(url, extraHeaders)
    }
}