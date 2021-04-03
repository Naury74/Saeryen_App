package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Random;

public class CreateBlockQRActivity extends AppCompatActivity {

    ConstraintLayout cover_background_Layout;
    ImageView qr_imageView;
    TextView block_code_text, timer_text;

    SharedPreferences UserData;

    Bitmap QRbitmap;

    String conversionTime = "000130";
    int timer_state = 0;
    CountDownTimer c_timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_block_qr);

        cover_background_Layout = (ConstraintLayout)findViewById(R.id.cover_background_Layout);
        qr_imageView = (ImageView)findViewById(R.id.qr_imageView);
        block_code_text = (TextView)findViewById(R.id.block_code_text);
        timer_text = (TextView)findViewById(R.id.timer_text);

        UserData = getSharedPreferences("user_data",MODE_PRIVATE);

    }

    public void onClick_ShareQR(View v){

        if(QRbitmap!=null){
            Uri imageToShare = Uri.parse(MediaStore.Images.Media.insertImage(CreateBlockQRActivity.this.getContentResolver(), QRbitmap, "Share image", null));
            String textToShare = "세련 블록 가입인증: " + UserData.getString("addr_sub", "") + "\nQR인증에서 QR이미지를 스캔하거나 블록가입 코드 "+block_code_text.getText().toString()+"를 입력해 주세요.";

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_TEXT, textToShare);
            share.putExtra(Intent.EXTRA_STREAM, imageToShare);
            startActivity(Intent.createChooser(share, "Share with"));
        }else{
            Toast.makeText(this, "QR 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    public void onClick_CreateQR(View v){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            if(!UserData.getString("block_code", "").equals(null)){
                cover_background_Layout.setVisibility(View.GONE);
                String text_code = make_rnd_string()+UserData.getString("block_code", "");
                String num_code = text_code.substring(20, 26);
                BitMatrix bitMatrix = multiFormatWriter.encode(text_code, BarcodeFormat.QR_CODE,300,300);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                QRbitmap = barcodeEncoder.createBitmap(bitMatrix);
                qr_imageView.setImageBitmap(QRbitmap);
                block_code_text.setText(num_code);
                start_timer();
            }else{
                Toast.makeText(this, "QR 생성 실패, 블록 가입 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "QR 생성 실패", Toast.LENGTH_SHORT).show();
        }
    }

    public String make_rnd_string(){
        Random rnd =new Random();
        StringBuffer buf =new StringBuffer();
        for(int i=0;i<20;i++) {
            if (rnd.nextBoolean()) {
                buf.append((char) ((int) (rnd.nextInt(26)) + 97));
            } else {
                buf.append((rnd.nextInt(10)));
            }
        }
        return buf.toString();
    }

    public void onClick_back(View v){
        finish();
    }
    public void onBackPressed() {
        finish();
    }

    public void start_timer(){
        timer_text.setVisibility(View.VISIBLE);
        if(timer_state==0){
            countDown(conversionTime);
            timer_state = 1;
        }else {
            c_timer.cancel();
            countDown(conversionTime);
        }
    }

    public void countDown(String time) {

        long conversionTime = 0;

        // 1000 단위가 1초
        // 60000 단위가 1분
        // 60000 * 3600 = 1시간

        String getHour = time.substring(0, 2);
        String getMin = time.substring(2, 4);
        String getSecond = time.substring(4, 6);

        // "00"이 아니고, 첫번째 자리가 0 이면 제거
        if (getHour.substring(0, 1) == "0") {
            getHour = getHour.substring(1, 2);
        }

        if (getMin.substring(0, 1) == "0") {
            getMin = getMin.substring(1, 2);
        }

        if (getSecond.substring(0, 1) == "0") {
            getSecond = getSecond.substring(1, 2);
        }

        // 변환시간
        conversionTime = Long.valueOf(getHour) * 1000 * 3600 + Long.valueOf(getMin) * 60 * 1000 + Long.valueOf(getSecond) * 1000;

        // 첫번쨰 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번쨰 인자 : 주기( 1000 = 1초)
        c_timer = new CountDownTimer(conversionTime, 1000) {

            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {

                // 시간단위
                String hour = String.valueOf(millisUntilFinished / (60 * 60 * 1000));

                // 분단위
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000)) ;
                String min = String.valueOf(getMin / (60 * 1000)); // 몫

                // 초단위
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지

                // 밀리세컨드 단위
                String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // 몫

                // 시간이 한자리면 0을 붙인다
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }

                // 분이 한자리면 0을 붙인다
                if (min.length() == 1) {
                    min = "0" + min;
                }

                // 초가 한자리면 0을 붙인다
                if (second.length() == 1) {
                    second = "0" + second;
                }

                timer_text.setText(min + ":" + second);
            }

            // 제한시간 종료시
            public void onFinish() {

                // 변경 후
                cover_background_Layout.setVisibility(View.VISIBLE);
                timer_text.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "입력시간이 초과되었습니다", Toast.LENGTH_LONG).show();
                // TODO : 타이머가 모두 종료될때 어떤 이벤트를 진행할지

            }
        }.start();

    }
}
