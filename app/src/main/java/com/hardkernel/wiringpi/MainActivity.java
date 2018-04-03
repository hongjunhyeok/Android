package com.hardkernel.wiringpi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.ToggleButton;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    TabHost mTabHost;
    private final static String TAG = "example-wiringPi";
    //GPIO {{{
    private ToggleButton mBtn_GPIO;
    private final int DATA_UPDATE_PERIOD = 100; // 100ms
    private final int PORT_ADC1 = 0;   // ADC.AIN0
    private ProgressBar mPB_ADC;

    private final static int INPUT = 0;
    private final static int OUTPUT = 1;

	/*
    private final int ledPorts[] = {
        214, // GPIOY.3
        234, // GPIOX.6
        219, // GPIOY.8
        228, // GPIOX.0
        230, // GPIOX.2
        232, // GPIOX.4
        235, // GPIOX.7
        237, // GPIOX.9
        239, // GPIOX.11
        247, // GPIOX.9
        249, // GPIOX.21
        238, // GPIOX.10
        236, // GPIOX.8
        233, // GPIOX.5
        231, // GPIOX.3
        229, // GPIOX.1
        224, // GPIOY.13
        225, // GPIOY.14
        218, // GPIOX.1
    };
	*/

    private final int ledPorts[] = {
        24, //214
        23, //234
        22, //219
        21, //228
        14, //230
        13, //232
        12, //235
        3,  //237
        2,  //239
        0,  //247
        7,  //249
        5,  //238
        4,  //236
        5,  //233
        6,  //231
        10, //229
        26, //224
        11, //225
        27, //218
    };


    private static final int[] CHECKBOX_IDS = {
        R.id.led01, R.id.led02, R.id.led03, R.id.led04, R.id.led05,
        R.id.led06, R.id.led07, R.id.led08, R.id.led09, R.id.led10,
        R.id.led11, R.id.led12, R.id.led13, R.id.led14, R.id.led15,
        R.id.led16, R.id.led17, R.id.led18, R.id.led19
    };

    private List<CheckBox>mLeds;
    private boolean mStopGPIO;
    private Process mProcess;

    private Handler handler = new Handler();
    Runnable mRunnableGPIO = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            updateGPIO();
        }
    };
    //GPIO }}}

    //PWM {{{
    private RadioButton mRB_PWM1;
    private RadioButton mRB_PWM2;
    private ToggleButton mBtn_PWM;
    private LinearLayout mLayout_PWM2;
    private CheckBox mCB_EnablePWM1;
    private CheckBox mCB_EnablePWM2;
    private TextView mTV_Duty1;
    private TextView mTV_Duty2;
    private SeekBar mSB_DutyPWM1;
    private SeekBar mSB_DutyPWM2;
    private int mPWMCount = 1;
    private final String PWM_PREFIX = "/sys/devices/pwm-ctrl.";
    private final String PWM_ENABLE = "/enable";
    private final String PWM_DUTY = "/duty";
    private final String PWM_FREQ = "/freq"; //added J.
    private String mPWMEnableNode;
    private String mPWMDutyNode;
    private String mPWMFreqNode; //added J.
    //PWM }}}

    //{{{opencv
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat matInput; // Input으로 들어갈 영상이나 사진
    private Mat matResult; // output으로 나올 영상이나 사진

    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);

    static {
        System.loadLibrary("wpi_android");
        System.loadLibrary("opencv_android");
        System.loadLibrary("opencv_java3");    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
//opencv}}}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabHost = (TabHost) findViewById(R.id.tabhost);
        mTabHost.setup();


        TabSpec tab1 = mTabHost.newTabSpec("GPIO");
        TabSpec tab2 = mTabHost.newTabSpec("PWM");
        TabSpec tab3 = mTabHost.newTabSpec("OPENCV");

        tab1.setIndicator("GPIO");
        tab1.setContent(R.id.tab1);
        tab2.setIndicator("PWM");
        tab2.setContent(R.id.tab2);
        tab3.setIndicator("OPENCV");
        tab3.setContent(R.id.tab3);


        mTabHost.addTab(tab1);
        mTabHost.addTab(tab2);
        mTabHost.addTab(tab3);


        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                // TODO Auto-generated method stub
                mBtn_PWM.setChecked(false);

                mBtn_GPIO.setChecked(false);
            }
        });

        //GPIO {{{
        mBtn_GPIO = (ToggleButton) findViewById(R.id.btn_gpio);
        mBtn_GPIO.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                //체크가 되었으면
                if (isChecked) {
                    wiringPiSetup(); //wiringPi를 준비하고 wiringPi는 native

                    for (int i = 0; i < ledPorts.length; i++)
                        pinMode(ledPorts[i], OUTPUT);  //pinmode는 native pinMode(ports,value)

                    mStopGPIO = false; //mStopGPIO는 boolean값을 갖는다.
                    handler.postDelayed(mRunnableGPIO, 100); // 지연값을 갖게해줌
                    for (CheckBox cb: mLeds)  //체크박스 활성화
                        cb.setEnabled(true);
                } else {
                    mStopGPIO = true;
                    for (CheckBox cb: mLeds)  //체크박스 비활성화
                        cb.setEnabled(false);
                    mPB_ADC.setEnabled(false);
                }
            }
        });

        mPB_ADC = (ProgressBar) findViewById(R.id.adc);

        mLeds = new ArrayList<CheckBox>();

        try {
            mProcess = Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int id: CHECKBOX_IDS) {
            CheckBox cb = (CheckBox) findViewById(id);
            mLeds.add(cb);
        }
        //GPIO }}}

        //PWM {{{
        mTV_Duty1 = (TextView) findViewById(R.id.tv_duty1);
        mTV_Duty2 = (TextView) findViewById(R.id.tv_duty2);

        mCB_EnablePWM1 = (CheckBox) findViewById(R.id.cb_pwm1);
        mCB_EnablePWM1.setEnabled(false);
        mCB_EnablePWM1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                mSB_DutyPWM1.setEnabled(isChecked);
                mSB_DutyPWM1.setProgress(0);
                mTV_Duty1.setText("Duty : 0");
                setEnalbePWM(0, isChecked);
            }
        });

        mCB_EnablePWM2 = (CheckBox) findViewById(R.id.cb_pwm2);
        mCB_EnablePWM2.setEnabled(false);
        mCB_EnablePWM2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                mSB_DutyPWM2.setEnabled(isChecked);
                mSB_DutyPWM2.setProgress(0);
                mTV_Duty2.setText("Duty : 0");
                setEnalbePWM(1, isChecked);
            }
        });

        for (int i = 0; i < 100; i++) {
            File f = new File(PWM_PREFIX + i);
            if (f.isDirectory()) {
                mPWMEnableNode = PWM_PREFIX + i + PWM_ENABLE;
                Log.e(TAG, "pwm enable : " + mPWMEnableNode);
                mPWMDutyNode = PWM_PREFIX + i + PWM_DUTY;
                Log.e(TAG, "pwm duty : " + mPWMDutyNode);
                mPWMFreqNode = PWM_PREFIX + i + PWM_FREQ; //added J.
                break;
            }
        }

        mSB_DutyPWM1 = (SeekBar) findViewById(R.id.sb_duty1);
        mSB_DutyPWM1.setEnabled(false);
        mSB_DutyPWM1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                mTV_Duty1.setText("Duty : " + seekBar.getProgress());
                setDuty(0, seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                // TODO Auto-generated method stub
            }
        });

        mSB_DutyPWM2 = (SeekBar) findViewById(R.id.sb_duty2);
        mSB_DutyPWM2.setEnabled(false);
        mSB_DutyPWM2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                mTV_Duty2.setText("Duty : " + seekBar.getProgress());
                setDuty(1, seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                // TODO Auto-generated method stub
            }
        });

        mLayout_PWM2 = (LinearLayout) findViewById(R.id.lo_pwm2);
        mLayout_PWM2.setVisibility(View.GONE);
 
        mRB_PWM1 = (RadioButton) findViewById(R.id.radio_pwm1);
        mRB_PWM1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mPWMCount = 1;
                mCB_EnablePWM2.setEnabled(false);
                mLayout_PWM2.setVisibility(View.GONE);
            }
        });

        mRB_PWM2 = (RadioButton) findViewById(R.id.radio_pwm2);
        mRB_PWM2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mPWMCount = 2;
                mLayout_PWM2.setVisibility(View.VISIBLE);
            }
        });

        mTV_Duty1 = (TextView) findViewById(R.id.tv_duty1);
        mTV_Duty2 = (TextView) findViewById(R.id.tv_duty2);

        mBtn_PWM = (ToggleButton) findViewById(R.id.btn_pwm);
        mBtn_PWM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    insmodPWM();
                    mCB_EnablePWM1.setEnabled(true);
                    mCB_EnablePWM1.setChecked(false);
                    mCB_EnablePWM2.setEnabled(true);
                    mCB_EnablePWM2.setChecked(false);
                    mSB_DutyPWM1.setProgress(0);
                    mSB_DutyPWM2.setProgress(0);
                    mRB_PWM1.setEnabled(false);
                    mRB_PWM2.setEnabled(false);
                } else {
                    rmmodPWM();
                    mCB_EnablePWM1.setEnabled(false);
                    mCB_EnablePWM1.setChecked(false);
                    mCB_EnablePWM2.setEnabled(false);
                    mCB_EnablePWM2.setChecked(false);
                    mSB_DutyPWM1.setProgress(0);
                    mSB_DutyPWM2.setProgress(0);
                    mRB_PWM1.setEnabled(true);
                    mRB_PWM2.setEnabled(true);
                }
            }
        });
        mBtn_PWM.setChecked(false);
        //PWM }}}


        //{{{opencv
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {

                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }
        //opencv}}}


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //{{{opencv
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        //opencv}}}
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        
        //GPIO {{{
        mBtn_GPIO.setChecked(false);
        //GPIO }}}
        //PWM {{{
        mBtn_PWM.setChecked(false);
        //PWM }}}
//{{opencv
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        //opencv}}}
    }
    //{{opencv
    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        matInput = inputFrame.rgba();

        if ( matResult != null ) matResult.release();
        matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());

        ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());

        return matResult;
    }

    //여기서부턴 퍼미션 관련 메소드
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS  = {"android.permission.CAMERA"};


    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED){
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }
    //opencv}}}







    //GPIO {{{
    public void updateGPIO() {
        int i = 0;
        int ledPos = 0;
        int adcValue = analogRead(PORT_ADC1);
        //Log.e(TAG, "updateGPIO adcValue = " + adcValue);

        //added J.
        //eleminated the hopping of checked checkboxes
        if (adcValue < 0) adcValue = 0;
        //if (adcValue > 0) {
        ledPos = adcValue * ledPorts.length / 1024;
            //ledPos = (ledPorts.length - (ledPos / 1000));
        mPB_ADC.setProgress(adcValue);
        //} else
        //    ledPos = 0;

        for (i = 0; i < ledPorts.length; i++) {
            digitalWrite (ledPorts[i], 0);
            mLeds.get(i).setChecked(false);
        }

        for (i = 0; i < ledPos; i++) {
            digitalWrite (ledPorts[i], 1);
            mLeds.get(i).setChecked(true);
        }

        if (!mStopGPIO)
            handler.postDelayed(mRunnableGPIO, 100);
    }
    //GPIO {{{

    //PWM {{{
    private void insmodPWM() {
        try {
            DataOutputStream os = new DataOutputStream(mProcess.getOutputStream());
            os.writeBytes("insmod /system/lib/modules/pwm-meson.ko npwm=" + mPWMCount + "\n");
            os.writeBytes("insmod /system/lib/modules/pwm-ctrl.ko\n");
            os.flush();
            Thread.sleep(100);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void rmmodPWM() {
        try {
            DataOutputStream os = new DataOutputStream(mProcess.getOutputStream());
            os.writeBytes("rmmod pwm_ctrl\n");
            os.writeBytes("rmmod pwm_meson\n");
            os.flush();
            Thread.sleep(100);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setEnalbePWM(int index, boolean enable) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(mPWMEnableNode + index));
            if (enable)
                bw.write("1");
            else
                bw.write("0");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //added J.
        //need to set the frequency
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(mPWMFreqNode + index));
            bw.write("100000");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDuty(int index, int duty) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(mPWMDutyNode + index));
            bw.write(Integer.toString(duty));
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //PWM }}}




    public native int wiringPiSetup();
//    public native int wiringPiSetupSys();
    public native int analogRead(int port);
    public native void digitalWrite(int port, int onoff);
    public native void pinMode(int port, int value);


}

