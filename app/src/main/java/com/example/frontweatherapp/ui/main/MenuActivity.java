package com.example.frontweatherapp.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.frontweatherapp.R;
import com.example.frontweatherapp.ui.fragments.GraficFragment;
import com.example.frontweatherapp.ui.fragments.HomeFragment;
import com.example.frontweatherapp.ui.fragments.MapFragment;
import com.example.frontweatherapp.ui.fragments.NotificationFragment;
import com.example.frontweatherapp.ui.fragments.HistoryFragment;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.ActionBarDrawerToggle;

public class MenuActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ImageView backgroundImage = findViewById(R.id.background_image);

        if (isNightMode()) {
            backgroundImage.setImageResource(R.drawable.bg_night);
        } else {
            backgroundImage.setImageResource(R.drawable.bg_day);
        }

        // Inicializar DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Configurar la Toolbar
        setSupportActionBar(findViewById(R.id.toolbar));

        // Configurar el DrawerToggle (icono para abrir/cerrar el drawer)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar),
                R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Cargar el fragmento inicial (HomeFragment)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new HomeFragment())
                    .commit();
        }

        // Configurar la navegación entre fragmentos
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new MapFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.nav_graficos) {
                selectedFragment = new GraficFragment();
            }else if (itemId == R.id.nav_notifications) {
                selectedFragment = new NotificationFragment();
            }else if (itemId == R.id.action_logout) {
                cerrarSesion();
                return true;
            }



            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
                drawerLayout.closeDrawer(navigationView); // Cerrar el drawer después de seleccionar un fragmento
            }
            return true;
        });
    }

    // Método para cerrar sesión
    private void cerrarSesion() {
        Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();

        // Redirigir al LoginActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Cerrar esta actividad
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Manejar clics en el menú de la toolbar (como el logout)
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
    private boolean isNightMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}
