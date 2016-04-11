package com.zovsky.labyrinth;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements  NewGameFragment.OnFragmentInteractionListener,
                    ButtonsFragment.OnFragmentInteractionListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerList;
    private Toolbar toolbar;
    private FragmentManager fm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                return false;
//            }
//        });
//        toolbar.inflateMenu(R.menu.menu_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (NavigationView) findViewById(R.id.left_drawer);
        mDrawerList.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //Closing drawer on item click
                        mDrawerLayout.closeDrawers();

                        //Check to see which item was clicked and perform the appropriate action.
                        switch (menuItem.getItemId()) {

                            case R.id.rules:
                                showRulesWebView();
                                return true;

                            case R.id.new_game:
                                showNewGameFragment();
                                return true;

                            default:
                                return true;
                        }
                    }
                });

//

        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new ButtonsFragment();
        ft.add(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        //setTransition()
        ft.commit();
    }

    public class MyPagerAdapter {
        private int NUM_ITEMS = 400;

        public MyPagerAdapter(FragmentManager fragmentManager) {
        }

        public Fragment getItem(int page) {
            switch (page) {
                case 400: // Fragment 400 - Buttons
                    showRulesWebView();
                    //return null;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    //return NewGameFragment.newInstance(1, "Page # 2");
                case 2: // Fragment # 1 - This will show SecondFragment
                    //return NewGameFragment.newInstance(2, "Page # 3");
                default:
                    return ButtonsFragment.newInstance(null,null);
            }
        }
    }

    public void showRulesWebView() {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new NewGameFragment();
        ft.add(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        //setTransition()
        ft.commit();
    }

    public void showNewGameFragment() {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new NewGameFragment();
        ft.replace(R.id.fragment_buttons, fragment);
        ft.addToBackStack(null);
        //setTransition()
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 ){
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

//    @Override
//    public void onBackPressed() {
//        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
//            super.onBackPressed();
//        } else {
//            mDrawerLayout.openDrawer(GravityCompat.START);
//
//        }
//    }

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