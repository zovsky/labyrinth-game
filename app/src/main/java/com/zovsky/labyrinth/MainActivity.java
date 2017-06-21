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
        int currentU = gamePref.getInt("KKK", 0);
        changeKKK(-1);
        Random rnd = new Random();
        int dice = rnd.nextInt(6)+rnd.nextInt(6)+2;
        if (dice <= currentU) {
            return 1;
        } else return -1;
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
        editor.putInt("KKK", 0); //todo поменять Удачу на Карму, использовать либо Карму игрока, либо шанс 50/50.
        editor.putInt("extraLLL", 0);
        editor.putInt("gold", 1); //default 0
        editor.putInt("food", 8); //default 8
        editor.putInt("gameOn", 0);
        editor.putInt("stoneDown", 0); //stone for 24 and 284

        //TODO Remove on release
        addRoomToWasHere(122);
        //editor.putInt("fatHitCount", 2);
        editor.commit();

        Set<String> things=new HashSet<>();
        things.add("Боевой шлем");//default меч
        things.add("Бутылка с водой"); //default фонарь
        editor.putStringSet("things", things);

        Set<String> keys=new HashSet<>();
        //keys.add("12"); //no keys on start
        //keys.add("70");

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
        if (gold < 0) {
            gold = 0;
        }
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
    public boolean isKeyAvailable(String keyToCheck) {
        Set<String> keys = gamePref.getStringSet("keys", new HashSet<String>());
        for (String item : keys) {
            if (item.contains(keyToCheck)) {
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
    public void changeKKK(int difference) {
        int KKK = gamePref.getInt("KKK", 0) + difference;
        if (KKK <= 0) {
            KKK = 0;
        }
        if (KKK > gamePref.getInt("startKKK", 0)) {
            editor.putInt("KKK", gamePref.getInt("startKKK", 0)).commit();
        } else {
            editor.putInt("KKK", KKK).commit();
        }
    }

    public void changeExtraLLL(int difference) {
        int newExtraLLL = gamePref.getInt("extraLLL", 0) + difference;
        editor.putInt("extraLLL", newExtraLLL).commit();
    }

    public boolean isAllowedToTakeChance() {
        if (gamePref.getInt("KKK", 0) == 0) {
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
        if (article == 16 || article == 20 || article == 27 || article == 32 || article == 47 || article == 61 || article == 68 ||
                article == 110 || article == 114 || article == 137 || article == 141 || article == 144 ||
                article == 248 || article == 269 || article == 273 || article == 293 || article == 296 || article == 339 || article == 374 ||
                article == 382) {
            if (gamePref.getInt("food", 0) > 0 && gamePref.getInt("foodTries", 0) > 0) {
                setOneMenuItemActive("Запасы еды");
            }
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
        if (article == 3) {
            int slctd = gamePref.getInt("selectedRadio", 0);
            if (slctd == 0) {
                changeGold(-13);
            }
        }
        if (article == 6) {
            addRoomToWasHere(336);
            changeKKK(1);
        }
        if (article == 11) {
            addRoomToWasHere(331);
        }
        if (article == 12) {
            removeThing("Изумруд (Дар Крыльев)");
        }
        if (article == 13 || article == 291 || article == 332) {
            changeVVV(-2);
        }
        if (article == 19) {
            changeFood(1);
            changeKKK(2);
        }
        if (article == 20) {
            editor.putInt("goBackArticleID", 156).commit();
        }
        //24 see 284
        if (article == 26) {
            changeGold(5 * gamePref.getInt("fatHitCount", 0));
        }
        if (article == 27) {
            editor.putInt("goBackArticleID", 316).commit();
        }
        if (article == 31) {
            changeGold(5);
            changeFood(-1);
            changeVVV(4);
            changeLLL(1);
            changeKKK(1);
        }
        //todo 32 alter text if come from 377 e.g. что нельзя забрать сеть и кол
        if (article == 35) {
            changeGold(-10);
        }
        if (article == 37) {
            int slctd = gamePref.getInt("selectedRadio", 0);
            if (slctd == 0) {
                addThing("Металлический щит");
            }
            if (slctd == 1) {
                addThing("Деревянный щит");
            }
            if (slctd == 2) {
                addThing("Кожаный щит");
            }
            addRoomToWasHere(37);
        }
        if (article == 41) {
            int slctd = gamePref.getInt("selectedRadio", 0);
            if (slctd == 1) {
                editor.remove("chanceInArticle41").commit();
            }
        }
        if (article == 45) {
            changeKKK(2);
            addThing("Бутылка с водой");
        }
        if (article == 51) {
            addRoomToWasHere(247);
            addRoomToWasHere(270);
        }
        if (article == 52) {
            int slctd = gamePref.getInt("selectedRadio", 0);
            if (slctd == 0) {
                removeThing("Герметичный шлем");
            }
        }
        if (article == 54) {
            removeThing("Молот гномов");
        }
        if (article == 55) {
            changeGold(-25);
            addThing("Деревянный кол");
            addThing("Банка ядовитой пыли");
        }
        if (article == 56 || article == 74 || article == 105 || article == 198 || article == 244 || article == 245) {
            changeVVV(-1);
        }
        if (article == 57) {
            int slctd = gamePref.getInt("selectedRadio", 0);
            if (slctd == 0) {
                changeGold(-10);
            }
        }
        if (article == 66) {
            changeGold(gamePref.getInt("savedGold66", 0));
            editor.remove("savedGold66").commit();
        }
        if (article == 71) {
            addRoomToWasHere(71);
        }
        //74 see 56
        if (article == 76) {
            addToSelectedCombinations(gamePref.getInt("selectedRadio", 0));
        }
        if (article == 81) {
            changeVVV(2);
            changeLLL(1);
        }
        if (article == 88) {
            changeVVV(-2);
            addThing("Сеть");
            editor.putInt("lakeSwimCount", 2).commit();
        }
        //todo 86 see excel
        if (article == 89) {
            changeGold(3);
            changeKKK(2);
            addRoomToWasHere(224);
        }
        if (article == 95) {
            int slctd = gamePref.getInt("selectedRadio", 0);
            if (slctd == 1) {
                changeExtraLLL(-1);
            }
            addRoomToWasHere(95);
            //todo убрать -1 extra после ближайшей битвы
        }
        if (article == 96) {
            addKeyNumber("12");
            changeKKK(2);
        }
        if (article == 98) {
            changeExtraLLL(3);
        }
        if (article == 104) {
            changeGold(30);
            addThing("Соска");
            removeThing("Жестяная бабочка");
        }
        if (article == 110) {
            changeLLL(-3);
            int vvv = gamePref.getInt("VVV", 0);
            int changeV = vvv/2;
            changeVVV(-changeV);
        }
        if (article == 115) {
            int count = gamePref.getInt("weaponSelectionCount", 0);
            editor.putInt("weaponSelectionCount", count + 1);
        }
        if (article == 119) {
            int slctd = gamePref.getInt("selectedRadio", 0);
            if (slctd == 0) {
                addThing("Канат с крюком");
                addThing("Пустая бутылка"); //todo может быть две пустые бутылки? 343
            } else if (slctd == 1) {
                addThing("Канат с крюком");
                addThing("Скальп Оборотня");
            } else {
                addThing("Скальп Оборотня");
                addThing("Пустая бутылка");
            }
        }
        if (article == 122) {
            removeThing("Бутылка с водой");
            addThing("Пустая бутылка");
        }
        if (article == 127) {
            changeKKK(2);
            changeExtraLLL(2);
            editor.putInt("lakeSwimCount", 1).commit();
        }
        if (article == 128) {
            addThing("Связка ключей");
        }
        if (article == 136) {
            changeGold(-13);
        }
        if (article == 138) {
            removeThing("Соска");
        }
        if (article == 142) {
            addKeyNumber("93");
        }
        if (article == 143 || article == 174 || article == 315) {
            int slctd = gamePref.getInt("selectedRadio", 0);
            if (slctd == 1) {
                changeLLL(-1);
                changeVVV(-3);
            }
        }
        if (article == 144) {
            changeKKK(1);
        }
        if (article == 145) {
            changeGold(-gamePref.getInt("loseGold145", 0));
            editor.remove("loseGold145").commit();
        }
        if (article == 147) {
            addThing("Деревянный кол");
            addThing("Банка ядовитой пыли");
        }
        if (article == 151) {
            changeVVV(5);
            changeLLL(2);
            changeKKK(3);
        }
        if (article == 151) {
            addRoomToWasHere(38);
        }
        if (article == 153) {
            changeKKK(1);
            changeExtraLLL(1);
            removeThing("Меч");
            addThing("Заколдованный меч");
        }
        if (article == 162) {
            changeVVV(-1);
        }
        if (article == 163) {
            changeKKK(1);
            addRoomToWasHere(163);
        }
        if (article == 166) {
            changeVVV(3);
            changeLLL(2);
        }
        if (article == 172) {
            int slctd = gamePref.getInt("selectedRadio", 0);
            if (slctd == 0) {
                changeGold(-20);
            }
        }
        //174 see 143
        if (article == 177) {
            changeKKK(3);
            addThing("Огненное ядро");
        }
        if (article == 180) {
            addRoomToWasHere(180);
            if (!isKeyAvailable("45")) {
                addKeyNumber("45");
            }
        }
        if (article == 181) {
            removeThing("Связка ключей");
        }
        if (article == 190) {
            editor.remove("chanceInArticle190").commit();
        }
        if (article == 192) {
            int selectedRadio = gamePref.getInt("selectedRadio", 0);
            if (selectedRadio == 1) {
                addThing("Кость чудовища");
            }
            if (selectedRadio == 2) {
                addThing("Банка с всеядными");
            }
            if (selectedRadio == 3) {
                addThing("Жестяная бабочка");
            }
            if (selectedRadio == 4) {
                addThing("Копье");
            }
        }
        if (article == 193) {
            changeGold(5 * gamePref.getInt("killedMonsters", 0));
            changeKKK(2);
            changeLLL(2);
        }
        if (article == 199) {
            changeGold(20);
        }
        if (article == 201) {
            changeGold(gamePref.getInt("winGold201", 0));
            editor.remove("winGold201").commit();
        }
        if (article == 207) {
            if (isThingAvailable("Заколдованная вода")) {
                changeLLL(3);
                changeVVV(3);
            }
        }
        if (article == 211) {
            addRoomToWasHere(18);
        }
        if (article == 216) {
            removeThing("Металлический щит"); //todo оборотень против волкодава
        }
        if (article == 220) {
            changeLLL(1);
            editor.putInt("lakeSwimCount", 2).commit();
        }
        if (article == 225) {
            changeGold(-10);
        }
        if (article == 229) {
            editor.remove("chanceInArticle229").commit();
        }
        if (article == 231) {
            addRoomToWasHere(239);
        }
        if (article == 237) {
            int tried = gamePref.getInt("triedCombinations", 0);
            editor.putInt("triedCombinations", tried + 1).commit();
        }
        if (article == 240) {
            int loseAmount = gamePref.getInt("loseAmount240", 0);
            changeVVV(-loseAmount);
        }
        if (article == 241) {
            int selectedRadio = gamePref.getInt("selectedRadio", 0);
            if (selectedRadio == 1) {
                if (isThingAvailable("Пустая бутылка")) {
                    removeThing("Пустая бутылка");
                    addThing("Бутылка с водой");
                } else if (isThingAvailable("Бутылка с эликсиром невидимости")) {
                    removeThing("Бутылка с эликсиром невидимости");
                    addThing("Бутылка с водой");
                }
            }
        }
        if (article == 250) {
            changeExtraLLL(-3);
        }
        if (article == 254) {
            changeGold(10);
        }
        if (article == 262) {
            int previousArticle = gamePref.getInt("previousArticle", 0);
            if (previousArticle == 60) {
                changeGold(20);
            } else changeGold(10);
        }
        if (article == 263) {
            changeVVV(-2);
            addThing("Изумруд (Дар Крыльев)");
        }
        if (article == 265) {
            addRoomToWasHere(310);
        }
        if (article == 269) {
            changeVVV(-2);
            removeThing("Канат с крюком");
        }
        if (article == 271) {
            changeVVV(-1);
            addRoomToWasHere(271);
        }
        if (article == 278) {
            addKeyNumber("122");
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
        if (article == 289) {
            changeGold(20);
            removeThing("Деревянный кол");
        } //291
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
        if (article == 294) {
            changeKKK(2);
            addThing("Бутылка с эликсиром невидимости");
        }
        if (article == 298) {
            int slctd = gamePref.getInt("selectedRadio", 0);
            if (slctd == 0) {
                addThing("Боевой шлем");
            }
            if (slctd == 1) {
                addThing("Герметичный шлем");
            }
            addRoomToWasHere(298);
        }
        if (article == 306) {
            changeKKK(2);
            if (isThingAvailable("Пустая бутылка")) {
                removeThing("Пустая бутылка");
                addThing("Бутылка с водой");
            } else if (isThingAvailable("Бутылка с эликсиром невидимости") || isThingAvailable("Бутылка с водой")) {
                removeThing("Бутылка с эликсиром невидимости");
                addThing("Бутылка с водой");
            }
        }
        if (article == 307) {
            changeExtraLLL(-2);
        }
        //315 see 143
        if (article == 316) {
            addRoomToWasHere(176);
        }
        if (article == 317) {
            addRoomToWasHere(268);
        }
        if (article == 318) {
            changeVVV(-3);
            changeLLL(-2);
        }
        if (article == 324) {
            addRoomToWasHere(324);
            addThing("Молот Гномов");
        }
        if (article == 326) {
            changeKKK(3);
            changeGold(30);
        }
        if (article == 329) {
            addKeyNumber("70");
            removeThing("Канат с крюком");
            changeKKK(1);
        }
        if (article == 330) {
            addRoomToWasHere(319);//todo 319: allow second entry but with no battle at 69
        }
        //332 see 13
        if (article == 334) {
            changeGold(gamePref.getInt("savedGold334", 0));
            editor.putInt("savedGold334", 0).commit();
        }
        if (article == 343) {
            if (!isThingAvailable("Пустая бутылка")) {
                addThing("Пустая бутылка");
            }
        }
        if (article == 344) {
            changeExtraLLL(2);
        }
        if (article == 346) {
            changeVVV(-1);
            removeThing("Молот гномов");
        }
        if (article == 355) {
            addRoomToWasHere(304);
        }
        if (article == 358) {
            removeThing("Бутылка с эликсиром невидимости");
        }
        if (article == 364) {
            addRoomToWasHere(224);
        }
        if (article == 369) {
            addThing("Соска");
            removeThing("Деревянный кол");
        }
        if (article == 371) {
            int slctd = gamePref.getInt("selectedRadio", 0);
            if (slctd == 0) {
                removeThing("Бутылка с водой");
                addThing("Пустая бутылка");
            }
        }
        if (article == 376) {
            changeLLL(1);
            changeKKK(2);
            return; //todo why return here?
        }
        if (article == 377) {
            int slctd = gamePref.getInt("selectedRadio", 0);
            if (slctd == 0) {
                removeThing("Деревянный кол");
            }
            removeThing("Сеть");
        }
        //todo check 378 text
        if (article == 380) {
            changeLLL(1);
            addRoomToWasHere(164);
        }
        if (article == 385) {
            addRoomToWasHere(53);
        }
        //todo: решетка, если идти на запад (см. блокнот) + 103
        if (article == 500) {
            gameOver(); //todo show buttonfragment
        }
    }

    private void addToSelectedCombinations(int slctd) {
        Set<String> set = gamePref.getStringSet("selectedCombinations", new HashSet<String>());
        set.add(Integer.toString(slctd));
        editor.putStringSet("selectedCombinations", set);
        editor.commit();
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
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new ButtonsFragment();
        ft.replace(R.id.fragment_container, fragment);
        //ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
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