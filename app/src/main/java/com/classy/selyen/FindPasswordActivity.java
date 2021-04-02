package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindPasswordActivity extends AppCompatActivity {

    private static final String TAG = "Selyen";
    int call_type;

    private BackPressCloseHendler backPressCloseHendler;//뒤로가기버튼 터치핸들러

    String receive_mail_address = "testaddress@gmail.com";//서버연결후""로 바꾸기
    String receive_check = "SUCCESS";//서버연결후""로 바꾸기
    String phone_num = "";
    String cert_email_server_address = " http://mobilekiosk.co.kr/admin/api/login/did_token_send.php";//앱 이메일 정보 요청 전송 경로

    String receive_code_check_result = "";//서버연결후""로 바꾸기
    String cert_code_server_address = " http://mobilekiosk.co.kr/admin/api/login/did_token_send.php";//앱 인증코드 정보 전송 경로

    ConstraintLayout main_background_Layout;

    InputMethodManager imm;
    String conversionTime = "000500";
    int timer_state = 0;
    CountDownTimer c_timer;

    ConstraintLayout hpnum_input_layout;
    ConstraintLayout code_input_layout;

    EditText hpnum_input;
    EditText code_input;
    TextView btn_request_verify;
    ConstraintLayout more_request_verify;
    TextView code_check_btn;
    TextView re_email_address_text;
    TextView timer_text;
    TextView code_input_title;

    CustomAnimationLoadingDialog customAnimationLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        backPressCloseHendler = new BackPressCloseHendler(this);//뒤로가기버튼 동작 제어 핸들러 생성

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        customAnimationLoadingDialog = new CustomAnimationLoadingDialog(FindPasswordActivity.this);
        main_background_Layout = (ConstraintLayout) findViewById(R.id.main_background_Layout);

        Intent intent = getIntent();
        call_type = intent.getExtras().getInt("call_type");

        hpnum_input = (EditText) findViewById(R.id.hpnum_input);
        btn_request_verify = (TextView) findViewById(R.id.btn_request_verify);
        hpnum_input_layout = (ConstraintLayout) findViewById(R.id.hpnum_input_layout);
        more_request_verify = (ConstraintLayout) findViewById(R.id.more_request_verify);
        code_input_layout = (ConstraintLayout) findViewById(R.id.code_input_layout);
        code_input = (EditText) findViewById(R.id.code_input);
        code_check_btn = (TextView) findViewById(R.id.code_check_btn);
        re_email_address_text = (TextView)findViewById(R.id.re_email_address_text);
        timer_text = (TextView)findViewById(R.id.timer_text);
        code_input_title = (TextView)findViewById(R.id.code_input_title);

        main_background_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(hpnum_input.getWindowToken(), 0);
            }
        });

        TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        phone_num = telManager.getLine1Number();
        if(phone_num.startsWith("+82")){
            phone_num = phone_num.replace("+82", "0");
        }
        hpnum_input.setText(phone_num);
    }

    public void SendCodeRequest(){
        Cert_Email task = new Cert_Email();
        task.execute(cert_email_server_address,phone_num);
        Log.d(TAG,"로그인 된 계정: "+ phone_num);
    }

    String result_check_json;
    //로그인 시에 서버에 로그인 정보 넘겨주고 리턴값 받음
    class Cert_Email extends AsyncTask<String, Void, String> {

        CustomAnimationLoadingDialog customAnimationLoadingDialog = new CustomAnimationLoadingDialog(FindPasswordActivity.this);

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
        }


        @Override
        protected String doInBackground(String... params) {

            Log.d("FCM Log", "전화번호: "+phone_num);
            String ph_num = (String)params[1];//사용자 번호

            String serverURL = (String)params[0];//서버주소 할당
            String postParameters = "phone_num" + ph_num;//전송할 파라미터,값
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

            receive_mail_address = jsonObject.getString("cert_address");
            receive_check = jsonObject.getString("rec_check");

            Log.d(TAG,"receive_mail_address : "+ receive_mail_address);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void onClick_auth_request(View v){

        hideKeyboard();
        phone_num = hpnum_input.getText().toString();
        boolean check = isValidCellPhoneNumber(phone_num);

        if(check == true){
            //SendCodeRequest();
            if(receive_check.equals("SUCCESS")){
                btn_request_verify.setBackgroundResource(R.drawable.round_btn_backgound_d6d6d6);
                hpnum_input_layout.setBackgroundResource(R.drawable.round_stroke_d6d6d6);
                btn_request_verify.setBackgroundResource(R.drawable.round_btn_backgound_d6d6d6);
                hpnum_input.setClickable(false);
                hpnum_input.setFocusable(false);
                more_request_verify.setVisibility(View.VISIBLE);
                btn_request_verify.setVisibility(View.GONE);
                code_input_layout.setVisibility(View.VISIBLE);
                code_input_title.setVisibility(View.VISIBLE);
                re_email_address_text.setVisibility(View.VISIBLE);
                code_check_btn.setVisibility(View.VISIBLE);
                String mask = receive_mail_address.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
                re_email_address_text.setText(mask+" 로 인증번호를 전송하였습니다");
                start_timer();
            }else {
                Toast.makeText(getApplicationContext(), "인증 진행에 실패 했습니다\n다시 시도해 주세요", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "잘못된 전화번호 형식입니다\n'-'없이 전화번호를 입력해 주세요", Toast.LENGTH_LONG).show();
        }
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
                hideKeyboard();
                code_input.setText("");
                code_input_layout.setVisibility(View.GONE);
                code_input_title.setVisibility(View.GONE);
                re_email_address_text.setVisibility(View.GONE);
                code_check_btn.setVisibility(View.GONE);
                timer_text.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "입력시간이 초과되었습니다", Toast.LENGTH_LONG).show();
                // TODO : 타이머가 모두 종료될때 어떤 이벤트를 진행할지

            }
        }.start();

    }

    public void onClick_code_check(View v){
        hideKeyboard();
        //SendCodeCheck();

        //TODO:임시 액티비티 전환용 - 서버 연결후 폐기
        if(call_type==0){
            Intent intent = new Intent(FindPasswordActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            finish();
        }else if(call_type==1){
            Intent intent = new Intent(FindPasswordActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            finish();
        }else if(call_type==2){
            Intent intent = new Intent(FindPasswordActivity.this, ChangeEmailActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            finish();
        }
    }

    public static boolean isValidCellPhoneNumber(String cellphoneNumber) {

        boolean returnValue = false;

        Log.i("cell", cellphoneNumber);

        String regex = "^\\s*(010|011|012|013|014|015|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$";

        Pattern p = Pattern.compile(regex);

        Matcher m = p.matcher(cellphoneNumber);

        if (m.matches()) {

            returnValue = true;

        }

        return returnValue;

    }

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(hpnum_input.getWindowToken(), 0);
    }

    public void onClick_back(View v){
        finish();
    }

    public void SendCodeCheck(){
        Cert_Code_Num task = new Cert_Code_Num();
        String input_num_sub = code_input.getText().toString();
        task.execute(cert_code_server_address,phone_num,input_num_sub);
        Log.d(TAG,"인증 요청 계정: "+ phone_num);
    }

    String result_check_json_return;
    //로그인 시에 서버에 로그인 정보 넘겨주고 리턴값 받음
    class Cert_Code_Num extends AsyncTask<String, Void, String> {

        CustomAnimationLoadingDialog customAnimationLoadingDialog = new CustomAnimationLoadingDialog(FindPasswordActivity.this);

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
            if(receive_code_check_result.equals("SUCCESS")){
                if(call_type==0){
                    Intent intent = new Intent(FindPasswordActivity.this, ChangePasswordActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    finish();
                }else if(call_type==1){
                    Intent intent = new Intent(FindPasswordActivity.this, ChangePasswordActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    finish();
                }else if(call_type==2){
                    Intent intent = new Intent(FindPasswordActivity.this, ChangeEmailActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    finish();
                }
            }else {
                Toast.makeText(getApplicationContext(), "인증 번호가 다릅니다\n다시 입력해 주세요", Toast.LENGTH_LONG).show();
                code_input.setText("");
            }
        }


        @Override
        protected String doInBackground(String... params) {

            Log.d("FCM Log", "전화번호: "+phone_num);
            String ph_num = (String)params[1];//사용자 번호
            String input_code = (String)params[2];//사용자 번호
            String serverURL = (String)params[0];//서버주소 할당
            String postParameters = "phone_num" + ph_num + "input_code" + input_code;//전송할 파라미터,값
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

                result_check_json_return = sb.toString();//수신된 데이터 스트링으로 변환
                ReturnCheck_CodeNum();
                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    protected void ReturnCheck_CodeNum(){//리턴 데이터 확인
        try{
            JSONObject jsonObject = new JSONObject(result_check_json_return);

            receive_code_check_result = jsonObject.getString("rec_check");

            Log.d(TAG,"receive_code_check_result : "+ receive_code_check_result);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onBackPressed() {
        finish();
    }
}
