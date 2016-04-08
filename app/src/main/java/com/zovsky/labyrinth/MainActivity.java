package com.zovsky.labyrinth;

import android.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private String[] mDrawerItems;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                return false;
            }
        });
        toolbar.inflateMenu(R.menu.menu_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //mDrawerList = (NavigationView) findViewById(R.id.left_drawer);
        //mDrawerItems = getResources().getStringArray(R.array.drawer_array);


//        //Set the adapter for the list view
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this, null, mDrawerItems));
//
//        // Set the list's click listener
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
