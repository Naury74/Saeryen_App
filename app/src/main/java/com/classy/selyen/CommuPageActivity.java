package com.classy.selyen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.util.List;

public class CommuPageActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SharedPreferences UserData;
    MarkerOptions markerOptions;
    LatLng latLng;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commu_page);

        UserData = getSharedPreferences("user_data",MODE_PRIVATE);

        markerOptions = new MarkerOptions();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if(!UserData.getString("addr", "").equals("empty")){
            String searchText = UserData.getString("addr", "");
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocationName(searchText, 3);
                if (addresses != null && !addresses.equals(" ")) {
                    Address address = addresses.get(0);

                    latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    markerOptions.position(latLng);
                    markerOptions.title(searchText);
                    markerOptions.snippet("세련");

                    mapFragment.getMapAsync(this);

                }else {
                    Toast.makeText(this, "위치정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            //Toast.makeText(this, "등록된 주소가 없습니다.", Toast.LENGTH_SHORT).show();
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.connect_map_activity_bottom_sheet,(LinearLayout)findViewById(R.id.container_edit_addr_bottom_sheet));

            bottomSheetView.findViewById(R.id.edit_addr__btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CommuPageActivity.this, MapLocationApplyActivity.class);
                    startActivityForResult(intent,1001);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    finish();
                }
            });
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.moveCamera(cameraUpdate);
    }

}
