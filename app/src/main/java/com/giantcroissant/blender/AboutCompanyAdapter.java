package com.giantcroissant.blender;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by liyihao on 15/7/14.
 */
public class AboutCompanyAdapter extends ArrayAdapter<CompanyItemSystem> {

    // 畫面資源編號
    private int resource;
    // 包裝的記事資料
    private List<CompanyItemSystem> companyItemSystems;

    public AboutCompanyAdapter(Context context, int resource, List<CompanyItemSystem> companyItemSystems) {
        super(context, resource, companyItemSystems);
        this.resource = resource;
        this.companyItemSystems = companyItemSystems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout itemView;
        // 讀取目前位置的記事物件
        final CompanyItemSystem companyItemSystem = getItem(position);
        if (convertView == null) {
            // 建立項目畫面元件
            itemView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater)
                    getContext().getSystemService(inflater);
            li.inflate(resource, itemView, true);
        }
        else {
            itemView = (LinearLayout) convertView;
        }

        // 讀取記事顏色、已選擇、標題與日期時間元件
        TextView titleView = (TextView) itemView.findViewById(R.id.company_item_name);
//        ImageView companyItemIcon = (ImageView) itemView.findViewById(R.id.company_item_icon);

        // 設定標題
        titleView.setText(companyItemSystem.getTitle());
//        companyItemIcon.setImageResource();

        return itemView;
    }

    // 設定指定編號的記事資料
    public void set(int index, CompanyItemSystem companyItemSystem) {
        if (index >= 0 && index < companyItemSystems.size()) {
            companyItemSystems.set(index, companyItemSystem);
            notifyDataSetChanged();
        }
    }

}
