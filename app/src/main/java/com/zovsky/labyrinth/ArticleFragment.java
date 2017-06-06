package com.zovsky.labyrinth;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArticleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArticleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleFragment extends Fragment {

    private final static String GAME = "com.zovsky.labyrinth";

    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    //private SharedPreferences gamePref;
    //private SharedPreferences.Editor editor;

    private int mArticle;
    private int mParas;
    private int mRadios;

    private int[] choice;

    private OnFragmentInteractionListener mListener;

    public ArticleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param article Parameter 1.
     * @param numberOfPara Parameter 2.
     * @param numberOfRadios Parameter 3.
     * @return A new instance of fragment ArticleFragment.
     */
    public static ArticleFragment newInstance(int article, int numberOfPara, int numberOfRadios) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, article);
        args.putInt(ARG_PARAM2, numberOfPara);
        args.putInt(ARG_PARAM3, numberOfRadios);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mArticle = getArguments().getInt(ARG_PARAM1);
            mParas = getArguments().getInt(ARG_PARAM2);
            mRadios = getArguments().getInt(ARG_PARAM3);
        }
        choice = new int[mRadios];
        ((MainActivity)getActivity()).editor.putInt("currentArticle", mArticle).commit();
        ((MainActivity)getActivity()).editor.putInt("breakIntoArticle", 0).commit();
        ((MainActivity)getActivity()).showAllParameters();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //show inventory button
        ((MainActivity)getActivity()).setInventoryVisibility(true);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.getMenu().getItem(0).setTitle(R.string.action_settings).setEnabled(true);
        //set LVU as toolbar title
        String toolbarTitle = "Л:" + ((MainActivity)getActivity()).gamePref.getInt("LLL",0) +
                                " В:" + ((MainActivity)getActivity()).gamePref.getInt("VVV",0) +
                                " У:" + ((MainActivity)getActivity()).gamePref.getInt("UUU",0);
        ((MainActivity) getActivity()).setToolbarTitle(toolbarTitle, Integer.toString(mArticle));

        View view = inflater.inflate(R.layout.fragment_article, container, false);

        TextView[] textView = new TextView[mParas];
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.article_layout);
        String articleID, optionID, textID;

        int paraStartCount = 0;
        int radioStartCount = 0;

        if (mArticle == 15) {
            if (((MainActivity) getActivity()).gamePref.getInt("lakeSwimCount", 0) == 0) {
                ((MainActivity) getActivity()).editor.putInt("goBackArticleID", 113).commit();
            } else if (((MainActivity) getActivity()).gamePref.getInt("lakeSwimCount", 0) == 1) {
                ((MainActivity) getActivity()).editor.putInt("goBackArticleID", 283).commit();
            } else {
                ((MainActivity) getActivity()).editor.putInt("goBackArticleID", 139).commit();
            }
        }
        //check wasHere in another article
        if (mArticle == 59) {
            ((MainActivity) getActivity()).showAllParameters(); //todo remove
            boolean was163 = ((MainActivity) getActivity()).wasIHere(163);
            if (was163) {
                mRadios = 1;
            } else if (!was163) {
                radioStartCount = 1;
            }
        }
        if (mArticle == 37 || mArticle == 95 || mArticle == 153 || mArticle == 298 || mArticle == 324) {
            if (((MainActivity) getActivity()).gamePref.getInt("weaponSelectionCount", 0) == 2) {
                ((MainActivity) getActivity()).editor.putInt("goBackArticleID", 68).commit();
            } else {
                ((MainActivity) getActivity()).editor.putInt("goBackArticleID", 115).commit();
            }
        }
        if (mArticle == 164) {
            ((MainActivity) getActivity()).showAllParameters();
            boolean was164 = ((MainActivity) getActivity()).wasIHere(164);
            if (was164) {
                mParas = 1;
            }
        }
        if (mArticle == 198 || mArticle == 281) {
            if (((MainActivity) getActivity()).isThingAvailable("Соска")) {
                ((MainActivity) getActivity()).editor.putInt("goBackArticleID", 138).commit();
            } else {
                ((MainActivity) getActivity()).editor.putInt("goBackArticleID", 255).commit();
            }
        }
        if (mArticle == 207) {
            if (!((MainActivity) getActivity()).isThingAvailable("Заколдованная вода")) {
                mParas = 6;
            }
        }
        if (mArticle == 401) {
            ((MainActivity) getActivity()).showAllParameters();
            boolean was122 = ((MainActivity) getActivity()).wasIHere(122);
            if (was122) {
                mParas = 1;
                mRadios = 1;
            } else if (!was122) {
                paraStartCount = 1;
                radioStartCount = 1;
            }
        }

        //generate article paragraphs
        for (int para = paraStartCount; para < mParas; para++) {
            textView[para] = new TextView(getContext());
            layout.addView(textView[para]);
            articleID = "article_" + mArticle + "_" + (para+1);
            int resID = getStringResourceByName(articleID);
            textView[para].setText(resID);
        }
        textView[mParas-1].setPadding(0, 0, 0, 20);

        boolean wasHere = ((MainActivity) getActivity()).wasIHere(mArticle);
        if (mArticle == 38 || mArticle == 53 || mArticle == 164 ||
                mArticle == 224 || mArticle == 239 || mArticle == 247 || mArticle == 268 ||
                mArticle == 270 || mArticle == 304 || mArticle == 310 || mArticle == 313 ||
                mArticle == 319 || mArticle == 331 || mArticle == 336 || mArticle == 350) {
            //was I here? if yes, show only first option (door open), otherwise, show only second option (door closed)
            if (wasHere) {
                mRadios = 1;
            } else if (!wasHere) {
                radioStartCount = 1;
                if (mArticle == 247 || mArticle == 270) {
                    radioStartCount = 0;
                }
            }
            Log.d(GAME, "was here:" + wasHere);
        }
        if (mArticle == 181) {
            if (((MainActivity) getActivity()).isThingAvailable("Канат с крюком")) {
                mRadios = 1;
            } else {
                radioStartCount = 1;
            }
        }

        //generate radio buttons
        final RadioGroup radioGroup = new RadioGroup(getContext());
        AppCompatRadioButton[] radioButton = new AppCompatRadioButton[mRadios];
        layout.addView(radioGroup);
        for (int radio = radioStartCount; radio < mRadios; radio++) {
            radioButton[radio] = new AppCompatRadioButton(getContext(), null, R.attr.radioButtonStyle);
            radioButton[radio].setId(radio+1);
            radioGroup.addView(radioButton[radio]);
            optionID = "option_" + mArticle + "_" + (radio+1); //option_25_1, option_25_2
            int resID = getStringResourceByName(optionID);
            textID = "text_" + mArticle + "_" + (radio+1); //text_25_1
            int radioTextID = getStringResourceByName(textID);
            choice[radio] = Integer.parseInt(getResources().getString(resID));
            String radioTextNumber = getResources().getString(resID);
            String radioTextShortDescription = ", " + getResources().getString(radioTextID);
            if (radioTextNumber.equals("0")) {
                int goBackID = ((MainActivity) getActivity()).gamePref.getInt("goBackArticleID", 0);
                radioTextNumber = Integer.toString(goBackID);
                choice[radio] = goBackID;
            }
            if ((mArticle == 247 || mArticle == 270) && radioStartCount == 0 && radio == 0 && wasHere) {
                radioTextShortDescription = ", Ты тут уже был. Возвращайся к перекрестку.";
                //TODO: remove ", "
            }
            String radioText = radioTextNumber + radioTextShortDescription;
            //set radio button text

            radioButton[radio].setText(radioText);

            if (mRadios == 1 || (radioStartCount == 1 && mRadios == 2)) { //second part for article 164
                //set checked radio button if it is sole
                radioGroup.check(radioButton[radio].getId());
            }
        }
        final Button daleeButton = new Button(getContext());
        layout.addView(daleeButton);
        //articles before battle;
        if (mArticle == 2) { //TODO: all articles before battle? maybe if mRadios == 0?
            daleeButton.setText("БИТВА");
        } else daleeButton.setText("Продолжить");

        //special conditions; execute on article load
        if (mArticle == 3) {
            if (((MainActivity) getActivity()).gamePref.getInt("gold", 0) < 13) {
                radioGroup.getChildAt(0).setEnabled(false);
            }
            if (!((MainActivity) getActivity()).isThingAvailable("Канат с крюком")) {
                radioGroup.getChildAt(1).setEnabled(false); //toDo веревку поменять на канат
            }
            if (((MainActivity) getActivity()).gamePref.getInt("VVV", 0) < 18 || ((MainActivity) getActivity()).gamePref.getInt("LLL", 0) < 9) {
                radioGroup.getChildAt(3).setEnabled(false);
            }
            if (!radioGroup.getChildAt(0).isEnabled() && !radioGroup.getChildAt(1).isEnabled() &&
                    !radioGroup.getChildAt(3).isEnabled()) {
                radioGroup.check(radioButton[2].getId());
            }
        }
        if (mArticle == 18) {
            if (((MainActivity) getActivity()).gamePref.getInt("gold", 0) < 5) {
                radioGroup.check(radioButton[1].getId());
                radioGroup.getChildAt(0).setEnabled(false);
                radioButton[1].setText("286, Недостаточно золота"); //TODO remove 286
            }
        }
        if (mArticle == 22) {
            radioGroup.getChildAt(0).setEnabled(false);
            radioGroup.getChildAt(1).setEnabled(false);
            radioGroup.getChildAt(2).setEnabled(false);
            if (((MainActivity) getActivity()).isThingAvailable("Жестяная бабочка")) {
                radioGroup.getChildAt(0).setEnabled(true);
            }
            if (((MainActivity) getActivity()).isThingAvailable("Изумруд")) {
                radioGroup.getChildAt(1).setEnabled(true);
            }
            if (((MainActivity) getActivity()).isThingAvailable("Деревянный кол")) {
                radioGroup.getChildAt(2).setEnabled(true);
            }
            //check radio if single thing
            if (radioGroup.getChildAt(0).isEnabled() && !radioGroup.getChildAt(1).isEnabled() && !radioGroup.getChildAt(2).isEnabled()) {
                radioGroup.check(radioButton[0].getId());
            }
            if (!radioGroup.getChildAt(0).isEnabled() && radioGroup.getChildAt(1).isEnabled() && !radioGroup.getChildAt(2).isEnabled()) {
                radioGroup.check(radioButton[1].getId());
            }
            if (!radioGroup.getChildAt(0).isEnabled() && !radioGroup.getChildAt(1).isEnabled() && radioGroup.getChildAt(2).isEnabled()) {
                radioGroup.check(radioButton[2].getId());
            }
        }
        if (mArticle == 23) {
            Set<String> keys = ((MainActivity) getActivity()).gamePref.getStringSet("keys", new HashSet<String>());
            if (keys.size() < 2) {
                radioGroup.check(radioButton[1].getId());
                radioGroup.getChildAt(0).setEnabled(false);
                //TODO: the end
                radioButton[1].setText("1 TBD, Недостаточно ключей");
            } else {
                radioGroup.check(radioButton[0].getId());
                radioGroup.getChildAt(1).setEnabled(false);
            }
        }
        if (mArticle == 28) {
            if (((MainActivity) getActivity()).gamePref.getInt("VVV", 0) >= 18) {
                radioGroup.check(radioButton[0].getId());
                radioGroup.getChildAt(1).setEnabled(false);
            } else {
                radioGroup.check(radioButton[1].getId());
                radioGroup.getChildAt(0).setEnabled(false);
            }
        }

        if (mArticle == 24 || mArticle == 30 || mArticle == 35 || mArticle == 36 || mArticle == 41 || mArticle == 95 ||
                mArticle == 126 || mArticle == 138 || mArticle == 149 || mArticle == 162 || mArticle == 225 ||
                mArticle == 229 || mArticle == 242 || mArticle == 274 || mArticle == 284 || mArticle == 381) {
            final Button takeChance = new Button(getContext());
            final String rememberedChanceInArticle = "chanceInArticle" + mArticle; //todo probably remove remembered chance in some articles (e.g. 229)
            int radioID = ((MainActivity) getActivity()).gamePref.getInt(rememberedChanceInArticle, -1);
            if (radioID < 0) {
                layout.addView(takeChance, 1);
                takeChance.setText("Проверь карму (-1К)");
                radioGroup.getChildAt(0).setEnabled(false);
                radioGroup.getChildAt(1).setEnabled(false);
                daleeButton.setEnabled(false);
                final int firstRadioID = radioButton[0].getId();
                final int secondRadioID = radioButton[1].getId();
                takeChance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        takeChance.setEnabled(false);
                        if (((MainActivity) getActivity()).takeChance() == 1) {
                            radioGroup.check(firstRadioID);
                            radioGroup.getChildAt(0).setEnabled(true);
                            ((MainActivity) getActivity()).editor.putInt(rememberedChanceInArticle, firstRadioID).commit();
                        } else {
                            radioGroup.check(secondRadioID);
                            radioGroup.getChildAt(1).setEnabled(true);
                            ((MainActivity) getActivity()).editor.putInt(rememberedChanceInArticle, secondRadioID).commit();
                        }
                        daleeButton.setEnabled(true);
                    }
                });

            } else {
                if (radioID == radioButton[0].getId()) {
                    radioGroup.check(radioID);
                    radioGroup.getChildAt(1).setEnabled(false);
                } else {
                    radioGroup.check(radioID);
                    radioGroup.getChildAt(0).setEnabled(false);
                }
                daleeButton.setEnabled(true);
            }

        }
        if (mArticle == 52) {
            if (((MainActivity) getActivity()).isThingAvailable("Герметичный шлем")) {
                radioGroup.getChildAt(1).setEnabled(false);
                radioGroup.check(radioButton[0].getId());
            } else {
                radioGroup.getChildAt(0).setEnabled(false);
                radioGroup.check(radioButton[1].getId());
            }
        }
        if (mArticle == 63) {
            boolean isAnyWeaponAvailable = false;
            for (int i = 0; i<5; i++) {
                radioGroup.getChildAt(i).setEnabled(false);
            }
            if (((MainActivity) getActivity()).isThingAvailable("Кость чудовища")) {
                radioGroup.getChildAt(0).setEnabled(true);
                isAnyWeaponAvailable = true;
            }
            if (((MainActivity) getActivity()).isThingAvailable("Молот гномов")) {
                radioGroup.getChildAt(1).setEnabled(true);
                isAnyWeaponAvailable = true;
            }
            if (((MainActivity) getActivity()).isThingAvailable("Металлический щит")) {
                radioGroup.getChildAt(2).setEnabled(true);
                isAnyWeaponAvailable = true;
            }
            if (((MainActivity) getActivity()).isThingAvailable("Сеть")) {
                radioGroup.getChildAt(3).setEnabled(true);
                isAnyWeaponAvailable = true;
            }
            if (((MainActivity) getActivity()).isThingAvailable("Связка ключей")) {
                radioGroup.getChildAt(4).setEnabled(true);
                isAnyWeaponAvailable = true;
            }
            if (!isAnyWeaponAvailable) {
                radioGroup.check(radioButton[5].getId());
            }
        }
        if (mArticle == 65) {
            int gold =((MainActivity) getActivity()).gamePref.getInt("gold", 0);
            if (gold >= 20) {
                radioGroup.getChildAt(2).setEnabled(false);
            } else if (gold >= 10) {
                radioGroup.getChildAt(1).setEnabled(false);
                radioGroup.getChildAt(2).setEnabled(false);
                radioGroup.check(radioButton[0].getId());
            } else {
                radioGroup.getChildAt(0).setEnabled(false);
                radioGroup.getChildAt(1).setEnabled(false);
                radioGroup.check(radioButton[2].getId());
            }
        }
        if (mArticle == 66) {
            final Button diceForGold = new Button(getContext());
            layout.addView(diceForGold, 1);
            int savedGold66 = ((MainActivity) getActivity()).gamePref.getInt("savedGold66", 0);
            if (savedGold66 == 0) {
                diceForGold.setText("Играть в кости");
                radioGroup.getChildAt(0).setEnabled(false);
                radioGroup.getChildAt(1).setEnabled(false);
                daleeButton.setEnabled(false);
                diceForGold.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        diceForGold.setEnabled(false);
                        Random rnd = new Random();
                        int dice = rnd.nextInt(6)+rnd.nextInt(6)+2;
                        ((MainActivity) getActivity()).editor.putInt("savedGold66", dice).commit();
                        diceForGold.setText("Получаешь золота: " + dice);
                        radioGroup.getChildAt(0).setEnabled(true);
                        radioGroup.getChildAt(1).setEnabled(true);
                        daleeButton.setEnabled(true);                    }
                });
            } else {
                diceForGold.setEnabled(false);
                diceForGold.setText("Получаешь золота: " + ((MainActivity) getActivity()).gamePref.getInt("savedGold66", 0));
                radioGroup.getChildAt(0).setEnabled(true);
                radioGroup.getChildAt(1).setEnabled(true);
                daleeButton.setEnabled(true);
            }

        }
        if (mArticle == 74) { //todo all radios can be disabled
            if (!((MainActivity) getActivity()).isThingAvailable("Сеть")) {
                radioGroup.getChildAt(0).setEnabled(false);
            }
            if (((MainActivity) getActivity()).gamePref.getInt("gold", 0) < 10) {
                radioGroup.getChildAt(1).setEnabled(false);
            }
            if (!((MainActivity) getActivity()).isThingAvailable("Канат с крюком")) {
                radioGroup.getChildAt(2).setEnabled(false);
            }
            if (((MainActivity) getActivity()).gamePref.getInt("VVV", 0) < 18 || ((MainActivity) getActivity()).gamePref.getInt("LLL", 0) < 9) {
                radioGroup.getChildAt(3).setEnabled(false);
            }
        }
        if (mArticle == 76) {
            if (((MainActivity) getActivity()).howManyKeys() >= 3) {
                radioGroup.getChildAt(1).setEnabled(false);
            } else {
                radioGroup.getChildAt(0).setEnabled(false);
            }
        }
        //91 see 335
        if (mArticle == 106) {
            radioGroup.getChildAt(1).setEnabled(false);
            if (((MainActivity) getActivity()).isThingAvailable("Банка ядовитой пыли") ||
                    ((MainActivity) getActivity()).isThingAvailable("Всеядные ракообразные")) {
                radioGroup.getChildAt(1).setEnabled(true);
            } else {
                radioGroup.check(radioButton[0].getId());
                radioButton[1].setText("Кроме меча, ничего нет");
            }
        }
        if (mArticle == 110 || mArticle == 143 || mArticle == 174 || mArticle == 315) {
            final Button oneDiceButton = new Button(getContext());
            final String rememberedChanceInArticle = "chanceInArticle" + mArticle; //todo probably remove remembered chance in some articles (e.g. 229)
            int radioID = ((MainActivity) getActivity()).gamePref.getInt(rememberedChanceInArticle, -1);
            if (radioID < 0) {
                layout.addView(oneDiceButton, 1);
                if (mArticle == 110) {
                    oneDiceButton.setText("Шанс 2 к 1");
                }
                if (mArticle == 143 || mArticle == 174 || mArticle == 315) {
                    oneDiceButton.setText("Шанс 50/50");
                }
                radioGroup.getChildAt(0).setEnabled(false);
                radioGroup.getChildAt(1).setEnabled(false);
                daleeButton.setEnabled(false);
                final int firstRadioID = radioButton[0].getId();
                final int secondRadioID = radioButton[1].getId();
                oneDiceButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        oneDiceButton.setEnabled(false);
                        if (buttonCondition()) { //condition for positive result
                            radioGroup.check(firstRadioID);
                            radioGroup.getChildAt(0).setEnabled(true);
                            ((MainActivity) getActivity()).editor.putInt(rememberedChanceInArticle, firstRadioID).commit();
                        } else {
                            radioGroup.check(secondRadioID);
                            radioGroup.getChildAt(1).setEnabled(true);
                            ((MainActivity) getActivity()).editor.putInt(rememberedChanceInArticle, secondRadioID).commit();
                        }
                        daleeButton.setEnabled(true);
                    }
                });

            } else {
                if (radioID == radioButton[0].getId()) {
                    radioGroup.check(radioID);
                    radioGroup.getChildAt(1).setEnabled(false);
                } else {
                    radioGroup.check(radioID);
                    radioGroup.getChildAt(0).setEnabled(false);
                }
                daleeButton.setEnabled(true);
            }

        }
        if (mArticle == 112) {
            final Button throwDiceButton = new Button(getContext());
            final TextView scoreCounterTextView = new TextView(getContext());
            layout.addView(throwDiceButton, 1);
            layout.addView(scoreCounterTextView,2);
            throwDiceButton.setText("Бросить кубик");
            scoreCounterTextView.setText(((MainActivity) getActivity()).gamePref.getString("scoreText", ""));
            radioGroup.getChildAt(0).setEnabled(false);
            radioGroup.getChildAt(1).setEnabled(false);
            final int firstRadioID = radioButton[0].getId();
            final int secondRadioID = radioButton[1].getId();
            if (((MainActivity) getActivity()).gamePref.getInt("thrownDice112", 0) < 4) {
                daleeButton.setEnabled(false);
                throwDiceButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Random rnd = new Random();
                        int dice = rnd.nextInt(6) + 1;
                        ((MainActivity) getActivity()).editor.putInt("thrownDice112", ((MainActivity) getActivity()).gamePref.getInt("thrownDice112", 0) + 1);
                        ((MainActivity) getActivity()).editor.putInt("score112", ((MainActivity) getActivity()).gamePref.getInt("score112", 0) + dice);
                        ((MainActivity) getActivity()).editor.putString("scoreText", ((MainActivity) getActivity()).gamePref.getString("scoreText", "")
                                + Integer.toString(dice) + " + ");
                        ((MainActivity) getActivity()).editor.commit();
                        scoreCounterTextView.setText(((MainActivity) getActivity()).gamePref.getString("scoreText", ""));
                        if (((MainActivity) getActivity()).gamePref.getInt("thrownDice112", 0) == 4) {
                            throwDiceButton.setEnabled(false);
                            String finalScoreString = ((MainActivity) getActivity()).gamePref.getString("scoreText", "").substring(0, 13);
                            int score = ((MainActivity) getActivity()).gamePref.getInt("score112", 0);
                            scoreCounterTextView.setText(finalScoreString + " = " + score);
                            ((MainActivity) getActivity()).editor.putString("scoreText", finalScoreString + " = " + score);
                            ((MainActivity) getActivity()).editor.commit();
                            if (score >= 18) {
                                radioGroup.getChildAt(0).setEnabled(true);
                                radioGroup.check(firstRadioID);
                            } else {
                                radioGroup.getChildAt(1).setEnabled(true);
                                radioGroup.check(secondRadioID);
                            }
                            daleeButton.setEnabled(true);
                        }
                    }
                });
            } else {
                throwDiceButton.setEnabled(false);
                if (((MainActivity) getActivity()).gamePref.getInt("score112", 0) >= 18) {
                    radioGroup.getChildAt(0).setEnabled(true);
                    radioGroup.check(radioButton[0].getId());
                } else {
                    radioGroup.getChildAt(1).setEnabled(true);
                    radioGroup.check(radioButton[1].getId());
                }
                daleeButton.setEnabled(true);
            }
        }
        if (mArticle == 115) {
            if (((MainActivity) getActivity()).wasIHere(271)) {
                radioGroup.getChildAt(0).setEnabled(false);
            }
            if (((MainActivity) getActivity()).wasIHere(95)) {
                radioGroup.getChildAt(1).setEnabled(false);
            }
            if (((MainActivity) getActivity()).wasIHere(37)) {
                radioGroup.getChildAt(2).setEnabled(false);
            }
            if (((MainActivity) getActivity()).wasIHere(298)) {
                radioGroup.getChildAt(3).setEnabled(false);
            }
            if (((MainActivity) getActivity()).wasIHere(324)) {
                radioGroup.getChildAt(4).setEnabled(false);
            }
        }
        if (mArticle == 125 || mArticle == 158) {
            if (((MainActivity) getActivity()).gamePref.getInt("gold", 0) < 10) {
                radioGroup.getChildAt(0).setEnabled(false);
            }
            if (!((MainActivity) getActivity()).isThingAvailable("Канат с крюком")) {
                radioGroup.getChildAt(1).setEnabled(false);
            }
            if (((MainActivity) getActivity()).gamePref.getInt("VVV", 0) < 18 || ((MainActivity) getActivity()).gamePref.getInt("LLL", 0) < 9) {
                radioGroup.getChildAt(3).setEnabled(false);
            }
            if (!radioGroup.getChildAt(0).isEnabled() && !radioGroup.getChildAt(1).isEnabled() &&
                    !radioGroup.getChildAt(3).isEnabled()) {
                radioGroup.check(radioButton[2].getId());
            }
        }
        if (mArticle == 135) {
            boolean iHaveAnything = ((MainActivity) getActivity()).isThingAvailable("Жестяная бабочка") ||
                    ((MainActivity) getActivity()).isThingAvailable("Изумруд") ||
                    ((MainActivity) getActivity()).isThingAvailable("Деревянный кол");
            if (!iHaveAnything) {
                radioGroup.getChildAt(0).setEnabled(false);
                radioGroup.check(radioButton[1].getId());
            }
        }
        if (mArticle == 138) {
            //todo smth here
        }
        //158 see 125
        if (mArticle == 165) {
            boolean isAnyWeaponAvailable = false;
            radioGroup.getChildAt(4).setVisibility(View.GONE);
            for (int i = 0; i<4; i++) {
                radioGroup.getChildAt(i).setEnabled(false);
            }
            if (((MainActivity) getActivity()).isThingAvailable("Молот гномов")) {
                radioGroup.getChildAt(0).setEnabled(true);
                isAnyWeaponAvailable = true;
            }
            if (((MainActivity) getActivity()).isThingAvailable("Алмазную шпору")) {
                radioGroup.getChildAt(1).setEnabled(true);
                isAnyWeaponAvailable = true;
            }
            if (((MainActivity) getActivity()).isThingAvailable("Огненное ядро")) {
                radioGroup.getChildAt(2).setEnabled(true);
                isAnyWeaponAvailable = true;
            }
            if (((MainActivity) getActivity()).isThingAvailable("Копье")) {
                radioGroup.getChildAt(3).setEnabled(true);
                isAnyWeaponAvailable = true;
            }
            if (!isAnyWeaponAvailable) {
                radioGroup.getChildAt(4).setVisibility(View.VISIBLE);
                radioGroup.check(radioButton[4].getId());
            }
        }
        if (mArticle == 180) {
            if (wasHere) {
                textView[0].setText("Тут ты уже все осмотрел.");
            }
        }
        if (mArticle == 193) {
            //TODO: +5gold for every monster defeated
        }
        if (mArticle == 240) {
            final Button loseVvvButton = new Button(getContext());
            final String rememberedLoseAmount = "loseAmount" + mArticle;
            int radioID = ((MainActivity) getActivity()).gamePref.getInt(rememberedLoseAmount, 0);
            layout.addView(loseVvvButton, 1);
            if (radioID < 1) {
                loseVvvButton.setText("Бросить кубик");
                radioGroup.getChildAt(0).setEnabled(false);
                daleeButton.setEnabled(false);
                loseVvvButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loseVvvButton.setEnabled(false);
                        Random rnd = new Random();
                        int currentVVV = ((MainActivity) getActivity()).gamePref.getInt("VVV", 0);
                        int dice;
                        if (currentVVV == 1) {
                            dice = 0;
                        } else {
                            dice = rnd.nextInt(currentVVV-1) + 1;
                        }
                        ((MainActivity) getActivity()).editor.putInt(rememberedLoseAmount, dice).commit();
                        Toast.makeText(getContext(), "" + dice, Toast.LENGTH_SHORT).show();
                        loseVvvButton.setText("Теряешь " + dice + "В");

                        daleeButton.setEnabled(true);
                    }
                });

            } else {
                loseVvvButton.setEnabled(false);
                loseVvvButton.setText("Теряешь " + radioID + "В");
                daleeButton.setEnabled(true);
            }

        }
        if (mArticle == 269) {
            if (((MainActivity) getActivity()).gamePref.getInt("gold", 0) < 10) {
                radioGroup.getChildAt(1).setEnabled(false);
            }
            if (((MainActivity) getActivity()).gamePref.getInt("VVV", 0) < 18 || ((MainActivity) getActivity()).gamePref.getInt("LLL", 0) < 9) {
                radioGroup.getChildAt(3).setEnabled(false);
            }
        }
        if (mArticle == 292) {
            radioGroup.getChildAt(0).setEnabled(false);
            radioGroup.getChildAt(1).setEnabled(false);
            if (((MainActivity) getActivity()).isThingAvailable("Банка ядовитой пыли")) {
                radioGroup.getChildAt(0).setEnabled(true);
            }
            if (((MainActivity) getActivity()).isThingAvailable("Всеядные ракообразные")) {
                radioGroup.getChildAt(1).setEnabled(true);
            }
            if (!radioGroup.getChildAt(1).isEnabled()) {
                radioGroup.check(radioButton[0].getId());
            } else if (!radioGroup.getChildAt(0).isEnabled()) {
                radioGroup.check(radioButton[1].getId());
            }
        }
        if (mArticle == 295) {
            radioGroup.getChildAt(0).setEnabled(false);
            if (((MainActivity) getActivity()).gamePref.getInt("gold", 0) > 0) {
                radioGroup.getChildAt(0).setEnabled(true);
            }
            if (!radioGroup.getChildAt(0).isEnabled()) {
                radioGroup.check(radioButton[1].getId());
            }
        }
        if (mArticle == 314) {
            radioGroup.getChildAt(0).setEnabled(false);
            radioGroup.getChildAt(1).setEnabled(false);
            if (((MainActivity) getActivity()).isThingAvailable("Изумруд (Дар Крыльев)")) {
                radioGroup.getChildAt(0).setEnabled(true);
                radioGroup.check(radioButton[0].getId());
            } else {
                radioGroup.getChildAt(1).setEnabled(true);
                radioGroup.check(radioButton[1].getId());
            }
        }
        if (mArticle == 323) {
            if (((MainActivity) getActivity()).gamePref.getInt("gold", 0) < 25) {
                radioGroup.getChildAt(0).setEnabled(false);
                radioGroup.check(radioButton[1].getId());
            }
        }
        if (mArticle == 334) {
            final Button twoDiceForGold = new Button(getContext());
            layout.addView(twoDiceForGold, 1);
            int savedGold66 = ((MainActivity) getActivity()).gamePref.getInt("savedGold334", 0);
            if (savedGold66 == 0) {
                twoDiceForGold.setText("Играть в кости");
                daleeButton.setEnabled(false);
                twoDiceForGold.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        twoDiceForGold.setEnabled(false);
                        Random rnd = new Random();
                        int dice = rnd.nextInt(6)+rnd.nextInt(6)+2;
                        if (dice % 2 == 0) {
                            ((MainActivity) getActivity()).editor.putInt("savedGold334", -dice).commit();
                            twoDiceForGold.setText("Проигрываешь золота: " + dice);
                        } else {
                            ((MainActivity) getActivity()).editor.putInt("savedGold334", dice).commit();
                            twoDiceForGold.setText("Выигрываешь золота: " + dice);
                        }
                        daleeButton.setEnabled(true);
                    }
                });
            } else {
                twoDiceForGold.setEnabled(false);
                int dice = ((MainActivity) getActivity()).gamePref.getInt("savedGold334", 0);
                if (dice < 0) {
                    twoDiceForGold.setText("Проигрываешь золота: " + -dice);
                } else {
                    twoDiceForGold.setText("Выигрываешь золота: " + dice);
                }
                //daleeButton.setEnabled(true);
            }
        }
        if (mArticle == 335 || mArticle == 91) {
            if (((MainActivity) getActivity()).gamePref.getInt("gold", 0) < 10) {
                radioGroup.getChildAt(0).setEnabled(false);
            }
            if (((MainActivity) getActivity()).gamePref.getInt("VVV", 0) < 18 || ((MainActivity) getActivity()).gamePref.getInt("LLL", 0) < 9) {
                radioGroup.getChildAt(2).setEnabled(false);
            }
            if (!radioGroup.getChildAt(0).isEnabled() && !radioGroup.getChildAt(2).isEnabled()) {
                radioGroup.check(radioButton[1].getId());
            }
        }
        if (mArticle == 358) {
            radioGroup.getChildAt(0).setEnabled(false);
            if (((MainActivity)getActivity()).isThingAvailable("Бутылка с эликсиром невидимости")) {
                radioGroup.getChildAt(0).setEnabled(true);
            } else {
                radioGroup.check(radioButton[1].getId());
            }
        }
        if (mArticle == 371) {
            radioGroup.getChildAt(0).setEnabled(false);
            if (((MainActivity)getActivity()).isThingAvailable("Бутылка с водой")) { //todo ?? бутылка с водой? менять текст в абзацах?
                radioGroup.getChildAt(0).setEnabled(true);
            }
        }
        if (mArticle == 377) {
            radioGroup.getChildAt(0).setEnabled(false);
            radioGroup.getChildAt(1).setEnabled(false);
            if (((MainActivity)getActivity()).isThingAvailable("Деревянный кол")) {
                radioGroup.getChildAt(0).setEnabled(true);
                radioGroup.getChildAt(2).setEnabled(false);
                radioGroup.check(radioButton[0].getId());
            } else if (((MainActivity)getActivity()).isThingAvailable("Кость чудовища") ||
                       ((MainActivity)getActivity()).isThingAvailable("Молот гномов") ||
                       ((MainActivity)getActivity()).isThingAvailable("Металлический щит") ||
                       ((MainActivity)getActivity()).isThingAvailable("Связка ключей")) {
                radioGroup.getChildAt(1).setEnabled(true);
            } else {
                radioGroup.check(radioButton[2].getId());
            }
        }

        //take special action on article load
        ((MainActivity) getActivity()).takeSpecialAction(mArticle);

        daleeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).editor.putInt("foodTries", 1).commit();
                if (mRadios == 0) {
                    ((MainActivity) getActivity()).takeAction(mArticle);
                    ((MainActivity) getActivity()).showPreArticle(mArticle+1000);
                } else {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    if (selectedId == -1) {
                        Toast.makeText(getContext(), "Сделайте выбор", Toast.LENGTH_SHORT).show();
                    } else {
                        ((MainActivity) getActivity()).editor.putInt("selectedRadio", selectedId - 1).commit();
                        ((MainActivity) getActivity()).takeAction(mArticle);
                        ((MainActivity) getActivity()).showAllParameters();
                        ((MainActivity) getActivity()).editor.putInt("previousArticle", mArticle).commit();
                        int breakInto = ((MainActivity) getActivity()).gamePref.getInt("breakIntoArticle",0);
                        if (breakInto > 0){
                            ((MainActivity) getActivity()).showArticle(breakInto);
                        } else ((MainActivity) getActivity()).showArticle(choice[selectedId - 1]);
                    }
                }
            }
        });

        return view;
    }

    private boolean buttonCondition() {
        Random rnd = new Random();
        if (mArticle == 110) {
            int dice = rnd.nextInt(6) + 1;
            Toast.makeText(getContext(), "" + dice, Toast.LENGTH_SHORT).show();
            if (dice > 2) {
                return true;
            }
        }
        if (mArticle == 143 || mArticle == 174 || mArticle == 315) {
            int dice = rnd.nextInt(6) + 1;
            Toast.makeText(getContext(), "" + dice, Toast.LENGTH_SHORT).show();
            if (dice > 3) {
                return true;
            }
        }
        return false;
    }

    // Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        //Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private int getStringResourceByName(String aString) {
        int resId = getResources().getIdentifier(aString, "string", GAME);
        return resId;
    }

    public void redrawToolbar() {
        String toolbarTitle = "Л:" + ((MainActivity)getActivity()).gamePref.getInt("LLL",0) +
                " В:" + ((MainActivity)getActivity()).gamePref.getInt("VVV",0) +
                " У:" + ((MainActivity)getActivity()).gamePref.getInt("UUU",0);
        ((MainActivity) getActivity()).setToolbarTitle(toolbarTitle, Integer.toString(mArticle));
    }

}
