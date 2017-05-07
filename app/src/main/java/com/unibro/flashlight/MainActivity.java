package com.unibro.flashlight;


import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.security.Policy;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    ImageButton im;

    /**
     * Object for camera service
     */
    private CameraManager objCameraManager;
    /**
     * id of current camera
     */
    private String mCameraId;
    /**
     * imageview in design which controls the flash light
     */
    private ImageView ivOnOFF;
    /**
     * Object of medial player to play sound while flash on/off
     */
    private MediaPlayer objMediaPlayer;
    /**
     * for getting torch mode
     */
    private Boolean isTorchOn;









//*************************************
    CameraDevice camera;
    private Camera kamera;
    private boolean flashOn=false;
    private boolean flashdurum; // telefonda flash olup olmadığı kontrol edilir
    Parameters param;
    boolean status=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        im=(ImageButton) findViewById(R.id.imagebtn);


        isTorchOn = false;

        Boolean isFlashAvailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!isFlashAvailable) {
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
            alert.setTitle("Warning!");
            alert.setMessage("Flash is Not Available");
            alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
            return;
        }


        objCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = objCameraManager.getCameraIdList()[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isTorchOn) {
                        im.setBackgroundResource(R.drawable.off);
                        turnOffLight();
                        isTorchOn = false;
                    } else {
                        im.setBackgroundResource(R.drawable.on);
                        turnOnLight();
                        isTorchOn = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



    }
    public void turnOnLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                objCameraManager.setTorchMode(mCameraId, true);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for turning light OFF
     */
    public void turnOffLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                objCameraManager.setTorchMode(mCameraId, false);

               // ivOnOFF.setImageResource(R.drawable.off);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (isTorchOn) {
            turnOffLight();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (isTorchOn) {
            turnOffLight();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isTorchOn) {
            turnOnLight();
        }
    }





}
