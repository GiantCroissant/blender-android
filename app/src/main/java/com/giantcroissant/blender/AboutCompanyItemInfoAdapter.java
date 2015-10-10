package com.giantcroissant.blender;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by liyihao on 15/7/14.
 */
public class AboutCompanyItemInfoAdapter extends ArrayAdapter<CompanyItem> {

    // 畫面資源編號
    private int resource;
    // 包裝的記事資料
    private List<CompanyItem> companyItems;

    public AboutCompanyItemInfoAdapter(Context context, int resource, List<CompanyItem> companyItems) {
        super(context, resource, companyItems);
        this.resource = resource;
        this.companyItems = companyItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout itemView;
        // 讀取目前位置的記事物件
        final CompanyItem companyItem = getItem(position);
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
        ImageView companyItemIcon = (ImageView) itemView.findViewById(R.id.company_item_icon);
        TextView contentView = (TextView) itemView.findViewById(R.id.company_item_content);

        // 設定標題
        titleView.setText(companyItem.getTitle());
        contentView.setText(companyItem.getContent());
//        companyItemIcon.setImageResource();

        return itemView;
    }

    // 設定指定編號的記事資料
    public void set(int index, CompanyItem companyItem) {
        if (index >= 0 && index < companyItems.size()) {
            companyItems.set(index, companyItem);
            notifyDataSetChanged();
        }
    }

}
