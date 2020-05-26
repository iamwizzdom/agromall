package com.agromall.agromall.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.agromall.agromall.R;
import com.agromall.agromall.preference.Session;
import com.agromall.agromall.utility.Utility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public NavController navController;
    public FloatingActionButton fab;
    public Button floatingBtn;
    public DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        floatingBtn = findViewById(R.id.floatingBtn);
        fab.setVisibility(View.INVISIBLE);
        floatingBtn.setVisibility(View.INVISIBLE);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_farmers, R.id.nav_farmer,
                R.id.nav_add_farmer, R.id.nav_farms, R.id.nav_farm,
                R.id.nav_add_farm, R.id.nav_logout
        ).setDrawerLayout(drawer).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) logout();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Objects.requireNonNull(getSupportActionBar()).setSubtitle("");
        floatingBtn.setVisibility(View.GONE);
        fab.hide();
    }


    // Calling override method.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 99 && grantResults.length > 0) {

            boolean granted = grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED;

            toastMessage(granted ? "Permission Granted" : "Permission Denied");

        } else toastMessage("Permission Denied");
    }

    public void loadFragment(int navItem) {
        loadFragment(navItem, null);
    }

    public void loadFragment(int navItem, Bundle bundle) {
        if (bundle != null) navController.navigate(navItem, bundle);
        else navController.navigate(navItem);
        Objects.requireNonNull(getSupportActionBar()).setSubtitle("");
        floatingBtn.setVisibility(View.GONE);
        fab.hide();
    }

    public void toastMessage(String message) {
        Utility.toastMessage(this, message);
    }

    public void logout() {
        toastMessage("Logging out");
        Session session = new Session(this);
        session.setLoggedIn(false);
        final Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra("message", "Logged out");
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(intent);

            }
        }, 1000);
    }
}
