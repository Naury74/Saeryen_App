package com.classy.selyen;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Fragment_Main_Home extends Fragment implements View.OnClickListener{

    Context context;

    SharedPreferences UserData;

    private static final String TAG = "Saeryen_Home";

    String post_down_url = "http://ec2-13-124-191-53.ap-northeast-2.compute.amazonaws.com/seryeon_Main_data.php";//앱 로그인 데이터 정보 전송 경로

    TextView user_name, sub_addr_text;
    TextView user_welcome_text;
    TextView plus_img;
    TextView no_community_title;
    ConstraintLayout no_community_Layout, cover_layout;

    FloatingActionButton w_notice_Fab, w_border_Fab, mainFab;
    TextView w_border_Fab_text, w_notice_Fab_text;
    ImageView block_setting_btn;

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
                if(UserData.getString("actual_resid", "").equals("admin")){
                    w_notice_Fab_text.setVisibility(View.VISIBLE);
                }
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
        }else if(v.getId() == R.id.block_setting_btn){
            Intent intent = new Intent(context, CreateBlockQRActivity.class);
            startActivity(intent);
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
        block_setting_btn = (ImageView)rootView.findViewById(R.id.block_setting_btn);
        plus_img.setOnClickListener(this);
        no_community_title.setOnClickListener(this);
        mainFab.setOnClickListener(this);
        w_notice_Fab.setOnClickListener(this);
        w_border_Fab.setOnClickListener(this);
        cover_layout.setOnClickListener(this);
        block_setting_btn.setOnClickListener(this);
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
            w_notice_Fab_text.setVisibility(View.GONE);
            w_border_Fab.setVisibility(View.VISIBLE);
            InsertData task = new InsertData();
            task.execute(post_down_url,UserData.getString("hp_num", ""),"0");
            //no_community_Layout.setVisibility(View.VISIBLE);
        }else if(UserData.getString("actual_resid", "").equals("admin")){
            sub_addr_text.setText(UserData.getString("addr_sub", ""));
            mainFab.setVisibility(View.VISIBLE);
            w_notice_Fab.setVisibility(View.VISIBLE);
            w_border_Fab.setVisibility(View.VISIBLE);
            block_setting_btn.setVisibility(View.VISIBLE);
            InsertData task = new InsertData();
            task.execute(post_down_url,UserData.getString("hp_num", ""),"0");
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

    String result_check_json;
    //로그인 시에 서버에 로그인 정보 넘겨주고 리턴값 받음
    class InsertData extends AsyncTask<String, Void, String> {

        CustomAnimationLoadingDialog customAnimationLoadingDialog = new CustomAnimationLoadingDialog(context);

        @Override
        protected void onPreExecute() {
            customAnimationLoadingDialog.show();
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String result) {
            customAnimationLoadingDialog.dismiss();
            super.onPostExecute(result);
            Log.d(TAG, "Data Post - App : " + result);

//            if(login_check.equals("SUCCESS")){
//                Log.d(TAG,"로그인 성공");
//
//                save_user_data();
//
//                Intent intent = new Intent(context_login, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//                save_login_data = true;
//                save();
//                finish();
//            }else{
//                if(err_return.equals("")){
//                    Snackbar.make(main_background_Layout, "로그인에 실패했습니다. 잠시후 다시 시도해 주세요.", Snackbar.LENGTH_LONG).show();
//                }else {
//                    Snackbar.make(main_background_Layout, err_return, Snackbar.LENGTH_LONG).show();
//                }
//                //Toast.makeText(LogIn_Activity.this, "입력하신 정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
//            }
        }


        @Override
        protected String doInBackground(String... params) {

            String user_ph = (String)params[1];//전화번호
            String request_init = (String)params[2];//시작 인덱스

            String serverURL = (String)params[0];//서버주소 할당
            String postParameters = "user_ph=" + user_ph + "&request_init=" + request_init;//전송할 파라미터,값
            Log.d(TAG,"postParameters : "+postParameters);

            try {

                URL url = new URL(serverURL);//주소입력
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);//5초내 무 응답시 예외처리
                httpURLConnection.setConnectTimeout(5000);//5초내 연결 불가시 예외처리
                httpURLConnection.setRequestMethod("POST");//post방식 요청
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));//전송할 데이터 할당
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();//응답
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {//정상응답
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();//에러
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");//수신값 저장
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                System.out.println("json 리턴: " + sb.toString());
                bufferedReader.close();

                result_check_json = sb.toString();//수신된 데이터 스트링으로 변환
                ReturnCheck();
                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    protected void ReturnCheck(){//리턴 데이터 확인
        try{
            JSONObject jsonObject = new JSONObject(result_check_json);

//            login_check = jsonObject.getString("sign_check");
//            if(login_check.equals("SUCCESS")){
//                hp_num_return = jsonObject.getString("user_ph_num");
//                mail_return = jsonObject.getString("user_email");
//                user_name_return = jsonObject.getString("user_nicname");
//                user_PN_return = jsonObject.getString("user_number");
//                push_msg_return = jsonObject.getString("push_msg");
//                device_token_return = jsonObject.getString("device_token");
//                addr_return = jsonObject.getString("registration_addr");
//                actual_resid_return = jsonObject.getString("actual_resid");
//                actual_resid_date_return = jsonObject.getString("actual_resid_date");
//                addr_sub_return = jsonObject.getString("registration_addr_sub");
//                block_code_return = jsonObject.getString("block_code");
//                user_img_return = jsonObject.getString("user_Image");
//            }else{
//                err_return = jsonObject.getString("err_reason");
//            }
//
//            Log.d(TAG,"Json Return login_check : "+ login_check);
//            Log.d(TAG,"Json Return biznum : "+ hp_num_return);
//            Log.d(TAG,"Json Return biz_name : "+ user_name_return);
//            Log.d(TAG,"Json Return device return : "+ device_token_return);
//            Log.d(TAG,"Json Return user_PN_return : "+ user_PN_return);
//            Log.d(TAG,"Json Return mail_return : "+ mail_return);
//            Log.d(TAG,"Json Return addr_return : "+ addr_return);
//            Log.d(TAG,"Json Return actual_resid_return : "+ actual_resid_return);
//            Log.d(TAG,"Json Return actual_resid_date_return : "+ actual_resid_date_return);
//            Log.d(TAG,"Json Return actual_resid_return : "+ actual_resid_return);
//            Log.d(TAG,"Json Return addr_sub_return : "+ addr_sub_return);
//            Log.d(TAG,"Json Return block_code_return : "+ block_code_return);
//            Log.d(TAG,"Json Return actual_push_msg_return : "+ err_return);
//            Log.d(TAG,"Json Return user_img_path : "+ user_img_return);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}
