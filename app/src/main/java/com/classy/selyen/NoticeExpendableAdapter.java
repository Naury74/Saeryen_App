package com.classy.selyen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class NoticeExpendableAdapter extends BaseExpandableListAdapter {


    Context context;
    private ArrayList<NoticeItem> groupList = null;
    private ArrayList<ArrayList<NoticeItem>> childList = null;
    private LayoutInflater inflater = null;
    private ViewHolder viewHolder = null;
    int temp_position = 0;

    public NoticeExpendableAdapter(Context c, ArrayList<NoticeItem> groupList, ArrayList<ArrayList<NoticeItem>> childList){

        super();
        this.inflater = LayoutInflater.from(c);
        this.groupList = groupList;
        this.childList = childList;
        this.context = c;
    }

    @Override
    public NoticeItem getGroup(int groupPosition){
        return groupList.get(groupPosition);
    }

    @Override
    public int getGroupCount(){
        return groupList.size();
    }

    @Override
    public long getGroupId(int groupPosition){
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null){
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.notice_list_row, parent, false);
            viewHolder.tv_groupName = (TextView)v.findViewById(R.id.tv_group);
            viewHolder.tv_group_date = (TextView) v.findViewById(R.id.tv_group_date);
            viewHolder.iv_image_arrow = (ImageView) v.findViewById(R.id.iv_image_arrow);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)v.getTag();
        }

        if(isExpanded){
            viewHolder.iv_image_arrow.setImageResource(R.drawable.ic_up_arrow);
        }else{
            viewHolder.iv_image_arrow.setImageResource(R.drawable.ic_down_arrow);
        }

        viewHolder.tv_groupName.setText(getGroup(groupPosition).getTitle());
        viewHolder.tv_group_date.setText(getGroup(groupPosition).getDate());

        return v;
    }

    @Override
    public NoticeItem getChild(int groupPosition, int childPosition){
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition){
        return 1;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition){
        return groupPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, final View convertView, ViewGroup parent){
        View v = convertView;
        final Context context1 = parent.getContext();

        temp_position = groupPosition;

        if(v == null){
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.notice_list_child, parent, false);
            viewHolder.tv_childName = (TextView)v.findViewById(R.id.tv_child);
            viewHolder.iv_image = (ImageView)v.findViewById(R.id.iv_image_in);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)v.getTag();
        }

        viewHolder.iv_image.setImageBitmap(getGroup(groupPosition).getImage());
        if(viewHolder.iv_image!=null){
            viewHolder.iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap = getGroup(temp_position).getImage();
                    float scale = (float) (1024/(float)bitmap.getWidth());
                    int image_w = (int) (bitmap.getWidth() * scale);
                    int image_h = (int) (bitmap.getHeight() * scale);
                    Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
                    resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    Intent intent = new Intent(context1, PictureZoomActivity.class);
                    intent.putExtra("image", byteArray);
                    context1.startActivity(intent);
                }
            });
        }

        viewHolder.tv_childName.setText(getGroup(groupPosition).getInText());

        return v;
    }

    @Override
    public boolean hasStableIds(){return true;}

    @Override
    public boolean isChildSelectable(int groupPostion, int childPosition){return true;}

    class ViewHolder{
        public ImageView iv_image;
        ImageView iv_image_arrow;
        public TextView tv_groupName;
        public TextView tv_group_date;
        public TextView tv_childName;
    }
}
