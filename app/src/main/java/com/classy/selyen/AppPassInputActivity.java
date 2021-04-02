package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class AppPassInputActivity extends AppCompatActivity {

    private BackPressCloseHendler backPressCloseHendler;//뒤로가기버튼 터치핸들러

    Intent intent;
    int start_type = 0;
    int input_sequence = 1;

    ImageView lock_Image;
    TextView notice_text;
    ImageView dot1,dot2,dot3,dot4;
    TextView btn_1,btn_2,btn_3,btn_4,btn_5,btn_6,btn_7,btn_8,btn_9,btn_0,btn_remove;
    String input_pw = "";
    String push_btn = "";
    String finish_input_pw = "";

    SharedPreferences LockScreen;
    SharedPreferences.Editor editor;

    String store_pw = "";
    Boolean lock_set_check;

    Animation shake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_pass_input);

        backPressCloseHendler = new BackPressCloseHendler(this);//뒤로가기버튼 동작 제어 핸들러 생성

        intent = getIntent();
        start_type = intent.getExtras().getInt("start_type");

        lock_Image = (ImageView)findViewById(R.id.lock_Image);
        notice_text = (TextView)findViewById(R.id.notice_text);
        dot1 = (ImageView)findViewById(R.id.dot1);
        dot2 = (ImageView)findViewById(R.id.dot2);
        dot3 = (ImageView)findViewById(R.id.dot3);
        dot4 = (ImageView)findViewById(R.id.dot4);
        btn_1 = (TextView)findViewById(R.id.btn_1);
        btn_2 = (TextView)findViewById(R.id.btn_2);
        btn_3 = (TextView)findViewById(R.id.btn_3);
        btn_4 = (TextView)findViewById(R.id.btn_4);
        btn_5 = (TextView)findViewById(R.id.btn_5);
        btn_6 = (TextView)findViewById(R.id.btn_6);
        btn_7 = (TextView)findViewById(R.id.btn_7);
        btn_8 = (TextView)findViewById(R.id.btn_8);
        btn_9 = (TextView)findViewById(R.id.btn_9);
        btn_0 = (TextView)findViewById(R.id.btn_0);
        btn_remove = (TextView)findViewById(R.id.btn_remove);

        LockScreen = getSharedPreferences("app_lock",MODE_PRIVATE);
        lock_set_check = LockScreen.getBoolean("app_lock_state", false);

        Log.d("LOCK_PW","패스워드 스타트 타입: "+start_type);
        if(start_type==1){

        }else if(start_type==2){
            notice_text.setText("암호를 입력해주세요");

            store_pw = LockScreen.getString("app_lock_pw", "");
            Log.d("LOCK_PW","패스워드: "+store_pw);
        }else if(start_type==3){
            notice_text.setText("암호를 입력해주세요");

            store_pw = LockScreen.getString("app_lock_pw", "");
            Log.d("LOCK_PW","패스워드: "+store_pw);
        }else if(start_type==4){
            notice_text.setText("암호를 입력해주세요");

            store_pw = LockScreen.getString("app_lock_pw", "");
            Log.d("LOCK_PW","패스워드: "+store_pw);
        }

        shake = AnimationUtils.loadAnimation(this,R.anim.shake);

    }

    public void onClick_num_btn(View v){
        lock_Image.setImageDrawable(getDrawable(R.drawable.ic_lock));
        if (v.getId() == R.id.btn_1) {
            push_btn = "1";
        }else if (v.getId() == R.id.btn_2) {
            push_btn = "2";
        }else if (v.getId() == R.id.btn_3) {
            push_btn = "3";
        }else if (v.getId() == R.id.btn_4) {
            push_btn = "4";
        }else if (v.getId() == R.id.btn_5) {
            push_btn = "5";
        }else if (v.getId() == R.id.btn_6) {
            push_btn = "6";
        }else if (v.getId() == R.id.btn_7) {
            push_btn = "7";
        }else if (v.getId() == R.id.btn_8) {
            push_btn = "8";
        }else if (v.getId() == R.id.btn_9) {
            push_btn = "9";
        }else if (v.getId() == R.id.btn_0) {
            push_btn = "0";
        }else if (v.getId() == R.id.btn_remove) {
            push_btn = "";
            if(input_pw.length() != 0){
                input_pw = input_pw.substring(0, input_pw.length()-1);
            }
        }
        input_pw = input_pw+push_btn;
        if(input_pw.length() == 0){
            dot1.setBackgroundResource(R.drawable.ic_dot_default);
            dot2.setBackgroundResource(R.drawable.ic_dot_default);
            dot3.setBackgroundResource(R.drawable.ic_dot_default);
            dot4.setBackgroundResource(R.drawable.ic_dot_default);
        }else if(input_pw.length() == 1){
            dot1.setBackgroundResource(R.drawable.ic_dot_use);
            dot2.setBackgroundResource(R.drawable.ic_dot_default);
            dot3.setBackgroundResource(R.drawable.ic_dot_default);
            dot4.setBackgroundResource(R.drawable.ic_dot_default);
        }else if(input_pw.length() == 2){
            dot1.setBackgroundResource(R.drawable.ic_dot_use);
            dot2.setBackgroundResource(R.drawable.ic_dot_use);
            dot3.setBackgroundResource(R.drawable.ic_dot_default);
            dot4.setBackgroundResource(R.drawable.ic_dot_default);
        }else if(input_pw.length() == 3){
            dot1.setBackgroundResource(R.drawable.ic_dot_use);
            dot2.setBackgroundResource(R.drawable.ic_dot_use);
            dot3.setBackgroundResource(R.drawable.ic_dot_use);
            dot4.setBackgroundResource(R.drawable.ic_dot_default);
        }else if(input_pw.length() == 4){
            if(start_type==1){
                if(input_sequence==1){
                    notice_text.setText("암호를 다시 한번 입력해주세요");
                    dot1.setBackgroundResource(R.drawable.ic_dot_default);
                    dot2.setBackgroundResource(R.drawable.ic_dot_default);
                    dot3.setBackgroundResource(R.drawable.ic_dot_default);
                    dot4.setBackgroundResource(R.drawable.ic_dot_default);
                    finish_input_pw = input_pw;
                    input_pw = "";
                    input_sequence=2;
                }else if(input_sequence==2){
                    if(input_pw.equals(finish_input_pw)){
                        editor = LockScreen.edit();
                        editor.putString("app_lock_pw", finish_input_pw);
                        editor.putBoolean("app_lock_state", true);
                        editor.commit();
                        finish();
                    }else {
                        lock_Image.setImageDrawable(getDrawable(R.drawable.ic_lock_red));
                        lock_Image.startAnimation(shake);
                        notice_text.setText("암호가 일치하지 않습니다. 다시 입력해 주세요");
                        dot1.setBackgroundResource(R.drawable.ic_dot_default);
                        dot2.setBackgroundResource(R.drawable.ic_dot_default);
                        dot3.setBackgroundResource(R.drawable.ic_dot_default);
                        dot4.setBackgroundResource(R.drawable.ic_dot_default);
                        input_pw = "";
                    }
                }
            }else if(start_type==2){
                if(input_sequence==1){
                    if(input_pw.equals(store_pw)){
                        editor = LockScreen.edit();
                        editor.remove("app_lock_pw");
                        editor.putBoolean("app_lock_state", false);
                        editor.commit();
                        finish();
                    }else {
                        lock_Image.setImageDrawable(getDrawable(R.drawable.ic_lock_red));
                        lock_Image.startAnimation(shake);
                        notice_text.setText("암호가 일치하지 않습니다. 다시 입력해 주세요");
                        dot1.setBackgroundResource(R.drawable.ic_dot_default);
                        dot2.setBackgroundResource(R.drawable.ic_dot_default);
                        dot3.setBackgroundResource(R.drawable.ic_dot_default);
                        dot4.setBackgroundResource(R.drawable.ic_dot_default);
                        input_pw = "";
                    }
                }
            }else if(start_type==3){
                if(input_sequence==1){
                    if(input_pw.equals(store_pw)){
                        notice_text.setText("변경하실 암호를 입력해주세요");
                        dot1.setBackgroundResource(R.drawable.ic_dot_default);
                        dot2.setBackgroundResource(R.drawable.ic_dot_default);
                        dot3.setBackgroundResource(R.drawable.ic_dot_default);
                        dot4.setBackgroundResource(R.drawable.ic_dot_default);
                        input_pw = "";
                        input_sequence=2;
                    }else {
                        lock_Image.setImageDrawable(getDrawable(R.drawable.ic_lock_red));
                        lock_Image.startAnimation(shake);
                        notice_text.setText("암호가 일치하지 않습니다. 다시 입력해 주세요");
                        dot1.setBackgroundResource(R.drawable.ic_dot_default);
                        dot2.setBackgroundResource(R.drawable.ic_dot_default);
                        dot3.setBackgroundResource(R.drawable.ic_dot_default);
                        dot4.setBackgroundResource(R.drawable.ic_dot_default);
                        input_pw = "";
                    }
                }else if(input_sequence==2){
                    notice_text.setText("암호를 다시 한번 입력해주세요");
                    dot1.setBackgroundResource(R.drawable.ic_dot_default);
                    dot2.setBackgroundResource(R.drawable.ic_dot_default);
                    dot3.setBackgroundResource(R.drawable.ic_dot_default);
                    dot4.setBackgroundResource(R.drawable.ic_dot_default);
                    finish_input_pw = input_pw;
                    input_pw = "";
                    input_sequence=3;
                }else if(input_sequence==3){
                    if(input_pw.equals(finish_input_pw)){
                        editor = LockScreen.edit();
                        editor.putString("app_lock_pw", finish_input_pw);
                        editor.putBoolean("app_lock_state", true);
                        editor.commit();
                        finish();
                    }else {
                        lock_Image.setImageDrawable(getDrawable(R.drawable.ic_lock_red));
                        lock_Image.startAnimation(shake);
                        notice_text.setText("암호가 일치하지 않습니다. 다시 입력해 주세요");
                        dot1.setBackgroundResource(R.drawable.ic_dot_default);
                        dot2.setBackgroundResource(R.drawable.ic_dot_default);
                        dot3.setBackgroundResource(R.drawable.ic_dot_default);
                        dot4.setBackgroundResource(R.drawable.ic_dot_default);
                        input_pw = "";
                    }
                }
            }else if(start_type==4){
                if(input_sequence==1){
                    if(input_pw.equals(store_pw)){
                        finish();
                    }else {
                        lock_Image.setImageDrawable(getDrawable(R.drawable.ic_lock_red));
                        lock_Image.startAnimation(shake);
                        notice_text.setText("암호가 일치하지 않습니다. 다시 입력해 주세요");
                        dot1.setBackgroundResource(R.drawable.ic_dot_default);
                        dot2.setBackgroundResource(R.drawable.ic_dot_default);
                        dot3.setBackgroundResource(R.drawable.ic_dot_default);
                        dot4.setBackgroundResource(R.drawable.ic_dot_default);
                        input_pw = "";
                    }
                }
            }
        }
    }

    public void onBackPressed() {

    }
}
