package com.hardkernel.wiringpi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
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
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {



    private static final int VIEW_MODE_RGBA = 0;
    private static final int VIEW_MODE_GRAY = 1;
    private static final int VIEW_MODE_CANNY = 2;
    private static final int VIEW_MODE_FEATURES = 5;

    public boolean first = true;
    private int mViewMode;

    private Mat mIntermediateMat;
    private Mat mGray;
    private Mat current;
    private Mat previous = null;
    private Mat difference;
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
            R.id.led01, R.id.led02, R.id.led03
    };

    private List<CheckBox> mLeds;
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
    private Handler ROIhandler = new Handler();
    Runnable mRunnableROI = new Runnable() {
        @Override
        public void run() {
            updateROI();
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
    public int getRed[],getGreen[],getBlue[];
    TextView rgbStatus1;
    TextView rgbStatus2;
    TextView rgbStatus3;
    TextView rgbStatus4;
    TextView rgbStatus5;

    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat matInput; // Input으로 들어갈 영상이나 사진
    private Mat matResult; // output으로 나올 영상이나 사진

    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);

    public native void FindFeatures(long matAddrGr, long matAddrRgba);

    public native void Labling(long matAddrGr, long matAddrRgba);

    static {
        System.loadLibrary("wpi_android");
        System.loadLibrary("opencv_android");
        System.loadLibrary("opencv_java3");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
//opencv}}}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //{{{opencv

        //opencv}}}

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
//        tab3.setContent(R.id.tab3);


        mTabHost.addTab(tab1);
        mTabHost.addTab(tab2);
//        mTabHost.addTab(tab3);


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
                    for (CheckBox cb : mLeds)  //체크박스 활성화
                        cb.setEnabled(true);
                } else {
                    mStopGPIO = true;
                    for (CheckBox cb : mLeds)  //체크박스 비활성화
                        cb.setEnabled(false);

                }
            }
        });


        mLeds = new ArrayList<CheckBox>();

        try {
            mProcess = Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int id : CHECKBOX_IDS) {
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

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_surface_view);
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
        matInput = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        previous = new Mat(width, height, CvType.CV_64FC4);        //차프레임 구할때 쓰는 프레임 RESULT BUTTON
        current = new Mat(width, height, CvType.CV_64FC4);         //차프레임 구할때 쓰는 프레임 RESULT BUTTON
        difference = new Mat(width, height, CvType.CV_64FC4);
    }

    @Override
    public void onCameraViewStopped() {
        matInput.release();
        mGray.release();
        mIntermediateMat.release();
        current.release();
        previous.release();
        difference.release();
    }

    private int scale = 5;
    private int[] red = new int[scale];
    private int[] green = new int[scale];
    private int[] blue = new int[scale];
    private int[] gray = new int[scale];


    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//{{{roi
        Button rgbBtn = (Button) findViewById(R.id.rgbBtn);
        rgbStatus1 = (TextView) findViewById(R.id.ROI1);
        rgbStatus2 = (TextView) findViewById(R.id.ROI2);
        rgbStatus3 = (TextView) findViewById(R.id.ROI3);
        rgbStatus4 = (TextView) findViewById(R.id.ROI4);
        rgbStatus5 = (TextView) findViewById(R.id.ROI5);


        final int viewMode = mViewMode;


        Scalar color = new Scalar(255, 255, 255);
        int length = 400;
        int center = 750;

        int height = 50;
        int width = 10;

        int startX = 300 - (length / 2) - (height / 2);
        int startY = 200;

        Rect[] roibars = new Rect[scale];
        int[][] avgRGB = new int[scale][4];

        for (int i = 0; i < scale; i++) {
            roibars[i] = new Rect(startX + (2 * i * height), startY, width, height);
        }
        int threshold = 10;
        int count = 0;

        matInput = inputFrame.rgba();

        for (int i = 0; i < scale; i++)
            for (int j = 0; j < 4; j++)
                avgRGB[i][j] = calcRGB(roibars[i])[j];

        if (first) {
            int[] temp_red = Getcol(avgRGB, 0);
            int[] temp_green = Getcol(avgRGB, 1);
            int[] temp_blue = Getcol(avgRGB, 2);
            int[] temp_gray = Getcol(avgRGB, 3);
            for (int i = 0; i < scale; i++) {
                gray[i] = temp_gray[i];
                red[i] = temp_red[i];
                green[i] = temp_green[i];
                blue[i] = temp_blue[i];
            }
            first = false;
            return matInput;
        }

        int[] temp_grey = Getcol(avgRGB, 3);
        final int[] temp_red = Getcol(avgRGB, 0);
        int[] temp_green = Getcol(avgRGB, 1);
        int[] temp_blue = Getcol(avgRGB, 2);  //기준값 저장 이후 프레임의 gray값
        String redResult;
        final String r1 = "R: " + temp_red[0];
        getRed=temp_red;
        getGreen=temp_green;
        getBlue=temp_blue;
        for (int i = 0; i < scale; i++) {
//                    temp_grey[i] = Math.abs(gray[i] - temp_grey[i]); //기준값 대비 차이
            Imgproc.rectangle(matInput, new Point(roibars[i].x, roibars[i].y), new Point(roibars[i].x + width, roibars[i].y + height), color, 1); //test용으로 출력 (지우면 처리속도 증가)
        //Imgproc.putText(matInput, new String("r" + temp_red[i] + " "), new Point(roibars[i].x, roibars[i].y - 80 + 200 * (i % 2)), 1, 2, color, 2);
//            rgbStatus1.setText(r1);

            ROIhandler.postDelayed(mRunnableROI,100);

        }






        matResult= new Mat(matInput.rows(),matInput.cols(),matInput.type());

        return matInput;



//roi}}}
//        matInput = inputFrame.rgba();
//
//        matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());
//
//        ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
//        Labling(matInput.getNativeObjAddr(),matResult.getNativeObjAddr());
//        return matResult;
//        public native void Labling(long matAddrGr, long matAddrRgba);
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
    //{{{opencv
    public void updateROI(){
        String red1= String.valueOf(getRed[0]);
        String red2= String.valueOf(getRed[1]);
        String red3= String.valueOf(getRed[2]);
        String red4= String.valueOf(getRed[3]);
        String red5= String.valueOf(getRed[4]);

        String green1=String.valueOf(getGreen[0]);
        String green2=String.valueOf(getGreen[1]);
        String green3=String.valueOf(getGreen[2]);
        String green4=String.valueOf(getGreen[3]);
        String green5=String.valueOf(getGreen[4]);

        String blue1=String.valueOf(getBlue[0]);
        String blue2=String.valueOf(getBlue[1]);
        String blue3=String.valueOf(getBlue[2]);
        String blue4=String.valueOf(getBlue[3]);
        String blue5=String.valueOf(getBlue[4]);

        rgbStatus1.setText("red :"+red1+" green :"+green1+" blue :"+blue1);

        rgbStatus2.setText("red :"+red2+" green :"+green2+" blue :"+blue2);
        rgbStatus3.setText("red :"+red3+" green :"+green3+" blue :"+blue3);
        rgbStatus4.setText("red :"+red4+" green :"+green4+" blue :"+blue4);
        rgbStatus5.setText("red :"+red5+" green :"+green5+" blue :"+blue5);
    }


    public int[] calcRGB(Rect Roi)   //Roi를 전달받아서 RGB Gray 평균값을 계산하고 배열을 리턴 [R G B Gray]
    {
        int avg1=0,avg2=0,avg3=0,avg4=0;

        for(int i = Roi.x; i< Roi.x+Roi.width ;i++)
            for(int j = Roi.y; j<Roi.y+Roi.height;j++)
            {
                double[] rgbV = matInput.get(j, i);
                avg1 += rgbV[0] ;
                avg2 += rgbV[1] ;
                avg3 += rgbV[2] ;
            }
        avg1 /= (Roi.width * Roi.height) ;    //RED
        avg2 /= (Roi.width * Roi.height) ;    //Green
        avg3 /= (Roi.width * Roi.height) ;    //BLUE
        avg4 = (avg1+avg2+avg3)/3;

        int arr[] = {avg1,avg2,avg3,avg4}; //RED,Green,Blue,Gray

        return arr;
    }
    public int[] Getcol(int[][] arr, int col) //2차원 배열에서 특정 열을 1차원 배열로 리턴
    {
        int length = arr.length;
        int[] arr1 = new int[length];

        for(int i=0;i<length;i++)
            arr1[i] = arr[i][col];

        return arr1;

    }
    //opencv}}}




    public native int wiringPiSetup();
//    public native int wiringPiSetupSys();
    public native int analogRead(int port);
    public native void digitalWrite(int port, int onoff);
    public native void pinMode(int port, int value);


}

