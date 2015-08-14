package com.giantcroissant.blender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AboutCompanyFragment.OnAboutCompanyFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AboutCompanyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutCompanyFragment extends Fragment {

    AboutCompanyAdapter mAboutCompanyAdapter;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView itemListView;
    private View rootView;
    private ArrayList<CompanyItem> companyItems;

    private OnAboutCompanyFragmentInteractionListener mListener;

    public static AboutCompanyFragment newInstance(int sectionNumber) {
        AboutCompanyFragment fragment = new AboutCompanyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AboutCompanyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_about_company, container, false);
        itemListView = (ListView) rootView.findViewById(R.id.aboutcompanylistView);
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        createFakeData();
        mAboutCompanyAdapter = new AboutCompanyAdapter(this.getActivity() , R.layout.about_company_item, companyItems);
        itemListView.setAdapter(mAboutCompanyAdapter);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String string) {
        if (mListener != null) {
            mListener.onAboutCompanyFragmentInteraction(string);
        }
    }

    private void createFakeData()
    {
        companyItems = new ArrayList<CompanyItem>();
        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "磨豆機", "磨豆機 很棒喔", "Http://xd.com"));
        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "快煮壺", "快煮壺 一級棒", "Http://xd.com"));
        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "咖啡機", "咖啡機 提神醒腦", "Http://xd.com"));
    }

    private void selectItem(int position) {

        Intent intent = new Intent(this.getActivity(), CompanyItemActivity.class);

        intent.putExtra("position", position);
        intent.putExtra("itemListViewID", companyItems.get(position).getId());
        intent.putExtra("itemListViewTitle", companyItems.get(position).getTitle());
        intent.putExtra("itemListViewContent", companyItems.get(position).getContent());
        intent.putExtra("itemListViewIconUrl", companyItems.get(position).getIconUrl());

        startActivityForResult(intent, 0);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 如果被啟動的Activity元件傳回確定的結果
        if (resultCode == Activity.RESULT_OK) {

            // 讀取標題
//            String titleText = data.getStringExtra("titleText");
            // 加入標題項目
//            this.data.add(titleText);

            // 通知資料已經改變，ListView元件才會重新顯示
            mAboutCompanyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        try {
            mListener = (OnAboutCompanyFragmentInteractionListener) activity;
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
    public interface OnAboutCompanyFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onAboutCompanyFragmentInteraction(String String);
    }

}
