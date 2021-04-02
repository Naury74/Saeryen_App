package com.classy.selyen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;

public class CustomScannerActivity extends AppCompatActivity implements
        DecoratedBarcodeView.TorchListener{

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ConstraintLayout switchFlashlightButton;
    private ViewfinderView viewfinderView;

    Boolean flash_state = false;

    TextView sub_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);

        switchFlashlightButton = findViewById(R.id.switch_flashlight);

        viewfinderView = findViewById(R.id.zxing_viewfinder_view);

        sub_text = findViewById(R.id.sub_text);

        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }

        capture = new CaptureManager(CustomScannerActivity.this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.setShowMissingCameraPermissionDialog(false);
        capture.decode();

        changeMaskColor(null);
        changeLaserVisibility(true);

        SpannableStringBuilder ssb = new SpannableStringBuilder(sub_text.getText().toString());
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#3065F3")), 12, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new StyleSpan(Typeface.BOLD), 12, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#3065F3")), 22, 27, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new StyleSpan(Typeface.BOLD), 22, 27, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sub_text.setText(ssb);
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public void onClick_scan_image(View v){
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType( android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, 111);
    }

    public void onClick_input_code(View v){
        Intent inputIntent = new Intent(CustomScannerActivity.this,Block_Verify_Code_Input_Activity.class);
        startActivityForResult(inputIntent, 222);
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight(View view) {
        if (flash_state==false) {
            barcodeScannerView.setTorchOn();
        } else {
            barcodeScannerView.setTorchOff();
        }
    }

    public void changeMaskColor(View view) {
        Random rnd = new Random();
        int color = Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        viewfinderView.setMaskColor(0);
    }

    public void changeLaserVisibility(boolean visible) {
        viewfinderView.setLaserVisibility(false);
    }

    @Override
    public void onTorchOn() {
        flash_state=true;
    }

    @Override
    public void onTorchOff() {
        flash_state=false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onClick_back(View v){
        finish();
    }
    public void onBackPressed() {
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //the case is because you might be handling multiple request codes here
            case 222:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent();
                    intent.putExtra("input_code", data.getStringExtra("input_code"));
                    setResult(RESULT_OK, intent);
                    finish();
                } else {   // RESULT_CANCEL
                    //Toast.makeText(CustomScannerActivity.this, "코드입력 취소", Toast.LENGTH_SHORT).show();
                }
                break;
            case 111:
                if (data == null || data.getData() == null) {
                    Log.e("TAG", "The uri is null, probably the user cancelled the image selection process using the back button.");
                    return;
                }
                Uri uri = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap == null) {
                        Log.e("TAG", "uri is not a bitmap," + uri.toString());
                        return;
                    }
                    int width = bitmap.getWidth(), height = bitmap.getHeight();
                    int[] pixels = new int[width * height];
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                    bitmap.recycle();
                    bitmap = null;
                    RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                    BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
                    MultiFormatReader reader = new MultiFormatReader();
                    try {
                        //인식 성공시
                        Result result = reader.decode(bBitmap);
                        //Toast.makeText(this, "The content of the QR image is: " + result.getText(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("input_code", result.getText());
                        setResult(RESULT_OK, intent);
                        finish();
                    } catch (NotFoundException e) {
                        //인식 실패시
                        Log.e("TAG", "decode exception", e);
                        Toast.makeText(this, "인식할 수 없는 이미지 입니다", Toast.LENGTH_SHORT).show();
                    }
                } catch (FileNotFoundException e) {
                    Log.e("TAG", "can not open file" + uri.toString(), e);
                    Toast.makeText(this, "이미지를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
