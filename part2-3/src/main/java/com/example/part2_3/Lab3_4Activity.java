package com.example.part2_3;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class Lab3_4Activity extends AppCompatActivity {
    CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab3_4);


        //Custom Font 적용
        TextView textView = (TextView)findViewById(R.id.fontView);
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"xmas.ttf");
        textView.setTypeface(typeFace);

        //checkBox event Program
        checkBox=(CheckBox)findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkBox.setText("is checked");
                }
                else{
                    checkBox.setText("is unchecked");
                }
            }
        });
    }
}
