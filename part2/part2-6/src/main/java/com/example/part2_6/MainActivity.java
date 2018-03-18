package com.example.part2_6;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    TextView bellTextView;
    TextView labelTextVeiw;
    CheckBox repeatCheckView;
    CheckBox vibrateCheckView;
    Switch switchView;

    float initX;
    long initTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bellTextView = (TextView)findViewById(R.id.bell_name);
        labelTextVeiw=(TextView)findViewById(R.id.label);
        repeatCheckView=(CheckBox)findViewById(R.id.repeatCheck);
        vibrateCheckView=(CheckBox)findViewById(R.id.vibrate);
        switchView=(Switch)findViewById(R.id.onOff);

        bellTextView.setOnClickListener(this);
        labelTextVeiw.setOnClickListener(this);

        repeatCheckView.setOnCheckedChangeListener(this);
        vibrateCheckView.setOnCheckedChangeListener(this);
        switchView.setOnCheckedChangeListener(this);
    }


    //toast 이벤트 함수
    private void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onClick(View v) {
        if(v==bellTextView){
            showToast("bell Text click event");
        }
        else if(v==labelTextVeiw){
            showToast("Label text click event");

        }
    }

    //체크박스 체크가 바뀔때
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
     if(buttonView==repeatCheckView){
         showToast("repeat checkbox is "+isChecked);
     }
     else if(buttonView==vibrateCheckView){
         showToast("v checkbox is "+isChecked);
     }
     else if(buttonView==switchView){
         showToast("s checkbox is "+isChecked);
     }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            initX=event.getRawX();

        }else if(event.getAction()==MotionEvent.ACTION_UP){
            float diffX=initX-event.getRawX();
            if(diffX>30){
                showToast("왼쪽");
            }
            else if(diffX<-30){
                showToast("오른쪽");

            }
        }
        return true ;

    }

    @Override

    // 버튼 두번 누르면 종료하는 로직.
    public boolean onKeyDown(int Keycode, KeyEvent event){
        if(Keycode == KeyEvent.KEYCODE_BACK)
        {
            if(System.currentTimeMillis()-initTime >3000){
                showToast("종료할거면 한번 더눌러");
                initTime=System.currentTimeMillis();
            }
            else{
                finish();
                }
            return true;
        }
        return super.onKeyDown(Keycode,event);

} }



