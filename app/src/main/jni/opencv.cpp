
#include "com_hardkernel_wiringpi_MainActivity.h"
#include "../../../../../AppData/Local/Android/Sdk/ndk-bundle/sysroot/usr/include/jni.h"

#include <opencv2/core/core.hpp>

#include <opencv2/imgproc/imgproc.hpp>


using namespace cv;

extern "C" {
JNIEXPORT void JNICALL Java_com_hardkernel_wiringpi_MainActivity_ConvertRGBtoGray

(JNIEnv *env, jobject instance, jlong matAddrInput, jlong matAddrResult){

Mat &matInput = *(Mat *)matAddrInput;
Mat &matResult = *(Mat *)matAddrResult;

cvtColor(matInput, matResult,CV_RGB2GRAY);

}
}