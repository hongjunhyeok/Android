#include<jni.h>
#include<string.h>


jstring Java_com_example_ygyg331_jnitest_MainActivity_jniM(JNIEnv *env, jobject obj) {

    return (*env)->NewStringUTF(env, "성공입니다.!!");

}




