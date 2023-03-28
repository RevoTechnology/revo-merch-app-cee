package merchant.mokka.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

object RevoWebView {

    @SuppressLint("SetJavaScriptEnabled")
    fun create(webView: WebView,
               isJavascriptEnabled: Boolean = true,
               isZoomSupported: Boolean = true,
               isBuiltInZoomControls: Boolean = true,
               isLoadWithOverviewMode: Boolean = true,
               isUseWideViewPort: Boolean = true,
               onPageFinished: (String?) -> Unit = {},
               onLoadResource: () -> Unit = {},
               scrollBarStyle: Int? = null,
               isScrollbarFadingEnabled: Boolean? = null) = webView.apply {

        settings.apply {
            javaScriptEnabled = isJavascriptEnabled
            setSupportZoom(isZoomSupported)
            builtInZoomControls = isBuiltInZoomControls
            loadWithOverviewMode = isLoadWithOverviewMode
            useWideViewPort = isUseWideViewPort
        }

        scrollBarStyle?.let { this.scrollBarStyle = it }
        isScrollbarFadingEnabled?.let { this.isScrollbarFadingEnabled = it }

        webChromeClient = WebChromeClient()
        webViewClient = object : WebViewClient() {
            var isRedirected = false

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                isRedirected = false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isRedirected = true
                onPageFinished(url)
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                if (!isRedirected) onLoadResource()
            }
        }
    }
}

