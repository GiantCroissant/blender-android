package com.giantcroissant.blender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.giantcroissant.blender.realm.CookBookRealm;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CookBooksFragment.OnCookBooksFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CookBooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CookBooksFragment extends Fragment implements CookBooksDataFragment {

    private static final int REQUEST_COOKBOOK = 2;

    private CookBookAdapter mCookBookAdapter;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView cookbookListView;
    private View rootView;
    private ArrayList<Cookbook> newCookBooks;
    private ArrayList<Cookbook> hotCookBooks;
    private ArrayList<Cookbook> currentCookBooks;
//    private ArrayList<CookBook> cookBooks;
//    private int tabIndex = 0;

    private OnCookBooksFragmentInteractionListener mListener;
    private Realm realm;
//    private RealmQuery<CookBookRealm> cookBookRealmQuery;
//    private RealmResults<CookBookRealm> newCookBooksRealmResult;
//    private RealmResults<CookBookRealm> hotCookBooksRealmResult;

    public static CookBooksFragment newInstance(int sectionNumber, Realm realm) {
        CookBooksFragment cookBooksFragment = new CookBooksFragment();
        cookBooksFragment.realm = realm;
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        cookBooksFragment.setArguments(args);
        return cookBooksFragment;
    }

    public CookBooksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cook_books, container, false);
        cookbookListView = (ListView) rootView.findViewById(R.id.cookbooklistView);
        cookbookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
//        createFakeData();
//        getNewCookBooks();
//        getHotCookBooks();
        realm = RealmData.getInstance().realm;
        setCurrentCookBooks(realm, 0);

        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String string) {
        if (mListener != null) {
            mListener.onCookBookFragmentInteraction(string);
        }
    }

    private void getNewCookBooks(Realm realm) {
        RealmResults<CookBookRealm> newCookBooksRealmResult = realm.where(CookBookRealm.class).findAll();
        newCookBooksRealmResult.sort("createTime");

        newCookBooks = new ArrayList<>();
        for (CookBookRealm cookBookRealm : newCookBooksRealmResult) {
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
                cookBookRealm.getCategory(),
                cookBookRealm.getName(),
                cookBookRealm.getDescription(),
                cookBookRealm.getUrl(),
                cookBookRealm.getImageUrl(),
                cookBookRealm.getIngredient(),
                cookbookSteps,
                cookBookRealm.getViewedPeopleCount(),
                cookBookRealm.getCollectedPeopleCount(),
                cookBookRealm.getBeCollected(),
                cookBookRealm.getImageName(),
                cookBookRealm.getVideoCode());

            newCookBook.setUploadTimestamp(cookBookRealm.getUploadTimestamp());
            newCookBook.setImageID(cookBookRealm.getImageID());
            newCookBook.setImageName(cookBookRealm.getImageName());
            newCookBooks.add(newCookBook);
        }
    }

    private void getHotCookBooks(Realm realm) {
        RealmResults<CookBookRealm> hotCookBooksRealmResult = realm.where(CookBookRealm.class).findAll();
        hotCookBooksRealmResult.sort("viewedPeopleCount", Sort.DESCENDING);
        hotCookBooks = new ArrayList<>();
        for (CookBookRealm cookBookRealm : hotCookBooksRealmResult) {
            List<CookbookStep> cookbookSteps = new ArrayList<>();
            for (CookbookStepRealm cookbookStepRealm : cookBookRealm.getSteps1()) {
                CookbookStep cookbookStep = new CookbookStep();
                cookbookStep.setStepDesc(cookbookStepRealm.getStepDesc());
                cookbookStep.setStepSpeed(cookbookStepRealm.getStepSpeed());
                cookbookStep.setStepTime(cookbookStepRealm.getStepTime());
                cookbookSteps.add(cookbookStep);
            }

            Cookbook newCookBook = new Cookbook(
                cookBookRealm.getId(),
                cookBookRealm.getCategory(),
                cookBookRealm.getName(),
                cookBookRealm.getDescription(),
                cookBookRealm.getUrl(),
                cookBookRealm.getImageUrl(),
                cookBookRealm.getIngredient(),
                cookbookSteps,
                cookBookRealm.getViewedPeopleCount(),
                cookBookRealm.getCollectedPeopleCount(),
                cookBookRealm.getBeCollected(),
                cookBookRealm.getImageName(),
                cookBookRealm.getVideoCode());
            newCookBook.setUploadTimestamp(cookBookRealm.getUploadTimestamp());

            // This cause out of memory exception, comment for now.
            //newCookBook.setImage(BitmapFactory.decodeResource(getResources(), cookBookRealm.getImageID()));
            newCookBook.setImageID(cookBookRealm.getImageID());
            newCookBook.setImageName(cookBookRealm.getImageName());
            hotCookBooks.add(newCookBook);
        }
    }

    public void setCurrentCookBooks(Realm realm, int tabIndex) {
        getNewCookBooks(realm);
        getHotCookBooks(realm);

        Button newCookBookButton = (Button) rootView.findViewById(R.id.newCookBookButton);
        Button hotCookBookButton = (Button) rootView.findViewById(R.id.hotCookBookButton);
        ImageButton newCookBookButtonColor = (ImageButton) rootView.findViewById(R.id.newCookBookButton_SelectColor);
        ImageButton hotCookBookButtonColor = (ImageButton) rootView.findViewById(R.id.hotCookBookButton_SelectColor);
        if (tabIndex == 0) {
            currentCookBooks = newCookBooks;
            mCookBookAdapter = new CookBookAdapter(this.getActivity(), R.layout.cook_book_list_item, currentCookBooks);
            cookbookListView.setAdapter(mCookBookAdapter);
            newCookBookButton.setTextColor(getResources().getColor(R.color.White));
            newCookBookButtonColor.setImageResource(R.color.TabSelectColor);
            hotCookBookButton.setTextColor(getResources().getColor(R.color.c70White));
            hotCookBookButtonColor.setImageResource(R.color.TabNoSelectColor);

        } else if (tabIndex == 1) {
            currentCookBooks = hotCookBooks;
            mCookBookAdapter = new CookBookAdapter(this.getActivity(), R.layout.cook_book_list_item, currentCookBooks);
            cookbookListView.setAdapter(mCookBookAdapter);

            newCookBookButton.setTextColor(getResources().getColor(R.color.c70White));
            newCookBookButtonColor.setImageResource(R.color.TabNoSelectColor);
            hotCookBookButton.setTextColor(getResources().getColor(R.color.White));
            hotCookBookButtonColor.setImageResource(R.color.TabSelectColor);
        }
    }

    public void upDateListView(Realm realm) {
        if (currentCookBooks == newCookBooks) {
            getNewCookBooks(realm);
            currentCookBooks = newCookBooks;
        } else if (currentCookBooks == hotCookBooks) {
            getHotCookBooks(realm);
            currentCookBooks = hotCookBooks;
        }
        mCookBookAdapter = new CookBookAdapter(this.getActivity(), R.layout.cook_book_list_item, currentCookBooks);
        cookbookListView.setAdapter(mCookBookAdapter);
    }

    private void selectItem(int position) {
        CookbookParcelable cookbook = ConvertToCookbook.convertToParcelable(currentCookBooks.get(position));

        Intent intent = new Intent(this.getActivity(), CookBookDetailActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("cookbook", cookbook);
        intent.putExtra("requestCode", 103);

        getActivity().startActivityFromFragment(this, intent, 103);
        getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
            getArguments().getInt(ARG_SECTION_NUMBER));
        try {
            mListener = (OnCookBooksFragmentInteractionListener) activity;
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
    public interface OnCookBooksFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCookBookFragmentInteraction(String string);
    }

}
