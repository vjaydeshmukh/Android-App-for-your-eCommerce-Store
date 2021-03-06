package com.iqonic.store.activity

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.iqonic.store.AppBaseActivity
import com.iqonic.store.R
import com.iqonic.store.utils.Constants
import kotlinx.android.synthetic.main.activity_webview_payment.*
import kotlinx.android.synthetic.main.toolbar.*

class WebViewExternalProductActivity : AppBaseActivity() {
    var mIsError = false
    private var mWebViewClient = WebViewClient()
    private var webChromeClient = WebChromeClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_payment)
        setToolbar(toolbar)
        setupWebView()
        title = getString(R.string.lbl_ex_product)

        webView.loadUrl(
            intent.getStringExtra(Constants.KeyIntent.EXTERNAL_URL)
        )
    }

    private fun setupWebView() {
        showProgress(true)
        configureWebView()
        configureWebClient()
        initChromeClient()
    }

    private fun initChromeClient() {
        var customView: View? = null
        var customViewCallback: WebChromeClient.CustomViewCallback? = null
        var originalOrientation: Int = 0
        var originalSystemUiVisibility: Int = 0

        webChromeClient = object : WebChromeClient() {

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                toolbar.title = title
            }

            override fun onShowCustomView(
                paramView: View?,
                paramCustomViewCallback: CustomViewCallback
            ) {
                if (customView != null) {
                    onHideCustomView()
                    return
                }
                customView = paramView
                originalSystemUiVisibility = window.decorView.systemUiVisibility
                originalOrientation = requestedOrientation
                customViewCallback = paramCustomViewCallback
                (window.decorView as FrameLayout).addView(
                    customView,
                    FrameLayout.LayoutParams(-1, -1)
                )
                window.decorView.systemUiVisibility = 3846
            }

            override fun onHideCustomView() {
                (window.decorView as FrameLayout).removeView(customView)
                customView = null
                window.decorView.systemUiVisibility = originalSystemUiVisibility
                requestedOrientation = originalOrientation
                customViewCallback?.onCustomViewHidden()
                customViewCallback = null
            }
        }

        webView.webChromeClient = webChromeClient
    }

    private fun configureWebClient() {
        mWebViewClient = object : WebViewClient() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showProgress(true)
            }

            override fun onPageFinished(view: WebView, url: String) {
                Log.e("Error", "onPageFinished")
                super.onPageFinished(view, url)
                if (!mIsError) {
                    showProgress(false)
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError
            ) {
                mIsError = true
                super.onReceivedError(view, request, error)
                Toast.makeText(
                    this@WebViewExternalProductActivity,
                    error.toString(),
                    Toast.LENGTH_LONG
                )
                    .show()
                finish()
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                Log.e("Error", "onReceivedHttpError")
                super.onReceivedHttpError(view, request, errorResponse)
                Toast.makeText(
                    this@WebViewExternalProductActivity,
                    "onReceivedHttpError",
                    Toast.LENGTH_LONG
                )
                    .show()
                finish()
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                Log.e("Error", "onReceivedSslError")
                super.onReceivedSslError(view, handler, error)
                Toast.makeText(
                    this@WebViewExternalProductActivity,
                    "onReceivedSslError",
                    Toast.LENGTH_LONG
                )
                    .show()
                finish()
            }

        }
        // Assign Web View Client to webview
        webView.webViewClient = mWebViewClient

    }

    private fun configureWebView() {
        try {
            webView.settings.apply {
                javaScriptCanOpenWindowsAutomatically = true
                javaScriptEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                databaseEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                webView.isFocusable = true
                webView.isFocusableInTouchMode = true
                webView.isScrollContainer = false
                webView.isVerticalScrollBarEnabled = false
                webView.isHorizontalScrollBarEnabled = false
                setAppCacheEnabled(false)
                setGeolocationEnabled(true)
                setAppCachePath(applicationContext.filesDir.absolutePath + "/cache")
                cacheMode = WebSettings.LOAD_NO_CACHE
                layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
                loadsImagesAutomatically = true
                loadWithOverviewMode = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
                setSupportMultipleWindows(false)
                builtInZoomControls = false
                setSupportZoom(
                    false
                )
                userAgentString =
                    "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.0.0 Mobile Safari/537.36"
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        webView.clearHistory()
        webView.clearCache(true)
    }

}
