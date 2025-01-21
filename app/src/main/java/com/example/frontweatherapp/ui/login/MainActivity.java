package com.example.frontweatherapp.ui.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.ui.fragments.LoginFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cargar el LoginFragment al iniciar
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment()) // Cargar LoginFragment
                    .commit();
        }
    }
}
