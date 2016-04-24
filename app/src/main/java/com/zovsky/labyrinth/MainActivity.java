package com.zovsky.labyrinth;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements  NewGameFragment.OnFragmentInteractionListener,
                    ButtonsFragment.OnFragmentInteractionListener,
                    RulesFragment.OnFragmentInteractionListener,
                    ArticleFragment.OnFragmentInteractionListener {

    private final static String GAME = "com.zovsky.labyrinth";

    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerList;
    private Toolbar toolbar;
    private ActionMenuItemView inventory;
    public SharedPreferences gamePref;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gamePref = getSharedPreferences(GAME, Context.MODE_PRIVATE);
        editor = gamePref.edit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu_main);
        setInventoryVisibility(false);

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
                                if (gamePref.getInt("gameOn", 0) == 1) {
                                    showAlert();
                                    return true;
                                }
                                showNewGameFragment();
                                return true;
                            default:
                                return true;
                        }
                    }
                });

//
        if (gamePref.getInt("gameOn", 0) == 0) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = new ButtonsFragment();
            ft.replace(R.id.fragment_container, fragment);
            //ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            ft.commit();
        } else {
            showArticle(gamePref.getInt("currentArticle", 0));
        }
    }

    private void showAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LinearLayout layout       = new LinearLayout(this);
        TextView tvMessage        = new TextView(this);

        tvMessage.setText("Вы действительно хотите начать заново? Текущая игра будет потеряна.");
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(tvMessage);
        tvMessage.setPadding(50, 20, 50, 0);
        alert.setTitle("Внимание");
        alert.setView(layout);


        alert.setNegativeButton("Нет", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alert.setPositiveButton("Да", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.clear();
                editor.commit();
                showNewGameFragment();
            }
        });
        alert.show();
    }

    public void setToolbarTitle(String title, String subtitle) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setSubtitle(subtitle);
    }

    public void setInventoryVisibility(boolean visibility) {
        inventory = (ActionMenuItemView) findViewById(R.id.action_settings);
        if (visibility == false) {
            inventory.setVisibility(View.INVISIBLE);
        } else {
            inventory.setVisibility(View.VISIBLE);
        }

    }

    public void showRulesWebView() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new RulesFragment();
        Fragment myFragment = fm.findFragmentByTag("rules");
        if (!(myFragment instanceof RulesFragment)) {
            ft.replace(R.id.fragment_container, fragment, "rules");
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            ft.commit();
        }
    }

    public void showNewGameFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new NewGameFragment();
        ft.replace(R.id.fragment_container, fragment);
        //ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
    }

    public void showArticle(int article) {
        generateInitialMenu();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String paras = "para_" + article;
        String opts = "opt_" + article;
        int numberOfPara = Integer.valueOf(getResources().getString(getResources().getIdentifier(paras, "string", GAME)));
        int numberOfRadios = Integer.valueOf(getResources().getString(getResources().getIdentifier(opts, "string", GAME)));
        Fragment fragment = ArticleFragment.newInstance(article, numberOfPara, numberOfRadios);
        ft.replace(R.id.fragment_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
    }

    public void setInitialParameters() {
        editor.putInt("LLL", 0);
        editor.putInt("VVV", 0);
        editor.putInt("UUU", 0);
        editor.putInt("gold", 0);
        editor.putInt("food", 8);
        editor.putInt("gameOn", 0);
        Set<String> things=new HashSet<String>();
        things.add("Меч");
        things.add("Фонарь");
        editor.putStringSet("things", things);
        editor.commit();
    }

    public void showAllParameters() {
        Map<String, ?> allEntries = gamePref.getAll();
        Log.d(GAME, "-----------------");
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d(GAME, entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 ){
            getSupportFragmentManager().popBackStack();
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            super.onBackPressed();
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
        public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
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

    public void generateInitialMenu() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem inventory = menu.getItem(0);
        SubMenu subMenu = inventory.getSubMenu();
        subMenu.clear();
        changeFood(0);
        changeGold(0);
        changeElixir(gamePref.getInt("elixirCounter", 0));

        ArrayList<String> thingsList = new ArrayList<String>();
        Set<String> things = gamePref.getStringSet("things", null);
        for (String thing : things) {
            thingsList.add(thing);
        }
        addAllThingsToMenu(thingsList);
    }

    public void changeFood(int difference) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem inventory = menu.getItem(0);
        SubMenu subMenu = inventory.getSubMenu();
        subMenu.removeItem(10);
        int food = gamePref.getInt("food", 0) + difference;
        String menufood = "Запасы еды: " + food;
        editor.putInt("food", food).commit();
        subMenu.add(1, 10, 10, menufood);
    }

    public void changeGold(int difference) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem inventory = menu.getItem(0);
        SubMenu subMenu = inventory.getSubMenu();
        subMenu.removeItem(30);
        int gold = gamePref.getInt("gold", 0) + difference;
        String menugold = "Золото: " + gold;
        editor.putInt("gold", gold).commit();
        if (gold > 0) {
            subMenu.add(1, 30, 30, menugold);
        }
    }
    //TODO: if some parameters get to 0
    public void changeElixir(int count) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem inventory = menu.getItem(0);
        SubMenu subMenu = inventory.getSubMenu();
        subMenu.removeItem(20);
        String elixirmenu;
        int elixir = gamePref.getInt("elixir", 0);
        if (elixir == 1) {
            elixirmenu = "Эликсир ловкости: " + gamePref.getInt("elixirCounter", 0);
        } else if (elixir == 2) {
            elixirmenu = "Эликсир выносливости: " + gamePref.getInt("elixirCounter", 0);
        } else {
            elixirmenu = "Эликсир удачи: " + gamePref.getInt("elixirCounter", 0);
        }
        editor.putInt("elixirCounter", count).commit();
        subMenu.add(1, 20, 20, elixirmenu);
    }

    public void addAllThingsToMenu(ArrayList<String> things) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem inventory = menu.getItem(0);
        SubMenu subMenu = inventory.getSubMenu();
        subMenu.removeGroup(2);
        for (int i = 0; i < things.size(); i++) {
            subMenu.add(2, i+40, i+40, things.get(i));
        }
    }

    public void addThing(String thing) {
        ArrayList<String> thingsList = new ArrayList<String>();
        Set<String> things = gamePref.getStringSet("things", null);
        for (String item : things) {
            thingsList.add(item);
        }
        thingsList.add(thing);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem inventory = menu.getItem(0);
        SubMenu subMenu = inventory.getSubMenu();
        subMenu.removeGroup(2);
        editor.remove("things");
        Set<String> newThings = new HashSet<String>();
        for (int i = 0; i < thingsList.size(); i++) {
            subMenu.add(2, i+40, i+40, thingsList.get(i));
            newThings.add(thingsList.get(i));
        }
        editor.putStringSet("things", newThings).commit();
    }

    public void removeThing(String thing) {
        ArrayList<String> thingsList = new ArrayList<String>();
        Set<String> things = gamePref.getStringSet("things", null);
        for (String item : things) {
            thingsList.add(item);
        }
        for (int i = 0; i < thingsList.size(); i++) {
            if (thingsList.get(i).equals(thing)) {
                thingsList.remove(i);
                break;
            };
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem inventory = menu.getItem(0);
        SubMenu subMenu = inventory.getSubMenu();
        subMenu.removeGroup(2);
        editor.remove("things");
        Set<String> newThings = new HashSet<String>();
        for (int i = 0; i < thingsList.size(); i++) {
            subMenu.add(2, i+40, i+40, thingsList.get(i));
            newThings.add(thingsList.get(i));
        }
        editor.putStringSet("things", newThings).commit();
    }

    public void changeVVV(int difference) {
        int VVV = gamePref.getInt("VVV", 0) + difference;
        if (VVV > gamePref.getInt("startVVV", 0)) {
            editor.putInt("VVV", gamePref.getInt("startVVV", 0)).commit();
        } else editor.putInt("VVV", VVV).commit();
        if (VVV == 0) {
            //TODO: gameOver();
        }
    }

    public void takeAction(int article) {
        if (article == 105 || article == 56) {
            changeVVV(-1);
        }
    }

    public void allowAction(int article) {
        //TODO: Если есть в xml, то надувать одноразовую кнопку между абзацами
    }

    public int wasIHere(int article) {
        if (article == 200) {
            Set<String> set = gamePref.getStringSet("wasHere", new HashSet<String>());
            for (String room : set) {
                if (room.equals(Integer.toString(article))) {
                    return 1000;
                }
            }
            Set<String> room = new HashSet<String>();
            room.add(Integer.toString(article));
            editor.putStringSet("wasHere", room);
            editor.commit();
            showAllParameters();
            return article;
        }
        return 3000;
    }
}