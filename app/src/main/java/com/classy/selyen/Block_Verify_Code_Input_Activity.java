package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class Block_Verify_Code_Input_Activity extends AppCompatActivity {

    TextView sub_text;
    EditText edit_text;
    ConstraintLayout main_background_Layout;
    TextView btn_next;
    TextView num1,num2,num3,num4,num5,num6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_verify_code_input);
        sub_text = findViewById(R.id.sub_text);
        main_background_Layout = findViewById(R.id.main_background_Layout);
        edit_text = findViewById(R.id.edit_text);
        btn_next = findViewById(R.id.btn_next);
        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        num3 = findViewById(R.id.num3);
        num4 = findViewById(R.id.num4);
        num5 = findViewById(R.id.num5);
        num6 = findViewById(R.id.num6);

        SpannableStringBuilder ssb = new SpannableStringBuilder(sub_text.getText().toString());
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#3065F3")), 8, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new StyleSpan(Typeface.BOLD), 8, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sub_text.setText(ssb);

        edit_text.setCursorVisible(false);

        main_background_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit_text.getWindowToken(), 0);
            }
        });

        edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length()<6){
                    btn_next.setClickable(false);
                    btn_next.setBackgroundResource(R.drawable.round_btn_backgound_d6d6d6);
                    set_visual_text();
                } else if(s.length()==6){
                    btn_next.setClickable(true);
                    btn_next.setBackgroundResource(R.drawable.ripple_custom_login_btn);
                    set_visual_text();
                }

            }
        });
    }

    private void set_visual_text(){
        String input = edit_text.getText().toString();
        char[] chars = input.toCharArray();
        refresh_visual_text();

        if(chars.length==1){
            num1.setText(""+chars[0]);
        }else if(chars.length==2){
            num1.setText(""+chars[0]);
            num2.setText(""+chars[1]);
        }else if(chars.length==3){
            num1.setText(""+chars[0]);
            num2.setText(""+chars[1]);
            num3.setText(""+chars[2]);
        }else if(chars.length==4){
            num1.setText(""+chars[0]);
            num2.setText(""+chars[1]);
            num3.setText(""+chars[2]);
            num4.setText(""+chars[3]);
        }else if(chars.length==5){
            num1.setText(""+chars[0]);
            num2.setText(""+chars[1]);
            num3.setText(""+chars[2]);
            num4.setText(""+chars[3]);
            num5.setText(""+chars[4]);
        }else if(chars.length==6){
            num1.setText(""+chars[0]);
            num2.setText(""+chars[1]);
            num3.setText(""+chars[2]);
            num4.setText(""+chars[3]);
            num5.setText(""+chars[4]);
            num6.setText(""+chars[5]);
        }else {

        }
    }

    private void refresh_visual_text(){
        num1.setText("");
        num2.setText("");
        num3.setText("");
        num4.setText("");
        num5.setText("");
        num6.setText("");
    }

    public void onClick_back(View v){
        finish();
    }
    public void onBackPressed() {
        finish();
    }

    public void onClick_input_code(View v){
        Intent intent = new Intent();
        intent.putExtra("input_code",edit_text.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

}
