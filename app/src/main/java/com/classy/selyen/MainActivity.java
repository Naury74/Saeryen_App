package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements LifecycleObserver {

    private static final String TAG = "Selyen";

    public static Context context_main;

    private BackPressCloseHendler backPressCloseHendler;//뒤로가기버튼 터치핸들러

    SharedPreferences LockScreenPref;
    Boolean lock_set_check = false;

    TabLayout tabs;
    SharedPreferences UserData;

    Fragment_Main_Home fragment_main_home;
    Fragment_Main_Timeline fragment_main_timeline;
    Fragment_Main_Chat fragment_main_chat;
    Fragment_Main_Myinfo fragment_main_myinfo;

    Intent intent;

    public String hp_num = "";
    public String user_name = "";
    public String user_email = "";
    public String user_PN = "";
    public String user_addr = "";
    public String user_actual_resid = "";
    public String user_actual_resid_date = "";
    public String act_push_msg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context_main = this;

        backPressCloseHendler = new BackPressCloseHendler(this);//뒤로가기버튼 동작 제어 핸들러 생성

        LockScreenPref = getSharedPreferences("app_lock",MODE_PRIVATE);
        lock_set_check = LockScreenPref.getBoolean("app_lock_state", false);
        Log.d("MAIN_ACTIVITY_TEST", "SampleLifeCycle - 앱락상태: "+lock_set_check);

        UserData = getSharedPreferences("user_data",MODE_PRIVATE);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        fragment_main_home = new Fragment_Main_Home();
        fragment_main_timeline = new Fragment_Main_Timeline();
        fragment_main_chat = new Fragment_Main_Chat();
        fragment_main_myinfo = new Fragment_Main_Myinfo();

        tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab());
        tabs.addTab(tabs.newTab());
        tabs.addTab(tabs.newTab());
        tabs.addTab(tabs.newTab());

        tabs.getTabAt(0).setIcon(R.drawable.ic_home);
        tabs.getTabAt(1).setIcon(R.drawable.ic_bell_stroke);
        tabs.getTabAt(2).setIcon(R.drawable.ic_chat_box_stroke);
        tabs.getTabAt(3).setIcon(R.drawable.ic_user2_stroke);

        //tabs.getTabAt(0).setText("홈");
        //tabs.getTabAt(1).setText("소식");
        //tabs.getTabAt(2).setText("채팅");
        //tabs.getTabAt(3).setText("MY");

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_main_home).commit();

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabs.getTabAt(0).setIcon(R.drawable.ic_home_stroke);
                tabs.getTabAt(1).setIcon(R.drawable.ic_bell_stroke);
                tabs.getTabAt(2).setIcon(R.drawable.ic_chat_box_stroke);
                tabs.getTabAt(3).setIcon(R.drawable.ic_user2_stroke);

                int position = tab.getPosition();
                Fragment selected = null;
                if(position == 0){
                    selected = fragment_main_home;
                    tabs.getTabAt(0).setIcon(R.drawable.ic_home);
                } else if(position == 1){
                    selected = fragment_main_timeline;
                    tabs.getTabAt(1).setIcon(R.drawable.ic_bell2);
                } else if(position == 2){
                    selected = fragment_main_chat;
                    tabs.getTabAt(2).setIcon(R.drawable.ic_chat_box);
                } else if(position == 3){
                    selected = fragment_main_myinfo;
                    tabs.getTabAt(3).setIcon(R.drawable.ic_user2);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Intent intent = getIntent();
        if(intent.hasExtra("addr")){//카카오 링크로 실행될 경우
            String invite_addr = intent.getStringExtra("addr");
            Log.d(TAG,"kakao_invite_addr: "+invite_addr);
            if(UserData.getString("actual_resid", "").equals("false")){
                Intent popupintent = new Intent(MainActivity.this, JoinBlockKakaoInviteActivity.class);
                popupintent.putExtra("addr", invite_addr);
                startActivity(popupintent);
            }
        }


    }

    public void onBackPressed() {
        backPressCloseHendler.onBackPressed();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void created() {
        Log.d("SampleLifeCycle", "ON_CREATE");
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void started() {
        Log.d("SampleLifeCycle", "ON_START");
        Log.d("MAIN_ACTIVITY_TEST", "SampleLifeCycle - start앱락상태: "+lock_set_check);
        if(lock_set_check==true){
            Intent intent = new Intent(this, FingerPrintActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resumed() {
        Log.d("SampleLifeCycle", "ON_RESUME");
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void paused() {
        lock_set_check = LockScreenPref.getBoolean("app_lock_state", false);
        Log.d("MAIN_ACTIVITY_TEST", "SampleLifeCycle - pause앱락상태: "+lock_set_check);
        Log.d("SampleLifeCycle", "ON_PAUSE");
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stopped() {
        Log.d("SampleLifeCycle", "ON_STOP");
    }
}
