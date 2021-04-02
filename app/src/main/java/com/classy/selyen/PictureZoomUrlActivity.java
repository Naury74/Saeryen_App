package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class PictureZoomUrlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_zoom_url);
        PhotoView photoView = (PhotoView)findViewById(R.id.photoView);

        Intent intent = getIntent();
        String url = intent.getStringExtra("image");

        Picasso.get()
                .load(url)
                .into(photoView);
    }
}
