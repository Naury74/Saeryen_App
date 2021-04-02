package com.classy.selyen;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Fragment_Main_Home extends Fragment implements View.OnClickListener{

    Context context;

    SharedPreferences UserData;

    private static final String TAG = "Saeryen_Home";

    TextView user_name, sub_addr_text;
    TextView user_welcome_text;
    TextView plus_img;
    TextView no_community_title;
    ConstraintLayout no_community_Layout, cover_layout;

    FloatingActionButton w_notice_Fab, w_border_Fab, mainFab;
    TextView w_border_Fab_text, w_notice_Fab_text;

    private boolean isOpen = false;

    @Override
    public void onClick(View v){

        if (v.getId() == R.id.plus_img) {
            Intent intent = new Intent(context, Address_Map_Find_Activity.class);
            startActivityForResult(intent,1001);
        }else if(v.getId() == R.id.no_community_title){
            Intent intent = new Intent(context, Address_Map_Find_Activity.class);
            startActivityForResult(intent,1001);
        }else if(v.getId() == R.id.mainFab){
            if(!isOpen) {
                //View view, int translationY,int animatorTime, boolean isStartAnimator,boolean isStartInterpolator
                mainFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                mainFab.setImageResource(R.drawable.ic_x_white);
                cover_layout.setVisibility(View.VISIBLE);
                showUpAndDownBounce(w_notice_Fab,-400f,250,true,true);
                showUpAndDownBounce(w_border_Fab,-200f,250,true,true);
                w_notice_Fab_text.setVisibility(View.VISIBLE);
                showUpAndDownBounce(w_notice_Fab_text,-400f,250,true,true);
                w_border_Fab_text.setVisibility(View.VISIBLE);
                showUpAndDownBounce(w_border_Fab_text,-200f,250,true,true);
                isOpen = true;
            } else{
                mainFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.selyen_blue)));
                mainFab.setImageResource(R.drawable.ic_write);
                cover_layout.setVisibility(View.GONE);
                w_notice_Fab_text.setVisibility(View.GONE);
                showUpAndDownBounce(w_notice_Fab_text,0f,200,true,true);
                w_border_Fab_text.setVisibility(View.GONE);
                showUpAndDownBounce(w_border_Fab_text,0f,200,true,true);
                showUpAndDownBounce(w_notice_Fab,0f,200,true,false);
                showUpAndDownBounce(w_border_Fab,0f,200,true,false);
                isOpen = false;
            }

        }else if(v.getId() == R.id.w_notice_Fab){
            Intent intent = new Intent(context, Write_Notice_Activity.class);
            startActivityForResult(intent,1001);
        }else if(v.getId() == R.id.w_border_Fab){
            Intent intent = new Intent(context, Write_Post_Activity.class);
            startActivityForResult(intent,1001);
        }else if(v.getId() == R.id.cover_layout){
            mainFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.selyen_blue)));
            mainFab.setImageResource(R.drawable.ic_write);
            cover_layout.setVisibility(View.GONE);
            w_notice_Fab_text.setVisibility(View.GONE);
            showUpAndDownBounce(w_notice_Fab_text,0f,200,true,true);
            w_border_Fab_text.setVisibility(View.GONE);
            showUpAndDownBounce(w_border_Fab_text,0f,200,true,true);
            showUpAndDownBounce(w_notice_Fab,0f,200,true,false);
            showUpAndDownBounce(w_border_Fab,0f,200,true,false);
            isOpen = false;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        UserData = context.getSharedPreferences("user_data",Context.MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_main_home, container, false);
        user_welcome_text = (TextView)rootView.findViewById(R.id.user_welcome_text);
        user_name = (TextView)rootView.findViewById(R.id.user_name);
        sub_addr_text = (TextView)rootView.findViewById(R.id.sub_addr_text);
        plus_img = (TextView)rootView.findViewById(R.id.plus_img);
        no_community_title = (TextView)rootView.findViewById(R.id.no_community_title);
        no_community_Layout = (ConstraintLayout)rootView.findViewById(R.id.no_community_Layout);
        mainFab = (FloatingActionButton)rootView.findViewById(R.id.mainFab);
        w_notice_Fab = (FloatingActionButton)rootView.findViewById(R.id.w_notice_Fab);
        w_border_Fab = (FloatingActionButton)rootView.findViewById(R.id.w_border_Fab);
        w_border_Fab_text = (TextView)rootView.findViewById(R.id.w_border_Fab_text);
        w_notice_Fab_text = (TextView)rootView.findViewById(R.id.w_notice_Fab_text);
        cover_layout = (ConstraintLayout)rootView.findViewById(R.id.cover_layout);
        plus_img.setOnClickListener(this);
        no_community_title.setOnClickListener(this);
        mainFab.setOnClickListener(this);
        w_notice_Fab.setOnClickListener(this);
        w_border_Fab.setOnClickListener(this);
        cover_layout.setOnClickListener(this);
        //no_community_Layout.setOnClickListener(this);

        user_name.setText(UserData.getString("user_name", ""));

        String time = new SimpleDateFormat("HH").format(new Date(System.currentTimeMillis()));
        int time_parse = Integer.parseInt(time);

        user_welcome_text.setText(welcom_text(time_parse));

        if(UserData.getString("actual_resid", "").equals("false")){
            no_community_Layout.setVisibility(View.VISIBLE);
            sub_addr_text.setVisibility(View.GONE);
            plus_img.setBackgroundResource(R.drawable.ic_plus_circle);
            no_community_title.setText("아직 함께하는 블록이 없어요\n여기를 눌러 블록에 가입하세요");
        }else if(UserData.getString("actual_resid", "").equals("waiting")){
            sub_addr_text.setText(UserData.getString("addr_sub", ""));
            no_community_Layout.setVisibility(View.VISIBLE);
            plus_img.setBackgroundResource(R.drawable.ic_sand_clock);
            no_community_title.setText("관리자의 블록 가입신청 승인을\n기다리고 있습니다");
        }else if(UserData.getString("actual_resid", "").equals("join")){
            sub_addr_text.setText(UserData.getString("addr_sub", ""));
            mainFab.setVisibility(View.VISIBLE);
            w_notice_Fab.setVisibility(View.GONE);
            w_border_Fab.setVisibility(View.VISIBLE);
            //no_community_Layout.setVisibility(View.VISIBLE);
        }else if(UserData.getString("actual_resid", "").equals("admin")){
            sub_addr_text.setText(UserData.getString("addr_sub", ""));
            mainFab.setVisibility(View.VISIBLE);
            w_notice_Fab.setVisibility(View.VISIBLE);
            w_border_Fab.setVisibility(View.VISIBLE);
            //no_community_Layout.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    private String welcom_text(int time){
        String text = "";
        if(time >=21 || time<=06){
            text = "님, 좋은 꿈 꾸세요!";
        }else if(time >=11 && time<=14){
            text = "님, 점심은 드셨나요?";
        }else if(time >=7 && time<=9){
            text = "님, 상쾌한 아침이에요!";
        }else if(time >=18 && time<21){
            text = "님, 오늘도 수고하셨어요!";
        }else{
            text = "님, 좋은하루 되세요!";
        }
        return text;
    }

    public static Animator showUpAndDownBounce(View view, float translationY,
                                               int animatorTime, boolean isStartAnimator,
                                               boolean isStartInterpolator) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,
                "translationY", translationY);
        if (isStartInterpolator) {
            objectAnimator.setInterpolator(new OvershootInterpolator());
        }
        objectAnimator.setDuration(animatorTime);
        if (isStartAnimator) {
            objectAnimator.start();
        }
        return objectAnimator;
    }

}
