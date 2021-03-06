package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.message.template.SocialObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.network.storage.ImageUploadResponse;
import com.kakao.util.helper.log.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CreateBlockQRActivity extends AppCompatActivity {

    ConstraintLayout cover_background_Layout;
    ImageView qr_imageView;
    TextView block_code_text, timer_text;

    SharedPreferences UserData;

    Bitmap QRbitmap;
    String num_code;

    String conversionTime = "000130";
    int timer_state = 0;
    CountDownTimer c_timer;

    String qr_img_url;

    CustomAnimationLoadingDialog customAnimationLoadingDialog;

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
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CreateBlockQRActivity.this,R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.qr_share_method_select_bottomsheet,(LinearLayout)findViewById(R.id.container_bottom_sheet));
            bottomSheetDialog.setCanceledOnTouchOutside(false);

            bottomSheetView.findViewById(R.id.kakao_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    kakaolink();
                    bottomSheetDialog.dismiss();
                }
            });
            bottomSheetView.findViewById(R.id.share_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DefaultShare();
                    bottomSheetDialog.dismiss();
                }
            });
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        }else{
            Toast.makeText(this, "QR ????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
        }

    }

    public void onClick_CreateQR(View v){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            if(!UserData.getString("block_code", "").equals(null)){
                cover_background_Layout.setVisibility(View.GONE);
                String text_code = make_rnd_string()+UserData.getString("block_code", "");
                num_code = text_code.substring(20, 26);
                BitMatrix bitMatrix = multiFormatWriter.encode(text_code, BarcodeFormat.QR_CODE,300,300);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                QRbitmap = barcodeEncoder.createBitmap(bitMatrix);
                qr_imageView.setImageBitmap(QRbitmap);
                block_code_text.setText(num_code);
                start_timer();
            }else{
                Toast.makeText(this, "QR ?????? ??????, ?????? ?????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "QR ?????? ??????", Toast.LENGTH_SHORT).show();
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

        // 1000 ????????? 1???
        // 60000 ????????? 1???
        // 60000 * 3600 = 1??????

        String getHour = time.substring(0, 2);
        String getMin = time.substring(2, 4);
        String getSecond = time.substring(4, 6);

        // "00"??? ?????????, ????????? ????????? 0 ?????? ??????
        if (getHour.substring(0, 1) == "0") {
            getHour = getHour.substring(1, 2);
        }

        if (getMin.substring(0, 1) == "0") {
            getMin = getMin.substring(1, 2);
        }

        if (getSecond.substring(0, 1) == "0") {
            getSecond = getSecond.substring(1, 2);
        }

        // ????????????
        conversionTime = Long.valueOf(getHour) * 1000 * 3600 + Long.valueOf(getMin) * 60 * 1000 + Long.valueOf(getSecond) * 1000;

        // ????????? ?????? : ????????? ?????? (???????????? 30?????? 30 x 1000(??????))
        // ????????? ?????? : ??????( 1000 = 1???)
        c_timer = new CountDownTimer(conversionTime, 1000) {

            // ?????? ???????????? ??? ??????
            public void onTick(long millisUntilFinished) {

                // ????????????
                String hour = String.valueOf(millisUntilFinished / (60 * 60 * 1000));

                // ?????????
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000)) ;
                String min = String.valueOf(getMin / (60 * 1000)); // ???

                // ?????????
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // ?????????

                // ??????????????? ??????
                String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // ???

                // ????????? ???????????? 0??? ?????????
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }

                // ?????? ???????????? 0??? ?????????
                if (min.length() == 1) {
                    min = "0" + min;
                }

                // ?????? ???????????? 0??? ?????????
                if (second.length() == 1) {
                    second = "0" + second;
                }

                timer_text.setText(min + ":" + second);
            }

            // ???????????? ?????????
            public void onFinish() {

                // ?????? ???
                cover_background_Layout.setVisibility(View.VISIBLE);
                timer_text.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "??????????????? ?????????????????????", Toast.LENGTH_LONG).show();
                // TODO : ???????????? ?????? ???????????? ?????? ???????????? ????????????

            }
        }.start();

    }

    public void DefaultShare() {
        Uri imageToShare = Uri.parse(MediaStore.Images.Media.insertImage(CreateBlockQRActivity.this.getContentResolver(), QRbitmap, "Share image", null));
        String textToShare = "?????? ?????? ????????????: " + UserData.getString("addr_sub", "") + "\n????????? QR???????????? QR???????????? ??????????????? ???????????? ?????? "+block_code_text.getText().toString()+"??? ????????? ?????????.";

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_TEXT, textToShare);
        share.putExtra(Intent.EXTRA_STREAM, imageToShare);
        startActivity(Intent.createChooser(share, "Share with"));
    }

    public void kakaolink() {
        //qr_????????? ???????????? ?????? ??????
        customAnimationLoadingDialog = new CustomAnimationLoadingDialog(CreateBlockQRActivity.this);
        customAnimationLoadingDialog.show();
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.img_share_title);

        File qr_file = new File(CreateBlockQRActivity.this.getCacheDir(),"QRimg.png");
        try {
            FileOutputStream out = new FileOutputStream(qr_file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //????????? ?????? ????????? ?????? ??????????????? ????????? ????????? ????????? ??????????????? ????????? ?????????
        KakaoLinkService.getInstance().uploadImage(this, true, qr_file, new ResponseCallback<ImageUploadResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e("QR_UPLOAD_TAG", "????????? ????????? ??????: "+ errorResult);
                Toast.makeText(CreateBlockQRActivity.this, "????????? ?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(ImageUploadResponse result) {
                Log.i("QR_UPLOAD_TAG", "????????? ????????? ?????? \n${imageUploadResult.infos.original}"+"\n????????? ?????? ??????: "+result);
                qr_img_url = result.getOriginal().getUrl();//url ??????

                LocationTemplate template = LocationTemplate.newBuilder(UserData.getString("addr", ""), ContentObject.newBuilder("?????? ??????, ????????? ????????? ?????????! \n?????? ??????: "+num_code,
                        qr_img_url,
                        LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com").build())
                                .setDescrption(UserData.getString("addr", "")+" "+UserData.getString("addr_sub", ""))
                                .build())
                        .addButton(new ButtonObject("?????? ????????????", LinkObject.newBuilder()
                                .setAndroidExecutionParams("addr="+UserData.getString("addr", ""))
                                .setIosExecutionParams("key1=value1")
                                .build()))
                        .build();

                Map<String, String> serverCallbackArgs = new HashMap<String, String>();
                serverCallbackArgs.put("user_id", "${current_user_id}");
                serverCallbackArgs.put("product_id", "${shared_product_id}");

                KakaoLinkService.getInstance().sendDefault(CreateBlockQRActivity.this, template, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Logger.e(errorResult.toString());
                    }

                    @Override
                    public void onSuccess(KakaoLinkResponse result) {
                        // ????????? ?????????????????? ?????? ????????? ??????????????? ??????. ????????? ??????????????? ??????????????? ????????? ??? ??? ??????. ?????? ?????? ????????? ???????????? ????????? ??????????????? ??????.
                    }
                });
                customAnimationLoadingDialog.dismiss();
            }
        });

    }
}
