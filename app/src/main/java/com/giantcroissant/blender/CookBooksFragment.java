package com.giantcroissant.blender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CookBooksFragment.OnCookBooksFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CookBooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CookBooksFragment extends Fragment implements CookBooksDataFragment {

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
    private int tabIndex = 0;

    private OnCookBooksFragmentInteractionListener mListener;
    private Realm realm;
//    private RealmQuery<CookBookRealm> cookBookRealmQuery;
    private RealmResults<CookBookRealm> newCookBooksRealmResult;
    private RealmResults<CookBookRealm> hotCookBooksRealmResult;

    public static CookBooksFragment newInstance(int sectionNumber,  Realm realm) {
        CookBooksFragment cookBooksFragment = new CookBooksFragment();
        cookBooksFragment.realm = realm;
//        cookBooksFragment.cookBookRealmQuery = cookBookRealmQuery;
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

    private void getNewCookBooks(Realm realm)
    {
        RealmQuery tmpCookBookRealmQuery = realm.where(CookBookRealm.class);
        newCookBooksRealmResult = tmpCookBookRealmQuery.findAllSorted("createTime",false);
        newCookBooks = new ArrayList<Cookbook>();
        for (CookBookRealm cookBookRealm : newCookBooksRealmResult) {
            ArrayList<String> tmpSteps = new ArrayList<String>();
            String[] tmpStepParts = cookBookRealm.getSteps().split("\\;");
            for (String tmpStepPart : tmpStepParts) {
                tmpSteps.add(tmpStepPart);
            }
            Cookbook newCookBook = new Cookbook(cookBookRealm.getId(), cookBookRealm.getName(), cookBookRealm.getDescription(), cookBookRealm.getUrl(), cookBookRealm.getImageUrl(), cookBookRealm.getIngredient(), tmpSteps, cookBookRealm.getViewedPeopleCount(), cookBookRealm.getCollectedPeopleCount(), cookBookRealm.getBeCollected());
            newCookBook.setUploadTimestamp(cookBookRealm.getUploadTimestamp());
            newCookBooks.add(newCookBook);
        }
    }

    private void getHotCookBooks(Realm realm)
    {
        RealmQuery tmpCookBookRealmQuery = realm.where(CookBookRealm.class);
        hotCookBooksRealmResult = tmpCookBookRealmQuery.findAllSorted("viewedPeopleCount",false);
        hotCookBooks = new ArrayList<Cookbook>();
        for (CookBookRealm cookBookRealm : hotCookBooksRealmResult) {
            ArrayList<String> tmpSteps = new ArrayList<String>();
            String[] tmpStepParts = cookBookRealm.getSteps().split("\\;");
            for (String tmpStepPart : tmpStepParts) {
                tmpSteps.add(tmpStepPart);
            }
            Cookbook newCookBook = new Cookbook(cookBookRealm.getId(), cookBookRealm.getName(), cookBookRealm.getDescription(), cookBookRealm.getUrl(), cookBookRealm.getImageUrl(), cookBookRealm.getIngredient(), tmpSteps, cookBookRealm.getViewedPeopleCount(), cookBookRealm.getCollectedPeopleCount(), cookBookRealm.getBeCollected());
            newCookBook.setUploadTimestamp(cookBookRealm.getUploadTimestamp());
            hotCookBooks.add(newCookBook);
        }
    }

    public void setCurrentCookBooks(Realm realm,int tabIndex) {
        getNewCookBooks(realm);
        getHotCookBooks(realm);
        if (tabIndex == 0) {
            currentCookBooks = newCookBooks;
                mCookBookAdapter = new CookBookAdapter(this.getActivity() , R.layout.cook_book_list_item, currentCookBooks);
                cookbookListView.setAdapter(mCookBookAdapter);

                ImageButton newCookBookButton = (ImageButton) rootView.findViewById(R.id.newCookBookButton_SelectColor);
                newCookBookButton.setImageResource(R.color.TabSelectColor);

                ImageButton hotCookBookButton = (ImageButton) rootView.findViewById(R.id.hotCookBookButton_SelectColor);
                hotCookBookButton.setImageResource(R.color.TabNoSelectColor);

                this.tabIndex = 0;
            }
            else if(tabIndex == 1)
            {
                currentCookBooks = hotCookBooks;
                mCookBookAdapter = new CookBookAdapter(this.getActivity() , R.layout.cook_book_list_item, currentCookBooks);
                cookbookListView.setAdapter(mCookBookAdapter);

                ImageButton newCookBookButton = (ImageButton) rootView.findViewById(R.id.newCookBookButton_SelectColor);
                newCookBookButton.setImageResource(R.color.TabNoSelectColor);

                ImageButton hotCookBookButton = (ImageButton) rootView.findViewById(R.id.hotCookBookButton_SelectColor);
                hotCookBookButton.setImageResource(R.color.TabSelectColor);

                this.tabIndex = 1;
            }

    }

    public void upDateListView(Realm realm) {

        if(currentCookBooks == newCookBooks)
        {
            getNewCookBooks(realm);
            currentCookBooks = newCookBooks;
        }
        else if(currentCookBooks == hotCookBooks)
        {
            getHotCookBooks(realm);
            currentCookBooks = hotCookBooks;
        }
        mCookBookAdapter = new CookBookAdapter(this.getActivity() , R.layout.cook_book_list_item, currentCookBooks);
        cookbookListView.setAdapter(mCookBookAdapter);
    }

    private void selectItem(int position) {

        Intent intent = new Intent(this.getActivity(), CookBookDetailActivity.class);

        intent.putExtra("position", position);
        intent.putExtra("cookBookListViewID", currentCookBooks.get(position).getId());
        intent.putExtra("cookBookListViewName", currentCookBooks.get(position).getName());
        intent.putExtra("cookBookListViewDescription", currentCookBooks.get(position).getDescription());
        intent.putExtra("cookBookListViewUrl", currentCookBooks.get(position).getUrl());
        intent.putExtra("cookBookListViewImageUrl", currentCookBooks.get(position).getImageUrl());
        intent.putExtra("cookBookListViewIngredient", currentCookBooks.get(position).getIngredient());
        intent.putExtra("cookBookListViewSteps", currentCookBooks.get(position).getSteps());
        intent.putExtra("cookBookListViewViewPeople", currentCookBooks.get(position).getViewedPeopleCount());
        intent.putExtra("cookBookListViewCollectedPeople", currentCookBooks.get(position).getCollectedPeopleCount());
        intent.putExtra("cookBookListIsCollected", currentCookBooks.get(position).getIsCollected());

        startActivityForResult(intent, 0);

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
//        ((SideMenuActivity) activity).onSectionAttached(
//                getArguments().getInt(ARG_SECTION_NUMBER));
//        try {
//            mListener = (OnCookBooksFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
    public interface OnCookBooksFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onCookBookFragmentInteraction(String string);
    }


}
