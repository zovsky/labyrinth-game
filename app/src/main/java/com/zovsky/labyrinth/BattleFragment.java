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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Set;

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
    private int roundText;
    private String monster_name;

    private Button calc_monster_attack;
    private TextView text_monster_attack;
    private Button calc_hero_attack;
    private TextView text_hero_attack;
    private Button good_luck;
    private TextView round_result;
    private Button fleeBattle;
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

        monsterLLL = ((MainActivity) getActivity()).gamePref.getInt("monsterLLL", 0);
        monsterVVV = ((MainActivity) getActivity()).gamePref.getInt("monsterVVV", 0);
        String monster_data = "Л:" + monsterLLL + " В:" + monsterVVV;

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.getMenu().getItem(0).setTitle(monster_data).setEnabled(false);
        //set LVU as toolbar title
        String toolbarTitle = "Л:" + ((MainActivity)getActivity()).gamePref.getInt("LLL",0) +
                " В:" + ((MainActivity)getActivity()).gamePref.getInt("VVV",0) +
                " У:" + ((MainActivity)getActivity()).gamePref.getInt("UUU",0);
        ((MainActivity) getActivity()).setToolbarTitle(toolbarTitle, Integer.toString(mArticle)); //null
        luck = 0;

        View view = inflater.inflate(R.layout.fragment_battle, container, false);

        monster_name = ((MainActivity) getActivity()).gamePref.getString("monsterName", "");
        TextView monster_name_corner = (TextView) view.findViewById(R.id.monster_name_corner);
        monster_name_corner.setText(monster_name);

        roundText = ((MainActivity) getActivity()).gamePref.getInt("round", 0);
        final TextView round = (TextView) view.findViewById(R.id.battle_round_text);
        round.setText("Раунд " + roundText);

        calc_monster_attack = (Button) view.findViewById(R.id.calc_monster_attack);
        calc_monster_attack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rnd = new Random();
                int monster_attack = rnd.nextInt(6)+rnd.nextInt(6)+2+((MainActivity)getActivity()).gamePref.getInt("monsterLLL",0);
                ((MainActivity) getActivity()).editor.putInt("monster_attack", monster_attack).commit();
                calc_monster_attack.setEnabled(false);
                text_monster_attack.setText("" + monster_attack);
                calc_hero_attack.setEnabled(true);
            }
        });

        text_monster_attack = (TextView) view.findViewById(R.id.text_monster_attack);
        if (((MainActivity) getActivity()).gamePref.getInt("monster_attack", 0) > 0) {
            calc_monster_attack.setEnabled(false);
            text_monster_attack.setText("" + ((MainActivity) getActivity()).gamePref.getInt("monster_attack", 0));
        }

        calc_hero_attack = (Button) view.findViewById(R.id.calc_hero_attack);
        calc_hero_attack.setEnabled(false);
        calc_hero_attack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rnd = new Random();
                int hero_attack = rnd.nextInt(6)+rnd.nextInt(6)+2+((MainActivity)getActivity()).gamePref.getInt("LLL",0);
                ((MainActivity) getActivity()).editor.putInt("hero_attack", hero_attack).commit();
                calc_hero_attack.setEnabled(false);
                text_hero_attack.setText("" + hero_attack);
                setRoundResult();
                if (((MainActivity) getActivity()).gamePref.getInt("hero_attack", 0) !=
                        ((MainActivity) getActivity()).gamePref.getInt("monster_attack", 0)) {
                    if (((MainActivity) getActivity()).gamePref.getInt("UUU", 0) > 0) {
                        good_luck.setEnabled(true);
                    }
                    fleeBattle.setEnabled(true);
                    dalee.setEnabled(true);
                } else {
                    fleeBattle.setEnabled(true);
                    dalee.setEnabled(true);
                }
            }
        });

        text_hero_attack = (TextView) view.findViewById(R.id.text_hero_attack);
        if (((MainActivity) getActivity()).gamePref.getInt("hero_attack", 0) > 0) {
            calc_hero_attack.setEnabled(false);
            text_hero_attack.setText("" + ((MainActivity) getActivity()).gamePref.getInt("hero_attack", 0));
        }

        good_luck = (Button) view.findViewById(R.id.good_luck);
        good_luck.setEnabled(false);
        good_luck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                luck = ((MainActivity) getActivity()).takeChance();
                good_luck.setEnabled(false);
                setRoundChanceResult();
            }
        });

        round_result = (TextView) view.findViewById(R.id.round_result);

        fleeBattle = (Button) view.findViewById(R.id.battle_flee_button);
        fleeBattle.setEnabled(false);
        fleeBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO alert
                ((MainActivity) getActivity()).showArticle(((MainActivity) getActivity()).gamePref.getInt("fleeArticle", 0));
            }
        });

        dalee = (Button) view.findViewById(R.id.battle_continue_button);
        dalee.setEnabled(false);
        dalee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).showAllParameters();
                ((MainActivity) getActivity()).editor.putInt("round", roundText + 1);
                ((MainActivity) getActivity()).editor.remove("monster_attack");
                ((MainActivity) getActivity()).editor.remove("hero_attack");
                ((MainActivity) getActivity()).showBattle(mArticle);
            }
        });

        ((MainActivity) getActivity()).showAllParameters();
        return view;
    }

    private void setRoundChanceResult() {
        String result = "";
        int monsterA = ((MainActivity)getActivity()).gamePref.getInt("monster_attack", 0);
        int heroA = ((MainActivity)getActivity()).gamePref.getInt("hero_attack", 0);
        if (monsterA > heroA && luck > 0 ) {
            result = "Ты теряешь 1В";
        } else if (monsterA > heroA && luck < 0) {
            result = "Ты теряешь 3В";
        } else if (monsterA < heroA && luck > 0) {
            result = "" + ((MainActivity) getActivity()).gamePref.getString("monsterName", "") + " теряет 4В";
        } else if (monsterA < heroA && luck < 0) {
            result = "" + ((MainActivity) getActivity()).gamePref.getString("monsterName", "") + " теряет 1В";
        }
        round_result.setText(result);
    }

    private void setRoundResult() {
        String result = "";
        int monsterA = ((MainActivity)getActivity()).gamePref.getInt("monster_attack", 0);
        int heroA = ((MainActivity)getActivity()).gamePref.getInt("hero_attack", 0);
        if (monsterA > heroA) {
            result = "Ты теряешь 2В";
        } else if (monsterA < heroA) {
            result = "" + ((MainActivity) getActivity()).gamePref.getString("monsterName", "") + " теряет 2В";
        } else {
            result = "Равный раунд";
        }
        round_result.setText(result);
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
