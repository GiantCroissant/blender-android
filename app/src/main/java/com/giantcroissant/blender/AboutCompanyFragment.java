package com.giantcroissant.blender;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AboutCompanyFragment.OnAboutCompanyFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AboutCompanyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutCompanyFragment extends Fragment
        implements
        AboutCompanyInfoFragment.OnAboutCompanyInfoFragmentInteractionListener,
        AboutCompanyItemFragment.OnAboutCompanyItemFragmentInteractionListener
{

    AboutCompanyAdapter mAboutCompanyAdapter;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView itemListView;
    private View rootView;
    private ArrayList<CompanyItemSystem> companyItemSystems;
    private ArrayList<CompanyItem> companyItems;
    private AboutCompanyInfoFragment aboutCompanyInfoFragment;
    private AboutCompanyItemFragment aboutCompanyItemFragment;
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

    public ArrayList<CompanyItemSystem> getCompanyItemSystems()
    {
        return companyItemSystems;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_about_company, container, false);
//        itemListView = (ListView) rootView.findViewById(R.id.aboutcompanylistView);
//        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                selectItem(position);
//            }
//        });
        createFakeData();
//        mAboutCompanyAdapter = new AboutCompanyAdapter(this.getActivity() , R.layout.about_company_item, companyItems);
//        itemListView.setAdapter(mAboutCompanyAdapter);
        setCurrentTab(0);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String string) {
        if (mListener != null) {
            mListener.onAboutCompanyFragmentInteraction(string);
        }
    }

    public void setCurrentTab(int tabIndex) {

        Button aboutCompanyButton = (Button) rootView.findViewById(R.id.AboutCompanyButton);
        Button aboutGoodButton = (Button) rootView.findViewById(R.id.AboutGoodButton);
        ImageButton aboutCompanyButtonColor = (ImageButton) rootView.findViewById(R.id.AboutCompanyButton_SelectColor);
        ImageButton aboutGoodButtonColor = (ImageButton) rootView.findViewById(R.id.AboutGoodButton_SelectColor);
        if (tabIndex == 0)
        {

            aboutCompanyButton.setTextColor(getResources().getColor(R.color.White));
            aboutGoodButton.setTextColor(getResources().getColor(R.color.c70White));
            aboutCompanyButtonColor.setImageResource(R.color.TabSelectColor);
            aboutGoodButtonColor.setImageResource(R.color.TabNoSelectColor);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            if (aboutCompanyInfoFragment == null)
            {
                aboutCompanyInfoFragment = new AboutCompanyInfoFragment();
            }
            fragmentTransaction.replace(R.id.aboutCompanyContainView, aboutCompanyInfoFragment);
            fragmentTransaction.commit();

        }
        else if(tabIndex == 1)
        {
            aboutCompanyButton.setTextColor(getResources().getColor(R.color.c70White));
            aboutGoodButton.setTextColor(getResources().getColor(R.color.White));
            aboutCompanyButtonColor.setImageResource(R.color.TabNoSelectColor);
            aboutGoodButtonColor.setImageResource(R.color.TabSelectColor);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            if (aboutCompanyItemFragment == null)
            {
                aboutCompanyItemFragment = new AboutCompanyItemFragment().newInstance(companyItemSystems);
            }

            fragmentTransaction.replace(R.id.aboutCompanyContainView, aboutCompanyItemFragment);
            fragmentTransaction.commit();
        }

    }

    private void createFakeData()
    {
        companyItemSystems = new ArrayList<CompanyItemSystem>();
        companyItemSystems.add(new CompanyItemSystem(UUID.randomUUID().toString(), "專業養生調理機", "磨豆機", BitmapFactory.decodeResource(getResources() ,R.drawable.as_128)));
//        companyItemSystems.add(new CompanyItemSystem(UUID.randomUUID().toString(), "冰沙果汁機", "快煮壺", BitmapFactory.decodeResource(getResources() ,R.drawable.as_628_w)));
//        companyItemSystems.add(new CompanyItemSystem(UUID.randomUUID().toString(), "食物料理機", "咖啡機", BitmapFactory.decodeResource(getResources() ,R.drawable.as_688_w)));

        ArrayList<String> tmpContents1 = new ArrayList<String>();
//        tmpContents1.add("電動食品混和器，料理肉品魚泥、醬料、蛋糕、沙拉醬等，方便好用又實惠的廚房好幫手。電動食品混和器，料理肉品魚泥、醬料、蛋糕方便好用又實惠的廚房好幫手。");
        tmpContents1.add("");
        tmpContents1.add("功  率：1200W");
        tmpContents1.add("電  壓：AC 220V/50Hz");
        tmpContents1.add("容  量：2000cc");
        tmpContents1.add("尺  寸：230*05*212mm");
        tmpContents1.add("淨  重：5.0KG");

        ArrayList<String> tmpContents2 = new ArrayList<String>();
        tmpContents2.add("日本規格800c.c 厚體玻璃杯 + 日本規格玻璃磨粉料理杯。\n" +
                "天然美味輕鬆製作，生活上的好幫手。電動食品混和器，料理肉品魚泥、醬料、蛋糕、沙拉醬等，方便好用又實惠的廚房好幫手。");
        tmpContents2.add("功  率：1200W");
        tmpContents2.add("電  壓：AC 220V/50Hz");
        tmpContents2.add("容  量：2000cc");
        tmpContents2.add("尺  寸：230*05*212mm");
        tmpContents2.add("淨  重：5.0KG");


        companyItems = new ArrayList<CompanyItem>();
        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "健康食品調製機", "AS-128(灰)", tmpContents1, BitmapFactory.decodeResource(getResources() ,R.drawable.as_128),companyItemSystems.get(0).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "生機食品調製機", "AS-128(白)", tmpContents2, BitmapFactory.decodeResource(getResources() ,R.drawable.as_628_w),companyItemSystems.get(0).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "健康食品調製機", "AS-128(黑)", tmpContents1, BitmapFactory.decodeResource(getResources() ,R.drawable.as_688_w),companyItemSystems.get(0).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "生機食品調製機", "AS-128(白)", tmpContents2, BitmapFactory.decodeResource(getResources() ,R.drawable.bl_1200_r),companyItemSystems.get(0).getId()));
        companyItemSystems.get(0).contentIds.add(companyItems.get(0));
//        companyItemSystems.get(0).contentIds.add(companyItems.get(1));
//        companyItemSystems.get(0).contentIds.add(companyItems.get(2));
//        companyItemSystems.get(0).contentIds.add(companyItems.get(3));

//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "健康食品調製機", "AS-129(灰)", tmpContents1, BitmapFactory.decodeResource(getResources() ,R.drawable.as_128),companyItemSystems.get(1).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "生機食品調製機", "AS-129(白)", tmpContents2, BitmapFactory.decodeResource(getResources() ,R.drawable.as_628_w),companyItemSystems.get(1).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "健康食品調製機", "AS-129(黑)", tmpContents1, BitmapFactory.decodeResource(getResources() ,R.drawable.as_688_w),companyItemSystems.get(1).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "生機食品調製機", "AS-129(白)", tmpContents2, BitmapFactory.decodeResource(getResources() ,R.drawable.bl_1200_r),companyItemSystems.get(1).getId()));
//        companyItemSystems.get(1).contentIds.add(companyItems.get(4));
//        companyItemSystems.get(1).contentIds.add(companyItems.get(5));
//        companyItemSystems.get(1).contentIds.add(companyItems.get(6));
//        companyItemSystems.get(1).contentIds.add(companyItems.get(7));

//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "健康食品調製機", "AS-130(灰)", tmpContents1, BitmapFactory.decodeResource(getResources() ,R.drawable.as_128),companyItemSystems.get(2).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "生機食品調製機", "AS-130(白)", tmpContents2, BitmapFactory.decodeResource(getResources() ,R.drawable.as_628_w),companyItemSystems.get(2).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "健康食品調製機", "AS-130(黑)", tmpContents1, BitmapFactory.decodeResource(getResources() ,R.drawable.as_688_w),companyItemSystems.get(2).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "生機食品調製機", "AS-130(白)", tmpContents2, BitmapFactory.decodeResource(getResources() ,R.drawable.bl_1200_r),companyItemSystems.get(2).getId()));
//        companyItemSystems.get(2).contentIds.add(companyItems.get(8));
//        companyItemSystems.get(2).contentIds.add(companyItems.get(9));
//        companyItemSystems.get(2).contentIds.add(companyItems.get(10));
//        companyItemSystems.get(2).contentIds.add(companyItems.get(11));

        CompanyData.getInstance().companyItems = companyItems;
        CompanyData.getInstance().companyItemSystems = companyItemSystems;
    }

//    private void selectItem(int position) {
//
//        Intent intent = new Intent(this.getActivity(), CompanyItemActivity.class);
//
//        intent.putExtra("position", position);
//        intent.putExtra("itemListViewID", companyItems.get(position).getId());
//        intent.putExtra("itemListViewTitle", companyItems.get(position).getTitle());
//        intent.putExtra("itemListViewName", companyItems.get(position).getName());
//        intent.putExtra("itemListViewContent", companyItems.get(position).getContents());
//        intent.putExtra("itemListViewIconUrl", companyItems.get(position).getIconUrl());
//        byte[] tmpIconByteArray = Bitmap2Bytes(companyItems.get(position).getIcon());
//
//        intent.putExtra("itemListViewIcon", tmpIconByteArray);
//
//        startActivityForResult(intent, 0);
//
//    }



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

    @Override
    public void onAboutCompanyInfoFragmentInteraction(String string) {

    }

    @Override
    public void onAboutCompanyItemFragmentInteraction(String id) {
//        if(id.compareTo("getCompanyItemSystems") == 0)
//        {
//            aboutCompanyItemFragment.setListAdapterData(companyItemSystems);
//        }
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


    public void upDateListView(Realm realm){

    }

}
