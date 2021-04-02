package com.classy.selyen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
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

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelfcertificationActivity extends AppCompatActivity {

    private static final String TAG = "Selyen_Verify_Activity";

    int selyen_tos_result = 0;
    int personal_information_collection_result = 0;
    int map_service_tos_result = 0;
    int receive_push_result = 0;
    int receive_sms_result = 0;
    int receive_email_result = 0;

    ConstraintLayout selfcerti_tos_option_select;
    ImageView selfcerti_tos_option_select_check;
    TextView selfcerti_tos_option_select_text;
    int selfcerti_tos_option_check_result = 0;

    ConstraintLayout hpnum_input_layout;
    ConstraintLayout code_input_layout;
    ConstraintLayout main_background_Layout;

    Intent intent;
    Animation scale500;
    Animation scale1000;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    String sms_code = "123456";
    String phone_num = "";

    EditText hpnum_input;
    EditText code_input;
    TextView btn_request_verify;
    ConstraintLayout more_request_verify;
    TextView code_check_btn;
    TextView text1;
    TextView text2;
    ImageView finish_icon;
    TextView finish_btn;
    TextView finish_text;

    InputMethodManager imm;
    CustomAnimationLoadingDialog customAnimationLoadingDialog;

    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfcertification);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        customAnimationLoadingDialog = new CustomAnimationLoadingDialog(SelfcertificationActivity.this);
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("kr");

        intent = getIntent();
        selyen_tos_result = intent.getExtras().getInt("selyen_tos_result");
        personal_information_collection_result = intent.getExtras().getInt("personal_information_collection_result");
        map_service_tos_result = intent.getExtras().getInt("map_service_tos_result");
        receive_push_result = intent.getExtras().getInt("receive_push_result");
        receive_sms_result = intent.getExtras().getInt("receive_sms_result");
        receive_email_result = intent.getExtras().getInt("receive_email_result");

        selfcerti_tos_option_select = (ConstraintLayout) findViewById(R.id.selfcerti_tos_option_select);
        selfcerti_tos_option_select_check = (ImageView) findViewById(R.id.selfcerti_tos_option_select_check);
        selfcerti_tos_option_select_text = (TextView) findViewById(R.id.selfcerti_tos_option_select_text);
        main_background_Layout = (ConstraintLayout) findViewById(R.id.main_background_Layout);
        scale500 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale500);
        scale1000 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale1000);

        Spannable span = (Spannable) selfcerti_tos_option_select_text.getText();
        span.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 23, 24, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        hpnum_input = (EditText) findViewById(R.id.hpnum_input);
        btn_request_verify = (TextView) findViewById(R.id.btn_request_verify);
        hpnum_input_layout = (ConstraintLayout) findViewById(R.id.hpnum_input_layout);
        more_request_verify = (ConstraintLayout) findViewById(R.id.more_request_verify);
        code_input_layout = (ConstraintLayout) findViewById(R.id.code_input_layout);
        code_input = (EditText) findViewById(R.id.code_input);
        code_check_btn = (TextView) findViewById(R.id.code_check_btn);
        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        finish_btn = (TextView) findViewById(R.id.finish_btn);
        finish_icon = (ImageView) findViewById(R.id.finish_icon);
        finish_text = (TextView) findViewById(R.id.finish_text);

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
        hpnum_input.setClickable(false);
        hpnum_input.setFocusable(false);

        main_background_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(hpnum_input.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(code_input.getWindowToken(), 0);
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
                    Toast.makeText(getApplicationContext(), "사용할 수 없는 전화번호 입니다.", Toast.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "인증번호가 틀립니다. 다시 확인해 주세요", Toast.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                }

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
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]

    }

    public void onClick_code_check(View v){
        hideKeyboard();

        customAnimationLoadingDialog.show();
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                if(!code_input.getText().toString().equals("")){
                    verifyPhoneNumberWithCode(mVerificationId, code_input.getText().toString());

                }else{
                    Toast.makeText(getApplicationContext(), "인증번호를 입력해 주세요.", Toast.LENGTH_LONG).show();
                }
                customAnimationLoadingDialog.dismiss();
            }
        }, 1000);
    }

    public void onClick_finish(View v){

        Intent intent = new Intent(this, SignUp_Activity.class);
        intent.putExtra("selyen_tos_result",selyen_tos_result);
        intent.putExtra("personal_information_collection_result",personal_information_collection_result);
        intent.putExtra("map_service_tos_result",map_service_tos_result);
        intent.putExtra("receive_push_result",receive_push_result);
        intent.putExtra("receive_sms_result",receive_sms_result);
        intent.putExtra("receive_email_result",receive_email_result);
        intent.putExtra("phone_num",phone_num);

        Log.d(TAG,"selyen_tos_result: "+ selyen_tos_result);
        Log.d(TAG,"personal_information_collection_result: "+ personal_information_collection_result);
        Log.d(TAG,"map_service_tos_result: "+ map_service_tos_result);
        Log.d(TAG,"receive_push_result: "+ receive_push_result);
        Log.d(TAG,"receive_sms_result: "+ receive_sms_result);
        Log.d(TAG,"receive_email_result: "+ receive_email_result);

        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }

    public void onClick_auth_resend(View v){
        resendVerificationCode(phone_num, mResendToken);
    }

    public void onClick_auth_request(View v){

        hideKeyboard();
        if(selfcerti_tos_option_check_result==1){
            boolean check = isValidCellPhoneNumber(hpnum_input.getText().toString());
            if(check == true){
                phone_num = "+82"+hpnum_input.getText().toString().substring(1);
                Log.d(TAG,"전화번호: "+phone_num);

                startPhoneNumberVerification(phone_num);

                btn_request_verify.setBackgroundResource(R.drawable.round_btn_backgound_d6d6d6);
                hpnum_input_layout.setBackgroundResource(R.drawable.round_stroke_d6d6d6);
                btn_request_verify.setBackgroundResource(R.drawable.round_btn_backgound_d6d6d6);
                hpnum_input.setClickable(false);
                hpnum_input.setFocusable(false);
                selfcerti_tos_option_select.setClickable(false);
                more_request_verify.setVisibility(View.VISIBLE);
                btn_request_verify.setVisibility(View.GONE);
                code_input_layout.setVisibility(View.VISIBLE);
                code_check_btn.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(getApplicationContext(), "휴대전화 번호를 가져오지 못했습니다", Toast.LENGTH_LONG).show();
            }
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

    public void onClick_selfcerti_tos(View v){
        if(selfcerti_tos_option_check_result==1){
            selfcerti_tos_option_check_result = 0;
            selfcerti_tos_option_select_check.setBackgroundResource(R.drawable.ic_check);
            btn_request_verify.setBackgroundResource(R.drawable.round_btn_backgound_d6d6d6);
            hpnum_input_layout.setBackgroundResource(R.drawable.round_stroke_d6d6d6);
            btn_request_verify.setBackgroundResource(R.drawable.round_btn_backgound_d6d6d6);
            hpnum_input.setText("");
            hpnum_input.setClickable(false);
            hpnum_input.setFocusable(false);
        }else{
            selfcerti_tos_option_check_result = 1;
            selfcerti_tos_option_select_check.setBackgroundResource(R.drawable.ic_check_selyen_skyblue);
            selfcerti_tos_option_select_check.startAnimation(scale500);
            hpnum_input_layout.setBackgroundResource(R.drawable.round_stroke_selyenskyblue);
            hpnum_input.setText(phone_num);
            if(!hpnum_input.getText().toString().equals("")){
                btn_request_verify.setBackgroundResource(R.drawable.ripple_custom_login_btn);
            }
            hpnum_input.setClickable(true);
            hpnum_input.setFocusableInTouchMode(true);
        }
    }

    public void onClick_back(View v){
        Intent intent = new Intent(this, TOS_Activity.class);
        intent.putExtra("selyen_tos_result",selyen_tos_result);
        intent.putExtra("personal_information_collection_result",personal_information_collection_result);
        intent.putExtra("map_service_tos_result",map_service_tos_result);
        intent.putExtra("receive_push_result",receive_push_result);
        intent.putExtra("receive_sms_result",receive_sms_result);
        intent.putExtra("receive_email_result",receive_email_result);

        Log.d(TAG,"selyen_tos_result: "+ selyen_tos_result);
        Log.d(TAG,"personal_information_collection_result: "+ personal_information_collection_result);
        Log.d(TAG,"map_service_tos_result: "+ map_service_tos_result);
        Log.d(TAG,"receive_push_result: "+ receive_push_result);
        Log.d(TAG,"receive_sms_result: "+ receive_sms_result);
        Log.d(TAG,"receive_email_result: "+ receive_email_result);

        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }

    public void onClick_next(View v){

        if(selfcerti_tos_option_check_result == 1){

        }else {
            Toast.makeText(this, "본인확인 완료를 위해 서비스 이용약관에 동의해 주세요", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(this, TOS_Activity.class);
        intent.putExtra("selyen_tos_result",selyen_tos_result);
        intent.putExtra("personal_information_collection_result",personal_information_collection_result);
        intent.putExtra("map_service_tos_result",map_service_tos_result);
        intent.putExtra("receive_push_result",receive_push_result);
        intent.putExtra("receive_sms_result",receive_sms_result);
        intent.putExtra("receive_email_result",receive_email_result);

        Log.d(TAG,"selyen_tos_result: "+ selyen_tos_result);
        Log.d(TAG,"personal_information_collection_result: "+ personal_information_collection_result);
        Log.d(TAG,"map_service_tos_result: "+ map_service_tos_result);
        Log.d(TAG,"receive_push_result: "+ receive_push_result);
        Log.d(TAG,"receive_sms_result: "+ receive_sms_result);
        Log.d(TAG,"receive_email_result: "+ receive_email_result);

        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(hpnum_input.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(code_input.getWindowToken(), 0);
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
                            Toast.makeText(getApplicationContext(), "로그인 성공.", Toast.LENGTH_LONG).show();
                            selfcerti_tos_option_select.setVisibility(View.GONE);
                            hpnum_input_layout.setVisibility(View.GONE);
                            text1.setVisibility(View.GONE);
                            text2.setVisibility(View.GONE);
                            code_input_layout.setVisibility(View.GONE);
                            code_check_btn.setVisibility(View.GONE);
                            finish_btn.setVisibility(View.VISIBLE);
                            finish_icon.setVisibility(View.VISIBLE);
                            finish_icon.startAnimation(scale1000);
                            finish_text.setVisibility((View.VISIBLE));
                            finish_text.startAnimation(scale1000);
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
                            Toast.makeText(getApplicationContext(), "로그인 실패.", Toast.LENGTH_LONG).show();
                            // [START_EXCLUDE silent]
                            // Update UI
                            //updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]
}
