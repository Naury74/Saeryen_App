package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "Saeryen";

    String admin_login_info = "http://ec2-13-124-191-53.ap-northeast-2.compute.amazonaws.com/seryeon_Login.php";//앱 로그인 데이터 정보 전송 경로
    public String login_biz_num = "";//사업자 번호
    String Device_token = "";//기기 토큰
    String password = "";//비밀번호
    String hp_num_return = "010-0000-0000";
    String device_token_return = "";
    String user_name_return = "세련 이웃";
    String login_check = "";
    String mail_return = "--";
    String user_PN_return = "000000";
    String addr_return = "아직 등록된 주소가 없어요!";
    String actual_resid_return = "false";
    String actual_resid_date_return = "none";
    String push_msg_return = "false";
    String err_return = "";
    String user_img_return = "";
    String addr_sub_return = "";
    String block_code_return = "";

    public static Context context_splash;

    SharedPreferences UserData;

    SharedPreferences appData;
    private boolean saveLoginData;
    private String id;
    private String pwd;
    private CheckBox checkBox;
    ConstraintLayout main_background_Layout;

    String newToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//화면 항상켜짐

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("Is on?", "Turning immersive mode mode off. ");
        } else {
            Log.i("Is on?", "Turning immersive mode mode on.");
        }
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        context_splash = this;

        main_background_Layout = (ConstraintLayout)findViewById(R.id.main_background_Layout);

        final TextView logo;
        logo = (TextView)findViewById(R.id.logo);

        final Animation logo_anim;
        logo_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.logo_scale);
        logo.startAnimation(logo_anim);

        appData = getSharedPreferences("auto_login",MODE_PRIVATE);
        UserData = getSharedPreferences("user_data",MODE_PRIVATE);

        //FCM 기기 토큰 생성
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        newToken = instanceIdResult.getToken();

                        Log.d(TAG, "FCM_Token: "+newToken);
                    }
                });

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run(){
                try
                {
                    Thread.sleep(2000);
                }
                catch (Exception e)
                {

                }
                load();
            }
        });
        thread.start();
    }

    private void save_user_data() {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor_user = UserData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움
        editor_user.putString("hp_num", hp_num_return);
        editor_user.putString("user_name", user_name_return);
        editor_user.putString("token",device_token_return);
        editor_user.putString("mail", mail_return);
        editor_user.putString("user_PN", user_PN_return);
        editor_user.putString("addr", addr_return);
        editor_user.putString("actual_resid", actual_resid_return);
        editor_user.putString("actual_resid_date", actual_resid_date_return);
        editor_user.putString("addr_sub", addr_sub_return);
        editor_user.putString("block_code", block_code_return);
        editor_user.putString("push_msg", push_msg_return);
        editor_user.putString("user_img", user_img_return);

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor_user.apply();
    }

    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        if(saveLoginData==true){
            id = appData.getString("ID", "");
            pwd = appData.getString("PWD", "");
            onClick_confirm();
        }else{
            Intent intent = new Intent(SplashActivity.this, LogIn_Activity.class);
            intent.putExtra("state", "launch");
            startActivity(intent);
            //overridePendingTransition(R.anim.fadein, R.anim.more_smaller);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            finish();
        }
    }

    String result_check_json;
    //로그인 시에 서버에 로그인 정보 넘겨주고 리턴값 받음
    class InsertData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "Data Post - App : " + result);

            if(login_check.equals("SUCCESS")){
                Log.d(TAG,"로그인 성공");

                save_user_data();

                Intent intent = new Intent(context_splash, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }else{
                if(err_return.equals("")){
                    //Snackbar.make(main_background_Layout, "서버연결에 실패했습니다. 잠시후 다시 시도해 주세요.", Snackbar.LENGTH_LONG).show();
                }else {
                    //Snackbar.make(main_background_Layout, err_return, Snackbar.LENGTH_LONG).show();
                }
                //Toast.makeText(LogIn_Activity.this, "입력하신 정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SplashActivity.this, LogIn_Activity.class);
                intent.putExtra("state", "launch");
                startActivity(intent);
                //overridePendingTransition(R.anim.fadein, R.anim.more_smaller);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            Log.d("FCM Log", "사업자번호: "+login_biz_num);
            Log.d("FCM Log", "비밀번호: "+password);
            String biz_num = (String)params[1];//사업자 번호
            String device_token = (String)params[2];//디바이스 토큰
            String password = (String)params[3];//비밀번호

            String serverURL = (String)params[0];//서버주소 할당
            String postParameters = "ph_num=" + biz_num + "&device_token=" + device_token + "&password=" + password;//전송할 파라미터,값
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

            login_check = jsonObject.getString("sign_check");
            if(login_check.equals("SUCCESS")){
                hp_num_return = jsonObject.getString("user_ph_num");
                mail_return = jsonObject.getString("user_email");
                user_name_return = jsonObject.getString("user_nicname");
                user_PN_return = jsonObject.getString("user_number");
                push_msg_return = jsonObject.getString("push_msg");
                device_token_return = jsonObject.getString("device_token");
                addr_return = jsonObject.getString("registration_addr");
                actual_resid_return = jsonObject.getString("actual_resid");
                actual_resid_date_return = jsonObject.getString("actual_resid_date");
                addr_sub_return = jsonObject.getString("registration_addr_sub");
                block_code_return = jsonObject.getString("block_code");
                user_img_return = jsonObject.getString("user_Image");
            }else{
                err_return = jsonObject.getString("err_reason");
            }

            Log.d(TAG,"Json Return login_check : "+ login_check);
            Log.d(TAG,"Json Return biznum : "+ hp_num_return);
            Log.d(TAG,"Json Return biz_name : "+ user_name_return);
            Log.d(TAG,"Json Return device return : "+ device_token_return);
            Log.d(TAG,"Json Return user_PN_return : "+ user_PN_return);
            Log.d(TAG,"Json Return mail_return : "+ mail_return);
            Log.d(TAG,"Json Return addr_return : "+ addr_return);
            Log.d(TAG,"Json Return actual_resid_return : "+ actual_resid_return);
            Log.d(TAG,"Json Return actual_resid_date_return : "+ actual_resid_date_return);
            Log.d(TAG,"Json Return addr_sub_return : "+ addr_sub_return);
            Log.d(TAG,"Json Return block_code_return : "+ block_code_return);
            Log.d(TAG,"Json Return actual_push_msg_return : "+ push_msg_return);
            Log.d(TAG,"Json Return err_return_return : "+ err_return);
            Log.d(TAG,"Json Return user_img_path : "+ user_img_return);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void onClick_confirm(){


        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());

        if(status == NetworkStatus.TYPE_MOBILE){
            login_biz_num = id;
            password = pwd;
            Log.d(TAG,"login_biz_num_ONCLICK : "+ login_biz_num);
            Log.d(TAG,"password_ONCLICK : "+ password);

            SendLoginInfo();

        }else if(status==NetworkStatus.TYPE_WIFI){
            login_biz_num = id;
            password = pwd;
            Log.d(TAG,"login_biz_num_ONCLICK : "+ login_biz_num);
            Log.d(TAG,"password_ONCLICK : "+ password);

            SendLoginInfo();

        }else{
            Snackbar.make(main_background_Layout, "네트워크 연결을 확인해주세요", Snackbar.LENGTH_LONG).show();
            //Toast.makeText(LogIn_Activity.this, "네트워크 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
        }

    }

    public void SendLoginInfo(){
        InsertData task = new InsertData();
        task.execute(admin_login_info,login_biz_num,newToken,password);
        Log.d(TAG,"로그인 된 계정: "+ login_biz_num);
    }
}
