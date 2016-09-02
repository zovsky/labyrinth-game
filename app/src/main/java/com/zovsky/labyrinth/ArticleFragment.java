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

        if (mArticle == 401) {
            ((MainActivity) getActivity()).showAllParameters();
            boolean was122 = ((MainActivity) getActivity()).wasIHere(122);
            if (was122 == true) {
                mParas = 1;
                mRadios = 1;
            } else if (was122 == false) {
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
        if (mArticle == 200 || mArticle == 224 || mArticle == 38) {
            //was I here? if yes, show only first option, otherwise, show only second option
            if (wasHere == true) {
                mRadios = 1;
            } else if (wasHere == false) {
                radioStartCount = 1;
            }
            Log.d(GAME, "was here:" + wasHere);
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
            String radioText = radioTextNumber + radioTextShortDescription;
            //set radio button text

            radioButton[radio].setText(radioText);

            if (mRadios == 1 || radioStartCount == 1) {
                //set checked radio button if it is sole
                radioGroup.check(radioButton[radio].getId());
            }
        }
        final Button dalee = new Button(getContext());
        layout.addView(dalee);
        //articles before battle;
        if (mArticle == 2) {
            dalee.setText("БИТВА");
        } else dalee.setText("Продолжить");

        //special conditions; execute on article load
        if (mArticle == 3) {
            if (((MainActivity) getActivity()).gamePref.getInt("gold", 0) < 13) {
                radioGroup.getChildAt(0).setEnabled(false);
                radioButton[0].setText("Недостаточно золота");
            }
        }

        if (mArticle == 18) {
            if (((MainActivity) getActivity()).gamePref.getInt("gold", 0) < 5) {
                radioGroup.check(radioButton[1].getId());
                radioGroup.getChildAt(0).setEnabled(false);
                radioButton[1].setText("286, Недостаточно золота");
            }
        }
        if (mArticle == 22) {
            //TODO: если нету ничего из списка в пункте 22
            radioGroup.getChildAt(0).setEnabled(false);
            radioGroup.getChildAt(1).setEnabled(false);
            radioGroup.getChildAt(2).setEnabled(false);
            Set<String> things = ((MainActivity) getActivity()).gamePref.getStringSet("things", new HashSet<String>());
            for (String thing : things) {
                if (thing.equals("Железная бабочка")) {
                    radioGroup.getChildAt(0).setEnabled(true);
                }
                if (thing.equals("Изумруд")) {
                    radioGroup.getChildAt(1).setEnabled(true);
                }
                if (thing.equals("Деревянный кол")) {
                    radioGroup.getChildAt(2).setEnabled(true);
                }
            }
            if (!radioGroup.getChildAt(0).isEnabled() && !radioGroup.getChildAt(1).isEnabled() && !radioGroup.getChildAt(2).isEnabled()) {
                radioGroup.check(radioButton[3].getId());
            } else {
                radioGroup.getChildAt(3).setVisibility(View.INVISIBLE);
            }
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
        if (mArticle == 30) {
            if (((MainActivity) getActivity()).takeChance() == 1) {
                radioGroup.check(radioButton[0].getId());
                radioGroup.getChildAt(1).setEnabled(false);
            } else {
                radioGroup.check(radioButton[1].getId());
                radioGroup.getChildAt(0).setEnabled(false);
            }
        }
        if (mArticle == 35 || mArticle == 36 || mArticle == 41) {
            final Button takeChance = new Button(getContext());
            boolean wasHereForChance = ((MainActivity) getActivity()).wasIHere(mArticle);
            if (wasHereForChance == false) {
                layout.addView(takeChance, 1);
                takeChance.setText("Испытай удачу (-1У)");
                radioGroup.getChildAt(0).setEnabled(false);
                radioGroup.getChildAt(1).setEnabled(false);
                dalee.setEnabled(false);
                final int firstRadioID = radioButton[0].getId();
                final int secondRadioID = radioButton[1].getId();
                takeChance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        takeChance.setEnabled(false);
                        if (((MainActivity) getActivity()).takeChance() == 1) {
                            radioGroup.check(firstRadioID);
                            radioGroup.getChildAt(0).setEnabled(true);
                            ((MainActivity) getActivity()).editor.putInt("chanceRadioID", firstRadioID).commit();
                        } else {
                            radioGroup.check(secondRadioID);
                            radioGroup.getChildAt(1).setEnabled(true);
                            ((MainActivity) getActivity()).editor.putInt("chanceRadioID", secondRadioID).commit();
                        }
                        Set<String> room = new HashSet<>();
                        room.add(Integer.toString(mArticle));
                        ((MainActivity) getActivity()).editor.putStringSet("wasHere", room).commit();
                        dalee.setEnabled(true);
                    }
                });

            } else {
                int radioID = ((MainActivity) getActivity()).gamePref.getInt("chanceRadioID", 0);
                if (radioID == radioButton[0].getId()) {
                    radioGroup.check(radioID);
                    radioGroup.getChildAt(1).setEnabled(false);
                } else {
                    radioGroup.check(radioID);
                    radioGroup.getChildAt(0).setEnabled(false);
                }
                dalee.setEnabled(true);
            }

        }
        if (mArticle == 106) {
            radioGroup.getChildAt(1).setEnabled(false);
            Set<String> things = ((MainActivity) getActivity()).gamePref.getStringSet("things", new HashSet<String>());
            for (String thing : things) {
                if (thing.equals("Банка ядовитой пыли")) {
                    radioGroup.getChildAt(1).setEnabled(true);
                } else if (thing.equals("Всеядные ракообразные")) {
                    radioGroup.getChildAt(1).setEnabled(true);
                }
            }
            if (!radioGroup.getChildAt(1).isEnabled()) {
                radioGroup.check(radioButton[0].getId());
            }
        }
        if (mArticle == 292) {
            radioGroup.getChildAt(0).setEnabled(false);
            radioGroup.getChildAt(1).setEnabled(false);
            Set<String> things = ((MainActivity) getActivity()).gamePref.getStringSet("things", new HashSet<String>());
            for (String thing : things) {
                if (thing.equals("Банка ядовитой пыли")) {
                    radioGroup.getChildAt(0).setEnabled(true);
                } else if (thing.equals("Всеядные ракообразные")) {
                    radioGroup.getChildAt(1).setEnabled(true);
                }
            }
            if (!radioGroup.getChildAt(1).isEnabled()) {
                radioGroup.check(radioButton[0].getId());
            } else if (!radioGroup.getChildAt(0).isEnabled()) {
                radioGroup.check(radioButton[1].getId());
            }
        }

        //take special action on article load
        ((MainActivity) getActivity()).takeSpecialAction(mArticle);

        dalee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
