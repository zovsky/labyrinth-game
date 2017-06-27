package com.zovsky.labyrinth;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ButtonsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ButtonsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewGameFragment extends Fragment {

    private final static String GAME = "com.zovsky.labyrinth";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button generator;
    private RadioGroup radioGroup;
    private Button mainEntrance;
    private TextView vinos;
    private TextView lovk;
    private TextView udaca;
    int LLL;
    int VVV;
    int KKK;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public NewGameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ButtonsFragment.
     */

    public static ButtonsFragment newInstance(String param1, String param2) {
        ButtonsFragment fragment = new ButtonsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ((MainActivity)getActivity()).setInventoryVisibility(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_game, container, false);

        if (((MainActivity)getActivity()).gamePref.getInt("gameOn", 0) == 0) {
            ((MainActivity)getActivity()).setInitialParameters();
        }

        generator = (Button) view.findViewById(R.id.init_calc_button);
        generator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateInitials();
            }
        });

        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        int elixir = ((MainActivity)getActivity()).gamePref.getInt("elixir", 0);
        if (elixir != 0) {
            radioGroup.check(elixir);
        }

        lovk = (TextView) view.findViewById(R.id.lovk_text);
        vinos = (TextView) view.findViewById(R.id.vinos_text);
        udaca = (TextView) view.findViewById(R.id.udaca_text);

        setLVUnumbers();

        mainEntrance = (Button) view.findViewById(R.id.entrance_button);
        mainEntrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1 || ((MainActivity)getActivity()).gamePref.getInt("VVV", 0) == 0) {
                    Toast.makeText(getContext(), "Рассчитай начальные параметры и выбери эликсир", Toast.LENGTH_SHORT).show();
                } else {
                    ((MainActivity)getActivity()).editor.putInt("startLLL", 1+((MainActivity)getActivity()).gamePref.getInt("LLL", 0));
                    ((MainActivity)getActivity()).editor.putInt("startVVV", 1+((MainActivity)getActivity()).gamePref.getInt("VVV", 0));
                    ((MainActivity)getActivity()).editor.putInt("startKKK", 1+((MainActivity)getActivity()).gamePref.getInt("KKK", 0));
                    ((MainActivity)getActivity()).editor.putInt("elixirCounter", 2);
                    switch (selectedId){
                        case R.id.radioButton1:
                            ((MainActivity)getActivity()).editor.putInt("elixir", 1);
                            //((MainActivity)getActivity()).editor.commit();
                            break;
                        case R.id.radioButton2:
                            ((MainActivity)getActivity()).editor.putInt("elixir", 2);
                            //((MainActivity)getActivity()).editor.commit();
                            break;
                        case R.id.radioButton3:
                            ((MainActivity)getActivity()).editor.putInt("elixir", 3);
                            //((MainActivity)getActivity()).editor.commit();
                            break;

                    }
                    ((MainActivity)getActivity()).editor.putInt("gameOn", 1);
                    ((MainActivity)getActivity()).editor.commit();
                    ((MainActivity)getActivity()).generateInitialMenu();
                    ((MainActivity) getActivity()).showAllParameters();
                    ((MainActivity)getActivity()).showArticle(1);
                }
            }
        });
        ((MainActivity) getActivity()).setToolbarTitle("Лабиринт", "");
        return view;
    }

    private void generateInitials() {
        Random rnd = new Random();
        LLL = rnd.nextInt(6)+7;
        VVV = rnd.nextInt(6)+rnd.nextInt(6)+14;
        KKK = rnd.nextInt(6)+7;
        ((MainActivity)getActivity()).editor.putInt("LLL", LLL);
        ((MainActivity)getActivity()).editor.putInt("VVV", VVV);
        ((MainActivity)getActivity()).editor.putInt("KKK", KKK);
        ((MainActivity)getActivity()).editor.commit();

        setLVUnumbers();
    }

    private void setLVUnumbers() {
        //TODO: Не больше 3х раз, пауза на сутки
        if (((MainActivity)getActivity()).gamePref.getInt("LLL", 0) == 0) {
            lovk.setText("ЛОВКОСТЬ (7-12)");
            vinos.setText("ВЫНОСЛИВОСТЬ (14-24)");
            udaca.setText("КАРМА (7-12)");
        } else {
            lovk.setText("ЛОВКОСТЬ (7-12) " + Integer.toString(((MainActivity)getActivity()).gamePref.getInt("LLL", 0)));
            vinos.setText("ВЫНОСЛИВОСТЬ (14-24) " + Integer.toString(((MainActivity)getActivity()).gamePref.getInt("VVV", 0)));
            udaca.setText("КАРМА (7-12) " + Integer.toString(((MainActivity)getActivity()).gamePref.getInt("KKK", 0)));
        }
    }

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
        void onFragmentInteraction(Uri uri);
    }
}
