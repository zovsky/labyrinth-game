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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements  NewGameFragment.OnFragmentInteractionListener,
                    ButtonsFragment.OnFragmentInteractionListener,
                    RulesFragment.OnFragmentInteractionListener,
                    ArticleFragment.OnFragmentInteractionListener,
                    BattleFragment.OnFragmentInteractionListener,
                    PreBattleFragment.OnFragmentInteractionListener {

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
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 10:
                        eatFood();
                        int foodTries = gamePref.getInt("foodTries", 0);
                        toolbar.getMenu().findItem(10).setEnabled(false);
                        editor.putInt("foodTries", foodTries-1).commit();
                        ArticleFragment fragment = (ArticleFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        fragment.redrawToolbar();
                        return true;
                    default:
                        return true;
                }
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
            int current = gamePref.getInt("currentArticle", 0);
            if (current < 1000) {
                showArticle(current);
            } else if (current > 2000) {
                showBattle(current);
            } else showPreArticle(current);
        }
    }

    private void eatFood() {
        changeFood(-1);
        changeVVV(4);
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

    public int takeChance() {
        int currentU = gamePref.getInt("UUU", 0);
        changeUUU(-1);
        Random rnd = new Random();
        int dice = rnd.nextInt(6)+rnd.nextInt(6)+2;
        if (dice <= currentU) {
            return 1;
        } else return -1;
    }

    public void debug() {
//        editor.putInt("LLL", 0);
//        editor.putInt("VVV", 0);
//        editor.putInt("UUU", 0);
//        editor.putInt("gold", 0);
//        editor.putInt("food", 8);
//        editor.putInt("gameOn", 0);
        Set<String> things=new HashSet<String>();
        things.add("Шлем");
        editor.putStringSet("things", things);
//        editor.putInt("currentArticle", 2);
        editor.commit();
        //showAllParameters();
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
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String paras = "para_" + article;
        String opts = "opt_" + article;
        int numberOfPara = Integer.valueOf(getResources().getString(getResources().getIdentifier(paras, "string", GAME)));
        int numberOfRadios = Integer.valueOf(getResources().getString(getResources().getIdentifier(opts, "string", GAME)));
        generateInitialMenu();
        Fragment fragment = ArticleFragment.newInstance(article, numberOfPara, numberOfRadios);
        ft.replace(R.id.fragment_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
    }
    public void showPreArticle(int article) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String paras = "para_" + article;
        String opts = "opt_" + article;
        int numberOfPara = Integer.valueOf(getResources().getString(getResources().getIdentifier(paras, "string", GAME)));
        int numberOfRadios = Integer.valueOf(getResources().getString(getResources().getIdentifier(opts, "string", GAME)));
        generateInitialMenu();
        Fragment fragment = PreBattleFragment.newInstance(article, numberOfPara, numberOfRadios);
        ft.replace(R.id.fragment_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();

    }
    public void showBattle(int article) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = BattleFragment.newInstance(article, 0, 0);
        ft.replace(R.id.fragment_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
    }

    public void setInitialParameters() {
        editor.putInt("LLL", 0);
        editor.putInt("VVV", 0);
        editor.putInt("UUU", 0);
        editor.putInt("gold", 10); //default 0
        editor.putInt("food", 8); //default 8
        editor.putInt("gameOn", 0);
        editor.putInt("stoneDown", 0); //stone for 24 and 284

        //TODO Remove on release
        addRoomToWasHere(163);
        editor.putInt("fatHitCount", 2);
        editor.commit();

        Set<String> things=new HashSet<>();
        things.add("Меч");//default меч
        things.add("Молот гномов"); //default фонарь
        editor.putStringSet("things", things);

        Set<String> keys=new HashSet<>();
        keys.add("17"); //no keys on start
        keys.add("21");
        keys.add("56");
        editor.putStringSet("keys", keys);
        editor.commit();
        //REMOVE on release
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
        //getMenuInflater().inflate(R.menu.menu_main, menu); //why is this commented?
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
        changeElixirCount(gamePref.getInt("elixirCounter", 0));

        ArrayList<String> thingsList = new ArrayList<String>();
        Set<String> things = gamePref.getStringSet("things", new HashSet<String>());
        for (String thing : things) {
            thingsList.add(thing);
        }
        addAllThingsToMenu(thingsList);

        ArrayList<String> keysList = new ArrayList<String>();
        Set<String> keys = gamePref.getStringSet("keys", new HashSet<String>());
        for (String key : keys) {
            keysList.add(key);
        }
        addAllKeysToMenu(keysList);
        setMenuItemsInactive();
    }

    public void setMenuItemsInactive() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem inventory = menu.getItem(0);
        SubMenu subMenu = inventory.getSubMenu();
        for (int i=0; i<subMenu.size(); i++) {
            subMenu.getItem(i).setEnabled(false);
        }
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
    public void changeElixirCount(int difference) {
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
        editor.putInt("elixirCounter", difference).commit();
        subMenu.add(1, 20, 20, elixirmenu);
        //TODO: когда и как использовать эликсир
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
    public void addAllKeysToMenu(ArrayList<String> keys) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem inventory = menu.getItem(0);
        SubMenu subMenu = inventory.getSubMenu();
        subMenu.removeGroup(3);
        for (int i = 0; i < keys.size(); i++) {
            subMenu.add(3, i+70, i+70, "Ключ на " + keys.get(i));
        }
    }
    public void addKeyNumber(String keyNumber) {
        ArrayList<String> keysList = new ArrayList<String>();
        Set<String> keys = gamePref.getStringSet("keys", new HashSet<String>());
        for (String item : keys) {
            keysList.add(item);
        }
        keysList.add(keyNumber);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem inventory = menu.getItem(0);
        SubMenu subMenu = inventory.getSubMenu();
        subMenu.removeGroup(3);
        editor.remove("keys");
        Set<String> newKeys = new HashSet<String>();
        for (int i = 0; i < keysList.size(); i++) {
            subMenu.add(3, i+70, i+70, "Ключ на " + keysList.get(i));
            newKeys.add(keysList.get(i));
        }
        editor.putStringSet("keys", newKeys).commit();
    }

    public void addThing(String thing) {
        ArrayList<String> thingsList = new ArrayList<String>();
        Set<String> things = gamePref.getStringSet("things", new HashSet<String>());
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
        Set<String> things = gamePref.getStringSet("things", new HashSet<String>());
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

    public boolean isThingAvailable(String thingToCheck) {
        Set<String> things = gamePref.getStringSet("things", null);
        for (String thing : things) {
            if (thing.equals(thingToCheck)) {
                return true;
            }
        }
        return false;
    }

    public void changeVVV(int difference) {
        int VVV = gamePref.getInt("VVV", 0) + difference;
        if (VVV > gamePref.getInt("startVVV", 0)) {
            editor.putInt("VVV", gamePref.getInt("startVVV", 0)).commit();
        } else {
            editor.putInt("VVV", VVV).commit();
        }
        if (VVV <= 0) {
            gameOver();
        }
    }
    //todo опустошить флягу в начале
    //TODO put all else in brackets
    public void changeUUU(int difference) {
        int UUU = gamePref.getInt("UUU", 0) + difference;
        if (UUU <= 0) {
            UUU = 0;
        }
        if (UUU > gamePref.getInt("startUUU", 0)) {
            editor.putInt("UUU", gamePref.getInt("startUUU", 0)).commit();
        } else {
            editor.putInt("UUU", UUU).commit();
        }
    }

    public boolean isAllowedToTakeChance() {
        if (gamePref.getInt("UUU", 0) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void changeLLL(int difference) {
        int LLL = gamePref.getInt("LLL", 0) + difference;
        if (LLL <= 1) {
            LLL = 1;
        }
        if (LLL > gamePref.getInt("startLLL", 0)) {
            editor.putInt("LLL", gamePref.getInt("startLLL", 0)).commit();
        } else {
            editor.putInt("LLL", LLL).commit();
        }
    }

    public void takeSpecialAction(int article) {
        if (article == 16 || article == 32 || article == 47 || article == 61 || article == 68 || article == 137 ||
                article == 248 || article == 293 || article == 296 || article == 339 || article == 374 ||
                article == 382) {
            if (gamePref.getInt("food", 0) > 0 && gamePref.getInt("foodTries", 0) > 0) {
                setOneMenuItemActive("Запасы еды");
            }
        }

        if (article == 136) {
            changeGold(-13);
        }
    }

    private void setOneMenuItemActive(String string) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem inventory = menu.getItem(0);
        SubMenu subMenu = inventory.getSubMenu();
        for (int i=0; i<subMenu.size(); i++) {
            MenuItem item = subMenu.getItem(i);
            if (item.getTitle().toString().contains(string)) {
                item.setEnabled(true);
                break;
            }
        }
    }

    public void takeAction(int article) {
        if (article == 12) {
            removeThing("Изумруд");
        }
        if (article == 13 || article == 88 || article == 263 || article == 291) {
            changeVVV(-2);
        }
        if (article == 15) {
            editor.putInt("goBackArticleID", article).commit();
            //int lakeSwimCount = gamePref.getInt("lakeSwimCount", 0);
            //editor.putInt("lakeSwimCount", lakeSwimCount + 1).commit();
            //TODO 266, marked on graph
        }
        if (article == 19) {
            changeFood(1);
            changeUUU(2);
        }
        if (article == 20) {
            editor.putInt("goBackArticleID", 316).commit();
        }
        //24 see 284
        if (article == 26) {
            changeGold(5 * gamePref.getInt("fatHitCount", 0));
        }
        if (article == 31) {
            changeGold(5);
        }
        if (article == 35) {
            changeGold(-10);
        }
        if (article == 37) {
            int slctd = gamePref.getInt("selectedRadio", 0);
            if (slctd == 0) {
                addThing("Металлический щит");
            }
            if (slctd == 1) {
                addThing("Парадный щит");
            }
            if (slctd == 2) {
                addThing("Кожаный щит");
            }
        }
        if (article == 41) {
            int slctd = gamePref.getInt("selectedRadio", 0);
            if (slctd == 1) {
                editor.remove("chanceInArticle41").commit();
            }
        }
        if (article == 45) {
            changeUUU(2);
            addThing("Банка с водой");
        }
        if (article == 49) {
            addThing("Книга");
        }
        if (article == 51) {
            addRoomToWasHere(247);
            addRoomToWasHere(270);
        }
        if (article == 55) {
            changeGold(-25);
            addThing("Деревянный кол");
            addThing("Банка ядовитой пыли");
        }
        if (article == 56 || article == 105 || article == 198 || article == 244 || article == 245) {
            changeVVV(-1);
        }
        if (article == 66) {
            changeGold(gamePref.getInt("savedGold66", 0));
            editor.remove("savedGold66").commit();
        }
        if (article == 81) {
            changeVVV(2);
            changeLLL(1);
        }
        //todo 86 see excel
        if (article == 89) {
            changeGold(3);
            changeUUU(2);
        }
        if (article == 96) {
            addKeyNumber("12");
            changeUUU(2);
        }
        if (article == 98) {
            editor.putInt("extraLLL", 3).commit();
        }
        if (article == 142) {
            addKeyNumber("93");
        }
        if (article == 151) {
            addRoomToWasHere(38);
        }
        if (article == 166) {
            changeVVV(3);
            changeLLL(2);
        }
        if (article == 199) {
            changeGold(20);
        }
        if (article == 250) {
            editor.remove("extraLLL").commit();
        }
        if (article == 254) {
            changeGold(10);
        }

        if (article == 284 || article == 24) {
            int selectedRadio = gamePref.getInt("selectedRadio", 0);
            if (selectedRadio == 0) {
                editor.remove("chanceInArticle284");
                editor.remove("chanceInArticle24");
                editor.commit();
            } else {
                if (gamePref.getInt("stoneDown", 0) == 0){
                    editor.putInt("chanceInArticle284", 2);
                    editor.putInt("chanceInArticle24", 2);
                    changeVVV(-2);
                    editor.putInt("stoneDown", 1);
                    editor.commit();
                }
            }
        }
        if (article == 292) {
            int slctd = gamePref.getInt("selectedRadio", 0);
            if (slctd == 0) {
                removeThing("Банка ядовитой пыли");
            } else removeThing("Всеядные ракообразные");
            Random rnd = new Random();
            int r = rnd.nextInt(6);
            if (r <=2) {
                editor.putInt("breakIntoArticle", 58).commit();
                return;
            } else {
                editor.putInt("breakIntoArticle", 168).commit();
                return;
            }
        }
        if (article == 318) {
            changeVVV(-3);
            changeLLL(-2);
        }
        if (article == 326) {
            changeUUU(3);
            changeGold(30);
        }
        if (article == 364) {
            addRoomToWasHere(200);
            addRoomToWasHere(224);
        }
        if (article == 376) {
            changeLLL(1);
            changeUUU(2);
            return;
        }

        if (article == 380) {
            changeLLL(1);
        }
        if (article == 385) {
            addRoomToWasHere(53);
        }
    }

    public boolean wasIHere(int article) {
        Set<String> set = gamePref.getStringSet("wasHere", new HashSet<String>());
        for (String room : set) {
            if (room.equals(Integer.toString(article))) {
                return true;
            }
        }
        return false;
    }

    public void gameOver() {
        editor.putInt("gameOn", 0).commit();
        Toast.makeText(this, "Конец игры", Toast.LENGTH_SHORT).show();
    }

    public void addRoomToWasHere(int roomToAdd) {
        Set<String> set = gamePref.getStringSet("wasHere", new HashSet<String>());
        set.add(Integer.toString(roomToAdd));
        editor.putStringSet("wasHere", set);
        editor.commit();
    }

    public int howManyKeys() {
        ArrayList<String> keysList = new ArrayList<String>();
        Set<String> keys = gamePref.getStringSet("keys", new HashSet<String>());
        return keys.size();
    }
}