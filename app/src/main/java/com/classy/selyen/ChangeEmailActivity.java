package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChangeEmailActivity extends AppCompatActivity {

    private BackPressCloseHendler backPressCloseHendler;//뒤로가기버튼 터치핸들러

    CustomAnimationLoadingDialog customAnimationLoadingDialog1;
    InputMethodManager imm;

    EditText email_input;
    ConstraintLayout main_background_Layout;
    TextView email_check;
    TextView btn_request_change;

    int check_email = 0;

    String result_check = "";
    String err_return = "";
    String user_ph_num = "";
    String user_email = "";

    SharedPreferences UserData;

    String admin_url = "http://ec2-3-36-108-8.ap-northeast-2.compute.amazonaws.com/seryeon_Sql_Change.php";//앱 로그인 데이터 정보 전송 경로

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        customAnimationLoadingDialog1 = new CustomAnimationLoadingDialog(ChangeEmailActivity.this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        backPressCloseHendler = new BackPressCloseHendler(this);//뒤로가기버튼 동작 제어 핸들러 생성

        UserData = getSharedPreferences("user_data",MODE_PRIVATE);

        email_input = (EditText)findViewById(R.id.email_input);
        main_background_Layout = (ConstraintLayout)findViewById(R.id.main_background_Layout);
        email_check = (TextView)findViewById(R.id.email_check);
        btn_request_change = (TextView)findViewById(R.id.btn_request_change);

        email_input.setText(UserData.getString("mail", ""));
        user_ph_num = UserData.getString("hp_num", "");
        user_email = UserData.getString("mail", "");

        main_background_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(email_input.getWindowToken(), 0);
            }
        });

        email_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()){
                    email_check.setText("잘못된 이메일 형식입니다");    // 경고 메세지
                    email_input.setBackgroundResource(R.drawable.round_stroke_ff0000);  // 적색 테두리 적용
                    check_email = 0;
                    Disable_btn();
                }
                else{
                    email_check.setText("");         //에러 메세지 제거
                    email_input.setBackgroundResource(R.drawable.selector_round_stroke_focus_selyenskyblue);  //테투리 흰색으로 변경
                    check_email = 1;
                    Enable_btn();
                }
            }// afterTextChanged()..
        });
    }

    public void Enable_btn(){
        if(check_email==1){
            btn_request_change.setBackgroundResource(R.drawable.ripple_custom_login_btn);
            btn_request_change.setClickable(true);
        }
    }

    public void Disable_btn(){
        if(check_email!=1){
            btn_request_change.setBackgroundResource(R.drawable.round_btn_backgound_d6d6d6);
            btn_request_change.setClickable(false);
        }
    }

    private void save_user_data() {
        SharedPreferences.Editor editor_user = UserData.edit();
        editor_user.putString("mail", email_input.getText().toString());
        editor_user.apply();
    }

    public void Submit(View v){
        if(check_email==1){
            SendInfo();
        }else {
            Toast.makeText(getApplicationContext(), "입력하신 정보를 다시 확인해주세요", Toast.LENGTH_LONG).show();
        }
    }

    public void onBackPressed() {
        finish();
    }

    public void onClick_back(View v){
        finish();
    }

    public void SendInfo(){
        user_email = email_input.getText().toString();
        InsertData task = new InsertData();
        task.execute(admin_url,user_ph_num,user_email);
    }

    String result_check_json;
    //로그인 시에 서버에 로그인 정보 넘겨주고 리턴값 받음
    class InsertData extends AsyncTask<String, Void, String> {

        CustomAnimationLoadingDialog customAnimationLoadingDialog = new CustomAnimationLoadingDialog(ChangeEmailActivity.this);

        @Override
        protected void onPreExecute() {
            customAnimationLoadingDialog.show();
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String result) {
            customAnimationLoadingDialog.dismiss();
            super.onPostExecute(result);
            Log.d("Saeryen", "Data Post - App : " + result);

            if(result_check.equals("SUCCESS")){

                save_user_data();

                Intent intent = new Intent();
                setResult(101, intent);
                finish();
            }else{
                Snackbar.make(main_background_Layout, err_return, Snackbar.LENGTH_LONG).show();
                //Toast.makeText(LogIn_Activity.this, "입력하신 정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String ph_num = (String)params[1];//전화 번호
            String email = (String)params[2];//입력 데이터

            String serverURL = (String)params[0];//서버주소 할당
            String postParameters = "request_type=" + "ch_email" +"&ph_num=" + ph_num + "&request_Contents=" + email ;//전송할 파라미터,값
            Log.d("Saeryen","postParameters : "+postParameters);

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
                Log.d("Saeryen", "POST response code - " + responseStatusCode);

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

                Log.d("Saeryen", "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    protected void ReturnCheck(){//리턴 데이터 확인
        try{
            JSONObject jsonObject = new JSONObject(result_check_json);

            result_check = jsonObject.getString("sign_check");
            if(result_check.equals("SUCCESS")){

            }else{
                err_return = jsonObject.getString("err_reason");
            }

            Log.d("Saeryen","Json Return result_chaeck : "+ result_check);
            Log.d("Saeryen","Json Return err_return : "+ err_return);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
