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
            Toast.makeText(this, "QR 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
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

    public void DefaultShare() {
        Uri imageToShare = Uri.parse(MediaStore.Images.Media.insertImage(CreateBlockQRActivity.this.getContentResolver(), QRbitmap, "Share image", null));
        String textToShare = "세련 블록 가입인증: " + UserData.getString("addr_sub", "") + "\n세련앱 QR인증에서 QR이미지를 스캔하거나 블록가입 코드 "+block_code_text.getText().toString()+"를 입력해 주세요.";

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_TEXT, textToShare);
        share.putExtra(Intent.EXTRA_STREAM, imageToShare);
        startActivity(Intent.createChooser(share, "Share with"));
    }

    public void kakaolink() {
        //qr_이미지 업로드용 파일 생성
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

        //카카오 링크 이미지 파일 미지원으로 파일을 카카오 서버에 업로드하여 링크를 받아옴
        KakaoLinkService.getInstance().uploadImage(this, true, qr_file, new ResponseCallback<ImageUploadResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e("QR_UPLOAD_TAG", "이미지 업로드 실패: "+ errorResult);
                Toast.makeText(CreateBlockQRActivity.this, "카카오 서버 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(ImageUploadResponse result) {
                Log.i("QR_UPLOAD_TAG", "이미지 업로드 성공 \n${imageUploadResult.infos.original}"+"\n업로드 성공 결과: "+result);
                qr_img_url = result.getOriginal().getUrl();//url 파싱

                LocationTemplate template = LocationTemplate.newBuilder(UserData.getString("addr", ""), ContentObject.newBuilder("블록 초대, 블록에 참여해 보세요! \n인증 코드: "+num_code,
                        qr_img_url,
                        LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com").build())
                                .setDescrption(UserData.getString("addr", "")+" "+UserData.getString("addr_sub", ""))
                                .build())
                        .addButton(new ButtonObject("블록 참여하기", LinkObject.newBuilder()
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
                        // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                    }
                });
                customAnimationLoadingDialog.dismiss();
            }
        });

    }
}
