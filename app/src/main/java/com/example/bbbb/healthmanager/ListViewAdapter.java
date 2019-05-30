package com.example.bbbb.healthmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    public ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView itemTitle = (TextView) convertView.findViewById(R.id.item_title);
        TextView itemBody = (TextView) convertView.findViewById(R.id.item_body);
        itemBody.setSelected(true);
        ImageView itemImg = (ImageView) convertView.findViewById(R.id.post_imageButton);

        // Data Set(filteredItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItemList.get(position);

        itemTitle.setText(listViewItem.getItemTitle());
        itemBody.setText(listViewItem.getItemContext());
        itemImg.setImageBitmap(listViewItem.getItemImg());

        return convertView;
    }

    public void addItem(String text1, String text2, Bitmap img) {
        ListViewItem item = new ListViewItem();
        item.setItemTitle(text1);
        item.setItemContext(text2);
        item.setItemImg(img);

        listViewItemList.add(item);
    }

    public void clearItem(){
        listViewItemList.clear();
    }
}
