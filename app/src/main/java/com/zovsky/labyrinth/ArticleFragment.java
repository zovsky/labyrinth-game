package com.zovsky.labyrinth;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Map;


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

    private SharedPreferences gamePref;
    private SharedPreferences.Editor editor;

    private int mArticle;
    private int mParas;
    private int mRadios;

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

        gamePref = getActivity().getSharedPreferences(GAME, Context.MODE_PRIVATE);
        editor = gamePref.edit();
        editor.putInt("currentArticle", mArticle);
        editor.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setInventoryVisibility(true);
        gamePref = getActivity().getSharedPreferences(GAME, Context.MODE_PRIVATE);
        String toolbarTitle = "Л:" + gamePref.getInt("LLL",0) +
                                " В:" + gamePref.getInt("VVV",0) +
                                " У:" + gamePref.getInt("UUU",0);
        ((MainActivity) getActivity()).setToolbarTitle(toolbarTitle, Integer.toString(mArticle));
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        TextView[] textView = new TextView[mParas];
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.article_layout);
        String articleID, optionID, textID;
        //generate text
        for (int para = 0; para< mParas; para++) {
            textView[para] = new TextView(getContext());
            layout.addView(textView[para]);
            articleID = "article_" + mArticle + "_" + (para+1);
            int resID = getStringResourceByName(articleID);
            textView[para].setText(resID);
        }
        //generate radio buttons
        final RadioGroup radioGroup = new RadioGroup(getContext());
        RadioButton[] radioButton = new RadioButton[mRadios];
        layout.addView(radioGroup);
        for (int radio = 0; radio< mRadios; radio++) {
            radioButton[radio] = new RadioButton(getContext());
            radioButton[radio].setId(View.generateViewId());
            radioGroup.addView(radioButton[radio]);
            optionID = "option_" + mArticle + "_" + (radio+1); //option_25_1, option_25_2
            int resID = getStringResourceByName(optionID); //R.string
            textID = "text_" + mArticle + "_" + (radio+1); //text_25_1
            int radioTextID = getStringResourceByName(textID);
            if (mRadios == 1) {
                radioGroup.check(radioButton[radio].getId());
                gamePref = getActivity().getSharedPreferences(GAME, Context.MODE_PRIVATE);
                editor = gamePref.edit();
                editor.putInt("nextArticle", Integer.parseInt(getResources().getString(resID)));
                editor.commit();
            }
            String radioText = getResources().getString(resID) + ", " + getResources().getString(radioTextID);
            radioButton[radio].setText(radioText);
        }
        Button dalee = new Button(getContext());
        //TODO: get nextArticle if select manually.
        layout.addView(dalee);
        dalee.setText("Продолжить");
        dalee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int selectedRadioID = radioGroup.getCheckedRadioButtonId();
                int nextArticle = getActivity().getSharedPreferences(GAME, Context.MODE_PRIVATE).getInt("nextArticle", 0);
                ((MainActivity) getActivity()).showArticle(nextArticle);
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
