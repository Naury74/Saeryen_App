package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class JoinBlockKakaoInviteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Join_Block";

    String server_adress = "http://ec2-13-124-191-53.ap-northeast-2.compute.amazonaws.com/seryeon_Find_Group.php";//블록 체크 데이터 정보 전송 경로

    GoogleMap mMap;
    private Marker currentMarker = null;

    String invite_addr;

    Intent intent;

    String return_check = "";
    String address_return = "";
    String addr_detail_return = "";
    String block_code_return = "";
    String dump_code_return = "";
    String err_return = "";
    String ph_num_return = "";

    TextView addr_text;
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_block_kakao_invite);

        intent = getIntent();
        invite_addr = intent.getStringExtra("addr");

        addr_text = (TextView)findViewById(R.id.addr_text);
        input = (EditText)findViewById(R.id.input);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Block_Check task = new Block_Check();
        task.execute(server_adress,invite_addr);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //지도타입 - 일반
        setDefaultLocation();

    }

    public void setDefaultLocation() {

        Geocoder geocoder = new Geocoder(this);
        List<Address> default_addr = null;


        if(intent.hasExtra("addr")){//카카오 링크로 실행될 경우
            try {
                default_addr = geocoder.getFromLocationName(invite_addr,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LatLng DEFAULT_LOCATION;
        String markerTitle;
        String markerSnippet;

        if(default_addr!=null){
            DEFAULT_LOCATION = new LatLng(default_addr.get(0).getLatitude(), default_addr.get(0).getLongitude());
            markerTitle = invite_addr;
            markerSnippet = "블록 주소";
        }else {
            //디폴트 위치, Seoul
            DEFAULT_LOCATION = new LatLng(37.56, 126.97);
            markerTitle = "위치정보 가져올 수 없음";
            markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_marker_white);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 17);
        mMap.moveCamera(cameraUpdate);

    }

    public void onClick_join(View v){
        if(input.getText().toString().equals("")){
            Toast.makeText(this, "인증 코드를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }else if(input.getText().toString().equals(dump_code_return)){
            Toast.makeText(this, "인증성공, 가입중 입니다.", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "블록 인증코드가 일치하지 않습니다. 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick_back(View v){
        finish();
    }
    public void onBackPressed() {
        finish();
    }

    String result_check_json;
    //블록 체크용
    class Block_Check extends AsyncTask<String, Void, String> {

        CustomAnimationLoadingDialog customAnimationLoadingDialog = new CustomAnimationLoadingDialog(JoinBlockKakaoInviteActivity.this);

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

            if(return_check.equals("Exist")){
                addr_text.setText(address_return+"\n"+addr_detail_return);
            }else if(return_check.equals("Not_Found")){
                Toast.makeText(JoinBlockKakaoInviteActivity.this, "잘못된 블록 정보 입니다.", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(JoinBlockKakaoInviteActivity.this, "블록정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String address = (String)params[1];//지오코드 주소

            String serverURL = (String)params[0];//서버주소 할당
            String postParameters = "addr_position=" + address;//전송할 파라미터,값
            Log.d(TAG,"postParameters : "+postParameters);

            try {

                URL url = new URL(serverURL);//주소입력
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);//5초내 무 응답시 예외처리
                httpURLConnection.setConnectTimeout(5000);//5초내 연결 불가시 예외처리
                httpURLConnection.setRequestMethod("POST");//post방식 요청
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));//전송할 데이터 할당
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();//응답
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {//정상응답
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();//에러
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");//수신값 저장
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


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    protected void ReturnCheck(){//리턴 데이터 확인
        try{
            JSONObject jsonObject = new JSONObject(result_check_json);

            return_check = jsonObject.getString("sign_check");
            if(return_check.equals("Exist")){
                address_return = jsonObject.getString("address");
                addr_detail_return = jsonObject.getString("addr_detail");
                block_code_return = jsonObject.getString("block_code");
                dump_code_return = block_code_return.substring(0, 6);
            }else if(return_check.equals("Not_Found")){
                address_return = jsonObject.getString("address");
                addr_detail_return = "";
                block_code_return = "";
            }else {
                err_return = jsonObject.getString("err_reason");
                address_return = "";
                addr_detail_return = "";
                block_code_return = "";
            }

            Log.d(TAG,"Json Return return_check : "+ return_check);
            Log.d(TAG,"Json Return address_return : "+ address_return);
            Log.d(TAG,"Json Return addr_detail_return : "+ addr_detail_return);
            Log.d(TAG,"Json Return block_code_return : "+ block_code_return);
            Log.d(TAG,"Json Return dump_code_return : "+ dump_code_return);
            Log.d(TAG,"Json Return err_return : "+ err_return);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
