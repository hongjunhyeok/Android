package com.example.ygyg331.part2;

import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

// View.onClickListener를 implements 하여서 setOnClickListener의  함수를 사용할 수 있다.

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button vibrationButton, systemButton, customButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        vibrationButton = (Button) findViewById(R.id.btn_vibration);
        systemButton = (Button) findViewById(R.id.btn_system_beep);
        customButton = (Button) findViewById(R.id.btn_cutom_sound);


        // this 자리엔 어떤 내용이 실행될지 정해져야하는데 아래 onClick 함수에서 정의하고 있다.
        vibrationButton.setOnClickListener(this);
        systemButton.setOnClickListener(this);
        customButton.setOnClickListener(this);

    }

    @Override

    //진동을 사용하기 위해서는 menifest에 등록을 해야함!!!
    //vibratior 객체로 systemservice 사용과 동시에 시간을 설정하자
    public void onClick(View v) {
        if (v == vibrationButton) {
            Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vib.vibrate(1000);
    //system 알림을 이용하기 위해선 URi 객체를 이용해야한다. 이건 있다는 것만 알아두고 찾아서 쓰자.
        } else if (v == systemButton) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            ringtone.play();
            // 실행에 이상은 없으나 소리가 들리지 않는다 뭔가 더 설정이 필요해보임 ..
        } else if (v == customButton) {
            MediaPlayer player = MediaPlayer.create(this, R.raw.fallbackring);
            player.start();
        }
    }
}