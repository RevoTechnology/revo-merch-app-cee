package pl.revo.merchant.ui.client.policy

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import kotlinx.android.synthetic.main.fragment_policy.view.*
import pl.revo.merchant.R
import pl.revo.merchant.api.HttpConfig
import pl.revo.merchant.common.BaseFragment
import pl.revo.merchant.common.ExtrasKey
import pl.revo.merchant.common.HomeIconType
import pl.revo.merchant.common.ToolbarStyle
import pl.revo.merchant.model.PolicyDto
import pl.revo.merchant.pref.Prefs
import pl.revo.merchant.utils.RevoWebView
import timber.log.Timber

class PolicyFragment : BaseFragment(), PolicyView {

    companion object {
        fun getInstance(policy: PolicyDto): PolicyFragment {
            val fragment = PolicyFragment()
            val bundle = Bundle()
            bundle.putSerializable(ExtrasKey.POLICY.name, policy)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val layoutResId = R.layout.fragment_policy
    override var titleResId = R.string.contract_ru_individual_conditions_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.ACCENT

    private lateinit var policy: PolicyDto

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        policy = arguments?.getSerializable(ExtrasKey.POLICY.name) as PolicyDto
        titleResId = policy.titleResId
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @SuppressLint("SetJavaScriptEnabled", "BinaryOperationInTimber")
    override fun initView(view: View, savedInstanceState: Bundle?) {
        with(view) {

            RevoWebView.create(webView = policyWebView,
                    onPageFinished = { hideProgress() },
                    onLoadResource = { showProgress() },
                    scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY,
                    isScrollbarFadingEnabled = false)

            Timber.i("data=" + !policy.data.isNullOrEmpty())

            if (!policy.data.isNullOrEmpty()) {
                policyWebView.loadDataWithBaseURL("", policy.data!!, "text/html; charset=utf-8", "UTF-8", "")
            } else {
                val url = if (policy.kind.contains("?")) {
                    HttpConfig.POLICY_URL
                            .replace("%s.html", policy.kind)
                            .let {
                                Timber.i(it)
                                String.format(it, policy.loanToken)
                            }
                } else
                    String.format(HttpConfig.POLICY_URL, policy.loanToken, policy.kind)


                Timber.i(url)
                val extraHeaders = mapOf("Authorization" to Prefs.token)
                policyWebView.loadUrl(url, extraHeaders)
            }
        }
    }
}