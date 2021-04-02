package com.classy.selyen;

public class AppUserInfo {

    private static String hp_num = "";
    private static String user_name = "";
    private static String user_email = "";
    private static String user_PN = "";
    private static String user_addr = "";
    private static String user_actual_resid = "";
    private static String user_actual_resid_date = "";
    private static String act_push_msg = "";

    public static void set_hp_num(String hp){
        hp_num = hp;
    }

    public static String get_hp_num(){
        return hp_num;
    }

    public static void set_user_name(String name){
        user_name = name;
    }

    public static String get_user_name(){
        return user_name;
    }

    public static void set_user_email(String email){
        user_email = email;
    }

    public static String get_user_email(){
        return user_email;
    }

    public static void set_user_PN(String PN){
        user_PN = PN;
    }

    public static String get_user_PN(){
        return user_PN;
    }

    public static void set_user_addr(String addr){
        user_addr = addr;
    }

    public static String get_user_addr(){
        return user_addr;
    }

    public static void set_user_actual_resid(String actual_resid){
        user_actual_resid = actual_resid;
    }

    public static String get_user_actual_resid(){
        return user_actual_resid;
    }

    public static void set_user_actual_resid_date(String actual_resid_date){
        user_actual_resid_date = actual_resid_date;
    }

    public static String get_user_actual_resid_date(){
        return user_actual_resid_date;
    }

    public static void set_act_push_msg(String msg){
        act_push_msg = msg;
    }

    public static String get_act_push_msg(){
        return act_push_msg;
    }
}
