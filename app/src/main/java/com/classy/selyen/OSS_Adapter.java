package com.classy.selyen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class OSS_Adapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<OSS_Data> list;

    public OSS_Adapter(Context context, ArrayList<OSS_Data> data){
        mContext = context;
        list = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public OSS_Data getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converview, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.oss_item, null);

        TextView oss_name = (TextView)view.findViewById(R.id.oss_name);
        TextView oss_addr = (TextView)view.findViewById(R.id.oss_addr);
        TextView oss_license = (TextView)view.findViewById(R.id.oss_license);

        oss_name.setText(list.get(position).getName());
        oss_addr.setText(list.get(position).getAddr());
        oss_license.setText(list.get(position).getLicense());

        return view;
    }
}
