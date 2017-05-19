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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;


import java.lang.reflect.Field;
import java.security.Policy;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    ImageButton im;
    SeekBar s1;
    private Camera camera2;
    Parameters params;
    private CameraManager objCameraManager;
    private String mCameraId;
    //private ImageView ivOnOFF;
    //private MediaPlayer objMediaPlayer;
    private Boolean isTorchOn = false,startup=false;
    public boolean blinking = false;
    Handler handle = null;
    Runnable runnable = null;
    private boolean basildimi=false;
    StroboRunner sr;
    Thread t;
    private int freq,musictype=1; //1 ise buton 2 ise seekbar
    public boolean run=false;

    //*************************************
    CameraDevice camera;
    private Camera kamera;
    private boolean flashOn = false;
    private boolean flashdurum; // telefonda flash olup olmadığı kontrol edilir
    Parameters param;
    private MediaPlayer objMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        im = (ImageButton) findViewById(R.id.imagebtn);

        s1 = (SeekBar) this.findViewById(R.id.seekBar1);


        if (Build.VERSION.SDK_INT > 22) {
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

            if(startup==false)
                basildimi=true;
            startup=true;

            ToggleFlash(basildimi);

            im.setBackgroundResource(R.drawable.on);

                    im.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                    musictype=1;
                                                  playOnOffSound(musictype);

                                                  if(basildimi==false) {
                                                      basildimi = true;
                                                      im.setBackgroundResource(R.drawable.on);
                                                      ToggleFlash(true);
                                                  }
                                                  else {
                                                      basildimi = false;
                                                      im.setBackgroundResource(R.drawable.off);
                                                      if (t != null) {
                                                          sr.stopRunning = true;
                                                          t = null;
                                                          ToggleFlash(false);
                                                          return;
                                                      }
                                                      else
                                                          ToggleFlash(false);



                                                  }

                                              }
                                          }
                    );


        } else {
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
            if(startup==false)
                basildimi=true;
            startup=true;

            ToggleFlash(basildimi);
            im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(basildimi==false) {
                        basildimi = true;
                        im.setBackgroundResource(R.drawable.on);
                        ToggleFlash(true);
                    }
                    else {
                        basildimi = false;
                        im.setBackgroundResource(R.drawable.off);
                        if (t != null) {
                            sr.stopRunning = true;
                            t = null;
                            ToggleFlash(false);
                            return;
                        }
                        else
                            ToggleFlash(false);



                    }


                }
            });
        }

        s1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                musictype=2;
                playOnOffSound(musictype);

                freq = s1.getProgress();

                if (freq == 0 && basildimi) {


                    sr.stopRunning = true;
                   // ToggleFlash(true);
                }
                else{
                if (freq != 0 && basildimi) {
                    if (t != null) {
                        sr.stopRunning = true;
                        t = null;
                        sr = new StroboRunner();
                        sr.freq = freq;
                        t = new Thread(sr);
                        t.start();
                        return;
                    } else {
                        sr = new StroboRunner();
                        sr.freq = freq;
                        t = new Thread(sr);
                        t.start();
                        return;
                    }
                }
            }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public void ToggleFlash(boolean on)
    {
    try {

        if (on) {
            if(freq!=0)
            {
                sr=new StroboRunner();
                sr.freq=s1.getProgress();
                t=new Thread(sr);
                t.start();
                return;

            }
            else{
                if (Build.VERSION.SDK_INT > 22) {
                    objCameraManager.setTorchMode(mCameraId, true);


                }
                else {
                    params = camera2.getParameters();
                    params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                    camera2.setParameters(params);
                    camera2.startPreview();

                }

            }


        }
        if(!on)
        {
            if(t!=null)
            {
                sr.stopRunning=true;
                t=null;
                ToggleFlash(false);
                return;
            }
            else
            {
                if (Build.VERSION.SDK_INT > 22) {
                    objCameraManager.setTorchMode(mCameraId, false);
                } else {
                    params = camera2.getParameters();
                    params.setFlashMode(Parameters.FLASH_MODE_OFF);
                    camera2.setParameters(params);
                    camera2.stopPreview();

                }

            }


        }

    }catch (Exception e)
    {

    }

    }


    private class StroboRunner implements Runnable
    {
        int freq;
        boolean stopRunning=false;


        public void run(){

            try {

                while(!stopRunning)
                {
                    if (Build.VERSION.SDK_INT > 22) {


                            objCameraManager.setTorchMode(mCameraId, true);
                            Thread.sleep(100 * freq);

                            objCameraManager.setTorchMode(mCameraId, false);
                            Thread.sleep(100 * freq);



                    }
                    else
                    {
                        params = camera2.getParameters();
                        params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                        camera2.setParameters(params);
                        camera2.startPreview();
                        Thread.sleep(100 * freq);
                        params = camera2.getParameters();
                        params.setFlashMode(Parameters.FLASH_MODE_OFF);
                        camera2.setParameters(params);
                        camera2.stopPreview();
                        Thread.sleep(100 * freq);
                    }


                }


            }
            catch (Exception e)
            {

            }
        }
    }

    /**
     * Method for turning light OFF
     */
    private void playOnOffSound(int soundtype) {
        if(soundtype==1)
        objMediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.buttonsound);
        else
            objMediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.seeksound);
        objMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        objMediaPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isTorchOn) {
            //turnOffLight();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isTorchOn) {
           // turnOffLight();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isTorchOn) {
           // turnOnLight();
        }
    }

    private void cameraAc() {
        if (camera2 == null) {

            camera2 = Camera.open();
            params = camera2.getParameters();

        }
    }


}
