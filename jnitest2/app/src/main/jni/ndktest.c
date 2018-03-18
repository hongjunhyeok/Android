#include "com_example_ygyg331_jnitest2_jniClass.h"
#include <jni.h>
#include <stdio.h>
#include <string.h>

JNIEXPORT jstring JNICALL Java_com_example_ygyg331_jnitest2_jniClass_jniM
        (JNIEnv *env, jobject obj) {

    return (*env)->NewStringUTF(env, "성공입니다.!!");

}

JNIEXPORT void JNICALL Java_com_example_ygyg331_jnitest2_jniClass_hello
        (JNIEnv *env, jobject obj)
{


}

//
//JNIEXPORT jstring JNICALL Java_com_example_ygyg331_jnitest2_jniClass_getNumString(JNIEnv *a, jobject b, jstring str, jint num)
//
//{
//    jclass cls = (struct)a->getObjectClass(b);
//    jmethodID func=GetMethodID(cls, "callback", "(Ljava/lang/String;I)Ljava/lang/String;");
//    return (*a)->CallObjectMethod(b, func, str, num);
//}
