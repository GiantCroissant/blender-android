package com.giantcroissant.blender;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserDataFragment.OnUserDataFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDataFragment extends Fragment implements CookBooksDataFragment {

    CookBookAdapter mCookBookAdapter;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView cookbookListView;
    private View rootView;
    private ArrayList<Cookbook> userRecordCookBooks;
    private ArrayList<Cookbook> userLikeCookBooks;
    private ArrayList<Cookbook> currentCookBooks;
//    private ArrayList<CookBook> cookBooks;
    private int tabIndex = 0;

    private OnUserDataFragmentInteractionListener mListener;
    private Realm realm;
//    private RealmQuery<CookBookRealm> cookBookRealmQuery;
//    private RealmResults<CookBookRealm> userRecordRealmResult;
//    private RealmResults<CookBookRealm> userLikeRealmResult;


    // TODO: Rename and change types and number of parameters
    public static UserDataFragment newInstance(int sectionNumber, Realm realm) {
        UserDataFragment userDataFragment = new UserDataFragment();
        userDataFragment.realm = realm;
//        userDataFragment.cookBookRealmQuery = cookBookRealmQuery;
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        userDataFragment.setArguments(args);
        return userDataFragment;
    }

    public UserDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_data, container, false);
        cookbookListView = (ListView) rootView.findViewById(R.id.usercookbooklistView);
        cookbookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

//        createFakeData();
//        getUserRecordCookBooks();
//        getUserLikeCookBooks();

        setCurrentCookBooks(realm, 0);

        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String string) {
        if (mListener != null) {
            mListener.onUserDataFragmentInteraction(string);
        }
    }

    private void getUserRecordCookBooks(Realm realm)
    {
        //RealmQuery tmpCookBookRealmQuery = realm.where(CookBookRealm.class);
        RealmResults<CookBookRealm> userRecordRealmResult = realm.where(CookBookRealm.class).findAll();//.findAllSorted("uploadTimestamp", false);
        userRecordRealmResult.sort("uploadTimestamp");
        userRecordCookBooks = new ArrayList<Cookbook>();

        for (CookBookRealm cookBookRealm : userRecordRealmResult) {
//            ArrayList<String> tmpSteps = new ArrayList<String>();
//            String[] tmpStepParts = cookBookRealm.getSteps().split("\\;");
//            for (String tmpStepPart : tmpStepParts) {
//                tmpSteps.add(tmpStepPart);
//            }
//            ArrayList<String> tmpTimeOfSteps = new ArrayList<String>();
//            String[] tmpTimeOfStepParts = cookBookRealm.getTimeOfSteps().split("\\;");
//            for (String tmpTimeOfStepPart : tmpTimeOfStepParts) {
//                tmpTimeOfSteps.add(tmpTimeOfStepPart);
////            Log.e("XXX", tmpStepPart);
//            }
//            ArrayList<String> tmpSpeedOfSteps = new ArrayList<String>();
//            String[] tmpSpeedOfStepParts = cookBookRealm.getSpeedOfSteps().split("\\;");
//            for (String tmpSpeedOfStepPart : tmpSpeedOfStepParts) {
//                tmpSpeedOfSteps.add(tmpSpeedOfStepPart);
////            Log.e("XXX", tmpStepPart);
//            }

            List<CookbookStep> cookbookSteps = new ArrayList<CookbookStep>();
            for (CookbookStepRealm cookbookStepRealm : cookBookRealm.getSteps1()) {
                CookbookStep cookbookStep = new CookbookStep();
                cookbookStep.setStepDesc(cookbookStepRealm.getStepDesc());
                cookbookStep.setStepSpeed(cookbookStepRealm.getStepSpeed());
                cookbookStep.setStepTime(cookbookStepRealm.getStepTime());

                cookbookSteps.add(cookbookStep);
            }

            Cookbook newCookBook = new Cookbook(
                    cookBookRealm.getId(),
                    cookBookRealm.getName(),
                    cookBookRealm.getDescription(),
                    cookBookRealm.getUrl(),
                    cookBookRealm.getImageUrl(),
                    cookBookRealm.getIngredient(),
                    cookbookSteps,
                    cookBookRealm.getViewedPeopleCount(),
                    cookBookRealm.getCollectedPeopleCount(),
                    cookBookRealm.getBeCollected());
            newCookBook.setUploadTimestamp(cookBookRealm.getUploadTimestamp());
            //newCookBook.setImage(BitmapFactory.decodeResource(getResources(), cookBookRealm.getImageID()));
            newCookBook.setImageID(cookBookRealm.getImageID());
            userRecordCookBooks.add(newCookBook);
        }
    }

    private void getUserLikeCookBooks(Realm realm)
    {
//        RealmQuery tmpCookBookRealmQuery = realm.where(CookBookRealm.class);
//        tmpCookBookRealmQuery = tmpCookBookRealmQuery.equalTo("beCollected", true);
//        userLikeRealmResult = tmpCookBookRealmQuery.findAllSorted("uploadTimestamp", false);
        RealmResults<CookBookRealm> userLikeRealmResult = realm.where(CookBookRealm.class).equalTo("beCollected", true).findAll();
        userLikeRealmResult.sort("uploadTimestamp");
        userLikeCookBooks = new ArrayList<Cookbook>();
        for (CookBookRealm cookBookRealm : userLikeRealmResult) {
//            ArrayList<String> tmpSteps = new ArrayList<String>();
//            String[] tmpStepParts = cookBookRealm.getSteps().split("\\;");
//            for (String tmpStepPart : tmpStepParts) {
//                tmpSteps.add(tmpStepPart);
//            }
//            ArrayList<String> tmpTimeOfSteps = new ArrayList<String>();
//            String[] tmpTimeOfStepParts = cookBookRealm.getTimeOfSteps().split("\\;");
//            for (String tmpTimeOfStepPart : tmpTimeOfStepParts) {
//                tmpTimeOfSteps.add(tmpTimeOfStepPart);
////            Log.e("XXX", tmpStepPart);
//            }
//            ArrayList<String> tmpSpeedOfSteps = new ArrayList<String>();
//            String[] tmpSpeedOfStepParts = cookBookRealm.getSpeedOfSteps().split("\\;");
//            for (String tmpSpeedOfStepPart : tmpSpeedOfStepParts) {
//                tmpSpeedOfSteps.add(tmpSpeedOfStepPart);
////            Log.e("XXX", tmpStepPart);
//            }

            List<CookbookStep> cookbookSteps = new ArrayList<CookbookStep>();
            for (CookbookStepRealm cookbookStepRealm : cookBookRealm.getSteps1()) {
                CookbookStep cookbookStep = new CookbookStep();
                cookbookStep.setStepDesc(cookbookStepRealm.getStepDesc());
                cookbookStep.setStepSpeed(cookbookStepRealm.getStepSpeed());
                cookbookStep.setStepTime(cookbookStepRealm.getStepTime());

                cookbookSteps.add(cookbookStep);
            }

            Cookbook newCookBook = new Cookbook(
                    cookBookRealm.getId(),
                    cookBookRealm.getName(),
                    cookBookRealm.getDescription(),
                    cookBookRealm.getUrl(),
                    cookBookRealm.getImageUrl(),
                    cookBookRealm.getIngredient(),
                    cookbookSteps,
                    cookBookRealm.getViewedPeopleCount(),
                    cookBookRealm.getCollectedPeopleCount(),
                    cookBookRealm.getBeCollected());
            newCookBook.setUploadTimestamp(cookBookRealm.getUploadTimestamp());

            //newCookBook.setImage(BitmapFactory.decodeResource(getResources(), cookBookRealm.getImageID()));
            newCookBook.setImageID(cookBookRealm.getImageID());
            userLikeCookBooks.add(newCookBook);
        }
    }

    public void setCurrentCookBooks(Realm realm,int tabIndex) {
        getUserRecordCookBooks(realm);
        getUserLikeCookBooks(realm);
        Button recordCookBookButton = (Button) rootView.findViewById(R.id.userRecordCookBookButton);
        Button likeCookBookButton = (Button) rootView.findViewById(R.id.userLikeCookBookButton);
        ImageButton recordCookBookButtonColor = (ImageButton) rootView.findViewById(R.id.userRecordCookBookButton_SelectColor);
        ImageButton likeCookBookButtonColor = (ImageButton) rootView.findViewById(R.id.userLikeCookBookButton_SelectColor);
        if (tabIndex == 0)
        {
            currentCookBooks = userRecordCookBooks;
            mCookBookAdapter = new CookBookAdapter(this.getActivity() , R.layout.user_cook_book_list_item, currentCookBooks);
            cookbookListView.setAdapter(mCookBookAdapter);

            recordCookBookButton.setTextColor(getResources().getColor(R.color.White));
            recordCookBookButtonColor.setImageResource(R.color.TabSelectColor);
            likeCookBookButton.setTextColor(getResources().getColor(R.color.c70White));
            likeCookBookButtonColor.setImageResource(R.color.TabNoSelectColor);

            this.tabIndex = 0;
        }
        else if(tabIndex == 1)
        {
            currentCookBooks = userLikeCookBooks;
            mCookBookAdapter = new CookBookAdapter(this.getActivity() , R.layout.user_cook_book_list_item, currentCookBooks);
            cookbookListView.setAdapter(mCookBookAdapter);

            recordCookBookButton.setTextColor(getResources().getColor(R.color.c70White));
            recordCookBookButtonColor.setImageResource(R.color.TabNoSelectColor);
            likeCookBookButton.setTextColor(getResources().getColor(R.color.White));
            likeCookBookButtonColor.setImageResource(R.color.TabSelectColor);

            this.tabIndex = 1;
        }

    }

    private void selectItem(int position) {

        Intent intent = new Intent(this.getActivity(), CookBookDetailActivity.class);

        intent.putExtra("position", position);

        intent.putExtra("cookbook", ConvertToCookbook.convertToParceable(currentCookBooks.get(position)));

//        intent.putExtra("cookBookListViewID", currentCookBooks.get(position).getId());
//        intent.putExtra("cookBookListViewName", currentCookBooks.get(position).getName());
//        intent.putExtra("cookBookListViewDescription", currentCookBooks.get(position).getDescription());
//        intent.putExtra("cookBookListViewUrl", currentCookBooks.get(position).getUrl());
//        intent.putExtra("cookBookListViewImageUrl", currentCookBooks.get(position).getImageUrl());
//        intent.putExtra("cookBookListViewIngredient", currentCookBooks.get(position).getIngredient());
//        intent.putExtra("cookBookListViewSteps", currentCookBooks.get(position).getSteps());
//        intent.putExtra("cookBookListViewViewPeople", currentCookBooks.get(position).getViewedPeopleCount());
//        intent.putExtra("cookBookListViewCollectedPeople", currentCookBooks.get(position).getCollectedPeopleCount());
//        intent.putExtra("cookBookListIsCollected", currentCookBooks.get(position).getIsCollected());
//        intent.putExtra("cookBookListViewTimeOfSteps", currentCookBooks.get(position).getTimeOfSteps());
//        intent.putExtra("cookBookListViewSpeedOfSteps", currentCookBooks.get(position).getSpeedOfSteps());
//        intent.putExtra("cookBookImageId",currentCookBooks.get(position).getImageID());

        intent.putExtra("requestCode", 104);
        getActivity().startActivityFromFragment(this, intent, 104);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        try {
            mListener = (OnUserDataFragmentInteractionListener) activity;
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
    public interface OnUserDataFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onUserDataFragmentInteraction(String String);
    }

    public void upDateListView(Realm realm) {

        if(currentCookBooks == userRecordCookBooks)
        {
            getUserRecordCookBooks(realm);
            currentCookBooks = userRecordCookBooks;
        }
        else if(currentCookBooks == userLikeCookBooks)
        {
            getUserLikeCookBooks(realm);
            currentCookBooks = userLikeCookBooks;
        }
        mCookBookAdapter = new CookBookAdapter(this.getActivity() , R.layout.cook_book_list_item, currentCookBooks);
        cookbookListView.setAdapter(mCookBookAdapter);
    }

}
