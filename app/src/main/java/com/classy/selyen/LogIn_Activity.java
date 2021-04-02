package com.classy.selyen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

public class LogIn_Activity extends AppCompatActivity {

    private static final String TAG = "Saeryen";

    public static Context context_login;

    View decorView;
    int uiOption;

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
    String addr_return = "";
    String actual_resid_return = "false";
    String actual_resid_date_return = "none";
    String push_msg_return = "false";
    String err_return = "";
    String user_img_return = "";
    String addr_sub_return = "";
    String block_code_return = "";
    EditText biznum_input;
    EditText pw_input;
    ConstraintLayout main_background_Layout;
    boolean save_login_data = false;

    public Boolean MainActivity_check = false;

    InputMethodManager imm;

    private BackPressCloseHendler backPressCloseHendler;//뒤로가기버튼 터치핸들러

    SharedPreferences appData;
    private boolean saveLoginData;
    private String id;
    private String pwd;
    private CheckBox checkBox;

    final static int PERMISSION_REQUEST_CODE = 1000;

    CustomAnimationLoadingDialog customAnimationLoadingDialog;

    SharedPreferences User_Data;

    String newToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        context_login = this;
        customAnimationLoadingDialog = new CustomAnimationLoadingDialog(this);

        backPressCloseHendler = new BackPressCloseHendler(this);//뒤로가기버튼 동작 제어 핸들러 생성
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        main_background_Layout = (ConstraintLayout)findViewById(R.id.main_background_Layout);

        biznum_input = (EditText)findViewById(R.id.biznum_input);
        pw_input = (EditText)findViewById(R.id.pw_input);
        checkBox = (CheckBox) findViewById(R.id.checkbox);

        pw_input.setImeOptions(EditorInfo.IME_ACTION_DONE);

        //FCM 기기 토큰 생성
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        newToken = instanceIdResult.getToken();

                        Log.d(TAG, "FCM_Token: "+newToken);
                    }
                });

        pw_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        return false;
                    default:
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        onClick_confirm(v);
                        break;
                }
                return true;
            }
        });

        main_background_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(biznum_input.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(pw_input.getWindowToken(), 0);
            }
        });

        appData = getSharedPreferences("auto_login",MODE_PRIVATE);
        User_Data = getSharedPreferences("user_data",MODE_PRIVATE);
        //load();

        if (saveLoginData) {
            biznum_input.setText(id);
            pw_input.setText(pwd);
            checkBox.setChecked(saveLoginData);
        }

        //String biz_num_folder_path = this.getFilesDir().getAbsolutePath();
        //Log.d(TAG, "biz_num_info_zip_path : "+biz_num_folder_path);

        if(!hasPermissions(this, PERMISSIONS)){
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(LogIn_Activity.this,R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.login_permissions_notice_bottomsheet_layout,(LinearLayout)findViewById(R.id.container_bottom_sheet));
            bottomSheetDialog.setCanceledOnTouchOutside(false);

            bottomSheetView.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(LogIn_Activity.this, PERMISSIONS, PERMISSION_ALL);
                    bottomSheetDialog.dismiss();
                }
            });
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        }
        // 권한이 허용되어있다면 다음 화면 진행
        else {

        }

        final TextView logo;
        logo = (TextView)findViewById(R.id.logo);

        final Animation logo_anim;
        logo_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.logo_scale);
        //logo.startAnimation(logo_anim);
    }

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.READ_PHONE_NUMBERS,
            android.Manifest.permission.SEND_SMS
    };

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void getPermission(){

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE
                },
                1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (Build.VERSION.SDK_INT >= 23) {

            // requestPermission의 배열의 index가 아래 grantResults index와 매칭
            // 퍼미션이 승인되면
            if(grantResults.length > 0  && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Log.d(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);

                // TODO : 퍼미션이 승인되는 경우에 대한 코드

            }
            // 퍼미션이 승인 거부되면
            else {
                Log.d(TAG,"Permission denied");

                // TODO : 퍼미션이 거부되는 경우에 대한 코드
            }
        }
    }

    private void save_user_data() {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor_user = User_Data.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움
        editor_user.putString("hp_num", hp_num_return);
        editor_user.putString("user_name", user_name_return);
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
        editor_user.commit();
        editor_user.apply();
    }

    private void save() {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움
        editor.putBoolean("SAVE_LOGIN_DATA", save_login_data);
        editor.putString("ID", biznum_input.getText().toString().trim());
        editor.putString("PWD", pw_input.getText().toString().trim());

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
    }

    // 설정값을 불러오는 함수
    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        if(saveLoginData==true){
            id = appData.getString("ID", "");
            pwd = appData.getString("PWD", "");
        }
    }

    String result_check_json;
    //로그인 시에 서버에 로그인 정보 넘겨주고 리턴값 받음
    class InsertData extends AsyncTask<String, Void, String> {

        CustomAnimationLoadingDialog customAnimationLoadingDialog = new CustomAnimationLoadingDialog(LogIn_Activity.this);

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

            if(login_check.equals("SUCCESS")){
                Log.d(TAG,"로그인 성공");

                save_user_data();

                Intent intent = new Intent(context_login, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                save_login_data = true;
                save();
                finish();
            }else{
                if(err_return.equals("")){
                    Snackbar.make(main_background_Layout, "로그인에 실패했습니다. 잠시후 다시 시도해 주세요.", Snackbar.LENGTH_LONG).show();
                }else {
                    Snackbar.make(main_background_Layout, err_return, Snackbar.LENGTH_LONG).show();
                }
                //Toast.makeText(LogIn_Activity.this, "입력하신 정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            Log.d("FCM Log", "전화번호: "+login_biz_num);
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
            Log.d(TAG,"Json Return actual_resid_return : "+ actual_resid_return);
            Log.d(TAG,"Json Return addr_sub_return : "+ addr_sub_return);
            Log.d(TAG,"Json Return block_code_return : "+ block_code_return);
            Log.d(TAG,"Json Return actual_push_msg_return : "+ err_return);
            Log.d(TAG,"Json Return user_img_path : "+ user_img_return);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void onClick_confirm(View view){


//        Intent intent = new Intent(context_login, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        
        if(!(biznum_input.getText().toString().equals("")||pw_input.getText().toString().equals(""))){
            int status = NetworkStatus.getConnectivityStatus(getApplicationContext());

            if(status == NetworkStatus.TYPE_MOBILE){
                login_biz_num = biznum_input.getText().toString();
                password = pw_input.getText().toString();
                Log.d(TAG,"login_biz_num_ONCLICK : "+ login_biz_num);
                Log.d(TAG,"password_ONCLICK : "+ password);
                imm.hideSoftInputFromWindow(biznum_input.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(pw_input.getWindowToken(), 0);

                SendLoginInfo();

            }else if(status==NetworkStatus.TYPE_WIFI){
                login_biz_num = biznum_input.getText().toString();
                password = pw_input.getText().toString();
                Log.d(TAG,"login_biz_num_ONCLICK : "+ login_biz_num);
                Log.d(TAG,"password_ONCLICK : "+ password);
                imm.hideSoftInputFromWindow(biznum_input.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(pw_input.getWindowToken(), 0);

                SendLoginInfo();

            }else{
                Snackbar.make(main_background_Layout, "네트워크 연결을 확인해주세요", Snackbar.LENGTH_LONG).show();
                //Toast.makeText(LogIn_Activity.this, "네트워크 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "전화번호와 비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
        }

    }

    public void onClick_find_pw(View v){

        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());

        if(status == NetworkStatus.TYPE_MOBILE){
            Intent intent = new Intent(context_login, FindPasswordActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("call_type",0);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }else if(status==NetworkStatus.TYPE_WIFI){
            Intent intent = new Intent(context_login, FindPasswordActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("call_type",0);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }else{
            Toast.makeText(LogIn_Activity.this, "네트워크 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
        }

    }

    public void onClick_signup(View v){

        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());

        if(status == NetworkStatus.TYPE_MOBILE){
            Intent intent = new Intent(context_login, TOS_Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("selyen_tos_result",0);
            intent.putExtra("personal_information_collection_result",0);
            intent.putExtra("map_service_tos_result",0);
            intent.putExtra("receive_push_result",0);
            intent.putExtra("receive_sms_result",0);
            intent.putExtra("receive_email_result",0);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            //finish();
        }else if(status==NetworkStatus.TYPE_WIFI){
            Intent intent = new Intent(context_login, TOS_Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("selyen_tos_result",0);
            intent.putExtra("personal_information_collection_result",0);
            intent.putExtra("map_service_tos_result",0);
            intent.putExtra("receive_push_result",0);
            intent.putExtra("receive_sms_result",0);
            intent.putExtra("receive_email_result",0);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            //finish();
        }else{
            Toast.makeText(LogIn_Activity.this, "네트워크 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
        }

    }

    public void SendLoginInfo(){
        InsertData task = new InsertData();
        task.execute(admin_login_info,login_biz_num,newToken,password);
        Log.d(TAG,"로그인 된 계정: "+ login_biz_num);
    }

    public void onBackPressed() {
        backPressCloseHendler.onBackPressed();
    }
}
