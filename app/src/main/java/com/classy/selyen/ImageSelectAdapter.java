package com.classy.selyen;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.AnyRes;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageSelectAdapter extends RecyclerView.Adapter<ImageSelectAdapter.ViewHolder> {

    private final int resource;
    private Context context;
    private ArrayList<Bitmap> list;
    Animation item_remove_anim;

    public ImageSelectAdapter(Context context, @AnyRes int resource) {
        this.resource = resource;
        this.context = context;
        list = new ArrayList<Bitmap>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(resource, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap item = getItem(position);

        holder.image.setImageBitmap(item);
        item_remove_anim = AnimationUtils.loadAnimation(context,R.anim.info_scale);
        holder.card_view.startAnimation(item_remove_anim);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private Bitmap getItem(int position) {
        return list.get(position);
    }

    public void clear() {
        if(null != list) {
            list.clear();
        }
    }

    public void addItem(Bitmap bitmap) {
        list.add(bitmap);
        notifyDataSetChanged();
    }

    public void delete_Item(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Bitmap> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ArrayList<Bitmap> getList() {
        ArrayList<Bitmap> bitmaps = this.list;
        return bitmaps;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView image;
        public final ImageView delete_btn;
        public final CardView card_view;

        public ViewHolder(View parent) {
            super(parent);

            image= (ImageView) parent.findViewById(R.id.image);
            delete_btn= (ImageView) parent.findViewById(R.id.delete_btn);
            card_view= (CardView) parent.findViewById(R.id.card_view);

            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 데이터 리스트로부터 아이템 데이터 참조.
                        delete_Item(pos);
                    }
                }
            });
        }
    }
}
