package com.example.ygyg331.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Lab3_3Acitvity extends AppCompatActivity {
    Button trueBtn;
    Button falseBtn;
    Button targetButtonView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab3_3_acitvity);
   // View 객체 획득

        trueBtn=(Button)findViewById(R.id.btn_visible_true);
        targetButtonView=(Button)findViewById(R.id.text_visible_target);
        falseBtn=(Button)findViewById(R.id.btn_visible_false);


        trueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==trueBtn){
                    targetButtonView.setVisibility(View.VISIBLE);
                }
                else if(v==falseBtn){
                    targetButtonView.setVisibility(View.INVISIBLE);
            }
        }});


        falseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==trueBtn){
                    targetButtonView.setVisibility(View.VISIBLE);
                }
                else if(v==falseBtn){
                    targetButtonView.setVisibility(View.INVISIBLE);
                }
            }});

    }

//    //버튼 이벤트 콜백함수
//    @Override
//    public void onClick(View v){
//        if(v==trueBtn){
//            targetButtonView.setVisibility(View.VISIBLE);
//        }
//        else if(v==falseBtn){
//            targetButtonView.setVisibility(View.INVISIBLE);
//                    }
//    }
}
