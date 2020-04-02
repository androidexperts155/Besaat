package com.deepak.besaat.view.activities.signup;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.deepak.besaat.R;
import com.deepak.besaat.utils.Constants;

public class AuthenticationDialog extends Dialog {
    private final String request_url;
    private final String redirect_url;
    private AuthenticationListener listener;
    private WebView webView;

    public AuthenticationDialog(Context context, AuthenticationListener listener,String clientId,String redirect_url) {
        super(context);
        this.listener = listener;
        this.redirect_url = redirect_url;
        this.request_url = Constants.INSTANCE.getBASE_URL_INSTA() +
                "oauth/authorize/?client_id=" +
                clientId +
                "&redirect_uri=" + redirect_url +
                "&response_type=token&display=touch";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.auth_dialog);
        initializeWebView();
    }

    private void initializeWebView() {
         webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSaveFormData(false);
        webView.clearCache(true);
        webView.clearHistory();
        webView.loadUrl(request_url);
        webView.setWebViewClient(webViewClient);
    }

    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(redirect_url)) {
                //webView.destroy();
                AuthenticationDialog.this.dismiss();
                return true;
            }
            return false;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.contains("access_token=")) {
                Uri uri = Uri.EMPTY.parse(url);
                String access_token = uri.getEncodedFragment();
                access_token = access_token.substring(access_token.lastIndexOf("=") + 1);
                Log.e("access_token", access_token);
                listener.onTokenReceived(access_token);
               // webView.destroy();
                dismiss();
            } else if (url.contains("?error")) {
                Log.e("access_token", "getting error fetching access token");
                webView.destroy();
                dismiss();
            }
        }
    };
}
