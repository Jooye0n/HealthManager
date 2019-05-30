package com.example.bbbb.healthmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewDetailAdapter extends BaseAdapter {

    public ArrayList<ListViewDetailItem> listViewItemListDetail = new ArrayList<ListViewDetailItem>();

    @Override
    public int getCount() {
        return listViewItemListDetail.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemListDetail.get(position) ;
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
            convertView = inflater.inflate(R.layout.item_detail_list, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView itemTitleDetail = (TextView) convertView.findViewById(R.id.item_title_detail);
        TextView itemBodyDetail = (TextView) convertView.findViewById(R.id.item_body_detail);

        // Data Set(filteredItemList)에서 position에 위치한 데이터 참조 획득
        ListViewDetailItem listViewItemDetail = listViewItemListDetail.get(position);

        itemTitleDetail.setText(listViewItemDetail.getItemTitle());
        itemBodyDetail.setText(listViewItemDetail.getItemContext());

        return convertView;
    }

    public void addItem(String text1, String text2) {
        ListViewDetailItem item = new ListViewDetailItem();
        item.setItemTitle(text1);
        item.setItemContext(text2);

        listViewItemListDetail.add(item);
    }

    public void clearItem(){
        listViewItemListDetail.clear();
    }
}

