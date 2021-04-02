package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;
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
import java.util.ArrayList;

public class UserProfileEditActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 1002;

    private Boolean isPermission = true;

    TextView myinfo_name;
    TextView myinfo_ph_num;
    TextView myinfo_email;
    TextView my_location_title;
    ImageView imageView;
    ConstraintLayout picture_change_Layout;

    SharedPreferences UserData;

    private static final String TAG = "Saeryen_Myinfo";

    private static String UploadImgPath = "";

    String absolutePath = "";

    int maxBufferSize = 1 * 1024 * 1024;

    String ch_img_check = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);

        myinfo_name = (TextView)findViewById(R.id.myinfo_name);
        myinfo_ph_num = (TextView)findViewById(R.id.myinfo_ph_num);
        myinfo_email = (TextView)findViewById(R.id.myinfo_email);
        my_location_title = (TextView)findViewById(R.id.my_location_title);
        picture_change_Layout = (ConstraintLayout)findViewById(R.id.picture_change_Layout);
        imageView = findViewById(R.id.myinfo_Image);

        picture_change_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(UserProfileEditActivity.this,R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.profile_img_edit_bottom_sheet,(LinearLayout)findViewById(R.id.container_bottom_sheet));

                bottomSheetView.findViewById(R.id.camera_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isPermission){

                            CropImage.activity()
                                    .setAspectRatio(1,1)
                                    .setFixAspectRatio(true)
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .start(UserProfileEditActivity.this);

                        } else{
                            Toast.makeText(view.getContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
                        }
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.remove_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        UserData = getSharedPreferences("user_data",MODE_PRIVATE);

        Log.d(TAG,"app User Info hp_num : "+ UserData.getString("hp_num", ""));
        Log.d(TAG,"app User Info user_name : "+ UserData.getString("user_name", ""));
        Log.d(TAG,"app User Info user_PN : "+ UserData.getString("user_PN", ""));
        Log.d(TAG,"app User Info mail : "+ UserData.getString("mail", ""));
        Log.d(TAG,"app User Info addr : "+ UserData.getString("addr", ""));
        Log.d(TAG,"app User Info actual_resid : "+ UserData.getString("actual_resid", ""));
        Log.d(TAG,"app User Info actual_resid_date : "+ UserData.getString("actual_resid_date", ""));
        Log.d(TAG,"app User Info push_msg : "+ UserData.getString("push_msg", ""));

        refresh_user_info();

        tedPermission();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("UserProfile_change","UserProfile_change_onActivityResult");

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Log.d("IMAGE_PATH_PROFILE","uri 경로: "+resultUri);

                try {

                    absolutePath = resultUri.getPath();
                    Log.d("IMAGE_PATH_PROFILE","이미지 절대경로: "+absolutePath);

                    //이미지 데이터를 비트맵으로 받아옴
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);

                    int height = image_bitmap.getHeight();
                    int width = image_bitmap.getWidth();

                    Bitmap src = BitmapFactory.decodeFile(absolutePath);
                    Bitmap resized = Bitmap.createScaledBitmap( src, width/4, height/4, true );

                    saveBitmaptoJpeg(resized, "Saeryen", UserData.getString("hp_num", ""));

                    Request_apply_profile();

                    imageView.setImageBitmap(resized);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //imageView.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("Saeryen", "upload_file: "+error);
            }
        }

        refresh_user_info();
    }

    public void refresh_user_info(){
        myinfo_name.setText(UserData.getString("user_name", ""));
        myinfo_ph_num.setText(UserData.getString("hp_num", "")+"  #"+UserData.getString("user_PN", ""));
        myinfo_email.setText(UserData.getString("mail", ""));
        if(!UserData.getString("addr", "").equals("null")){
            my_location_title.setText(UserData.getString("addr", ""));
        }

        Picasso.get()
                .load("http://ec2-3-36-108-8.ap-northeast-2.compute.amazonaws.com"+UserData.getString("user_img", ""))
                .error(R.drawable.ic_default_user2)
                .into(imageView);
    }

    public void onClick_Zoom_Picture(View v){

        if(imageView.getDrawable()!=null){

            Intent intent = new Intent(this, PictureZoomUrlActivity.class);
            intent.putExtra("image","http://ec2-3-36-108-8.ap-northeast-2.compute.amazonaws.com"+UserData.getString("user_img", ""));
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }

    public static void saveBitmaptoJpeg(Bitmap bitmap,String folder, String name){
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        // Get Absolute Path in External Sdcard
        String foler_name = "/"+folder+"/";
        String file_name = name+".jpg";
        String string_path = ex_storage+foler_name;

        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path+file_name);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            UploadImgPath = string_path+file_name;
            out.close();

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }

    public void onClick_location_adress_change(View v){
        Intent intent = new Intent(this, MapLocationApplyActivity.class);
        startActivityForResult(intent,1001);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void onClick_NicName_change(View v){
        Intent intent = new Intent(this, ChangeNicnameActivity.class);
        startActivityForResult(intent,1001);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void onClick_pw_change(View v){
        Intent intent = new Intent(this, FindPasswordActivity.class);
        intent.putExtra("call_type",1);
        startActivityForResult(intent,1001);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void onClick_email_change(View v){
        Intent intent = new Intent(this, FindPasswordActivity.class);
        intent.putExtra("call_type",2);
        startActivityForResult(intent,1001);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void onClick_back(View v){
        Intent intent = new Intent();
        setResult(101, intent);
        finish();
    }

    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                isPermission = true;
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
                isPermission = false;
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(101, intent);
        finish();
    }

    public void Request_apply_profile(){
        InsertData task = new InsertData();
        task.execute();
    }

    String result_check_json;
    //로그인 시에 서버에 로그인 정보 넘겨주고 리턴값 받음
    class InsertData extends AsyncTask<String, Void, String> {

        CustomAnimationLoadingDialog customAnimationLoadingDialog = new CustomAnimationLoadingDialog(UserProfileEditActivity.this);

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

            if(ch_img_check.equals("SUCCESS")){

            }else{
                //Snackbar.make(main_background_Layout, err_return, Snackbar.LENGTH_LONG).show();
                //Toast.makeText(LogIn_Activity.this, "입력하신 정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
            }

        }


        @Override
        protected String doInBackground(String... params) {

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int maxBufferSize = 1 * 1024 * 1024;
            File upload_file = new File(UploadImgPath);
            int bufferSize;
            final int MAX_READ_TIME = 10000;
            final int MAX_CONNECT_TIME = 15000;

            System.out.println("UploadImgPath: "+UploadImgPath);

            JSONObject result = null;
            try{

                URL url = new URL("http://ec2-3-36-108-8.ap-northeast-2.compute.amazonaws.com/seryeon_Upload_Image.php/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(MAX_CONNECT_TIME);
                conn.setReadTimeout(MAX_READ_TIME);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("cache-control", "no-cache");
                conn.setRequestProperty("cache-length", "length");
                conn.setRequestProperty("image", UploadImgPath);
                conn.setRequestProperty("user-agent", "test");
                conn.connect();

                FileInputStream mFileInputStream = new FileInputStream(UploadImgPath);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                //text 전송
                dos.writeBytes("\r\n--" + boundary + "\r\n");
                dos.writeBytes("Content-Disposition: form-data; name=\"ph_num\"\r\n\r\n" + UserData.getString("hp_num", ""));
                //Image 전송
                dos.writeBytes("\r\n--" + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"user_Image\";filename=\"" + UploadImgPath + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                Log.d(TAG,"업로드 이미지 패스: "+UploadImgPath);

                int bytesAvailable = mFileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];
                int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0)
                {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = mFileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                    dos.flush();
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);



                int responseStatusCode = conn.getResponseCode();//응답
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream_return;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {//정상응답
                    inputStream_return = conn.getInputStream();
                }
                else{
                    inputStream_return = conn.getErrorStream();//에러
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream_return, "UTF-8");//수신값 저장
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                System.out.println("json 리턴: " + sb.toString());
                bufferedReader.close();

                result_check_json = sb.toString();//수신된 데이터 스트링으로 변환
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

    protected void ReturnCheck(){//리턴 데이터 확인
        try{
            JSONObject jsonObject = new JSONObject(result_check_json);

            ch_img_check = jsonObject.getString("sign_check");
            if(ch_img_check.equals("SUCCESS")){

            }else{
                //err_return = jsonObject.getString("err_reason");
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}
