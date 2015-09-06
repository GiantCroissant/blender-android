package com.giantcroissant.blender;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by liyihao on 15/7/14.
 */
public class CheckListAdapter extends ArrayAdapter<CheckListItem> {

    // 畫面資源編號
    private int resource;
    // 包裝的記事資料
    private List<CheckListItem> checkListItems;
    private CompoundButton.OnCheckedChangeListener newOnCheckedChangeListener;

    public CheckListAdapter(Context context, int resource, List<CheckListItem> checkListItems, CompoundButton.OnCheckedChangeListener newOnCheckedChangeListener) {
        super(context, resource, checkListItems);
        this.resource = resource;
        this.checkListItems = checkListItems;
        this.newOnCheckedChangeListener = newOnCheckedChangeListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout itemView;
        // 讀取目前位置的記事物件
        final CheckListItem checkListItem = getItem(position);
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
//        CheckBox checkBoxView = (CheckBox) itemView.findViewById(R.id.checkBoxItem);
//        checkListItem.checkBox = checkBoxView;

//        RadioButton radioButtonView = (RadioButton) itemView.findViewById(R.id.radioButtonItem);
//        checkListItem.radioButton = radioButtonView;
        // 設定標題
//        checkBoxView.setText(checkListItem.getTitle());
//        checkBoxView.setOnCheckedChangeListener(newOnCheckedChangeListener);
        TextView checkIndex = (TextView) itemView.findViewById(R.id.checkIndexText);
        checkIndex.setText(String.valueOf(position + 1)+" ");
        TextView checkContent = (TextView) itemView.findViewById(R.id.checkContentText);
        checkContent.setText(checkListItem.getTitle());

        ImageView checkImage = (ImageView) itemView.findViewById(R.id.checkImage);
        checkListItem.imageView = checkImage;
        if(checkListItem.getIsFinished())
        {
            checkImage.setVisibility(View.VISIBLE);
            checkImage.setImageResource(R.drawable.icon_check);
        }
        else
        {
            checkImage.setVisibility(View.INVISIBLE);
        }

//        Log.e("currentIndex",String.valueOf(position)+" position "+String.valueOf(radioButtonView.isChecked()));
        return itemView;
    }

    // 設定指定編號的記事資料
    public void set(int index, CheckListItem checkListItem) {
        if (index >= 0 && index < checkListItems.size()) {
            checkListItems.set(index, checkListItem);
            notifyDataSetChanged();
        }
    }

}
