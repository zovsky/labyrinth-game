package com.zovsky.labyrinth;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BattleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BattleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BattleFragment extends Fragment {

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

    private int luck;

    private int monsterLLL;
    private int monsterVVV;
    private int heroVVV;
    private int heroLLL;
    private int round;
    private String monsterName;
    private int step;

    private TextView roundTextView;
    private Button calcMonsterAttackButton;
    private TextView monsterAttackTextView;
    private Button calcHeroAttackButton;
    private TextView heroAttackTextView;
    private Button takeChanceButton;
    private TextView roundResultTextView;
    private Button fleeFromBattleButton;
    private Button dalee;

    private OnFragmentInteractionListener mListener;

    public BattleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param article Parameter 1.
     * @param numberOfPara Parameter 2.
     * @param numberOfRadios Parameter 3.
     * @return A new instance of fragment BattleFragment.
     */
    public static BattleFragment newInstance(int article, int numberOfPara, int numberOfRadios) {
        BattleFragment fragment = new BattleFragment();
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
        ((MainActivity)getActivity()).editor.putInt("currentArticle", mArticle).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //show inventory button
        ((MainActivity)getActivity()).setInventoryVisibility(true);

        heroVVV = ((MainActivity) getActivity()).gamePref.getInt("VVV", 0);
        heroLLL = ((MainActivity) getActivity()).gamePref.getInt("LLL", 0);
        monsterLLL = ((MainActivity) getActivity()).gamePref.getInt("monsterLLL", 0);
        monsterVVV = ((MainActivity) getActivity()).gamePref.getInt("monsterVVV", 0);
        final String monster_data = "Л:" + monsterLLL + " В:" + monsterVVV;

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.getMenu().getItem(0).setTitle(monster_data).setEnabled(false);
        //set LVU as toolbar title
        String toolbarTitle = "Л:" + ((MainActivity)getActivity()).gamePref.getInt("LLL",0) +
                " В:" + ((MainActivity)getActivity()).gamePref.getInt("VVV",0) +
                " У:" + ((MainActivity)getActivity()).gamePref.getInt("UUU",0);
        ((MainActivity) getActivity()).setToolbarTitle(toolbarTitle, Integer.toString(mArticle)); //null

        View view = inflater.inflate(R.layout.fragment_battle, container, false);
        round = ((MainActivity) getActivity()).gamePref.getInt("round", 0);
        step = ((MainActivity) getActivity()).gamePref.getInt("step", 0);
        luck = ((MainActivity) getActivity()).gamePref.getInt("luck", 0);


        monsterName = ((MainActivity) getActivity()).gamePref.getString("monsterName", "");
        TextView monsterNameTextView = (TextView) view.findViewById(R.id.monster_name_corner);
        monsterNameTextView.setText(monsterName);

        roundTextView = (TextView) view.findViewById(R.id.battle_round_text);
        roundTextView.setText("Раунд " + round);

        calcMonsterAttackButton = (Button) view.findViewById(R.id.calc_monster_attack);
        calcMonsterAttackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rnd = new Random();
                int monster_attack = rnd.nextInt(6)+rnd.nextInt(6)+2+monsterLLL;
                ((MainActivity) getActivity()).editor.putInt("monsterAttack", monster_attack).commit();
                calcMonsterAttackButton.setEnabled(false);
                monsterAttackTextView.setText("" + monster_attack);
                calcHeroAttackButton.setEnabled(true);
                ((MainActivity) getActivity()).editor.putInt("step", 1).commit();
            }
        });

        monsterAttackTextView = (TextView) view.findViewById(R.id.text_monster_attack);
        if (((MainActivity) getActivity()).gamePref.getInt("monsterAttack", 0) > 0) {
            calcMonsterAttackButton.setEnabled(false);
            monsterAttackTextView.setText("" + ((MainActivity) getActivity()).gamePref.getInt("monsterAttack", 0));
        }

        calcHeroAttackButton = (Button) view.findViewById(R.id.calc_hero_attack);
        calcHeroAttackButton.setEnabled(false);
        calcHeroAttackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rnd = new Random();
                int hero_attack = rnd.nextInt(6)+rnd.nextInt(6)+2+heroLLL;
                ((MainActivity) getActivity()).editor.putInt("heroAttack", hero_attack).commit();
                calcHeroAttackButton.setEnabled(false);
                heroAttackTextView.setText("" + hero_attack);
                boolean isAllowed = ((MainActivity)getActivity()).isAllowedToTakeChance() &&
                        ((MainActivity) getActivity()).gamePref.getInt("heroAttack",0) !=
                                ((MainActivity) getActivity()).gamePref.getInt("monsterAttack",0);
                takeChanceButton.setEnabled(isAllowed);
                fleeFromBattleButton.setEnabled(isFleeAllowed());
                setRoundResult();
                dalee.setEnabled(true);
                ((MainActivity) getActivity()).editor.putInt("step", 2).commit();
            }
        });
        heroAttackTextView = (TextView) view.findViewById(R.id.text_hero_attack);
        if (((MainActivity) getActivity()).gamePref.getInt("heroAttack", 0) > 0) {
            calcHeroAttackButton.setEnabled(false);
            heroAttackTextView.setText("" + ((MainActivity) getActivity()).gamePref.getInt("heroAttack", 0));
        }

        takeChanceButton = (Button) view.findViewById(R.id.good_luck);
        takeChanceButton.setEnabled(false);
        takeChanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                luck = ((MainActivity) getActivity()).takeChance();
                ((MainActivity) getActivity()).editor.putInt("luck", luck).commit();
                takeChanceButton.setEnabled(false);
                setRoundResult();
                ((MainActivity) getActivity()).editor.putInt("step", 3).commit();
            }
        });

        roundResultTextView = (TextView) view.findViewById(R.id.round_result);

        fleeFromBattleButton = (Button) view.findViewById(R.id.battle_flee_button);
        fleeFromBattleButton.setEnabled(false);
        fleeFromBattleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO alert
                ((MainActivity) getActivity()).showArticle(((MainActivity) getActivity()).gamePref.getInt("fleeArticle", 0));
            }
        });
        //TODO check all putInt commit
        dalee = (Button) view.findViewById(R.id.battle_continue_button);
        dalee.setEnabled(false);
        dalee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).editor.putInt("round", round + 1);
                ((MainActivity) getActivity()).editor.putInt("step", 0);
                ((MainActivity) getActivity()).editor.remove("luck");
                ((MainActivity) getActivity()).editor.remove("monsterAttack");
                ((MainActivity) getActivity()).editor.remove("heroAttack");
                ((MainActivity) getActivity()).editor.commit();
                if (heroVVV < 1) {
                    Log.d(GAME, "branch1");
                    ((MainActivity) getActivity()).gameOver();
                } else {
                    Log.d(GAME, "branch2");
                    ((MainActivity) getActivity()).editor.putInt("VVV", heroVVV).commit();
                    ((MainActivity) getActivity()).showBattle(mArticle);
                }
                if (monsterVVV < 1) {
                    if (mArticle == 2020 || mArticle == 2021 || mArticle == 2022 || mArticle == 2023) {
                        Log.d(GAME, "branch3");
                        ((MainActivity) getActivity()).showPreArticle(mArticle - 1000 + 1);
                        return;
                    } else {
                        Log.d(GAME, "branch4");
                        int victoryArticle = ((MainActivity) getActivity()).gamePref.getInt("victoryArticle", 0);
                        if (victoryArticle == 0) {
                            Log.d(GAME, "branch5");
                            ((MainActivity) getActivity()).showArticle(((MainActivity) getActivity()).gamePref.getInt("goBackArticleID", 0));
                            return;
                        } else {
                            Log.d(GAME, "branch6");
                            ((MainActivity) getActivity()).showArticle(victoryArticle);
                            return;
                        }
                    }
                } else {
                    Log.d(GAME, "branch7");
                    ((MainActivity) getActivity()).editor.putInt("monsterVVV", monsterVVV).commit();
                    ((MainActivity) getActivity()).showBattle(mArticle);
                    return;
                }
            }
        });
        restoreBattleState(step);
//        ((MainActivity) getActivity()).showAllParameters();
        return view;
    }

    private void calcHeroAttackButtonClicked() {

    }

    private boolean isFleeAllowed() {
        if (((MainActivity) getActivity()).gamePref.getInt("round", 0) > 1 &&
                ((MainActivity) getActivity()).gamePref.getInt("fleeCondition", 0) == 1) {
            return false;
        } else if (((MainActivity) getActivity()).gamePref.getInt("fleeCondition", 0) == 2){
            return false;
        } else {
            return true;
        }
    }

    private void restoreBattleState(int step) {
        switch (step){
            case 0:
                calcMonsterAttackButton.setEnabled(true);
                break;
            case 1:
                calcHeroAttackButton.setEnabled(true);
                break;
            case 2:
                boolean isAllowed = ((MainActivity)getActivity()).isAllowedToTakeChance() &&
                        ((MainActivity) getActivity()).gamePref.getInt("heroAttack",0) !=
                                ((MainActivity) getActivity()).gamePref.getInt("monsterAttack",0);
                takeChanceButton.setEnabled(isAllowed);
                fleeFromBattleButton.setEnabled(isFleeAllowed());
                setRoundResult();
                dalee.setEnabled(true);
                break;
            case 3:
                fleeFromBattleButton.setEnabled(isFleeAllowed());
                setRoundResult();
                dalee.setEnabled(true);
                break;
        }
    }

    private void setRoundResult() {
        String result = "";
        heroVVV = ((MainActivity) getActivity()).gamePref.getInt("VVV", 0);
        monsterVVV = ((MainActivity) getActivity()).gamePref.getInt("monsterVVV", 0);
        int monsterA = ((MainActivity)getActivity()).gamePref.getInt("monsterAttack", 0);
        int heroA = ((MainActivity)getActivity()).gamePref.getInt("heroAttack", 0);
        if (monsterA > heroA) {
            if (luck > 0) {
                result = "Ты теряешь 1В";
                heroVVV-=1;
            } else if (luck < 0) {
                result = "Ты теряешь 3В";
                heroVVV-=3;
            } else {
                result = "Ты теряешь 2В";
                heroVVV-=2;
            }
        } else if (monsterA < heroA) {
            if (luck > 0) {
                result = "" + monsterName + " теряет 4В";
                monsterVVV-=4;
            } else if (luck < 0) {
                result = "" + monsterName + " теряет 1В";
                monsterVVV-=1;
            } else {
                result = "" + monsterName + " теряет 2В";
                monsterVVV-=2;
            }
        } else {
            result = "Равный раунд";
        }
        if (monsterVVV < 1) {
            dalee.setText("Победа!");
            takeChanceButton.setEnabled(false);
            fleeFromBattleButton.setEnabled(false);
        } else {
            dalee.setText("Продолжить битву");
        }
        roundResultTextView.setText(result);
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
}