package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Select_Join_Method_Activity extends AppCompatActivity {

    String server_adress = "http://ec2-13-124-191-53.ap-northeast-2.compute.amazonaws.com/seryeon_Join_Group.php";//블록 체크 데이터 정보 전송 경로

    String select_address = "";
    String addr_detail = "";
    String block_code = "";
    String request_code = "";

    String return_check = "";
    String address_return = "";
    String addr_detail_return = "";
    String block_code_return = "";
    String dump_code_return = "";
    String err_return = "";
    String ph_num_return = "";

    int type_num = 0;

    private static final String TAG = "block_join";

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

                        Block_JOIN task = new Block_JOIN();
                        task.execute(server_adress,UserData.getString("hp_num", ""),block_code);

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

                    Block_JOIN task = new Block_JOIN();
                    task.execute(server_adress,UserData.getString("hp_num", ""),block_code);

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

    ////////////////////////////////////////////////////////////////////////////서버 가입요청

    String block_join_result_check_json;
    class Block_JOIN extends AsyncTask<String, Void, String> {

        CustomAnimationLoadingDialog customAnimationLoadingDialog = new CustomAnimationLoadingDialog(Select_Join_Method_Activity.this);

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

            if(return_check.equals("SUCCESS")){

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

            }else{
//                if(err_return.equals("")){
//                    Snackbar.make(main_background_Layout, "서버연결에 실패했습니다. 잠시후 다시 시도해 주세요.", Snackbar.LENGTH_LONG).show();
//                }else {
//                    Snackbar.make(main_background_Layout, err_return, Snackbar.LENGTH_LONG).show();
//                }
                Toast.makeText(Select_Join_Method_Activity.this, "서버연결에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String ph_num = (String)params[1];//휴대폰 번호
            String block_code = (String)params[2];//블록 코드

            String serverURL = (String)params[0];//서버주소 할당
            String postParameters = "ph_num=" + ph_num + "&block_code=" + block_code;//전송할 파라미터,값
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

                block_join_result_check_json = sb.toString();//수신된 데이터 스트링으로 변환
                ReturnCheck_block_add();
                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    protected void ReturnCheck_block_add(){//리턴 데이터 확인
        try{
            JSONObject jsonObject = new JSONObject(block_join_result_check_json);

            return_check = jsonObject.getString("sign_check");
            if(return_check.equals("SUCCESS")){
                address_return = jsonObject.getString("main_addr");
                addr_detail_return = jsonObject.getString("sub_addr");
                block_code_return = jsonObject.getString("block_code");
            }else {
                err_return = jsonObject.getString("err_reason");
            }

            Log.d(TAG,"Json Return return_check : "+ return_check);
            Log.d(TAG,"Json Return address_return : "+ address_return);
            Log.d(TAG,"Json Return addr_detail_return : "+ addr_detail_return);
            Log.d(TAG,"Json Return block_code_return : "+ block_code_return);
            Log.d(TAG,"Json Return err_return : "+ err_return);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
