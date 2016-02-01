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
public class CookBookAdapter extends ArrayAdapter<Cookbook> {

    // 畫面資源編號
    private int resource;
    // 包裝的記事資料
    private List<Cookbook> cookBooks;


    public CookBookAdapter(Context context, int resource, List<Cookbook> cookBooks) {
        super(context, resource, cookBooks);
        this.resource = resource;
        this.cookBooks = cookBooks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout itemView;
        // 讀取目前位置的記事物件
        final Cookbook cookBook = getItem(position);
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
        TextView cookBookNameTextView = (TextView) itemView.findViewById(R.id.cookbooknameTextView);
        TextView ingredientTextView = (TextView) itemView.findViewById(R.id.ingredientTextView);
        ImageView cookbookImage = (ImageView) itemView.findViewById(R.id.recipe_icon);

//        new loadImageAsync().execute(cookBook.getImageUrl());
        // 設定標題
        cookBookNameTextView.setText(cookBook.getName());
        ingredientTextView.setText(cookBook.getIngredient());
        if(cookBook.getImage() != null)
        {
            cookbookImage.setImageBitmap(cookBook.getImage());
        }

        return itemView;
    }

    // 設定指定編號的記事資料
    public void set(int index, Cookbook cookBook) {
        if (index >= 0 && index < cookBooks.size()) {
            cookBooks.set(index, cookBook);
            notifyDataSetChanged();
        }
    }

}
