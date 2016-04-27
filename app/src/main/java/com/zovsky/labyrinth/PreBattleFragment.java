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

import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PreBattleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PreBattleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreBattleFragment extends Fragment {

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

    private int monsterLLL;
    private int monsterVVV;

    private OnFragmentInteractionListener mListener;

    public PreBattleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param article Parameter 1.
     * @param numberOfPara Parameter 2.
     * @param numberOfRadios Parameter 3.
     * @return A new instance of fragment PreBattleFragment.
     */
    public static PreBattleFragment newInstance(int article, int numberOfPara, int numberOfRadios) {
        PreBattleFragment fragment = new PreBattleFragment();
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
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.getMenu().getItem(0).setTitle(R.string.action_settings).setEnabled(true);
        //set LVU as toolbar title
        String toolbarTitle = "Л:" + ((MainActivity)getActivity()).gamePref.getInt("LLL",0) +
                " В:" + ((MainActivity)getActivity()).gamePref.getInt("VVV",0) +
                " У:" + ((MainActivity)getActivity()).gamePref.getInt("UUU",0);
        ((MainActivity) getActivity()).setToolbarTitle(toolbarTitle, Integer.toString(mArticle));

        View view = inflater.inflate(R.layout.fragment_pre_battle, container, false);

        TextView[] textView = new TextView[mParas];
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.article_layout);
        String articleID, optionID, textID;
        //populate textViews
        for (int para = 0; para < mParas; para++) {
            articleID = "article_" + mArticle + "_" + (para);
            int resID = getStringResourceByName(articleID);
            if (para == 0) {
                textView[para] = (TextView) view.findViewById(R.id.monster_name);
                textView[para].setText(resID);
                ((MainActivity) getActivity()).editor.putString("monsterName", getResources().getString(resID)).commit();
            }
            if (para == 1) {
                monsterLLL = Integer.parseInt(getResources().getString(resID));
                textView[para] = (TextView) view.findViewById(R.id.monster_LLL);
                textView[para].setText("Ловкость: " + monsterLLL);
                ((MainActivity) getActivity()).editor.putInt("monsterLLL", monsterLLL).commit();
            }
            if (para == 2) {
                monsterVVV = Integer.parseInt(getResources().getString(resID));
                textView[para] = (TextView) view.findViewById(R.id.monster_VVV);
                textView[para].setText("Выносливость: " + monsterVVV);
                ((MainActivity) getActivity()).editor.putInt("monsterVVV", monsterVVV).commit();
            }
            if (para == 3) {
                //TODO: надеть шлем
                textView[para] = (TextView) view.findViewById(R.id.battle_condition_text);
                if (getResources().getString(resID).equals("")) {
                    textView[para].setEnabled(false);
                }
            }
            if (para == 4) {
                textView[para] = (TextView) view.findViewById(R.id.flee_condition_text);
                textView[para].setText(resID);
            }
        }

        //find out pre_battle_action and flee_condition
        //flee_conditions:
        // 0 - после каждого раунда
        // 1 - после первого раунда

        String flee_condition = "option_" + mArticle + "_1";
        int resID = getStringResourceByName(flee_condition);
        ((MainActivity) getActivity()).editor.putInt("fleeCondition", Integer.parseInt(getResources().getString(resID))).commit();

        String fleeArticle = "option_" + mArticle + "_2";
        resID = getStringResourceByName(fleeArticle);
        ((MainActivity) getActivity()).editor.putInt("fleeArticle", Integer.parseInt(getResources().getString(resID))).commit();

        String victoryArticle = "option_" + mArticle + "_3";
        resID = getStringResourceByName(victoryArticle);
        ((MainActivity) getActivity()).editor.putInt("victoryArticle", Integer.parseInt(getResources().getString(resID))).commit();

        Button startBattle = (Button) view.findViewById(R.id.start_battle_button);
        startBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).takeAction(mArticle);
                ((MainActivity) getActivity()).showAllParameters();
                ((MainActivity) getActivity()).editor.putInt("round", 1);
                ((MainActivity) getActivity()).editor.remove("monster_attack");
                ((MainActivity) getActivity()).editor.remove("hero_attack");
                ((MainActivity) getActivity()).showBattle(mArticle + 1000);
            }
        });
        return view;
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
