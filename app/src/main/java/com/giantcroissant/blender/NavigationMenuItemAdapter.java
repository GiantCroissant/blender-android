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
 * Created by liyihao on 15/8/17.
 */
public class NavigationMenuItemAdapter extends ArrayAdapter<NavigationMenuItem> {

    // 畫面資源編號
    private int resource;
    // 包裝的記事資料
    private List<NavigationMenuItem> navigationMenuItems;

    public NavigationMenuItemAdapter(Context context, int resource, List<NavigationMenuItem> navigationMenuItems) {
        super(context, resource, navigationMenuItems);
        this.resource = resource;
        this.navigationMenuItems = navigationMenuItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout itemView;
        // 讀取目前位置的記事物件
        final NavigationMenuItem navigationMenuItem = getItem(position);
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
        TextView titleView = (TextView) itemView.findViewById(R.id.menu_text);
        ImageView menuItemIcon = (ImageView) itemView.findViewById(R.id.menu_icon);

        // 設定標題
        titleView.setText(navigationMenuItem.getTitle());
        menuItemIcon.setImageResource(navigationMenuItem.getIconID());

        return itemView;
    }

    // 設定指定編號的記事資料
    public void set(int index, NavigationMenuItem navigationMenuItem) {
        if (index >= 0 && index < navigationMenuItems.size()) {
            navigationMenuItems.set(index, navigationMenuItem);
            notifyDataSetChanged();
        }
    }

}
