package com.example.ygyg331.jnitest2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class MainActivity extends AppCompatActivity {

    static{
        System.loadLibrary("ndktest");
    }

    jniClass jni = new jniClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bt1=(Button)findViewById(R.id.button);
        Button bt2=(Button)findViewById(R.id.button2);
        final TextView tv=(TextView)findViewById(R.id.textView);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            tv.setText(jni.jniM());
            }
        });


        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            tv.setText(jni.getNumString("hi",1));
            }
        });

    }


    }

