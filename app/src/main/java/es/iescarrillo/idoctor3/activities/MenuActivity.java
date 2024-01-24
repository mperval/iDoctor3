package es.iescarrillo.idoctor3.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import es.iescarrillo.idoctor3.R;
import es.iescarrillo.idoctor3.databinding.ActivityMenuBinding;

public class MenuActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenuBinding binding;
    private SharedPreferences sharedPreferences;
    private TextView tvUsernameNav, tvCategory;
    private ImageView imgNav;
    private Picasso picasso;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private String role, username, photo, id, stars, assessments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configuración inicial
        setSupportActionBar(binding.appBarMenu.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);

        tvUsernameNav = headerView.findViewById(R.id.tvUsernameNav);
        tvCategory = headerView.findViewById(R.id.tvCategory);
        imgNav = headerView.findViewById(R.id.imgNav);

        // Código nuevo
        sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", "");
        role = sharedPreferences.getString("role", "");
        username = sharedPreferences.getString("username", "");
        photo = sharedPreferences.getString("photo", "");
        stars = sharedPreferences.getString("stars", "");
        assessments = sharedPreferences.getString("assessments", "");

        Log.i("foto", photo);

        tvUsernameNav.setText(username);
        tvCategory.setText(role);
        // Usar Picasso para cargar la imagen en el ImageView
        picasso.get().load(photo).into(imgNav);

        // Mostrar el fragmento según el rol
        if ("Professional".equals(role)) {
            showProfessionalFragment();
        } else if ("Patient".equals(role)) {
            showPatientFragment();
        }

        // Resto del código original
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_profile, R.id.nav_changerPassword, R.id.nav_logOut)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //llamo al metodo donde doy acciones a los items del menu
        setupDrawerContent(navigationView);

    }

    // Resto del código original

    private void showProfessionalFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new ProfessionalFragment())
                .commit();
    }

    private void showPatientFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new PatientFragment())
                .commit();
    }

    //metodo para utilizar el navigation
    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    // Manejar clics en los elementos del menú aquí
                    int nav_logOut = menuItem.getItemId();
                    int nav_profile = menuItem.getItemId();
                    int nav_changerPassword = menuItem.getItemId();

                    //boton de log out
                    if (nav_logOut == R.id.nav_logOut) {

                        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                        startActivity(intent);

                        Toast.makeText(MenuActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();

                        //limio variables globales
                        sharedPreferences.edit().clear().apply();
                        finish();

                        return true;
                    }else if(nav_profile == R.id.nav_profile) {


                        // Mostrar el boton segun el rol
                        if ("Professional".equals(role)) {

                            sharedPreferences = getSharedPreferences("MiAppPreferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("id", id);
                            editor.putString("role", role);
                            editor.putString("username", username);
                            editor.apply();
                            Intent intent = new Intent(MenuActivity.this, PerfilProfessional.class);
                            startActivity(intent);
                            finish();

                        } else if ("Patient".equals(role)) {

                            Intent intent = new Intent(MenuActivity.this, PerfilPatient.class);
                            startActivity(intent);
                            finish();

                        }

                        return true;
                    }else if (nav_changerPassword == R.id.nav_changerPassword) {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("id", id);
                        editor.putString("role", role);
                        editor.apply();

                        Intent intent = new Intent(MenuActivity.this, ChangePassword.class);
                        startActivity(intent);
                        finish();

                    }

                    return true;
                }
            });
    }

    //metodo para manejar eventos del menu
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}