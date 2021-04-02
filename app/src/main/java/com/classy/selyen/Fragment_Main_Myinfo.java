package com.classy.selyen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class Fragment_Main_Myinfo extends Fragment implements View.OnClickListener{

    Context context;

    TextView myinfo_name;
    TextView myinfo_ph_num;
    TextView myinfo_email;
    TextView my_location_title;
    ImageView myinfo_Image;
    ConstraintLayout join_btn;

    SharedPreferences UserData;

    private static final String TAG = "Saeryen_Myinfo";

    public Fragment_Main_Myinfo() {

    }

    @Override
    public void onClick(View v){

        if (v.getId() == R.id.appset_Layout) {
            Intent intent = new Intent(context, MainSettingActivity.class);
            startActivity(intent);
        }else if (v.getId() == R.id.myinfo_background_Layout) {
            Intent intent = new Intent(context, UserProfileEditActivity.class);
            startActivityForResult(intent,1001);
        }else if (v.getId() == R.id.my_location_background_Layout) {
            Intent intent = new Intent(context, MapLocationApplyActivity.class);
            startActivityForResult(intent,1001);
        }else if (v.getId() == R.id.notice_Layout) {
            Intent intent = new Intent(context, NoticeActivity.class);
            startActivityForResult(intent,1001);
        }else if (v.getId() == R.id.join_btn) {
            Intent intent = new Intent(context, Address_Map_Find_Activity.class);
            startActivityForResult(intent,1001);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        UserData = context.getSharedPreferences("user_data",Context.MODE_PRIVATE);


        Log.d(TAG,"app User Info hp_num : "+ UserData.getString("hp_num", ""));
        Log.d(TAG,"app User Info user_name : "+ UserData.getString("user_name", ""));
        Log.d(TAG,"app User Info user_PN : "+ UserData.getString("user_PN", ""));
        Log.d(TAG,"app User Info mail : "+ UserData.getString("mail", ""));
        Log.d(TAG,"app User Info addr : "+ UserData.getString("addr", ""));
        Log.d(TAG,"app User Info actual_resid : "+ UserData.getString("actual_resid", ""));
        Log.d(TAG,"app User Info actual_resid_date : "+ UserData.getString("actual_resid_date", ""));
        Log.d(TAG,"app User Info push_msg : "+ UserData.getString("push_msg", ""));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_main_myinfo, container, false);

        ImageView appset_Layout = rootView.findViewById(R.id.appset_Layout);
        ConstraintLayout myinfo_background_Layout = rootView.findViewById(R.id.myinfo_background_Layout);
        ConstraintLayout my_location_background_Layout = rootView.findViewById(R.id.my_location_background_Layout);
        ConstraintLayout notice_Layout = rootView.findViewById(R.id.notice_Layout);
        appset_Layout.setOnClickListener(this);
        myinfo_background_Layout.setOnClickListener(this);
        my_location_background_Layout.setOnClickListener(this);
        notice_Layout.setOnClickListener(this);
        join_btn = rootView.findViewById(R.id.join_btn);
        join_btn.setOnClickListener(this);

        myinfo_name = rootView.findViewById(R.id.myinfo_name);
        myinfo_ph_num = rootView.findViewById(R.id.myinfo_ph_num);
        myinfo_email = rootView.findViewById(R.id.myinfo_email);
        my_location_title = rootView.findViewById(R.id.my_location_title);
        myinfo_Image = rootView.findViewById(R.id.myinfo_Image);

        refresh_user_info();

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    public void refresh_user_info(){
        myinfo_name.setText(UserData.getString("user_name", ""));
        myinfo_ph_num.setText(UserData.getString("hp_num", "")+"  #"+UserData.getString("user_PN", ""));
        myinfo_email.setText(UserData.getString("mail", ""));
        if(!UserData.getString("addr", "").equals("null")){
            my_location_title.setText(UserData.getString("addr", ""));
            join_btn.setVisibility(View.GONE);
        }
        Picasso.get()
                .load("http://ec2-3-36-108-8.ap-northeast-2.compute.amazonaws.com"+UserData.getString("user_img", ""))
                .into(myinfo_Image);
    }

}
