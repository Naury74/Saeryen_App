package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class CompleteJoinActivity extends AppCompatActivity {

    String select_address = "";
    String addr_detail = "";
    String block_code = "";
    String actual_resid = "";

    TextView title;
    ImageView check_img;
    TextView sub_text;
    TextView addr_text;
    TextView activity_sub_text;

    Animation check_ani;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_join);

        check_ani = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_scale);

        Intent GetIntent = getIntent();
        select_address = GetIntent.getStringExtra("select_address");
        addr_detail = GetIntent.getStringExtra("addr_detail");
        block_code = GetIntent.getStringExtra("block_code");
        actual_resid = GetIntent.getStringExtra("actual_resid");

        title = findViewById(R.id.title);
        check_img = findViewById(R.id.check_img);
        sub_text = findViewById(R.id.sub_text);
        addr_text = findViewById(R.id.addr_text);
        activity_sub_text = findViewById(R.id.activity_sub_text);

        addr_text.setText(addr_detail);

        if(actual_resid.equals("admin")){
            title.setText("블록 생성");
            sub_text.setText("생성이 완료되었어요!");
            activity_sub_text.setText("블록의 관리자로 활동이 가능합니다!");
        }else{
            title.setText("QR코드 인증");
            sub_text.setText("인증이 완료되었어요!");
            activity_sub_text.setText("블록에서 활동이 가능합니다!");
        }

        check_img.startAnimation(check_ani);
    }

    public void onClick_done(View v){
        finish();
    }

    public void onClick_back(View v){
        finish();
    }
    public void onBackPressed() {
        finish();
    }
}
