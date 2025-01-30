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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontweatherapp.R;

public class MapFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private WebView webView;
    private ProgressBar loadingIndicator;
    private TextView cardInfo1, cardInfo2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Referencias a los elementos del layout
        webView = view.findViewById(R.id.webView);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);
        cardInfo1 = view.findViewById(R.id.cardInfo1);
        cardInfo2 = view.findViewById(R.id.cardInfo2);

        // Establecer los valores estáticos en las tarjetas
        cardInfo1.setText("Valor 1: 23°C");
        cardInfo2.setText("Valor 2: 10 km/h");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Mostrar el indicador de carga antes de iniciar
        loadingIndicator.setVisibility(View.VISIBLE);

        // Obtener el token de SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences("APP_PREFS", getContext().MODE_PRIVATE);
        String token = preferences.getString("TOKEN", null);

        if (token != null) {
            Log.d(TAG, "Token obtenido de SharedPreferences: " + token);
            // Pasar el token ajustado al HTML
            webView.addJavascriptInterface(new AuthTokenProvider(token), "Android");
        } else {
            Log.e(TAG, "Token no encontrado en SharedPreferences");
        }

        // Configurar el WebViewClient para ocultar el ProgressBar después de cargar el HTML
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Ocultar el indicador de carga
                loadingIndicator.setVisibility(View.GONE);
            }
        });

        // Cargar el HTML desde assets
        webView.loadUrl("file:///android_asset/windy_map.html");

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null) {
            webView.destroy(); // Liberar recursos
        }
    }

    // Clase interna para proporcionar el token al archivo HTML
    private static class AuthTokenProvider {
        private final String token;

        AuthTokenProvider(String token) {
            if (token.startsWith("Bearer ")) {
                this.token = token.replace("Bearer ", "");
            } else {
                this.token = token;
            }
        }

        @JavascriptInterface
        public String getAuthToken() {
            Log.d(TAG, "Token enviado al HTML: " + token);
            return this.token;
        }
    }
}
