package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Select_Join_Method_Activity extends AppCompatActivity {

    String select_address = "";
    String addr_detail = "";
    String block_code = "";
    String request_code = "";

    SharedPreferences UserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_join_method);

        Intent GetIntent = getIntent();
        select_address = GetIntent.getStringExtra("select_address");
        addr_detail = GetIntent.getStringExtra("addr_detail");
        block_code = GetIntent.getStringExtra("block_code");
        request_code = GetIntent.getStringExtra("request_code");

        UserData = getSharedPreferences("user_data", Context.MODE_PRIVATE);

    }

    public void onClick_qr(View v){
        new IntentIntegrator(this).setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                if (resultCode == RESULT_OK) {//이미지 및 코드입력 성공
                    //Toast.makeText(Select_Join_Method_Activity.this, "Result: " + data.getStringExtra("input_code"), Toast.LENGTH_SHORT).show();

                    if(data.getStringExtra("input_code").equals(request_code)){

                        SharedPreferences.Editor editor_user = UserData.edit();

                        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
                        // 저장시킬 이름이 이미 존재하면 덮어씌움
                        editor_user.putString("addr", select_address);
                        editor_user.putString("addr_sub", addr_detail);
                        editor_user.putString("block_code", block_code);
                        editor_user.putString("actual_resid", "join");

                        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
                        editor_user.apply();

                        Intent intent = new Intent(Select_Join_Method_Activity.this, CompleteJoinActivity.class);
                        intent.putExtra("select_address", select_address);
                        intent.putExtra("addr_detail", addr_detail);
                        intent.putExtra("block_code", block_code);
                        intent.putExtra("actual_resid", "qr_join");
                        startActivity(intent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        finish();

                        Toast.makeText(Select_Join_Method_Activity.this, "가입 성공", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(Select_Join_Method_Activity.this, "가입 코드가 일치하지 않습니다. 다시 인증해 주세요", Toast.LENGTH_SHORT).show();
                    }
                } else {   //인증 취소 및 실패
                    Toast.makeText(Select_Join_Method_Activity.this, "인증 실패. 다시 진행해 주세요", Toast.LENGTH_SHORT).show();
                }
            } else {//QR 스캔 성공
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                String scan_code = result.getContents().toString().substring(20, 26);
                if(scan_code.equals(request_code)){

                    SharedPreferences.Editor editor_user = UserData.edit();

                    // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
                    // 저장시킬 이름이 이미 존재하면 덮어씌움
                    editor_user.putString("addr", select_address);
                    editor_user.putString("addr_sub", addr_detail);
                    editor_user.putString("block_code", block_code);
                    editor_user.putString("actual_resid", "join");

                    // apply, commit 을 안하면 변경된 내용이 저장되지 않음
                    editor_user.apply();

                    Intent intent = new Intent(Select_Join_Method_Activity.this, CompleteJoinActivity.class);
                    intent.putExtra("select_address", select_address);
                    intent.putExtra("addr_detail", addr_detail);
                    intent.putExtra("block_code", block_code);
                    intent.putExtra("actual_resid", "qr_join");
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    finish();

                    Toast.makeText(Select_Join_Method_Activity.this, "가입 성공", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(Select_Join_Method_Activity.this, "가입 코드가 일치하지 않습니다. 다시 인증해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onClick_back(View v){
        finish();
    }
    public void onBackPressed() {
        finish();
    }
}
