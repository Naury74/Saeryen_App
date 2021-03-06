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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.service.autofill.UserData;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Write_Post_Activity extends AppCompatActivity {
    final private static String TAG = "POST_ACTIVITY_TAG";

    ConstraintLayout main_background_Layout, apply_btn;
    InputMethodManager imm;
    EditText text_input;
    TextView date_text;

    boolean none_text_check = false;

    //????????? ??????
    final static int TAKE_PICTURE = 1;
    String mCurrentPhotoPath;
    final static int REQUEST_TAKE_PHOTO = 1;
    ImageSelectAdapter adapter;

    SharedPreferences UserData;
    String content_type = "Posted";
    String deadline_state = "false";
    String deadline_time = " ";

    /////////////?????? ?????????
    String sign_check_return = "";
    String post_index_return = "";
    String err_reason_return = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        main_background_Layout = (ConstraintLayout)findViewById(R.id.main_background_Layout);
        text_input = (EditText)findViewById(R.id.text_input);
        apply_btn = (ConstraintLayout)findViewById(R.id.apply_btn);

        UserData = getSharedPreferences("user_data",MODE_PRIVATE);

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
                Log.d(TAG, "?????? ?????? ??????");
            } else {
                Log.d(TAG, "?????? ?????? ??????");
                ActivityCompat.requestPermissions(Write_Post_Activity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        RecyclerView listView = (RecyclerView) findViewById(R.id.list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Write_Post_Activity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // horizonta, vertical ????????? ?????? ??????/?????? list
        listView.setHasFixedSize(true);
        listView.setLayoutManager(layoutManager);

        adapter = new ImageSelectAdapter(Write_Post_Activity.this,R.layout.image_select_item);
        listView.setAdapter(adapter);
    }

    public void onClick_apply(View v){
        if(none_text_check){
            ContentDataToServer task = new ContentDataToServer();
            task.execute();
        }else{
            Toast.makeText(this, "????????? ????????? ?????????!", Toast.LENGTH_SHORT).show();
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
                        .setActivityTitle("?????? ??????")
                        .setActivityMenuIconColor(Color.BLACK)
                        .setCropMenuCropButtonTitle("??????")
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
                        Log.d("IMAGE_PATH_PROFILE","uri ??????: "+resultUri);

                        try {

                            //????????? ???????????? ??????????????? ?????????
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

    private void dispatchTakePictureIntent() {//????????? ?????? ?????????
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.classy.selyen.fileprovider", photoFile);
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

    public String saveBitmaptoJpeg(Bitmap bitmap){
        File storage = getCacheDir();
        String fileName = "cache_img.jpg";

        File file_path = new File(storage,fileName);;
        try{

            FileOutputStream out = new FileOutputStream(file_path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
        return getCacheDir()+"/"+fileName;
    }

    String result_check_json;
    //????????? ?????? ????????? ????????? ?????? ???????????? ????????? ??????
    class ContentDataToServer extends AsyncTask<String, Void, String> {

        CustomAnimationLoadingDialog customAnimationLoadingDialog = new CustomAnimationLoadingDialog(Write_Post_Activity.this);

        @Override
        protected void onPreExecute() {
            customAnimationLoadingDialog.show();
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String result) {
            customAnimationLoadingDialog.dismiss();
            super.onPostExecute(result);
            Log.d(TAG, "Data Post - App : " + result);

            if(sign_check_return.equals("SUCCESS")){
                Toast.makeText(Write_Post_Activity.this, "?????? ??????", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(Write_Post_Activity.this, "??? ????????? ??????????????????. ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
            }

        }


        @Override
        protected String doInBackground(String... params) {

            try {

                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;


                URL url = new URL("http://ec2-13-124-191-53.ap-northeast-2.compute.amazonaws.com/seryeon_Chating.php");


                // Open a HTTP  connection to  the URL

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());



                // ????????? ????????????
                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"ph_num\"\r\n\r\n" + UserData.getString("ph_num", ""));
                wr.writeBytes("\r\n--" + boundary + "\r\n");

                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"Content_Type\"\r\n\r\n" + content_type);
                wr.writeBytes("\r\n--" + boundary + "\r\n");

                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"Main_Content\"\r\n\r\n" + text_input.getText().toString());
                wr.writeBytes("\r\n--" + boundary + "\r\n");

                ArrayList<Bitmap> bitmaps = adapter.getList();

                // PHP ?????? ???????????? ???????????? ????????? ????????? ????????? ??????.
                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"Image_Count\"\r\n\r\n" + bitmaps.size());
                wr.writeBytes("\r\n--" + boundary + "\r\n");

                if(deadline_state.equals("true")){
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"Is_Deadline\"\r\n\r\n" + "FALSE");
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"Deadline_Time\"\r\n\r\n" + deadline_time);
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                }


                // ????????? ?????? ?????? ?????? ??? ( ????????? ?????? ??????  ?????? ???????????? )
                // ??????????????? ????????? ?????????.
                if( bitmaps.size() > 0){

                    for ( int i = 0 ; i < bitmaps.size(); i++){
                        String a = String.valueOf(i+1);

                        String path = saveBitmaptoJpeg(bitmaps.get(i));//????????? ?????? ?????? ??????

                        File sourceFile = new File(path);
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);

                        //php????????? $_FILES['uploaded_file'] ???  ?????????  filename=""+ imageArray.get(i) ???????????????
                        // ???????????? ????????? ?????? ?????????  $_FILES['uploaded_file']???  'uploaded_file' ??? ???????????????????????????
                        // ???????????? ?????? ????????? ???????????? ???????????????  ??????????????? ???????????? i ?????? string?????? ???????????? ????????? ?????????.
                        dos = new DataOutputStream(conn.getOutputStream());
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"img_"+a+"\";filename=\""+ path + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);


                        // create a buffer of  maximum size
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);


                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        }

                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    }
                }




                int responseStatusCode = conn.getResponseCode();//??????
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream_return;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {//????????????
                    inputStream_return = conn.getInputStream();
                }
                else{
                    inputStream_return = conn.getErrorStream();//??????
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream_return, "UTF-8");//????????? ??????
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                System.out.println("json ??????: " + sb.toString());
                bufferedReader.close();

                result_check_json = sb.toString();//????????? ????????? ??????????????? ??????
                ReturnCheck();
                return sb.toString();


            } catch (ConnectException e) {
                Log.e(TAG, "ConnectException");
                e.printStackTrace();


            } catch (Exception e){
                e.printStackTrace();
            }

            return result_check_json;
        }

    }

    protected void ReturnCheck(){//?????? ????????? ??????
        try{
            JSONObject jsonObject = new JSONObject(result_check_json);

            sign_check_return = jsonObject.getString("sign_check");
            if(sign_check_return.equals("SUCCESS")){
                post_index_return = jsonObject.getString("Content_idx");
            }else{
                err_reason_return = jsonObject.getString("err_reason");
            }

            Log.d("Saeryen","Json Return sign_check_return : "+ sign_check_return);
            Log.d("Saeryen","Json Return post_index_return : "+ post_index_return);
            Log.d("Saeryen","Json Return err_reason_return : "+ err_reason_return);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
