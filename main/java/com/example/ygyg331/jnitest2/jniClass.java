package com.example.ygyg331.jnitest2;

/**
 * Created by ygyg331 on 2018-03-10.
 */

public class jniClass {
    static {
            System.loadLibrary("ndktest");
    }
    public native String jniM();
    public native String getNumString(String str, int num);

    private String callback(String str, int num) {

        return str + " : " + num;
    }
}