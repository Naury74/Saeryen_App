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
import android.view.WindowManager;
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
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {

    private BackPressCloseHendler backPressCloseHendler;//뒤로가기버튼 터치핸들러

    CustomAnimationLoadingDialog customAnimationLoadingDialog1;
    InputMethodManager imm;

    EditText pw_input;
    EditText re_pw_input;
    ConstraintLayout main_background_Layout;
    TextView pw_check;
    TextView re_pw_check;
    TextView btn_request_change;

    int check_pw = 0;
    int check_re_pw = 0;

    String result_check = "";
    String err_return = "";
    String user_ph_num = "";
    String user_pw = "";

    SharedPreferences UserData;

    String admin_url = "http://ec2-3-36-108-8.ap-northeast-2.compute.amazonaws.com/seryeon_Sql_Change.php";//앱 로그인 데이터 정보 전송 경로

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        customAnimationLoadingDialog1 = new CustomAnimationLoadingDialog(ChangePasswordActivity.this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        backPressCloseHendler = new BackPressCloseHendler(this);//뒤로가기버튼 동작 제어 핸들러 생성

        UserData = getSharedPreferences("user_data",MODE_PRIVATE);

        pw_input = (EditText)findViewById(R.id.pw_input);
        re_pw_input = (EditText)findViewById(R.id.re_pw_input);
        main_background_Layout = (ConstraintLayout)findViewById(R.id.main_background_Layout);
        pw_check = (TextView)findViewById(R.id.pw_check);
        re_pw_check = (TextView)findViewById(R.id.re_pw_check);
        btn_request_change = (TextView)findViewById(R.id.btn_request_change);

        user_ph_num = UserData.getString("hp_num", "");

        main_background_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(pw_input.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(re_pw_input.getWindowToken(), 0);
            }
        });

        re_pw_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!pw_input.getText().toString().equals(re_pw_input.getText().toString())){
                    re_pw_check.setText("비밀번호가 일치하지 않습니다");    // 경고 메세지
                    re_pw_input.setBackgroundResource(R.drawable.round_stroke_ff0000);  // 적색 테두리 적용
                    check_re_pw = 0;
                    Disable_btn();
                }
                else{
                    re_pw_check.setText("");         //에러 메세지 제거
                    re_pw_input.setBackgroundResource(R.drawable.selector_round_stroke_focus_selyenskyblue);  //테투리 흰색으로 변경
                    check_re_pw = 1;
                    Enable_btn();
                }
            }// afterTextChanged()..
        });

        pw_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", pw_input.getText().toString())){
                    pw_check.setText("잘못된 비밀번호 형식입니다");    // 경고 메세지
                    pw_input.setBackgroundResource(R.drawable.round_stroke_ff0000);  // 적색 테두리 적용
                    check_pw = 0;
                    Disable_btn();
                }
                else{
                    pw_check.setText("");         //에러 메세지 제거
                    pw_input.setBackgroundResource(R.drawable.selector_round_stroke_focus_selyenskyblue);  //테투리 흰색으로 변경
                    check_pw = 1;
                    Enable_btn();
                }
            }// afterTextChanged()..
        });
    }

    public void Enable_btn(){
        if(check_pw==1&&check_re_pw==1){
            btn_request_change.setBackgroundResource(R.drawable.ripple_custom_login_btn);
            btn_request_change.setClickable(true);
        }
    }

    public void Disable_btn(){
        if(check_pw!=1||check_re_pw!=1){
            btn_request_change.setBackgroundResource(R.drawable.round_btn_backgound_d6d6d6);
            btn_request_change.setClickable(false);
        }
    }


    public void Submit(View v){
        if(check_pw==1&&check_re_pw==1&&pw_input.getText().toString().equals(re_pw_input.getText().toString())){
            SendInfo();
        }else {
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
        }
    }

    public void onBackPressed() {
        finish();
    }

    public void onClick_back(View v){
        finish();
    }

    public void SendInfo(){
        user_pw = re_pw_input.getText().toString();
        InsertData task = new InsertData();
        task.execute(admin_url,user_ph_num,user_pw);
    }

    String result_check_json;
    //로그인 시에 서버에 로그인 정보 넘겨주고 리턴값 받음
    class InsertData extends AsyncTask<String, Void, String> {

        CustomAnimationLoadingDialog customAnimationLoadingDialog = new CustomAnimationLoadingDialog(ChangePasswordActivity.this);

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

                Intent intent = new Intent(ChangePasswordActivity.this, CompleteSignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();

            }else{
                Snackbar.make(main_background_Layout, err_return, Snackbar.LENGTH_LONG).show();
                //Toast.makeText(LogIn_Activity.this, "입력하신 정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String ph_num = (String)params[1];//전화 번호
            String user_password = (String)params[2];//입력 데이터

            String serverURL = (String)params[0];//서버주소 할당
            String postParameters = "request_type=" + "ch_password" +"&ph_num=" + ph_num + "&request_Contents=" + user_password ;//전송할 파라미터,값
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
