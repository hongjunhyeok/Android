
#include "com_hardkernel_wiringpi_MainActivity.h"
#include "../../../../../AppData/Local/Android/Sdk/ndk-bundle/sysroot/usr/include/jni.h"
#include "BlobLabeling.h"

#include <opencv2/core/core.hpp>

#include <opencv2/imgproc/imgproc.hpp>

#include <opencv2/features2d/features2d.hpp>
#include <opencv/cv.h>
#include <vector>
#include <string.h>

using namespace cv;

extern "C" {
    JNIEXPORT void JNICALL Java_com_hardkernel_wiringpi_MainActivity_ConvertRGBtoGray

        (JNIEnv *env, jobject instance, jlong matAddrInput, jlong matAddrResult) {

        Mat &matInput = *(Mat *) matAddrInput;
        Mat &matResult = *(Mat *) matAddrResult;

        cvtColor(matInput, matResult, CV_LOAD_IMAGE_COLOR);

    }
    JNIEXPORT void JNICALL Java_com_hardkernel_wiringpi_MainActivity_Labling
                (JNIEnv *, jobject, jlong addrGray, jlong addrRgba) {
            Mat &mRgb = *(Mat *) addrRgba;
            Mat &mGray = *(Mat *) addrGray;

            IplImage temp = mGray;  //Mat -> IplImage
            IplImage *iplGray = cvCreateImage(cvGetSize(&temp), temp.depth, 1);
            cvThreshold(iplGray, iplGray, 128, 255, CV_THRESH_BINARY_INV);

            CBlobLabeling blob;
            blob.SetParam(iplGray, 100);
            blob.DoLabeling(); //·¹ÀÌºí¸µ
            for (int i = 0; i < blob.m_nBlobs; i++) {
                CvPoint pt1 = cvPoint(blob.m_recBlobs[i].x/2,
                                      blob.m_recBlobs[i].y/2);
                CvPoint pt2 = cvPoint(pt1.x + blob.m_recBlobs[i].width,
                                      pt1.y + blob.m_recBlobs[i].height);
                rectangle(mRgb, pt1, pt2, Scalar(255, 0, 0), 1, 6, 1);
            }
            cvReleaseImage(&iplGray);
        }

    }


