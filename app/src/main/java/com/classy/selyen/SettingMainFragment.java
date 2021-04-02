package com.classy.selyen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.util.Log;

public class SettingMainFragment extends PreferenceFragment {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    RingtonePreference sound_preference;
    SwitchPreference vibe_preference;
    Preference lock_preference;
    Preference pw_change;
    SwitchPreference set_push_view_detail;

    SharedPreferences Lockset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.main_settings_preference);
        sound_preference = (RingtonePreference)findPreference("push_sound_list");
        vibe_preference = (SwitchPreference)findPreference("push_vibe");
        lock_preference = (Preference)findPreference("applock");
        pw_change = (Preference)findPreference("pw_change");
        set_push_view_detail = (SwitchPreference)findPreference("set_push_view_detail");

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = pref.edit();

        Lockset = getActivity().getSharedPreferences("app_lock", Context.MODE_PRIVATE);

        boolean b = Lockset.getBoolean("app_lock_state", false);
        if(b==false){
            lock_preference.setSummary("사용안함");
            pw_change.setEnabled(false);
        }else {
            lock_preference.setSummary("사용");
            pw_change.setEnabled(true);
        }

        pref.registerOnSharedPreferenceChangeListener(prefListener);

    }

    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("push_sound_list")){

            }

            if(key.equals("push_vibe")){
                boolean b = pref.getBoolean("push_vibe", false);
                //Toast.makeText(getActivity(), "진동설정"+b, Toast.LENGTH_SHORT).show();
                //2뎁스 PreferenceScreen 내부에서 발생한 환경설정 내용을 2뎁스 PreferenceScreen에 적용하기 위한 소스
                //((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
            }

            if(key.equals("app_lock_state")){
                boolean b = Lockset.getBoolean("app_lock_state", false);
                if(b==false){
                    lock_preference.setSummary("사용안함");
                    pw_change.setEnabled(false);
                }else {
                    lock_preference.setSummary("사용");
                    pw_change.setEnabled(true);
                }
            }
        }
    };

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        Log.d("tag","클릭된 Preference의 key는 "+key);

        if(key.equals("applock")){
            boolean b = Lockset.getBoolean("app_lock_state", false);
            if(b==false){
                Intent intent = new Intent(getActivity(), AppPassInputActivity.class);
                intent.putExtra("start_type",1);
                startActivity(intent);
            }else {
                Intent intent = new Intent(getActivity(), AppPassInputActivity.class);
                intent.putExtra("start_type",2);
                startActivity(intent);
            }
        }else if(key.equals("pw_change")){
            Intent intent = new Intent(getActivity(), AppPassInputActivity.class);
            intent.putExtra("start_type",3);
            startActivity(intent);
        }else if(key.equals("selyen_tos_view")){
            Intent intent = new Intent(getActivity(), Selyen_TosView_Activity.class);
            startActivity(intent);
        }else if(key.equals("gps_tos_view")){
            Intent intent = new Intent(getActivity(), GPS_Service_TosView_Activity.class);
            startActivity(intent);
        }else if(key.equals("personal_tos")){
            Intent intent = new Intent(getActivity(), PersonalInfo_TosView_Activity.class);
            startActivity(intent);
        }else if(key.equals("logout")){

            new AlertDialog.Builder(getActivity())
                    .setMessage("정말 로그아웃 하시겠습니까?")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            AppUserInfo appUserInfo = new AppUserInfo();
                            appUserInfo.set_hp_num("");
                            appUserInfo.set_user_name("");
                            appUserInfo.set_user_email("");
                            appUserInfo.set_user_PN("");
                            appUserInfo.set_user_addr("empty");
                            appUserInfo.set_user_actual_resid("false");
                            appUserInfo.set_user_actual_resid_date("none");
                            appUserInfo.set_act_push_msg("false");

                            SharedPreferences appData;
                            appData = getActivity().getSharedPreferences("auto_login",Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = appData.edit();

                            // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
                            // 저장시킬 이름이 이미 존재하면 덮어씌움
                            editor.putBoolean("SAVE_LOGIN_DATA", false);
                            editor.putString("ID", "");
                            editor.putString("PWD", "");
                            editor.apply();

                            Intent intent = new Intent(getActivity(), LogIn_Activity.class);
                            intent.putExtra("state", "launch");
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    })
                    .show();
        }else if(key.equals("notice_selyen")){
            Intent intent = new Intent(getActivity(), NoticeActivity.class);
            startActivity(intent);
        }else if(key.equals("open_source_library_list")){
            Intent intent = new Intent(getActivity(), OSS_License_Activity.class);
            startActivity(intent);
        }else if(key.equals("app_version")){
            Intent intent = new Intent(getActivity(), AppVersionPrintActivity.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void onResume(){
        super.onResume();
        pref.registerOnSharedPreferenceChangeListener(prefListener);
        boolean b = Lockset.getBoolean("app_lock_state", false);
        if(b==false){
            lock_preference.setSummary("사용안함");
            pw_change.setEnabled(false);
        }else {
            lock_preference.setSummary("사용");
            pw_change.setEnabled(true);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        pref.registerOnSharedPreferenceChangeListener(prefListener);
        boolean b = Lockset.getBoolean("app_lock_state", false);
        if(b==false){
            lock_preference.setSummary("사용안함");
            pw_change.setEnabled(false);
        }else {
            lock_preference.setSummary("사용");
            pw_change.setEnabled(true);
        }
    }
}
