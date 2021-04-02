package com.classy.selyen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Write_Post_Activity extends AppCompatActivity {
    final private static String TAG = "POST_ACTIVITY_TAG";

    ConstraintLayout main_background_Layout, apply_btn;
    InputMethodManager imm;
    EditText text_input;
    TextView date_text;

    boolean none_text_check = false;

    //카메라 관련
    final static int TAKE_PICTURE = 1;
    String mCurrentPhotoPath;
    final static int REQUEST_TAKE_PHOTO = 1;
    ImageSelectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        main_background_Layout = (ConstraintLayout)findViewById(R.id.main_background_Layout);
        text_input = (EditText)findViewById(R.id.text_input);
        apply_btn = (ConstraintLayout)findViewById(R.id.apply_btn);

        main_background_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(text_input.getWindowToken(), 0);
            }
        });

        text_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!text_input.getText().toString().equals("")){
                    apply_btn.setBackgroundResource(R.drawable.round_able_btn);
                    none_text_check = true;
                }else {
                    apply_btn.setBackgroundResource(R.drawable.round_unable_btn);
                    none_text_check = false;
                }
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(Write_Post_Activity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        RecyclerView listView = (RecyclerView) findViewById(R.id.list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Write_Post_Activity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // horizonta, vertical 옵션에 따라 가로/세로 list
        listView.setHasFixedSize(true);
        listView.setLayoutManager(layoutManager);

        adapter = new ImageSelectAdapter(Write_Post_Activity.this,R.layout.image_select_item);
        listView.setAdapter(adapter);
    }

    public void onClick_apply(View v){
        if(none_text_check){
            Toast.makeText(this, "입력 내용:"+text_input.getText().toString(), Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(this, "입력된 내용이 없어요!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick_picture(View v){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Write_Post_Activity.this,R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pic_select_bottom_sheet_layout,(LinearLayout)findViewById(R.id.container_bottom_sheet));

        bottomSheetView.findViewById(R.id.camera_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetView.findViewById(R.id.pic_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setActivityTitle("사진 편집")
                        .setActivityMenuIconColor(Color.BLACK)
                        .setCropMenuCropButtonTitle("완료")
                        .setMultiTouchEnabled(true)
                        .setBorderLineColor(Color.WHITE)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Write_Post_Activity.this);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK) {
                        File file = new File(mCurrentPhotoPath);
                        Bitmap bitmap;
                        if (Build.VERSION.SDK_INT >= 29) {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                            try { bitmap = ImageDecoder.decodeBitmap(source);
                                if (bitmap != null) {
                                    adapter.addItem(bitmap);
                                }
                            } catch (IOException e) { e.printStackTrace();
                            }
                        } else {
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                                if (bitmap != null) {
                                    adapter.addItem(bitmap);
                                }
                            } catch (IOException e) { e.printStackTrace();
                            }
                        }
                    } break;
                }

                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: {
                    CropImage.ActivityResult result = CropImage.getActivityResult(intent);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();
                        Log.d("IMAGE_PATH_PROFILE","uri 경로: "+resultUri);

                        try {

                            //이미지 데이터를 비트맵으로 받아옴
                            Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);

                            adapter.addItem(image_bitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //imageView.setImageURI(resultUri);

                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                        Log.d("Saeryen", "upload_file: "+error);
                    }
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {//카메라 실행 인텐트
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.selyen.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    public void onClick_back(View v){
        finish();
    }
    public void onBackPressed() {
        finish();
    }
}
