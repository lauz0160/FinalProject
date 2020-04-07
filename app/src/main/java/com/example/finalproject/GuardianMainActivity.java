package com.example.finalproject;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;


public class GuardianMainActivity extends AppCompatActivity { //implements NavigationView.OnNavigationItemSelectedListener

    private Guardian_Search searchFragment;

    /**
     * @param savedInstanceState is called when the activity first starts up.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.guardian_mainactivity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerSlide = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.addDrawerListener(drawerSlide);
        drawerSlide.syncState();

        //sets up top level navigation for app
        NavigationView navigationView = findViewById(R.id.navigationView);

        //https://www.codota.com/code/java/methods/android.support.v4.app.FragmentActivity/getSupportFragmentManager
        searchFragment = new Guardian_Search();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, searchFragment)
                .commit();
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.drawerMenuSearch:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, searchFragment)
                            .commit();
                    break;

                case R.id.drawerMenuSaved:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Guardian_Fav())
                            .commit();
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    /**
     * @param item called when user clicks on the item in the options menu
     * @return
     */
    //https://stackoverflow.com/questions/38195522/what-is-oncreateoptionsmenumenu-menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //toolbar options
        switch (item.getItemId()) {
            case R.id.menuAuthor:
                showAuthorMenu();
                return true;

            case R.id.menuHelp:
                showHelpMenu();
                return true;

            case R.id.menuVersion:
                showHelpVersion();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    /**
     *  corresponding alert pops -> click
     */
    private void showAuthorMenu() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.author_title));
        alertDialog.setMessage(getString(R.string.author_text));
        alertDialog.setPositiveButton(getString(R.string.author_exit), (dialog, which) -> {
        });
        alertDialog.create().show();
    }

    private void showHelpMenu() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.help_title));
        alertDialog.setMessage(getString(R.string.help_text));
        alertDialog.setPositiveButton(getString(R.string.got_it), (dialog, which) -> {
        });
        alertDialog.create().show();
    }

    private void showHelpVersion() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.version_title));
        alertDialog.setMessage(getString(R.string.help_version));
        alertDialog.setPositiveButton(getString(R.string.got_it), (dialog, which) -> {
        });
        alertDialog.create().show();

    }

}
