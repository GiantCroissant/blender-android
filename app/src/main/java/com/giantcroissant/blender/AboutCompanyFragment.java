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
//        companyItemSystems.add(new CompanyItemSystem(UUID.randomUUID().toString(), "營養調理機", "磨豆機", BitmapFactory.decodeResource(getResources() ,R.drawable.as_128)));
//        companyItemSystems.add(new CompanyItemSystem(UUID.randomUUID().toString(), "食物料理加工機", "磨豆機", BitmapFactory.decodeResource(getResources() ,R.drawable.as_128)));
//        companyItemSystems.add(new CompanyItemSystem(UUID.randomUUID().toString(), "電鍋", "磨豆機", BitmapFactory.decodeResource(getResources() ,R.drawable.as_128)));
        companyItemSystems.add(new CompanyItemSystem("0", "營養調理機", "磨豆機", BitmapFactory.decodeResource(getResources() ,R.drawable.as_128)));
        companyItemSystems.add(new CompanyItemSystem("1", "食物料理加工機", "磨豆機", BitmapFactory.decodeResource(getResources() ,R.drawable.as_128)));
        companyItemSystems.add(new CompanyItemSystem("2", "電鍋", "磨豆機", BitmapFactory.decodeResource(getResources() ,R.drawable.as_128)));

//        companyItemSystems.add(new CompanyItemSystem(UUID.randomUUID().toString(), "專業養生調理機", "磨豆機", BitmapFactory.decodeResource(getResources() ,R.drawable.as_128)));
//        companyItemSystems.add(new CompanyItemSystem(UUID.randomUUID().toString(), "冰沙果汁機", "快煮壺", BitmapFactory.decodeResource(getResources() ,R.drawable.as_628_w)));
//        companyItemSystems.add(new CompanyItemSystem(UUID.randomUUID().toString(), "食物料理機", "咖啡機", BitmapFactory.decodeResource(getResources() ,R.drawable.as_688_w)));

        ArrayList<String> tmpContents1 = new ArrayList<String>();
//        tmpContents1.add("電動食品混和器，料理肉品魚泥、醬料、蛋糕、沙拉醬等，方便好用又實惠的廚房好幫手。電動食品混和器，料理肉品魚泥、醬料、蛋糕方便好用又實惠的廚房好幫手。");
        tmpContents1.add("一機多功能\n無段式弱至強開關\n安全衛生容杯\n多角座刀頭\n超強馬力功率大");
        tmpContents1.add("顏色：黑、白、紅、灰");
        tmpContents1.add("機體尺寸：255（長）× 205（寬）× 512 （高）（mm）");
        tmpContents1.add("機體淨重：5.0kg");
        tmpContents1.add("生 產 國：台灣");
        tmpContents1.add("額定電壓：110 V / 220V\\~240V");
        tmpContents1.add("頻率：60 Hz / 50Hz");
        tmpContents1.add("消耗功率：1200 W");
        tmpContents1.add("容量：2500c.c.");
        tmpContents1.add(
            "一機多功能：\n\n" +
            "冰沙、冰淇淋、豆漿、研磨、攪碎、濃湯、精力湯、牧草汁、調理醬料、蔬菜調理、果泥、肉泥……等\n\n" +
            "無段式弱至強開關：\n\n" +
            "旋鈕式轉速控制，耐用好操作。具瞬間超強轉速開關，方便營業使用。\n\n" +
            "安全衛生容杯：\n\n" +
            "超大容量2500 CC強化PC容杯，耐酸鹼、耐冰熱、耐撞，不會產生有毒物質，健康有保障。\n\n" +
            "多角座刀頭：\n\n" +
            "六向多角度不銹鋼硬化刀頭，堅韌無比。\n\n" +
            "超強馬力功率大：\n\n" +
            "台灣製造1200W工業級超強馬達，可將堅硬食物充分攪碎，保留完整營養。馬達具雙重安全保護，溫度(135℃)/電流(12A)過載保護裝置。"
        );

        ArrayList<String> tmpContents2 = new ArrayList<String>();
        tmpContents2.add("一機多功能\n微電腦控制\n安全衛生容杯\n多角座刀頭\n超強馬力功率大");
        tmpContents2.add("顏色：黑、白、紅、灰");
        tmpContents2.add("機體尺寸：235（長）× 205（寬）× 512 （高）（mm）");
        tmpContents2.add("機體淨重：5.0kg");
        tmpContents2.add("生 產 國：台灣");
//        tmpContents2.add("淨  重：5.0KG");
        tmpContents2.add("額定電壓：110 V / 220V\\~240V");
        tmpContents2.add("頻率：60 Hz / 50Hz");
        tmpContents2.add("消耗功率：1200 W");
        tmpContents2.add("容量：2000c.c.");
        tmpContents2.add(
            "一機多功能：\n\n" +
            "冰沙、冰淇淋、豆漿、研磨、攪碎、濃湯、精力湯、牧草汁、調理醬料、蔬菜調理、果泥、肉泥……等\n\n" +
            "微電腦控制：\n\n" +
            "微電腦IC控制，可選擇時間及速度使用方便。具瞬間超強轉速按鈕，方便使用。\n\n" +
            "安全衛生容杯：\n\n" +
            "大容量調理杯，食品級PC材質，耐酸鹼、耐冰熱、耐撞，不會產生有毒物質，健康有保障。\n\n" +
            "多角座刀頭：\n\n" +
            "培林趨動手術刀材質不銹鋼刀，堅韌無比。\n\n" +
            "超強馬力功率大：\n\n" +
            "高扭力、高轉速馬達，可將堅硬食物充分攪碎，保留完整營養。馬達具雙重安全保護，溫度(135℃)/電流(12A)過載保護裝置。"
        );

        ArrayList<String> tmpContents3 = new ArrayList<String>();
        tmpContents3.add("一機多功能\n無段式弱至強開關\n安全衛生容杯\n多角座刀頭\n超強馬力功率大");
        tmpContents3.add("顏色：黑、白、紅、灰");
        tmpContents3.add("機體尺寸：230（長）× 205（寬）× 512 （高）（mm）");
        tmpContents3.add("機體淨重：5.0kg");
        tmpContents3.add("生 產 國：台灣");
//        tmpContents3.add("淨  重：5.0KG");
        tmpContents3.add("額定電壓：110 V / 220V\\~240V");
        tmpContents3.add("頻率：60 Hz / 50Hz");
        tmpContents3.add("消耗功率：1200 W");
        tmpContents3.add("容量：2000c.c.");
        tmpContents3.add(
            "一機多功能：\n\n" +
            "冰沙、冰淇淋、豆漿、研磨、攪碎、濃湯、精力湯、牧草汁、調理醬料、蔬菜調理、果泥、肉泥……等\n\n" +
            "無段式弱至強開關：\n\n" +
            "旋鈕式轉速控制，耐用好操作。具瞬間超強轉速開關，方便營業使用。\n\n" +
            "安全衛生容杯：\n\n" +
            "超大容量2000 CC強化PC容杯，耐酸鹼、耐冰熱、耐撞，不會產生有毒物質，健康有保障。\n\n" +
            "多角座刀頭：\n\n" +
            "六向多角度不銹鋼硬化刀頭，堅韌無比。\n\n" +
            "超強馬力功率大：\n\n" +
            "台灣製造1200W工業級超強馬達，可將堅硬食物充分攪碎，保留完整營養。馬達具雙重安全保護，溫度(135℃)/電流(12A)過載保護裝置。"
        );

        ArrayList<String> tmpContents4 = new ArrayList<String>();
        tmpContents4.add("兩段式轉速\n刀具可拆式設計\n獨創排水孔設計\n雙重保護裝置");
        tmpContents4.add("顏色：白");
        tmpContents4.add("機體尺寸：170（長）× 160（寬）× 258 （高）（mm）");
        tmpContents4.add("機體淨重：1.8kg");
        tmpContents4.add("生 產 國：台灣");
//        tmpContents4.add("淨  重：5.0KG");
        tmpContents4.add("額定電壓：110 V / 220V\\~240V");
        tmpContents4.add("頻率：60 Hz / 50Hz");
        tmpContents4.add("消耗功率：350 W");
        tmpContents4.add("容量：500ML");
        tmpContents4.add(
            "肉品、醬料調製、五榖豆類研磨、廚料理必備。\n\n" +
            "兩段式轉速、瞬轉鍵選擇，操作容易。\n\n" +
            "刀具可拆式設計，清洗容易。\n\n" +
            "獨創排水孔設計，液體不殘留。\n\n" +
            "雙重保護裝置:安全開關保護裝置、馬達溫度過高保護裝置。\n\n" +
            "採用手術刀材質不鏽鋼刀。\n\n"
        );

        ArrayList<String> tmpContents5 = new ArrayList<String>();
        tmpContents5.add("兩段式轉速\n刀具可拆式設計\n獨創排水孔設計\n雙重保護裝置");
        tmpContents5.add("顏色：不鏽鋼");
        tmpContents5.add("機體尺寸：350（長）× 300（寬）× 285 （高）（mm）");
        tmpContents5.add("機體淨重：3.2kg");
        tmpContents5.add("生 產 國：台灣");
//        tmpContents5.add("淨  重：5.0KG");
        tmpContents5.add("額定電壓：110 V / 220V\\~240V");
        tmpContents5.add("頻率：50 Hz / 60Hz");
        tmpContents5.add("消耗功率：450 W");
        tmpContents5.add("容量：11人份");
        tmpContents5.add(
            "專利節能魚鱗鍋自體導熱設計。\n\n" +
            "內鍋圓弧鍋底設計聚熱效果極佳。\n\n" +
            "蒸氣加濕及精準控制。\n\n" +
            "採用#304食品及不鏽鋼材。\n\n" +
            "全世界最省電450W/熱效率最高，煮飯最Q最快的電鍋。\n\n" +
            "專利證書:新型第M 507720號。\n\n"
        );

        companyItems = new ArrayList<CompanyItem>();
        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "全方位營養調理機", "AS-628", tmpContents1, BitmapFactory.decodeResource(getResources() ,R.drawable.as_628),companyItemSystems.get(0).getId()));
        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "微電腦營養調理機", "AS-688", tmpContents2, BitmapFactory.decodeResource(getResources() ,R.drawable.as_688),companyItemSystems.get(0).getId()));
        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "微電腦營養調理機", "AS-128", tmpContents3, BitmapFactory.decodeResource(getResources() ,R.drawable.as_128),companyItemSystems.get(0).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "健康食品調製機", "AS-128(黑)", tmpContents1, BitmapFactory.decodeResource(getResources() ,R.drawable.as_688_w),companyItemSystems.get(0).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "生機食品調製機", "AS-128(白)", tmpContents2, BitmapFactory.decodeResource(getResources() ,R.drawable.bl_1200_r),companyItemSystems.get(0).getId()));
        companyItemSystems.get(0).contentIds.add(companyItems.get(0));
        companyItemSystems.get(0).contentIds.add(companyItems.get(1));
        companyItemSystems.get(0).contentIds.add(companyItems.get(2));
//        companyItemSystems.get(0).contentIds.add(companyItems.get(3));

        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "食物料理加工機", "AS-499", tmpContents4, BitmapFactory.decodeResource(getResources() ,R.drawable.as_499),companyItemSystems.get(1).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "生機食品調製機", "AS-129(白)", tmpContents2, BitmapFactory.decodeResource(getResources() ,R.drawable.as_628_w),companyItemSystems.get(1).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "健康食品調製機", "AS-129(黑)", tmpContents1, BitmapFactory.decodeResource(getResources() ,R.drawable.as_688_w),companyItemSystems.get(1).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "生機食品調製機", "AS-129(白)", tmpContents2, BitmapFactory.decodeResource(getResources() ,R.drawable.bl_1200_r),companyItemSystems.get(1).getId()));
        companyItemSystems.get(1).contentIds.add(companyItems.get(3));
//        companyItemSystems.get(1).contentIds.add(companyItems.get(5));
//        companyItemSystems.get(1).contentIds.add(companyItems.get(6));
//        companyItemSystems.get(1).contentIds.add(companyItems.get(7));

        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "全不鏽鋼電鍋", "ASC-018", tmpContents5, BitmapFactory.decodeResource(getResources() ,R.drawable.asc_018),companyItemSystems.get(2).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "生機食品調製機", "AS-130(白)", tmpContents2, BitmapFactory.decodeResource(getResources() ,R.drawable.as_628_w),companyItemSystems.get(2).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "健康食品調製機", "AS-130(黑)", tmpContents1, BitmapFactory.decodeResource(getResources() ,R.drawable.as_688_w),companyItemSystems.get(2).getId()));
//        companyItems.add(new CompanyItem(UUID.randomUUID().toString(), "生機食品調製機", "AS-130(白)", tmpContents2, BitmapFactory.decodeResource(getResources() ,R.drawable.bl_1200_r),companyItemSystems.get(2).getId()));
        companyItemSystems.get(2).contentIds.add(companyItems.get(4));
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
