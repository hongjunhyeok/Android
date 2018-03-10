#include <com_example_ygyg331_jnitest2_jniClass.h>
#include <jni.h>

JNIEXPORT jstring JNICALL Java_com_example_ygyg331_jnitest2_jniClass_jniM
        (JNIEnv *env, jobject obj) {

    return (*env)->NewStringUTF(env, "성공입니다.!!");

}


JNIEXPORT jstring JNICALL Java_com_example_ygyg331_jnitest2_jniClass_getNumString(JNIEnv *a, jobject b, jstring str, jint num)

{
    jclass cls = a->GetObjectClass(b);
    jmethodID func = a->GetMethodID(cls, "callback", "(Ljava/lang/String;I)Ljava/lang/String;");
    return (jstring)a->CallObjectMethod(b, func, str, num);
}