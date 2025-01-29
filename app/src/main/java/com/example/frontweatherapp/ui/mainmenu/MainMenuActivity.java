package com.example.frontweatherapp.ui.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.ui.fragments.HomeFragment;
import com.example.frontweatherapp.ui.fragments.MapFragment;
import com.example.frontweatherapp.ui.fragments.HistoryFragment;
import com.example.frontweatherapp.ui.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Cargar el fragmento inicial (HomeFragment)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new HomeFragment())
                    .commit();
        }

        // Configurar la navegación entre fragmentos
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new MapFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new HistoryFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
            }
            return true;
        });
        setSupportActionBar(findViewById(R.id.toolbar));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú para mostrar el botón de logout
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Manejar clics en el menú
        if (item.getItemId() == R.id.action_logout) {
            Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();

            // Redirigir al LoginActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Cerrar la actividad actual
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
