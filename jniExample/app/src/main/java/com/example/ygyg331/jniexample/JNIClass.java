package com.example.ygyg331.jniexample;

/**
 * Created by ygyg331 on 2018-03-10.
 */
public class JNIClass {
    static
    {
        System.loadLibrary("cCode");
    }

    public native String getNumString(String str, int num);

    private String callback(String str, int num){

        return str+" : "+num;
    }
}

