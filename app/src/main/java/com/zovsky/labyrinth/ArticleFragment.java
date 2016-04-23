package com.zovsky.labyrinth;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //show inventory button
        ((MainActivity)getActivity()).setInventoryVisibility(true);
        //set LVU as toolbar title
        String toolbarTitle = "Л:" + ((MainActivity)getActivity()).gamePref.getInt("LLL",0) +
                                " В:" + ((MainActivity)getActivity()).gamePref.getInt("VVV",0) +
                                " У:" + ((MainActivity)getActivity()).gamePref.getInt("UUU",0);
        ((MainActivity) getActivity()).setToolbarTitle(toolbarTitle, Integer.toString(mArticle));

        View view = inflater.inflate(R.layout.fragment_article, container, false);

        TextView[] textView = new TextView[mParas];
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.article_layout);
        String articleID, optionID, textID;
        //generate article paragraphs
        for (int para = 0; para< mParas; para++) {
            textView[para] = new TextView(getContext());
            layout.addView(textView[para]);
            articleID = "article_" + mArticle + "_" + (para+1);
            int resID = getStringResourceByName(articleID);
            textView[para].setText(resID);
        }
        //generate radio buttons
        final RadioGroup radioGroup = new RadioGroup(getContext());
        AppCompatRadioButton[] radioButton = new AppCompatRadioButton[mRadios];
        layout.addView(radioGroup);

        for (int radio = 0; radio< mRadios; radio++) {
            radioButton[radio] = new AppCompatRadioButton(getContext(), null, R.attr.radioButtonStyle);
            radioButton[radio].setId(radio+1);
            radioGroup.addView(radioButton[radio]);
            optionID = "option_" + mArticle + "_" + (radio+1); //option_25_1, option_25_2
            int resID = getStringResourceByName(optionID);
            textID = "text_" + mArticle + "_" + (radio+1); //text_25_1
            int radioTextID = getStringResourceByName(textID);
            choice[radio] = Integer.parseInt(getResources().getString(resID));
            String radioText = getResources().getString(resID) + ", " + getResources().getString(radioTextID);
            //set radio button text
            radioButton[radio].setText(radioText);

            if (mRadios == 1) {
                //check radio button if it is sole
                radioGroup.check(radioButton[radio].getId());
            }
        }
        Button dalee = new Button(getContext());
        layout.addView(dalee);
        dalee.setText("Продолжить");
        dalee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(getContext(), "Сделайте выбор", Toast.LENGTH_SHORT).show();
                } else {
                    ((MainActivity) getActivity()).showArticle(choice[selectedId-1]);
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

}
