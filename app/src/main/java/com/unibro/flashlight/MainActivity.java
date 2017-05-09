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
import android.widget.Toast;


import java.lang.reflect.Field;
import java.security.Policy;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    ImageButton im;
    private boolean flashAcik=false;
    private boolean flashVarmı;
    private Camera camera2;
    Parameters params;
    private CameraManager objCameraManager;
    private String mCameraId;
    private ImageView ivOnOFF;
    private MediaPlayer objMediaPlayer;
    private Boolean isTorchOn=false;




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

      if (Build.VERSION.SDK_INT >22)
      {
          isTorchOn = false;

          Boolean isFlashAvailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
          if (!isFlashAvailable) {
              AlertDialog alert2 = new AlertDialog.Builder(MainActivity.this).create();
              alert2.setTitle("Warning!");
              alert2.setMessage("Flash is Not Available");
              alert2.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      finish();
                  }
              });
              alert2.show();
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
      else {
          //isTorchOn = false;

          Boolean isFlashAvailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
          if (!isFlashAvailable) {
              AlertDialog alert2 = new AlertDialog.Builder(MainActivity.this).create();
              alert2.setTitle("Warning!");
              alert2.setMessage("Flash is Not Available");
              alert2.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      finish();
                  }
              });
              alert2.show();
              return;
          }

            cameraAc();

          im.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  try {
                      if (isTorchOn) { // Flash Acik true dönücek. Eğer Flash Açıksa Kapatıcak
                          im.setBackgroundResource(R.drawable.off);
                          turnOffLight();
                      } else {
                          // Açık değilse button'a basınca açıcak
                          im.setBackgroundResource(R.drawable.on);
                          turnOnLight();
                      }

                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
          });
        }
    }
    public void turnOnLight() {
        try {
            if (Build.VERSION.SDK_INT > 22) {
                objCameraManager.setTorchMode(mCameraId, true);

            }
            else
            {
                params = camera2.getParameters();
                params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                camera2.setParameters(params);
                camera2.startPreview();
                isTorchOn = true;
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
            if (Build.VERSION.SDK_INT > 22) {
                objCameraManager.setTorchMode(mCameraId, false);

            }
            else{
                params = camera2.getParameters();
                params.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera2.setParameters(params);
                camera2.stopPreview();
                isTorchOn = false;
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

    private void cameraAc() {
        if (camera2 == null) {

            camera2 = Camera.open();
            params = camera2.getParameters();

        }
    }







}
