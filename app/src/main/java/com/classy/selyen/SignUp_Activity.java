package com.classy.selyen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp_Activity extends AppCompatActivity {

    private static final String TAG = "Selyen";

    private BackPressCloseHendler backPressCloseHendler;//뒤로가기버튼 터치핸들러

    int selyen_tos_result = 0;
    int personal_information_collection_result = 0;
    int map_service_tos_result = 0;
    int receive_push_result = 0;
    int receive_sms_result = 0;
    int receive_email_result = 0;

    String ph_data = "";
    CountDownTimer c_timer;
    int timer_state = 0;
    String conversionTime = "000200";

    String sign_up_adress = "http://ec2-13-124-191-53.ap-northeast-2.compute.amazonaws.com/seryeon_Table_User_Info_Add.php";//앱 로그인 데이터 정보 전송 경로
    String ph_num_return = "";
    String password_return = "";
    String email_return = "";
    String selyen_tos_return = "";
    String personal_information_collection_return = "";
    String map_service_tos_return = "";
    String receive_push_return = "";
    String receive_sms_return = "";
    String receive_email_return = "";
    String sms_verify_return = "";
    String sign_up_check = "";
    String user_name_return = "";
    String user_PN_return = "";
    String err_reason_return = "서버연결에 실패했습니다. 네트워크를 확인해주세요";

    String ph_num_parse = "";//이용자 가입요청 전화번호
    String password_parse = "";//이용자 가입요청 비밀번호
    String email_parse = "";//이용자 가입요청 이메일
    String selyen_tos_result_parse = "1";//세련 이용약관 동의여부
    String personal_information_collection_tos_result_parse = "1";//개인정보 수집 이용약관 동의여부
    String map_service_tos_result_parse = "1";//위치기반 서비스 이용약관 동의여부
    String receive_push_result_parse = "";//푸쉬알림 수신동의 여부
    String receive_sms_result_parse = "";//문자 수신동의 여부
    String receive_email_result_parse = "";//이메일 수신동의 여부
    String sms_verify_result_parse = "1";//sms 본인확인 여부
    String user_name_parse = "";//이용자 가입요청 닉네임

    InputMethodManager imm;
    CustomAnimationLoadingDialog customAnimationLoadingDialog1;

    EditText ph_num_input;
    EditText verify_code_input;
    EditText id_input;
    EditText pw_input;
    EditText re_pw_input;
    EditText email_input;
    ConstraintLayout main_background_Layout;
    ConstraintLayout pw_check_layout;
    ConstraintLayout re_pw_check_btn;
    TextView email_check;
    TextView pw_check;
    TextView pw_check2;
    TextView pw_check3;
    TextView pw_check4;
    TextView btn_request_sign_up;
    TextView timer_text;

    ConstraintLayout normal_btn;
    ConstraintLayout code_request_btn;
    ConstraintLayout code_resend_btn;
    ConstraintLayout green_btn;

    ConstraintLayout verify_code_input_layout;
    ConstraintLayout code_sent_btn;

    ConstraintLayout pw_input_layout;
    ConstraintLayout re_pw_input_layout;
    ConstraintLayout ph_num_verify_outline;

    ImageView pw_check_ic;
    ImageView pw_check_ic2;
    ImageView pw_check_ic3;
    ImageView pw_check_ic4;
    ImageView ic_re_pw_check_btn;

    int check_pw = 0;
    int check_pw1 = 0;
    int check_pw2 = 0;
    int check_pw3 = 0;
    int check_pw4 = 0;
    int check_re_pw = 0;
    int check_email = 0;
    int check_veri_num = 0;
    int check_name = 0;

    int rColor;
    int gColor;

    CheckBox checkbox;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    CustomAnimationLoadingDialog customAnimationLoadingDialog;

    Animation vertical_scale;
    Animation vertical_scale_dec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        backPressCloseHendler = new BackPressCloseHendler(this);//뒤로가기버튼 동작 제어 핸들러 생성
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        customAnimationLoadingDialog1 = new CustomAnimationLoadingDialog(SignUp_Activity.this);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("kr");

        ph_num_input = (EditText)findViewById(R.id.ph_num_input);
        verify_code_input = (EditText)findViewById(R.id.verify_code_input);
        id_input = (EditText)findViewById(R.id.id_input);
        pw_input = (EditText)findViewById(R.id.pw_input);
        re_pw_input = (EditText)findViewById(R.id.re_pw_input);
        email_input = (EditText)findViewById(R.id.email_input);
        main_background_Layout = (ConstraintLayout)findViewById(R.id.main_background_Layout);
        pw_check_layout = (ConstraintLayout)findViewById(R.id.pw_check_layout);
        re_pw_check_btn = (ConstraintLayout)findViewById(R.id.re_pw_check_btn);
        email_check = (TextView)findViewById(R.id.email_check);
        pw_check = (TextView)findViewById(R.id.pw_check);
        pw_check2 = (TextView)findViewById(R.id.pw_check2);
        pw_check3 = (TextView)findViewById(R.id.pw_check3);
        pw_check4 = (TextView)findViewById(R.id.pw_check4);
        btn_request_sign_up = (TextView)findViewById(R.id.btn_request_sign_up);
        checkbox= (CheckBox) findViewById(R.id.check_box);
        btn_request_sign_up.bringToFront();

        pw_check_ic = (ImageView)findViewById(R.id.pw_check_ic);
        pw_check_ic2 = (ImageView)findViewById(R.id.pw_check_ic2);
        pw_check_ic3 = (ImageView)findViewById(R.id.pw_check_ic3);
        pw_check_ic4 = (ImageView)findViewById(R.id.pw_check_ic4);
        ic_re_pw_check_btn = (ImageView)findViewById(R.id.ic_re_pw_check_btn);

        normal_btn = (ConstraintLayout)findViewById(R.id.normal_btn);
        code_request_btn = (ConstraintLayout)findViewById(R.id.code_request_btn);
        code_resend_btn = (ConstraintLayout)findViewById(R.id.code_resend_btn);
        green_btn = (ConstraintLayout)findViewById(R.id.green_btn);
        verify_code_input_layout = (ConstraintLayout)findViewById(R.id.verify_code_input_layout);
        code_sent_btn = (ConstraintLayout)findViewById(R.id.code_sent_btn);
        timer_text = (TextView)findViewById(R.id.timer_text);
        pw_input_layout = (ConstraintLayout)findViewById(R.id.pw_input_layout);
        re_pw_input_layout = (ConstraintLayout)findViewById(R.id.re_pw_input_layout);
        ph_num_verify_outline = (ConstraintLayout)findViewById(R.id.ph_num_verify_outline);

        rColor = ContextCompat.getColor(getApplicationContext(), R.color.sub_r);
        gColor = ContextCompat.getColor(getApplicationContext(), R.color.sub_g);

        vertical_scale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.vertical_scale);
        vertical_scale_dec = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.vertical_scale_dec);

//        intent = getIntent();
//        selyen_tos_result = intent.getExtras().getInt("selyen_tos_result");
//        personal_information_collection_result = intent.getExtras().getInt("personal_information_collection_result");
//        map_service_tos_result = intent.getExtras().getInt("map_service_tos_result");
//        receive_push_result = intent.getExtras().getInt("receive_push_result");
//        receive_sms_result = intent.getExtras().getInt("receive_sms_result");
//        receive_email_result = intent.getExtras().getInt("receive_email_result");
//        phone_num = intent.getExtras().getString("phone_num");

        main_background_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(ph_num_input.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(verify_code_input.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(id_input.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(pw_input.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(re_pw_input.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(email_input.getWindowToken(), 0);
            }
        });

        id_input.setImeOptions(EditorInfo.IME_ACTION_DONE);

        id_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().equals("")){
                    id_input.setBackgroundResource(R.drawable.round_stroke_ff0000);  // 적색 테두리 적용
                    check_name = 0;
                    Disable_btn();
                }
                else{
                    id_input.setBackgroundResource(R.drawable.selector_round_white_bg_focus_selyenblue);  //테투리 흰색으로 변경
                    check_name = 1;
                    Enable_btn();
                }
            }
        });

        ph_num_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ph_num_verify_outline.setBackgroundResource(R.drawable.round_stroke_blue_btn);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ph_num_verify_outline.setBackgroundResource(R.drawable.round_stroke_blue_btn);
                if(isValidCellPhoneNumber(s.toString())){
                    normal_btn.setVisibility(View.GONE);
                    code_request_btn.setVisibility(View.VISIBLE);
                }else{
                    normal_btn.setVisibility(View.VISIBLE);
                    code_request_btn.setVisibility(View.GONE);
                }
            }
        });

        verify_code_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().length()==6){
                    timer_text.setVisibility(View.GONE);
                    code_sent_btn.setVisibility(View.VISIBLE);
                }else {
                    code_sent_btn.setVisibility(View.GONE);
                    timer_text.setVisibility(View.VISIBLE);
                }
            }
        });

        id_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        return false;
                    default:
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        break;
                }
                return true;
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
                    email_input.setBackgroundResource(R.drawable.selector_round_white_bg_focus_selyenblue);  //테투리 흰색으로 변경
                    check_email = 1;
                    Enable_btn();
                }
            }// afterTextChanged()..
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
                re_pw_input.setBackgroundResource(R.drawable.selector_round_white_bg_focus_selyenblue);
                if(!pw_input.getText().toString().equals(re_pw_input.getText().toString())){
                    re_pw_check_btn.setVisibility(View.VISIBLE);
                    re_pw_check_btn.setBackgroundResource(R.drawable.round_red_btn);
                    ic_re_pw_check_btn.setImageResource(R.drawable.ic_x_white);// 경고 메세지
                    check_re_pw = 0;
                    Disable_btn();
                }
                else{
                    re_pw_check_btn.setVisibility(View.VISIBLE);
                    re_pw_check_btn.setBackgroundResource(R.drawable.round_green_btn);
                    ic_re_pw_check_btn.setImageResource(R.drawable.ic_check_white); //초록색 변경
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
                pw_input.setBackgroundResource(R.drawable.selector_round_white_bg_focus_selyenblue);
                pw_check_layout.setVisibility(View.VISIBLE);
                pw_check_layout.startAnimation(vertical_scale);
                checkbox.setVisibility(View.VISIBLE);
                pw_check.setTextColor(gColor);
                pw_check2.setTextColor(gColor);
                pw_check3.setTextColor(gColor);
                pw_check4.setTextColor(gColor);
                pw_check_ic.setImageResource(R.drawable.ic_check_green);
                pw_check_ic2.setImageResource(R.drawable.ic_check_green);
                pw_check_ic3.setImageResource(R.drawable.ic_check_green);
                pw_check_ic4.setImageResource(R.drawable.ic_check_green);
                check_pw1 = 1;
                check_pw2 = 1;
                check_pw3 = 1;
                check_pw4 = 1;

                if(!Pattern.matches("^(?=.{8,20}$).*", pw_input.getText().toString())){
                    pw_check.setTextColor(rColor);    // 경고 메세지
                    pw_check_ic.setImageResource(R.drawable.ic_x_sub_r);
                    check_pw = 0;
                    check_pw1 = 0;
                    Disable_btn();
                }
                if(!Pattern.matches("^(?=\\S*[A-Z]).*$", pw_input.getText().toString())){
                    pw_check2.setTextColor(rColor);    // 경고 메세지
                    pw_check_ic2.setImageResource(R.drawable.ic_x_sub_r);
                    check_pw = 0;
                    check_pw2 = 0;
                    Disable_btn();
                }
                if(!Pattern.matches("^(?=\\S*\\d).*$", pw_input.getText().toString())){
                    pw_check3.setTextColor(rColor);    // 경고 메세지
                    pw_check_ic3.setImageResource(R.drawable.ic_x_sub_r);
                    check_pw = 0;
                    check_pw3 = 0;
                    Disable_btn();
                }
                if(!Pattern.matches("^(?=.*\\W)(?=.*\\S+$).*$", pw_input.getText().toString())){
                    pw_check4.setTextColor(rColor);    // 경고 메세지
                    pw_check_ic4.setImageResource(R.drawable.ic_x_sub_r);
                    check_pw = 0;
                    check_pw4 = 0;
                    Disable_btn();
                }
                if(check_pw1 == 1&&check_pw2 == 1&&check_pw3 == 1&&check_pw4 == 1){
                    check_pw = 1;
                    pw_check_layout.startAnimation(vertical_scale_dec);
                    pw_check_layout.setVisibility(View.GONE);
                    Enable_btn();
                }
            }// afterTextChanged()..
        });

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    // 패스워드가 보임 (ex . 1234)

                    pw_input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // 패스워드 안보임 (ex. ****)

                    pw_input.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                //mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                //updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                //mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    //mBinding.fieldPhoneNumber.setError("Invalid phone number.");
                    //Toast.makeText(getApplicationContext(), "사용할 수 없는 전화번호 입니다.", Toast.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    //Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",Snackbar.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "인증번호가 틀립니다. 다시 확인해 주세요", Toast.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                }

                ph_num_verify_outline.setBackgroundResource(R.drawable.round_stroke_ff0000);
                normal_btn.setVisibility(View.GONE);
                code_request_btn.setVisibility(View.VISIBLE);
                Snackbar.make(main_background_Layout, "인증을 진행할 수 없습니다. 잠시 후 다시 시도해 주세요.", Snackbar.LENGTH_LONG).show();
                // Show a message and update the UI
                // [START_EXCLUDE]
                //updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                //updateUI(STATE_CODE_SENT);

                verify_code_input_layout.setVisibility(View.VISIBLE);
                normal_btn.setVisibility(View.GONE);
                code_resend_btn.setVisibility(View.VISIBLE);
                start_timer();

                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]

    }

    public void data_parse(){
        ph_num_parse = ph_num_input.getText().toString();//이용자 가입요청 전화번호
        password_parse = pw_input.getText().toString();//이용자 가입요청 비밀번호
        email_parse = email_input.getText().toString();//이용자 가입요청 이메일
        selyen_tos_result_parse = Integer.toString(selyen_tos_result);//세련 이용약관 동의여부
        personal_information_collection_tos_result_parse = Integer.toString(personal_information_collection_result);//개인정보 수집 이용약관 동의여부
        map_service_tos_result_parse = Integer.toString(map_service_tos_result);//위치기반 서비스 이용약관 동의여부
        receive_push_result_parse = Integer.toString(receive_push_result);//푸쉬알림 수신동의 여부
        receive_sms_result_parse = Integer.toString(receive_sms_result);//문자 수신동의 여부
        receive_email_result_parse = Integer.toString(receive_email_result);//이메일 수신동의 여부
        sms_verify_result_parse = "1";//sms 본인확인 여부
        user_name_parse = id_input.getText().toString();//이용자 가입요청 닉네임
    }

    public void Submit(View v){
        if(check_email==1&&check_pw==1&&check_re_pw==1&&check_veri_num==1&&check_name==1){
            SendLoginInfo();

        }else {
            Snackbar.make(main_background_Layout, "입력되지 않은 칸이 있습니다", Snackbar.LENGTH_LONG).show();
            if(check_email==0){
                email_input.setBackgroundResource(R.drawable.round_stroke_ff0000);
            }
            if(check_pw==0){
                pw_input.setBackgroundResource(R.drawable.round_stroke_ff0000);
            }
            if(check_re_pw==0){
                re_pw_input.setBackgroundResource(R.drawable.round_stroke_ff0000);
            }
            if(check_veri_num==0){
                ph_num_verify_outline.setBackgroundResource(R.drawable.round_stroke_ff0000);
            }
            if(check_name==0){
                id_input.setBackgroundResource(R.drawable.round_stroke_ff0000);
            }
        }
    }

    public void Enable_btn(){
        if(check_email==1&&check_pw==1&&check_re_pw==1&&check_veri_num==1&&check_name==1){
            btn_request_sign_up.setBackgroundResource(R.drawable.ripple_custom_login_btn);
        }
    }

    public void Disable_btn(){
        if(check_email!=1||check_pw!=1||check_re_pw!=1||check_veri_num!=1||check_name!=1){
            btn_request_sign_up.setBackgroundResource(R.drawable.round_btn_backgound_d6d6d6);
        }
    }

    String result_check_json;
    //로그인 시에 서버에 로그인 정보 넘겨주고 리턴값 받음
    class InsertData extends AsyncTask<String, Void, String> {

        CustomAnimationLoadingDialog customAnimationLoadingDialog = new CustomAnimationLoadingDialog(SignUp_Activity.this);

        @Override
        protected void onPreExecute() {
            customAnimationLoadingDialog.show();
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String result) {
            if(sign_up_check.equals("SUCCESS")){
                Intent intent = new Intent(SignUp_Activity.this, CompleteSignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }else {
                Snackbar.make(main_background_Layout, err_reason_return, Snackbar.LENGTH_LONG).show();
            }
            customAnimationLoadingDialog.dismiss();
            super.onPostExecute(result);
            Log.d(TAG, "Data Post - App : " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String ph_num = (String)params[1];//이용자 가입요청 전화번호
            String password = (String)params[2];//이용자 가입요청 비밀번호
            String email = (String)params[3];//이용자 가입요청 이메일

            String selyen_tos_result = (String)params[4];//세련 이용약관 동의여부
            String personal_information_collection_tos_result = (String)params[5];//개인정보 수집 이용약관 동의여부
            String map_service_tos_result = (String)params[6];//위치기반 서비스 이용약관 동의여부
            String receive_push_result = (String)params[7];//푸쉬알림 수신동의 여부
            String receive_sms_result = (String)params[8];//문자 수신동의 여부
            String receive_email_result = (String)params[9];//이메일 수신동의 여부
            String sms_verify_result = (String)params[10];//sms 본인확인 여부

            String user_name_result = (String)params[11];//이용자 가입요청 닉네임

            String serverURL = (String)params[0];//서버주소 할당
            String postParameters = "ph_num=" + ph_num + "&password=" + password + "&email=" + email + "&se_tos=" + selyen_tos_result + "&pic_tos=" + personal_information_collection_tos_result + "&map_tos=" + map_service_tos_result + "&re_push=" + receive_push_result + "&re_sms=" + receive_sms_result + "&re_mail=" + receive_email_result + "&sms_certi=" + sms_verify_result + "&user_name=" + user_name_result;//전송할 파라미터,값
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
                Log.d(TAG, "result_check_json = "+ result_check_json);
                ReturnCheck(result_check_json);
                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    protected void ReturnCheck(String result_check_json){//리턴 데이터 확인
        try{
            JSONObject jsonObject = new JSONObject(result_check_json);

            sign_up_check = jsonObject.getString("sign_check");
            if(sign_up_check.equals("SUCCESS")){
                ph_num_return = jsonObject.getString("ph_num");
                password_return = jsonObject.getString("password");
                email_return = jsonObject.getString("email");
                selyen_tos_return = jsonObject.getString("se_tos");
                personal_information_collection_return = jsonObject.getString("pic_tos");
                map_service_tos_return = jsonObject.getString("map_tos");
                receive_push_return = jsonObject.getString("re_push");
                receive_sms_return = jsonObject.getString("re_sms");
                receive_email_return = jsonObject.getString("re_mail");
                sms_verify_return = jsonObject.getString("sms_certi");
                user_name_return = jsonObject.getString("user_name");
                user_PN_return = jsonObject.getString("user_PN");
            }else{
                err_reason_return = jsonObject.getString("err_reason");
            }

            Log.d(TAG,"Json Return ph_num : "+ ph_num_return);
            Log.d(TAG,"Json Return password : "+ password_return);
            Log.d(TAG,"Json Return email : "+ email_return);
            Log.d(TAG,"Json Return selyen_tos : "+ selyen_tos_return);
            Log.d(TAG,"Json Return personal_information_collection : "+ personal_information_collection_return);
            Log.d(TAG,"Json Return map_service_tos : "+ map_service_tos_return);
            Log.d(TAG,"Json Return receive_push : "+ receive_push_return);
            Log.d(TAG,"Json Return receive_sms : "+ receive_sms_return);
            Log.d(TAG,"Json Return receive_email : "+ receive_email_return);
            Log.d(TAG,"Json Return sms_verify : "+ sms_verify_return);
            Log.d(TAG,"Json Return sign_up_check : "+ sign_up_check);
            Log.d(TAG,"Json Return user_name : "+ user_name_return);
            Log.d(TAG,"Json Return user_PN_return : "+ user_PN_return);
            Log.d(TAG,"Json Return err_reason_return : "+ err_reason_return);


        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void onClick_back(View v){
        finish();
    }

    public void SendLoginInfo(){
        data_parse();
        InsertData task = new InsertData();
        task.execute(sign_up_adress,ph_num_parse,password_parse,email_parse,selyen_tos_result_parse,personal_information_collection_tos_result_parse,map_service_tos_result_parse,receive_push_result_parse,receive_sms_result_parse,receive_email_result_parse,sms_verify_result_parse,user_name_parse);
    }

    public void onBackPressed() {
        finish();
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

    public void onClick_auth_request(View v){

        ph_data = "+82"+ph_num_input.getText().toString().substring(1);
        Log.d(TAG,"전화번호: "+ph_data);

        startPhoneNumberVerification(ph_data);

        ph_num_input.setClickable(false);
        ph_num_input.setFocusable(false);
        code_request_btn.setVisibility(View.GONE);
        normal_btn.setVisibility(View.VISIBLE);
        imm.hideSoftInputFromWindow(ph_num_input.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(verify_code_input.getWindowToken(), 0);
    }

    public void onClick_auth_resend(View v){
        c_timer.cancel();
        imm.hideSoftInputFromWindow(ph_num_input.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(verify_code_input.getWindowToken(), 0);
        resendVerificationCode(ph_data, mResendToken);
    }

    public void onClick_code_check(View v){
        c_timer.cancel();
        imm.hideSoftInputFromWindow(ph_num_input.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(verify_code_input.getWindowToken(), 0);
        timer_text.setVisibility(View.GONE);
        code_sent_btn.setVisibility(View.GONE);
        normal_btn.setVisibility(View.GONE);
        code_request_btn.setVisibility(View.GONE);
        code_resend_btn.setVisibility(View.GONE);
        code_sent_btn.setVisibility(View.GONE);
        verifyPhoneNumberWithCode(mVerificationId, verify_code_input.getText().toString());
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]

        //mVerificationInProgress = true;
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    // [END resend_verification]

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(getApplicationContext(), "인증완료", Toast.LENGTH_LONG).show();
                            verify_code_input_layout.setVisibility(View.GONE);
                            verify_code_input.setText("");
                            timer_text.setVisibility(View.GONE);
                            code_sent_btn.setVisibility(View.GONE);
                            normal_btn.setVisibility(View.GONE);
                            code_request_btn.setVisibility(View.GONE);
                            code_resend_btn.setVisibility(View.GONE);
                            code_sent_btn.setVisibility(View.GONE);
                            green_btn.setVisibility(View.VISIBLE);
                            ph_num_verify_outline.setBackgroundResource(R.drawable.selector_round_white_bg_focus_selyenblue);
                            check_veri_num = 1;
                            Enable_btn();
                            // [START_EXCLUDE]
                            //updateUI(STATE_SIGNIN_SUCCESS, user);
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                //mBinding.fieldVerificationCode.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }

                            verify_code_input.setText("");
                            code_resend_btn.setVisibility(View.VISIBLE);
                            ph_num_verify_outline.setBackgroundResource(R.drawable.round_stroke_ff0000);
                            Toast.makeText(getApplicationContext(), "인증실패. 인증번호를 다시 확인해 주세요", Toast.LENGTH_LONG).show();
                            // [START_EXCLUDE silent]
                            // Update UI
                            //updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }

                        if(c_timer!=null){
                            c_timer.cancel();
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

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
                verify_code_input_layout.setVisibility(View.GONE);
                verify_code_input.setText("");
                timer_text.setVisibility(View.GONE);
                code_sent_btn.setVisibility(View.GONE);
                normal_btn.setVisibility(View.GONE);
                code_request_btn.setVisibility(View.GONE);
                code_resend_btn.setVisibility(View.VISIBLE);
                code_sent_btn.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "인증번호 입력시간을 초과 하였습니다", Toast.LENGTH_LONG).show();
                // TODO : 타이머가 모두 종료될때 어떤 이벤트를 진행할지

            }
        }.start();

    }
}
