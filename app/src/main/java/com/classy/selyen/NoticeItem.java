package com.classy.selyen;

import android.graphics.Bitmap;

public class NoticeItem {

    public String title;
    public String date;
    public String in_text;
    public Bitmap image;

    public NoticeItem(String title, String date, String in_text, Bitmap image) {
        this.title = title;
        this.date = date;
        this.in_text = in_text;
        this.image = image;
    }

    public NoticeItem(String title, String date, String in_text) {
        this.title = title;
        this.date = date;
        this.in_text = in_text;
    }

    public String getTitle(){
        return title;
    }
    public String getDate(){
        return date;
    }
    public String getInText(){
        return in_text;
    }
    public Bitmap getImage(){
        return image;
    }
}
