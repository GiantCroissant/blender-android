package com.giantcroissant.blender;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CookBookDetailInfoFragment.OnCookBookDetailInfoFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CookBookDetailInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CookBookDetailInfoFragment extends Fragment {


    private OnCookBookDetailInfoFragmentInteractionListener mListener;
    private CookBook cookBook;
    private View rootView;

    public static CookBookDetailInfoFragment newInstance(CookBook cookBook) {
        CookBookDetailInfoFragment fragment = new CookBookDetailInfoFragment();
        fragment.cookBook = cookBook;
        return fragment;
    }

    public CookBookDetailInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cook_book_detial_info, container, false);
        setUIValue();
        return rootView;
    }

    private void setUIValue()
    {
        TextView cookBookNameText = (TextView) rootView.findViewById(R.id.cookBookNameText);
        cookBookNameText.setText(cookBook.getName());

        TextView cookBookIngredientText = (TextView) rootView.findViewById(R.id.cookBookIngredientText);
        cookBookIngredientText.setText("材料： " + cookBook.getIngredient());


        TextView cookBookStepsText = (TextView) rootView.findViewById(R.id.cookBookStepsText);
        String tmpSteps = "";
        for (int i = 0; i < cookBook.getSteps().size(); i++) {

            tmpSteps += i+")."+cookBook.getSteps().get(i) + "。\n";
        }

        cookBookStepsText.setText("作法 : \n" + tmpSteps);

        TextView cookBookDescriptionText = (TextView) rootView.findViewById(R.id.cookBookDescriptionText);
        cookBookDescriptionText.setText("描述 :"+ cookBook.getDescription());

        ImageButton likeCookbookButton = (ImageButton) rootView.findViewById(R.id.likeCookBookButton);
        if(cookBook.getIsCollected())
        {
            likeCookbookButton.setImageResource(R.drawable.icon_collect_y) ;
        }
        else
        {

            likeCookbookButton.setImageResource(R.drawable.icon_collect_n) ;
        }

        ImageView cookbookicon = (ImageView) rootView.findViewById(R.id.cookbookicon);
//        cookbookicon.setImageURI(Uri.parse(cookBook.getImageUrl()));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String string) {
        if (mListener != null) {
            mListener.onCookBookDetailInfoFragmentInteraction(string);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCookBookDetailInfoFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
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
    public interface OnCookBookDetailInfoFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onCookBookDetailInfoFragmentInteraction(String string);
    }
}
