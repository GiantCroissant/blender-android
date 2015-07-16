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
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CookBooksFragment.OnCookBooksFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CookBooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CookBooksFragment extends Fragment {

    private CookBookAdapter mCookBookAdapter;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView cookbookListView;
    private View rootView;
    private ArrayList<CookBook> newCookBooks;
    private ArrayList<CookBook> hotCookBooks;
    private ArrayList<CookBook> currentCookBooks;
    private int tabIndex = 0;

    private OnCookBooksFragmentInteractionListener mListener;

    public static CookBooksFragment newInstance(int sectionNumber) {
        CookBooksFragment fragment = new CookBooksFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
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
        createFakeData();

        setCurrentCookBooks(0);

        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String string) {
        if (mListener != null) {
            mListener.onCookBookFragmentInteraction(string);
        }
    }

    private void createFakeData()
    {
        ArrayList<String> newSteps = new ArrayList<String>();
        newSteps.add("步驟1");
        newSteps.add("步驟2");
        newSteps.add("步驟3");
        newSteps.add("步驟4");
        newSteps.add("步驟5");

        newCookBooks = new ArrayList<CookBook>();
        newCookBooks.add(new CookBook(UUID.randomUUID().toString(), "檸檬葡萄汁", "很好喝", "Http://xd.com", "Http://xd.com", "葡萄、蜂蜜、檸檬",newSteps, 100, 100, true));
        newCookBooks.add(new CookBook(UUID.randomUUID().toString(), "草莓葡萄汁", "超好喝", "Http://xd.com", "Http://xd.com", "葡萄、蜂蜜、草莓",newSteps, 100, 100, true));
        newCookBooks.add(new CookBook(UUID.randomUUID().toString(), "水蜜桃芒果汁", "非常好喝", "Http://xd.com", "Http://xd.com", "水蜜桃、蜂蜜、芒果",newSteps, 100, 100, true));


        hotCookBooks = new ArrayList<CookBook>();
        hotCookBooks.add(new CookBook(UUID.randomUUID().toString(), "水蜜桃芒果汁", "非常好喝", "Http://xd.com", "Http://xd.com", "水蜜桃、蜂蜜、芒果",newSteps, 100, 100, true));
        hotCookBooks.add(new CookBook(UUID.randomUUID().toString(), "草莓葡萄汁", "超好喝", "Http://xd.com", "Http://xd.com", "葡萄、蜂蜜、草莓",newSteps, 100, 100, true));
        hotCookBooks.add(new CookBook(UUID.randomUUID().toString(), "檸檬葡萄汁", "很好喝", "Http://xd.com", "Http://xd.com", "葡萄、蜂蜜、檸檬", newSteps, 100, 100, true));


    }

    public void setCurrentCookBooks(int tabIndex) {
        if (tabIndex == 0) {
            currentCookBooks = newCookBooks;
                mCookBookAdapter = new CookBookAdapter(this.getActivity() , R.layout.cook_book_list_item, currentCookBooks);
                cookbookListView.setAdapter(mCookBookAdapter);

                ImageButton newCookBookButton = (ImageButton) rootView.findViewById(R.id.newCookBookButton);
                newCookBookButton.setImageResource(R.drawable.newcookbook_true);

                ImageButton hotCookBookButton = (ImageButton) rootView.findViewById(R.id.hotCookBookButton);
                hotCookBookButton.setImageResource(R.drawable.hotcookbook_false);

                this.tabIndex = 0;
            }
            else if(tabIndex == 1)
            {
                currentCookBooks = hotCookBooks;
                mCookBookAdapter = new CookBookAdapter(this.getActivity() , R.layout.cook_book_list_item, currentCookBooks);
                cookbookListView.setAdapter(mCookBookAdapter);

                ImageButton newCookBookButton = (ImageButton) rootView.findViewById(R.id.newCookBookButton);
                newCookBookButton.setImageResource(R.drawable.newcookbook_false);

                ImageButton hotCookBookButton = (ImageButton) rootView.findViewById(R.id.hotCookBookButton);
                hotCookBookButton.setImageResource(R.drawable.hotcookbook_true);

                this.tabIndex = 1;
            }

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

        ((SideMenuActivity) activity).onSectionAttached(
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
