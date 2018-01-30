package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Lab5_2Activity extends AppCompatActivity implements View.OnClickListener{
    // 버튼선언
    Button alertBtn,listBtn,progressBtn,dateBtn,timeBtn,customDialogBtn;
    // 이벤트 처리를 위해 dialogr 객체를 멤버 변수로 선언
    android.app.AlertDialog customDialog;
    AlertDialog listDialog;
    AlertDialog alertDialog;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab5_2);
        //View 객체 획득
        alertBtn=(Button)findViewById(R.id.btn_alert);
        listBtn=(Button)findViewById(R.id.btn_list);
        progressBtn=(Button)findViewById(R.id.btn_progress);
        dateBtn=(Button)findViewById(R.id.btn_date);
        timeBtn=(Button)findViewById(R.id.btn_time);
        customDialogBtn=(Button)findViewById(R.id.btn_custom);

        //버튼 이벤트 등록
        alertBtn.setOnClickListener(this);
        listBtn.setOnClickListener(this);
        progressBtn.setOnClickListener(this);
        dateBtn.setOnClickListener(this);
        timeBtn.setOnClickListener(this);
        customDialogBtn.setOnClickListener(this);

}



    //매개변수의 문자열을 toast로 띄우는 개발자 함수
    private void showToast(String message){
        Toast toast=Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.show();
    }

    //dialog Button 이벤트 처리
    DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(dialog==customDialog && which==DialogInterface.BUTTON_POSITIVE){
                showToast("끝!!- 2편을 기대해주세요!!");
            }

            // 목록 dialog 항목이 선택되었을 때 항목 문자열 획득
            else if(dialog==listDialog){
                String[] dates=getResources().getStringArray(R.array.dialog_array);
                showToast(dates[which]+"선택했어!!");
            }
            else if(dialog==alertDialog && which ==DialogInterface.BUTTON_POSITIVE){
                showToast("오늘 공부하면서 4시간동안 만들었어요...");
            }
            else if(dialog==alertDialog && which==DialogInterface.BUTTON_NEGATIVE){
                showToast("사실은 꺼지지 않아 ");
            }

        }
    };

    @Override
    public void onClick(View v){
        if(v==alertBtn){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setTitle("알림");
            builder.setMessage("ㅋㅋㅋㅋ사실 이걸만들면서 이런식으로 프로포즈 해도 되겠다 싶었어");
            builder.setPositiveButton("계속 볼려면 OK",dialogListener);
            builder.setNegativeButton("끄고 싶으면 NO",dialogListener);

            alertDialog=builder.create();
            alertDialog.show();
        }
        else if(v==listBtn){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("다음 학생중 젤 맘에드는 사람을 골라주세여");
            builder.setSingleChoiceItems(R.array.dialog_array,0,dialogListener);
            builder.setPositiveButton("확인",null);
            builder.setNegativeButton("취소",null);
            listDialog=builder.create();
            listDialog.show();
        }
        else if(v==progressBtn){
            ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setIcon(android.R.drawable.ic_dialog_alert);
            progressDialog.setTitle("알림");
            progressDialog.setMessage("만들기 힘들다 ㅎㅎ");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);

            progressDialog.show();
        }else if(v==dateBtn){
            Calendar c=Calendar.getInstance();
            int year=c.get(Calendar.YEAR);
            int month=c.get(Calendar.MONTH);
            int day=c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    showToast("잊지 않았겠지만 기념일은 2018년 1월 26일이야");
                }
            },year, month, day);
            datePickerDialog.show();
        }else if(v==timeBtn){
            Calendar c=Calendar.getInstance();
            final int hour=c.get(Calendar.HOUR_OF_DAY);
            int minute=c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    showToast("정확히 말하자면 오후 3시 55분 이었어");
                }
            }, hour, minute, false);
            timePickerDialog.show();
        }else if(v==customDialogBtn){
            android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);

            LayoutInflater inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
            View view=inflater.inflate(R.layout.dialog_layout, null);
            builder.setView(view);

            builder.setPositiveButton("확인", dialogListener);
            builder.setNegativeButton("취소", null);

            customDialog=builder.create();
            customDialog.show();

        }

        }
    }


