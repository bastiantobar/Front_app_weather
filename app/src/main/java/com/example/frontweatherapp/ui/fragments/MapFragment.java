package com.example.frontweatherapp.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontweatherapp.R;

public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";
    private WebView webView;
    private ProgressBar loadingIndicator;
    private LoadingDialogFragment loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        showLoading(true);

        // Referencias UI
        webView = view.findViewById(R.id.webView);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);

        setupWebView();

        return view;
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadingIndicator.setVisibility(View.GONE);
                showLoading(false);
            }
        });

        // Obtener token desde SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences("APP_PREFS", getContext().MODE_PRIVATE);
        String token = preferences.getString("TOKEN", null);

        if (token != null) {
            Log.d(TAG, "Token obtenido de SharedPreferences: " + token);
            webView.addJavascriptInterface(new AuthTokenProvider(token), "Android");
        } else {
            Log.e(TAG, "Token no encontrado en SharedPreferences");
        }

        webView.loadUrl("file:///android_asset/windy_map.html");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null) {
            webView.destroy();
        }
    }

    private static class AuthTokenProvider {
        private final String token;

        AuthTokenProvider(String token) {
            this.token = token.startsWith("Bearer ") ? token.replace("Bearer ", "") : token;
        }

        @JavascriptInterface
        public String getAuthToken() {
            Log.d(TAG, "Token enviado al HTML: " + token);
            return this.token;
        }
    }

    public void showLoading(boolean show) {
        if (show) {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialogFragment();
            }
            if (!loadingDialog.isAdded()) {
                loadingDialog.show(getParentFragmentManager(), "loading");
            }
        } else {
            if (loadingDialog != null && loadingDialog.isAdded()) {
                loadingDialog.dismiss();
            }
        }
    }
}
