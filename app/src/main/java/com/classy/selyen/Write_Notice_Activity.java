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
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.service.autofill.UserData;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Write_Notice_Activity extends AppCompatActivity {

    final private static String TAG = "NOTICE_ACTIVITY_TAG";

    ConstraintLayout main_background_Layout, apply_btn;
    InputMethodManager imm;
    EditText text_input;
    TextView date_text;
    ImageView calendar_btn;

    int year_v = 0;
    int month_v = 0;
    int day_v = 0;
    boolean none_text_check = false;

    DatePicker datePicker;

    //카메라 관련
    final static int TAKE_PICTURE = 1;
    String mCurrentPhotoPath;
    final static int REQUEST_TAKE_PHOTO = 1;
    ImageSelectAdapter adapter;
    RecyclerView listView;

    SharedPreferences UserData;
    String content_type = "Notice";
    String deadline_state = "false";
    String deadline_time = "";

    /////////////리턴 수신용
    String sign_check_return = "";
    String post_index_return = "";
    String err_reason_return = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_notice);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        main_background_Layout = (ConstraintLayout)findViewById(R.id.main_background_Layout);
        text_input = (EditText)findViewById(R.id.text_input);
        date_text = (TextView) findViewById(R.id.date_text);
        calendar_btn = (ImageView)findViewById(R.id.calendar_btn);
        apply_btn = (ConstraintLayout)findViewById(R.id.apply_btn);

        main_background_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(text_input.getWindowToken(), 0);
            }
        });

        UserData = getSharedPreferences("user_data",MODE_PRIVATE);

        Calendar cal = Calendar.getInstance();

        year_v = cal.get(Calendar.YEAR);
        month_v = cal.get(Calendar.MONTH);
        day_v = cal.get(Calendar.DAY_OF_MONTH);

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
                ActivityCompat.requestPermissions(Write_Notice_Activity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        listView = (RecyclerView) findViewById(R.id.list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Write_Notice_Activity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // horizonta, vertical 옵션에 따라 가로/세로 list
        listView.setHasFixedSize(true);
        listView.setLayoutManager(layoutManager);

        adapter = new ImageSelectAdapter(Write_Notice_Activity.this,R.layout.image_select_item);
        listView.setAdapter(adapter);

    }

    public void onClick_apply(View v){
        if(none_text_check){
            ContentDataToServer task = new ContentDataToServer();
            task.execute();
        }else{
            Toast.makeText(this, "입력된 내용이 없어요!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick_picture(View v){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Write_Notice_Activity.this,R.style.BottomSheetDialogTheme);
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
                        .start(Write_Notice_Activity.this);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void onClick_calendar(View v){
        if(deadline_state.equals("false")){
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Write_Notice_Activity.this,R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.notice_date_set_bottomsheet_layout,(LinearLayout)findViewById(R.id.container_bottom_sheet));
            bottomSheetDialog.setCanceledOnTouchOutside(true);

            TextView date_tv = bottomSheetView.findViewById(R.id.date_text);

            datePicker = bottomSheetView.findViewById(R.id.dataPicker);

            datePicker.setMinDate(System.currentTimeMillis());

            date_tv.setText(year_v + "년 " + (month_v+1) + "월 " + day_v + "일   까지 공지를 상단에 노출합니다.");
            SpannableStringBuilder ssb = new SpannableStringBuilder(date_tv.getText().toString());
            ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#3065F3")), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            date_tv.setText(ssb);

            datePicker.init(year_v, month_v, day_v,
                    new DatePicker.OnDateChangedListener() {
                        //값이 바뀔때마다 텍스트뷰의 값을 바꿔준다.
                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // TODO Auto-generated method stub
                            //monthOfYear는 0값이 1월을 뜻하므로 1을 더해줌 나머지는 같다.

                            year_v = year; month_v = monthOfYear; day_v = dayOfMonth;

                            date_tv.setText(year_v + "년 " + (month_v+1) + "월 " + day_v + "일   까지 공지를 상단에 노출합니다.");
                            SpannableStringBuilder ssb = new SpannableStringBuilder(date_tv.getText().toString());
                            ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#3065F3")), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            date_tv.setText(ssb);
                        }
                    });

            bottomSheetView.findViewById(R.id.set_date_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    date_text.setText(year_v + "." + (month_v+1) + "." + day_v);
                    deadline_state = "true";
                    if((month_v+1)<10){
                        deadline_time = year_v+"0"+(month_v+1)+day_v+"";
                    }else{
                        deadline_time = year_v+(month_v+1)+day_v+"";
                    }
                    calendar_btn.setImageResource(R.drawable.ic_calendar_blue);
                    bottomSheetDialog.dismiss();
                }
            });

            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        }else{
            date_text.setText("");
            deadline_state = "false";
            deadline_time = "";
            calendar_btn.setImageResource(R.drawable.ic_calendar);
        }
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
                                    listView.setVisibility(View.VISIBLE);
                                    adapter.addItem(bitmap);
                                }
                            } catch (IOException e) { e.printStackTrace();
                            }
                        } else {
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                                if (bitmap != null) {
                                    listView.setVisibility(View.VISIBLE);
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

                            listView.setVisibility(View.VISIBLE);
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
        } catch (Exception error) { error.printStackTrace();
            Toast.makeText(this, "이미지 가져오기에서 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
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

        File file_path = new File(storage,fileName);
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
    //로그인 시에 서버에 로그인 정보 넘겨주고 리턴값 받음
    class ContentDataToServer extends AsyncTask<String, Void, String> {

        CustomAnimationLoadingDialog customAnimationLoadingDialog = new CustomAnimationLoadingDialog(Write_Notice_Activity.this);

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
                Toast.makeText(Write_Notice_Activity.this, "등록 완료", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(Write_Notice_Activity.this, "글 등록에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
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


                URL url = new URL("서버주소/php");


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



                // 텍스트 데이터들
                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"user_ph\"\r\n\r\n" + UserData.getString("hp_num", ""));
                wr.writeBytes("\r\n--" + boundary + "\r\n");

                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"Content_Type\"\r\n\r\n" + content_type);
                wr.writeBytes("\r\n--" + boundary + "\r\n");

                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"Main_Content\"\r\n\r\n" + text_input.getText().toString());
                wr.writeBytes("\r\n--" + boundary + "\r\n");

                if(deadline_state.equals("true")){
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"Is_Deadline\"\r\n\r\n" + deadline_state);
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"Deadline_Time\"\r\n\r\n" + deadline_time);
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                }


                ArrayList<Bitmap> bitmaps = adapter.getList();

                // PHP 에서 반복문을 사용하기 위하여 이미지 갯수를 센다.
                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"Image_Count\"\r\n\r\n" + bitmaps.size());
                wr.writeBytes("\r\n--" + boundary + "\r\n");




                // 파일의 존재 유무 확인 후 ( 파일이 없는 경우  그냥 지나간다 )
                // 반복문으로 파일을 보낸다.
                if( bitmaps.size() > 0){

                    for ( int i = 0 ; i < bitmaps.size(); i++){
                        String a = String.valueOf(i+1);

                        String path = saveBitmaptoJpeg(bitmaps.get(i));//이미지 캐시 파일 생성

                        File sourceFile = new File(path);
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);

                        //php단에서 $_FILES['uploaded_file'] 에  아래의  filename=""+ imageArray.get(i) 이들어간다
                        // 여러개를 보낼때 주의 사항은  $_FILES['uploaded_file']의  'uploaded_file' 는 키값처럼들어가는데
                        // 중복되는 경우 마지막 데이터만 전송됨으로  아래에서는 반복문의 i 값을 string으로 변환하여 구분을 주었다.
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
