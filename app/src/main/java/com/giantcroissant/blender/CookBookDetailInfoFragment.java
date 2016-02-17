package com.giantcroissant.blender;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.giantcroissant.blender.realm.RealmHelper;

import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CookBookDetailInfoFragment.OnCookBookDetailInfoFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CookBookDetailInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CookBookDetailInfoFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = CookBookDetailInfoFragment.class.getName();
    private OnCookBookDetailInfoFragmentInteractionListener mListener;
    private Cookbook cookBook;
    private View rootView;
    private String recipeId = "";
    private Realm realm;

    public static CookBookDetailInfoFragment newInstance(int sectionNumber, Cookbook cookBook) {
        CookBookDetailInfoFragment fragment = new CookBookDetailInfoFragment();
//        fragment.cookBook = cookBook;

        fragment.recipeId = cookBook.getId();

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public CookBookDetailInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = RealmHelper.getRealmInstance(getContext());
        CookBookRealm result = realm.where(CookBookRealm.class)
            .equalTo("Id", recipeId)
            .findAll()
            .first();

        if (result != null) {
            cookBook = new Cookbook(result);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cook_book_detial_info, container, false);
        setUIValue();
        return rootView;
    }

    private void setUIValue() {
        TextView cookBookNameText = (TextView) rootView.findViewById(R.id.cookBookNameText);
        cookBookNameText.setText(cookBook.getName());

        TextView cookBookIngredientText = (TextView) rootView.findViewById(R.id.cookBookIngredientText);
        cookBookIngredientText.setText(getResources().getTextArray(R.array.menu_items_labels)[0] + "\n" + cookBook.getIngredient() + "\n", TextView.BufferType.SPANNABLE);
        Spannable spanCookBookIngredientText = (Spannable) cookBookIngredientText.getText();

        spanCookBookIngredientText.setSpan(new ForegroundColorSpan(0xFF336900), 0, getResources().getTextArray(R.array.menu_items_labels)[0].length(),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        TextView cookBookStepsText = (TextView) rootView.findViewById(R.id.cookBookStepsText);
        String tmpSteps = "";
        for (int i = 0; i < cookBook.getSteps1().size(); ++i) {
            CookbookStep cs = cookBook.getSteps1().get(i);
            tmpSteps += (i + 1) + " " + cs.getStepDesc() + "";
            int speed = Integer.parseInt(cs.getStepSpeed());
            int time = Integer.parseInt(cs.getStepTime());
            if (speed > 0 && time > 0) {
                tmpSteps = tmpSteps + "轉速" + cs.getStepSpeed() + "，" + cs.getStepTime() + "秒。";

            }
            tmpSteps += "\n";
        }

        cookBookStepsText.setText(getResources().getTextArray(R.array.menu_items_labels)[1] + "\n" + tmpSteps, TextView.BufferType.SPANNABLE);
        Spannable spanCookBookStepsText = (Spannable) cookBookStepsText.getText();

        spanCookBookStepsText.setSpan(new ForegroundColorSpan(0xFF336900), 0, getResources().getTextArray(R.array.menu_items_labels)[1].length(),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        TextView cookBookDescriptionText = (TextView) rootView.findViewById(R.id.cookBookDescriptionText);
        cookBookDescriptionText.setText(getResources().getTextArray(R.array.menu_items_labels)[2] + "\n" + cookBook.getDescription(), TextView.BufferType.SPANNABLE);
        Spannable spanCookBookDescriptionText = (Spannable) cookBookDescriptionText.getText();
        spanCookBookDescriptionText.setSpan(new ForegroundColorSpan(0xFF336900), 0, getResources().getTextArray(R.array.menu_items_labels)[2].length(),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        updateLikeButtonImage();

        ImageButton likeCookbookButton = (ImageButton) rootView.findViewById(R.id.likeCookBookButton);
        likeCookbookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cookBook.setIsCollected(!cookBook.getIsCollected());
                updateLikeButtonImage();

                CookBookRealm cookBookRealm = realm.where(CookBookRealm.class)
                    .equalTo("Id", recipeId)
                    .findAll()
                    .first();

                realm.beginTransaction();
                cookBookRealm.setBeCollected(cookBook.getIsCollected());

//                if (cookBook.getIsCollected()) {
//                    cookBookRealm.setCollectedPeopleCount(cookBook.getCollectedPeopleCount() + 1);
//                    cookBook.setCollectedPeopleCount(cookBook.getCollectedPeopleCount() + 1);
//
//                } else {
//                    cookBookRealm.setCollectedPeopleCount(cookBook.getCollectedPeopleCount() - 1);
//                    cookBook.setCollectedPeopleCount(cookBook.getCollectedPeopleCount() - 1);
//                }
                realm.commitTransaction();
            }
        });

        ImageView imageView = (ImageView) rootView.findViewById(R.id.cookBookIcon);
        String imagePath = "file:///android_asset/recipe_images/" + cookBook.getImageName();

        Glide.with(this)
            .load(Uri.parse(imagePath))
            .centerCrop()
            .into(imageView);
    }

    private void updateLikeButtonImage() {
        ImageButton likeCookbookButton = (ImageButton) rootView.findViewById(R.id.likeCookBookButton);
        if (cookBook.getIsCollected()) {
            likeCookbookButton.setImageResource(R.drawable.icon_collect_y);

        } else {
            likeCookbookButton.setImageResource(R.drawable.icon_collect_n);
        }
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCookBookDetailInfoFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onCookBookDetailInfoFragmentInteraction(String string);
    }
}
