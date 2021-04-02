package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class Selyen_TosView_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selyen_tos_view);
    }

    public void onClick_back(View v){
        finish();
    }

    public void onBackPressed() {
        finish();
    }
}
