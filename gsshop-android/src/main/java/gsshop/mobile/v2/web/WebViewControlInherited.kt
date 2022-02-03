package gsshop.mobile.v2.web

import android.app.Activity
import android.graphics.Color
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.gsshop.mocha.web.BaseWebChromeClient
import com.gsshop.mocha.web.BaseWebViewClient
import com.gsshop.mocha.web.WebViewControl
import com.gsshop.mocha.web.WebViewProgress
import gsshop.mobile.v2.home.HomeActivity
import roboguice.util.Ln

class WebViewControlInherited(activity: Activity?, webView: WebView?, webViewClient: WebViewClient?, webChromeClient: WebChromeClient?, progress: WebViewProgress?) :
        WebViewControl(activity, webView, webViewClient, webChromeClient, progress) {

    override fun loadUrl(url: String) {
        isAvailableUrl(url)
        super.loadUrl(url)
    }

    override fun loadUrl(url: String, headers: MutableMap<String, String>) {
        isAvailableUrl(url)
        super.loadUrl(url, headers)
    }

    private fun isAvailableUrl(url: String) {
        Ln.i("[WebViewControlInherited isAvailableUrl] url : $url")

        // $가 키사 보안으로 추가되었다가 추후 빠짐 (이동에 문제 있는 URL 있음.
        val isAvailable = !url.contains("\\")

        if (!isAvailable) {
//            Toast.makeText(activity, "Unavailable address.\nplease try again.", Toast.LENGTH_SHORT).show()
            if (activity !is HomeActivity)
                activity.finish()
        }
    }

    class Builder(private val activity: Activity) {
        private var webView: WebView? = null
        private var progress: WebViewProgress? = null
        private var webViewClient: WebViewClient? = null
        private var webChromeClient: WebChromeClient? = null
        fun target(webView: WebView?): Builder {
            this.webView = webView
            return this
        }

        fun with(progress: WebViewProgress?): Builder {
            this.progress = progress
            return this
        }

        fun with(webViewClient: WebViewClient?): Builder {
            this.webViewClient = webViewClient
            return this
        }

        fun with(webChromeClient: WebChromeClient?): Builder {
            this.webChromeClient = webChromeClient
            return this
        }

        fun build(): WebViewControlInherited {
            if (webView == null) {
                webView = WebView(activity)
                webView!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                webView!!.setBackgroundColor(Color.WHITE)
            }
            if (webViewClient == null) {
                webViewClient = BaseWebViewClient(activity)
            }
            if (webChromeClient == null) {
                webChromeClient = BaseWebChromeClient(activity)
            }
            if (webViewClient is BaseWebViewClient) {
                (webViewClient as BaseWebViewClient?)!!.setProgress(progress)
            }
            if (webChromeClient is BaseWebChromeClient) {
                (webChromeClient as BaseWebChromeClient?)!!.setProgress(progress)
            }
            val control = WebViewControlInherited(activity, webView, webViewClient, webChromeClient, progress)
            control.setupWebView()
            return control
        }
    }
}