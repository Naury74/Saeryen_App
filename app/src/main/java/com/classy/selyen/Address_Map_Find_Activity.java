package com.classy.selyen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import java.util.Locale;

public class Address_Map_Find_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;

    String server_adress = "http://ec2-13-124-191-53.ap-northeast-2.compute.amazonaws.com/seryeon_Find_Group.php";//?????? ?????? ????????? ?????? ?????? ??????
    String add_server_adress = "http://ec2-13-124-191-53.ap-northeast-2.compute.amazonaws.com/seryeon_Creating_Group.php";//?????? ?????? ????????? ?????? ?????? ??????

    private static final String TAG = "map_block_select";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 300000;  // 1???
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5???

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    LatLng currentPosition;
    int state_check = 0;


    // onRequestPermissionsResult?????? ????????? ???????????? ActivityCompat.requestPermissions??? ????????? ????????? ????????? ???????????? ?????? ???????????????.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // ?????? ???????????? ?????? ????????? ???????????? ???????????????.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // ?????? ?????????

    String return_check = "";
    String address_return = "";
    String addr_detail_return = "";
    String block_code_return = "";
    String dump_code_return = "";
    String err_return = "";
    String ph_num_return = "";

    String ph_num = "";

    ConstraintLayout main_background_Layout;
    TextView btn_next;
    TextView address_text;
    ImageView marker_view;
    TextView apply_address_text;
    TextView btn_add_block;
    TextView title;
    TextView text1;
    ConstraintLayout address_text_layout;
    ConstraintLayout map_cover;
    ConstraintLayout address_sub_text_layout;
    EditText address_sub_text;

    InputMethodManager imm;

    SharedPreferences UserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_map_find);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        main_background_Layout = (ConstraintLayout)findViewById(R.id.main_background_Layout);
        btn_next = (TextView)findViewById(R.id.btn_next);
        address_text = (TextView)findViewById(R.id.address_text);
        marker_view = (ImageView)findViewById(R.id.marker_view);
        apply_address_text = (TextView)findViewById(R.id.apply_address_text);
        map_cover = (ConstraintLayout)findViewById(R.id.map_cover);
        address_text_layout = (ConstraintLayout)findViewById(R.id.address_text_layout);
        address_sub_text_layout = (ConstraintLayout)findViewById(R.id.address_sub_text_layout);
        btn_add_block = (TextView)findViewById(R.id.btn_add_block);
        address_sub_text = (EditText)findViewById(R.id.address_sub_text);
        title = (TextView)findViewById(R.id.title);
        text1 = (TextView)findViewById(R.id.text1);

        imm = (InputMethodManager)this.getSystemService(INPUT_METHOD_SERVICE);

        UserData = getSharedPreferences("user_data", Context.MODE_PRIVATE);

        ph_num = UserData.getString("hp_num", "");

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.gmap);
        mapFragment.getMapAsync(this);

        address_sub_text.setImeOptions(EditorInfo.IME_ACTION_DONE);

        address_sub_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().equals("")){
                    btn_add_block.setBackgroundResource(R.drawable.round_btn_backgound_d6d6d6);  // ?????? ????????? ??????
                    btn_add_block.setClickable(false);
                    btn_add_block.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }
                else{
                    btn_add_block.setBackgroundResource(R.drawable.ripple_custom_login_btn);  // ?????? ????????? ??????
                    btn_add_block.setClickable(true);
                    btn_add_block.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            onClick_add_block();
                        }
                    });
                }
            }
        });

    }

    public void onClick_next(View v){
        Block_Check task = new Block_Check();
        task.execute(server_adress,address_text.getText().toString());
    }

    public void onClick_add_block(){
        Block_ADD task = new Block_ADD();
        task.execute(add_server_adress,address_text.getText().toString(),address_sub_text.getText().toString(),ph_num);
    }

    public void onClick_back(View v){
        finish();
    }
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        setDefaultLocation();

        //????????? ????????? ??????
        // 1. ?????? ???????????? ????????? ????????? ???????????????.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. ?????? ???????????? ????????? ?????????
            // ( ??????????????? 6.0 ?????? ????????? ????????? ???????????? ???????????? ????????? ?????? ????????? ?????? ???????????????.)


            startLocationUpdates(); // 3. ?????? ???????????? ??????


        }else {  //2. ????????? ????????? ????????? ?????? ????????? ????????? ????????? ???????????????. 2?????? ??????(3-1, 4-1)??? ????????????.

            // 3-1. ???????????? ????????? ????????? ??? ?????? ?????? ????????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. ????????? ???????????? ?????? ?????????????????? ???????????? ????????? ????????? ???????????? ????????? ????????????.
                Snackbar.make(main_background_Layout, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.",
                        Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. ??????????????? ????????? ????????? ?????????. ?????? ????????? onRequestPermissionResult?????? ???????????????.
                        ActivityCompat.requestPermissions( Address_Map_Find_Activity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ????????? ????????? ?????? ?????????.
                // ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                //Log.d(TAG, "????????? move");
                btn_next.setBackgroundResource(R.drawable.round_btn_backgound_d6d6d6);
                btn_next.setClickable(false);
                address_text.setText("?????? ?????????...");
                marker_view.setImageResource(R.drawable.ic_marker_gray);
            }
        });

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //Log.d(TAG, "????????? idle");
                LatLng centerLatLng = map.getProjection().getVisibleRegion().latLngBounds.getCenter();
                String center_position = getCurrentAddress(centerLatLng);
                center_position = center_position.substring(5);
                address_text.setText(center_position);
                apply_address_text.setText(center_position);//?????? ????????????
                btn_next.setBackgroundResource(R.drawable.ripple_custom_login_btn);
                marker_view.setImageResource(R.drawable.ic_marker_blue);
                btn_next.setClickable(true);
            }
        });

    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng,18);
        map.moveCamera(cameraUpdate);

        address_text.setText(markerTitle);

        if(state_check==1){
            btn_next.setBackgroundResource(R.drawable.ripple_custom_login_btn);
            btn_next.setClickable(true);
        }else {
            btn_next.setBackgroundResource(R.drawable.round_btn_backgound_d6d6d6);
            btn_next.setClickable(false);
        }

    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                markerTitle = markerTitle.substring(5);
                String markerSnippet = "??????:" + String.valueOf(location.getLatitude())
                        + " ??????:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);

                setCurrentLocation(location, markerTitle, markerSnippet);
            }


        }

    };

    public String getCurrentAddress(LatLng latlng) {

        //????????????... GPS??? ????????? ??????
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //???????????? ??????
            Toast.makeText(this, "???????????? ????????? ????????????", Toast.LENGTH_LONG).show();
            state_check = 0;
            return "     ???????????? ????????? ????????????. ??????????????? ??????????????????.";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "????????? GPS ??????", Toast.LENGTH_LONG).show();
            state_check = 0;
            return "     ????????? GPS ???????????????.";

        }


        if (addresses == null || addresses.size() == 0) {
            //Toast.makeText(this, "?????? ?????????", Toast.LENGTH_LONG).show();
            state_check = 0;
            return "     ??????????????? ??????????????? ?????????";

        } else {
            Address address = addresses.get(0);
            state_check = 1;
            return address.getAddressLine(0).toString();
        }

    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : ????????? ???????????? ??????");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission()) {
                map.setMyLocationEnabled(false);
            }

        }

    }

    public void setDefaultLocation() {

        Marker currentMarker = null;
        final LatLng default_position = new LatLng(37.56, 126.97);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(default_position, 17);
        map.moveCamera(cameraUpdate);

    }

    //??????????????? ????????? ????????? ????????? ?????? ????????????
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }



    /*
     * ActivityCompat.requestPermissions??? ????????? ????????? ????????? ????????? ???????????? ??????????????????.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ??????????????????

            boolean check_result = true;


            // ?????? ???????????? ??????????????? ???????????????.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                // ???????????? ??????????????? ?????? ??????????????? ???????????????.
                startLocationUpdates();
            }
            else {
                // ????????? ???????????? ????????? ?????? ????????? ??? ?????? ????????? ??????????????? ?????? ???????????????.2 ?????? ????????? ????????????.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // ???????????? ????????? ????????? ???????????? ?????? ?????? ???????????? ????????? ???????????? ?????? ????????? ??? ????????????.
                    Snackbar.make(main_background_Layout, "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {


                    // "?????? ?????? ??????"??? ???????????? ???????????? ????????? ????????? ???????????? ??????(??? ??????)?????? ???????????? ???????????? ?????? ????????? ??? ????????????.
                    Snackbar.make(main_background_Layout, "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }

    //??????????????? GPS ???????????? ?????? ????????????
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Address_Map_Find_Activity.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n"
                + "?????? ????????? ???????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    String result_check_json;
    //?????? ?????????
    class Block_Check extends AsyncTask<String, Void, String> {

        CustomAnimationLoadingDialog customAnimationLoadingDialog = new CustomAnimationLoadingDialog(Address_Map_Find_Activity.this);

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

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Address_Map_Find_Activity.this,R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.matching_block_bottom_sheet,(LinearLayout)findViewById(R.id.container_bottom_sheet));
                bottomSheetDialog.setCanceledOnTouchOutside(false);

                TextView address_tv = bottomSheetView.findViewById(R.id.addr_text);
                address_tv.setText(addr_detail_return);

                bottomSheetView.findViewById(R.id.join_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Address_Map_Find_Activity.this, Select_Join_Method_Activity.class);
                        intent.putExtra("select_address", address_return);
                        intent.putExtra("addr_detail", addr_detail_return);
                        intent.putExtra("block_code", block_code_return);
                        intent.putExtra("request_code", dump_code_return);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        finish();
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

            }else if(return_check.equals("Not_Found")){
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Address_Map_Find_Activity.this,R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.not_found_block_bottom_sheet,(LinearLayout)findViewById(R.id.container_bottom_sheet));
                bottomSheetDialog.setCanceledOnTouchOutside(false);

                bottomSheetView.findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        map_cover.setVisibility(View.VISIBLE);
                        address_text_layout.setVisibility(View.GONE);
                        apply_address_text.setVisibility(View.VISIBLE);
                        address_sub_text_layout.setVisibility(View.VISIBLE);
                        btn_next.setVisibility(View.GONE);
                        btn_add_block.setVisibility(View.VISIBLE);
                        text1.setVisibility(View.VISIBLE);
                        title.setText("?????? ??????");
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }else{
                if(err_return.equals("")){
                    Snackbar.make(main_background_Layout, "?????? ????????? ??????????????????. ????????? ?????? ????????? ?????????.", Snackbar.LENGTH_LONG).show();
                }else {
                    Snackbar.make(main_background_Layout, err_return, Snackbar.LENGTH_LONG).show();
                }
                //Toast.makeText(LogIn_Activity.this, "???????????? ????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String address = (String)params[1];//???????????? ??????

            String serverURL = (String)params[0];//???????????? ??????
            String postParameters = "addr_position=" + address;//????????? ????????????,???
            Log.d(TAG,"postParameters : "+postParameters);

            try {

                URL url = new URL(serverURL);//????????????
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);//5?????? ??? ????????? ????????????
                httpURLConnection.setConnectTimeout(5000);//5?????? ?????? ????????? ????????????
                httpURLConnection.setRequestMethod("POST");//post?????? ??????
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));//????????? ????????? ??????
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();//??????
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {//????????????
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();//??????
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");//????????? ??????
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


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    protected void ReturnCheck(){//?????? ????????? ??????
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



    ////////////////////////////////////////////////////////////////////////////////////????????? ????????????
    String block_add_result_check_json;
    class Block_ADD extends AsyncTask<String, Void, String> {

        CustomAnimationLoadingDialog customAnimationLoadingDialog = new CustomAnimationLoadingDialog(Address_Map_Find_Activity.this);

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

            if(return_check.equals("SUCCESS")){

                SharedPreferences.Editor editor_user = UserData.edit();

                // ???????????????.put??????( ???????????? ??????, ???????????? ??? )
                // ???????????? ????????? ?????? ???????????? ????????????
                editor_user.putString("addr", address_return);
                editor_user.putString("addr_sub", addr_detail_return);
                editor_user.putString("block_code", block_code_return);
                editor_user.putString("actual_resid", "admin");

                // apply, commit ??? ????????? ????????? ????????? ???????????? ??????
                editor_user.apply();

                Intent intent = new Intent(Address_Map_Find_Activity.this, CompleteJoinActivity.class);
                intent.putExtra("select_address", address_return);
                intent.putExtra("addr_detail", addr_detail_return);
                intent.putExtra("block_code", block_code_return);
                intent.putExtra("actual_resid", "admin");
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();

            }else{
                if(err_return.equals("")){
                    Snackbar.make(main_background_Layout, "??????????????? ??????????????????. ????????? ?????? ????????? ?????????.", Snackbar.LENGTH_LONG).show();
                }else {
                    Snackbar.make(main_background_Layout, err_return, Snackbar.LENGTH_LONG).show();
                }
                //Toast.makeText(LogIn_Activity.this, "???????????? ????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String main_addr = (String)params[1];//???????????? ??????
            String sub_addr = (String)params[2];//?????? ??????
            String ph_num = (String)params[3];//????????? ??????

            String serverURL = (String)params[0];//???????????? ??????
            String postParameters = "main_addr=" + main_addr + "&sub_addr=" + sub_addr + "&ph_num=" + ph_num;;//????????? ????????????,???
            Log.d(TAG,"postParameters : "+postParameters);

            try {

                URL url = new URL(serverURL);//????????????
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);//5?????? ??? ????????? ????????????
                httpURLConnection.setConnectTimeout(5000);//5?????? ?????? ????????? ????????????
                httpURLConnection.setRequestMethod("POST");//post?????? ??????
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));//????????? ????????? ??????
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();//??????
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {//????????????
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();//??????
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");//????????? ??????
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                System.out.println("json ??????: " + sb.toString());
                bufferedReader.close();

                block_add_result_check_json = sb.toString();//????????? ????????? ??????????????? ??????
                ReturnCheck_block_add();
                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    protected void ReturnCheck_block_add(){//?????? ????????? ??????
        try{
            JSONObject jsonObject = new JSONObject(block_add_result_check_json);

            return_check = jsonObject.getString("sign_check");
            if(return_check.equals("SUCCESS")){
                address_return = jsonObject.getString("main_addr");
                addr_detail_return = jsonObject.getString("sub_addr");
                ph_num_return = jsonObject.getString("ph_num");
                block_code_return = jsonObject.getString("block_code");
            }else {
                err_return = jsonObject.getString("err_reason");
            }

            Log.d(TAG,"Json Return return_check : "+ return_check);
            Log.d(TAG,"Json Return address_return : "+ address_return);
            Log.d(TAG,"Json Return addr_detail_return : "+ addr_detail_return);
            Log.d(TAG,"Json Return block_code_return : "+ block_code_return);
            Log.d(TAG,"Json Return err_return : "+ err_return);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}
