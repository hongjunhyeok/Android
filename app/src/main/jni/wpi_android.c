#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <string.h>

#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOG_TAG "wpi_android"

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#include <unistd.h>
#include <string.h>
#include <time.h>

#include <wiringPi.h>
#include "bme280.h"
#include "bme280-i2c.h"
#include "si1132.h"

#ifdef __cplusplus
extern "C" {
#endif

#define I2C_DEVICE  "/dev/i2c-1"

int pressure;
int temperature;
int humidity;

float SEALEVELPRESSURE_HPA = 1024.25;

jint Java_com_hardkernel_wiringpi_MainActivity_analogRead(JNIEnv* env, jobject obj, jint port) {
    return analogRead(port);
}

void Java_com_hardkernel_wiringpi_MainActivity_digitalWrite(JNIEnv* env, jobject obj, jint port, jint onoff) {
    digitalWrite(port, onoff);
}

jint Java_example_jw_gpiodemo_MainActivity_digitalRead(JNIEnv* env, jobject obj, jint port) {
    return digitalRead(port);
}

void Java_com_hardkernel_wiringpi_MainActivity_pinMode(JNIEnv* env, jobject obj, jint port, jint value) {
    pinMode(port, value);
}

jint Java_com_hardkernel_wiringpi_MainActivity_wiringPiSetup(JNIEnv* env, jobject obj) {
    wiringPiSetup();
    return 0;
}
void Java_example_jw_gpiodemo_MainActivity_pullUpDnControl(JNIEnv* env, jobject obj, jint port, jint value) {
    pullUpDnControl(port, value);
}


//}

jint Java_com_hardkernel_wiringpi_MainActivity_bme280_begin(JNIEnv* env, jobject obj) {
    return bme280_begin(I2C_DEVICE);
}

void Java_com_hardkernel_wiringpi_MainActivity_bme280_end(JNIEnv* env, jobject obj) {
    bme280_end();
}


#ifdef __cplusplus
}
#endif
