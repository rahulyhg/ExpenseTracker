package com.expensetracker.expensetracker;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.expensetracker.db.DBHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CategoryFragment.OnFragmentInteractionListener,
        AccountFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,
        IncomeFragment.OnFragmentInteractionListener,
        ReportFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private CharSequence mTitle;
    private TextView navAccountName;
    private CircularImageView navImageView;
    private DBHelper db;

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "Onstart");
        getAccounts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "OnResume");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navAccountName = (TextView) findViewById(R.id.tv_nav_account_name);
        navImageView = (CircularImageView) findViewById(R.id.civ_nav_image);

        setSupportActionBar(toolbar);

        getAccounts();
        // Adding First Screen / Default Fragment
        Fragment fragment = null;

        Class fragmentClass = HomeFragment.class;
        setTitle("Home");

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;

        Class fragmentClass = null;

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_acct:
                fragmentClass = AccountFragment.class;
                break;

            case R.id.nav_cat:
                fragmentClass = CategoryFragment.class;
                break;

            case R.id.nav_inc:
                fragmentClass = IncomeFragment.class;
                break;

            case R.id.nav_rep:
                fragmentClass = ReportFragment.class;
                break;
            case R.id.nav_set:
                break;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(fragmentClass.toString()).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        toolbar.setTitleTextAppearance(getApplicationContext(), android.R.style.TextAppearance_DeviceDefault_Medium);
        toolbar.setTitle(mTitle);
    }

    @Override
    public void onFragmentInteraction(CharSequence title) {
        setTitle(title);
    }

    public void getAccounts() {
        db = new DBHelper(this);

        byte[] image;
        StringBuilder sb = new StringBuilder();
        Cursor c = db.getAllAccounts();
        int count = 0;
        if (c.moveToFirst()) {
            count++;
            image = c.getBlob(c.getColumnIndex(DBHelper.ACCOUNT_COLUMN_IMAGE));
            if (image != null)
                navImageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0,
                        image.length));
            do {
                sb.append("(" + count + ") " + c.getString(c.getColumnIndex(DBHelper.ACCOUNT_COLUMN_NAME)) + "\n\n");
            } while (c.moveToNext());
            navAccountName.setText(sb.toString());
        }

    }
}
