package com.dji.FPVDemo;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import dji.common.battery.BatteryState;
import dji.common.camera.SettingsDefinitions;
import dji.common.camera.SystemState;
import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.battery.Battery;
import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;

public class MainActivity extends Activity{

    private static final String TAG = MainActivity.class.getName();
    //protected Battery. mReceivedVideoDataCallBack = null;


    private TextView currentValue;
    private TextView voltageValue;
    private TextView temperatureValue;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        handler = new Handler();
        initUI();


        Battery battery = FPVDemoApplication.getBatteryInstance();

        if (battery != null) {
            try {
                FPVDemoApplication.getProductInstance().getBattery().setStateCallback(new BatteryState.Callback() {
                    @Override
                    public void onUpdate(BatteryState djiBatteryState) {
                        currentValue.setText(Integer.toString(djiBatteryState.getCurrent()));
                        voltageValue.setText(Integer.toString(djiBatteryState.getVoltage()));
                        temperatureValue.setText(Float.toString(djiBatteryState.getTemperature()));
                    }
                });
            } catch (Exception ignored) {

            }
        }

    }

    protected void onProductChange() {
        initPreviewer();
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        initPreviewer();
        onProductChange();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        uninitPreviewer();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    public void onReturn(View view){
        Log.e(TAG, "onReturn");
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        uninitPreviewer();
        super.onDestroy();
    }

    private void initPreviewer() {

        BaseProduct product = FPVDemoApplication.getProductInstance();

        if (product == null || !product.isConnected()) {
            showToast(getString(R.string.disconnected));
        }
    }

    private void uninitPreviewer() {
        Battery battery = FPVDemoApplication.getBatteryInstance();
        if (battery != null){
            // Reset the callback
            FPVDemoApplication.getProductInstance().getBattery().setStateCallback(null);
        }
    }

    private void initUI() {
        // init mVideoSurface

        currentValue = (TextView) findViewById(R.id.currentTextView);
        voltageValue = (TextView) findViewById(R.id.voltageTextView);
        temperatureValue = (TextView) findViewById(R.id.temperatureTextView);


    }


    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
