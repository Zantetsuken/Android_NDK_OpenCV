/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>
#include <android/log.h>
#include "cv.h"
#include "cxcore.h"
#include "bmpfmt.h"
#define ANDROID_LOG_VERBOSE ANDROID_LOG_DEBUG
#define LOG_TAG_ROT "ROTATE"
#define LOGV(...) __android_log_print(ANDROID_LOG_SILENT, LOG_TAG_FLIP, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG_FLIP, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG_FLIP, __VA_ARGS__)
#ifdef __cplusplus
extern "C" {
#endif
IplImage* pImage = NULL;
IplImage* dstImg = NULL;		// リサイズ後画像格納用
IplImage* loadPixels(int* pixels, int width, int height);
IplImage* getIplImageFromIntArray(JNIEnv* env, jintArray array_data,
		jint width, jint height);

/**
 * flipImg
 * 画像を回転させる
 */
JNIEXPORT void JNICALL Java_com_vsn_edit_cv_RotImage_rotImg(
	JNIEnv			*env,
	jobject			thiz,
	jint			p_rotMode)			// 回転モード
{
	LOGI("In:cvRot()");

	int angle = 0;

#ifdef LOG_TAG_ROT
	switch(p_rotMode){
	case 0:
		angle = 90;
		dstImg = cvCreateImage(cvSize((int)(pImage->height),(int)(pImage->width)),IPL_DEPTH_8U,3);		//　縮小用の作業領域
		break;
	case 1:
		angle = 180;
		dstImg = cvCreateImage(cvSize((int)(pImage->width),(int)(pImage->height)),IPL_DEPTH_8U,3);		//　縮小用の作業領域
		break;
	case 2:
		angle = 270;
		dstImg = cvCreateImage(cvSize((int)(pImage->height),(int)(pImage->width)),IPL_DEPTH_8U,3);		//　縮小用の作業領域
		break;
	case 3:
		angle = 360;
//		angle = 0;
		dstImg = cvCreateImage(cvSize((int)(pImage->width),(int)(pImage->height)),IPL_DEPTH_8U,3);		//　縮小用の作業領域
		break;
	default:
		//念のため
		angle = 0;
		LOGI("angle: %d", p_rotMode);
		break;
	}

#else
	if (dstImg != NULL) {
		cvReleaseImage(&dstImg);		// 画像とメモリ領域の開放
		dstImg = NULL;
		LOGI("cvReleaseImage Done");
	}
	if((p_rotMode == 0) || (p_rotMode = 2)){	// 90度もしくは270度の時
		if(p_rotMode == 0){
			angle = 90;
		}else{
			angle = 270;
		}
		dstImg = cvCreateImage(cvSize((int)(pImage->height),(int)(pImage->width)),IPL_DEPTH_8U,3);		//　縮小用の作業領域
	}else if((p_rotMode == 1) || (p_rotMode = 3)){	// 180度もしくは360度の時
		if(p_rotMode == 2){
			angle = 180;
		}else{
			angle = 360;
		}
		dstImg = cvCreateImage(cvSize((int)(pImage->width),(int)(pImage->height)),IPL_DEPTH_8U,3);		//　縮小用の作業領域
	}
#endif
	LOGI("pImage         : width = %d height = %d",pImage->width,pImage->height);
	LOGI("dstImg         : width = %d height = %d",dstImg->width,dstImg->height);
	LOGI("ROTMODE: %d angle: %d", p_rotMode,angle);

	float m[6];
	CvMat M;
	// (2)回転のための行列（アフィン行列）要素を設定し，CvMat行列Mを初期化する
	m[0] = (float) (cos (angle * CV_PI / 180.));
	m[1] = (float) (-sin (angle * CV_PI / 180.));
	m[2] = pImage->width * 0.5;
	m[3] = -m[1];
	m[4] = m[0];
	m[5] = pImage->height * 0.5;

	cvInitMatHeader (&M, 2, 3, CV_32FC1, m, CV_AUTOSTEP);

	LOGI("cvGetQuadrangleSubPix(in) : width = %d height = %d",pImage->width,pImage->height);
	// (3)指定された回転行列により，GetQuadrangleSubPixを用いて画像全体を回転させる
	cvGetQuadrangleSubPix ( pImage, dstImg, &M );

	LOGI("cvGetQuadrangleSubPix(out): width = %d height = %d",dstImg->width,dstImg->height);
	//回転後の状態を保存する
	pImage = dstImg;

	// cvReleaseImage : ヘッダと画像データを解放する
//	cvReleaseImage(&dstImg);		// 作業領域の開放
	LOGI("Out:cvRot()");
}

/**
 * setSourceImage
 * 画像のセット
 * JNIEnv * : すべての JNI 関数のポインタを格納する構造体を指すポインタ
 * jintArray photo_data : ピクセルデータ
 * jint width : 幅
 * jint height : 高さ
 */
JNIEXPORT void JNICALL Java_com_vsn_edit_cv_RotImage_setSourceImage(
		JNIEnv* env, jobject thiz, jintArray photo_data, jint width,
		jint height) {
	// すでに画像がセットされていたら開放する
	if (pImage != NULL) {
		cvReleaseImage(&pImage);		// 画像とメモリ領域の開放
		pImage = NULL;
	}
	pImage = getIplImageFromIntArray(env, photo_data, width, height);	// Java側にある本体のピクセルデータを取得し、IPL画像を作成する
	if (pImage == NULL) {
		return;
	}
	LOGI("Load Image Done.");
}

/**
 * getSourceImage
 * Windows Bitmapに変換してJava側に画像を返す
 */
JNIEXPORT jbyteArray JNICALL Java_com_vsn_edit_cv_RotImage_getSourceImage(
		JNIEnv* env, jobject thiz) {
	if (pImage == NULL) {
		LOGE("No source image.");
		return 0;
	}
	// cvFlip : 2次元配列を垂直，水平，または両軸で反転する
	cvFlip(pImage, 0, 0);			// 水平軸反転(上下反転)する
	int width = pImage->width;			// 幅
	int height = pImage->height;		// 高さ
	int rowStep = pImage->widthStep;	// 画像の行のバイトサイズ
	int headerSize = 54;				//　ヘッダーサイズ
	int imageSize = rowStep * height;	// 画像サイズ
	int fileSize = headerSize + imageSize;	// ファイルサイズ
	unsigned char* image = new unsigned char[fileSize];	// imgageバッファ,fileSize分領域を確保
	// ファイルヘッダ作成
	struct bmpfile_header* fileHeader = (struct bmpfile_header*) (image);	// bmpfile_header : bmpfmt.hで定義
	fileHeader->magic[0] = 'B';		// BMPファイルの識別子
	fileHeader->magic[1] = 'M';
	fileHeader->filesz = fileSize;	// ファイルサイズ
	fileHeader->creator1 = 0;		// 予約領域
	fileHeader->creator2 = 0;		// 予約領域
	fileHeader->bmp_offset = 54;	// ファイル先頭から画像データまでのオフセット(byte)
	// 情報ヘッダ作成
	struct bmp_dib_v3_header_t* imageHeader =				// bmp_dib_v3_header_t : bmpfmt.hで定義
			(struct bmp_dib_v3_header_t*) (image + 14);		// imageバッファの先頭アドレスからファイルヘッダ分アドレスをずらした箇所に情報ヘッダを入れる
	imageHeader->header_sz = 40;		// 情報ヘッダのサイズ
	imageHeader->width = width;			//　画像幅
	imageHeader->height = height;		// 画像高さ
	imageHeader->nplanes = 1;			//　プレーン数
	imageHeader->bitspp = 24;			// 1画素あたりのデータサイズ
	imageHeader->compress_type = 0;		// 圧縮形式(0 : 無圧縮)
	imageHeader->bmp_bytesz = imageSize;	//画像データ部のサイズ
	imageHeader->hres = 0;				// 横方向解像度 (1mあたりの画素数)
	imageHeader->vres = 0;				// 縦方向解像度 (1mあたりの画素数)
	imageHeader->ncolors = 0;			// 格納されているパレット数
	imageHeader->nimpcolors = 0;		//　重要なパレットのインデックス
	//　画像データ部作成
	memcpy(image + 54, pImage->imageData, imageSize);	//imageバッファの先頭アドレスからファイルヘッダ＋情報ヘッダ分アドレスをずらした箇所に画像データ部を入れる
	// Java側のデータに変換して返す
	jbyteArray bytes = env->NewByteArray(fileSize);		// NewByteArray : Javaのbyte配列を返す
	if (bytes == 0) {
		LOGE("Error in creating the image.");
		delete[] image;
		return 0;
	}
	env->SetByteArrayRegion(bytes, 0, fileSize, (jbyte*) image);	// imageバッファの内容ををbytesにコピー
	LOGI("Get Image Done.");
	delete[] image;		// imageバッファの開放
	return bytes;
}

/**
 * loadPixels
 * ピクセルデータからIPL画像作成
 */
IplImage* loadPixels(int* pixels, int width, int height) {
	int x, y;
	IplImage *img = cvCreateImage(cvSize(width, height), IPL_DEPTH_8U, 3);	//　ヘッダの作成と領域の確保
	unsigned char* base = (unsigned char*) (img->imageData);		// アライメントが調整された画像データへのポインタ
	unsigned char* ptr;		//　画像の行
	for (y = 0; y < height; y++) {
		ptr = base + y * img->widthStep;		// 次の行へ
		for (x = 0; x < width; x++) {
			// blue
			ptr[3 * x] = pixels[x + y * width] & 0xFF;
			// green
			ptr[3 * x + 1] = pixels[x + y * width] >> 8 & 0xFF;
			// blue (red?)
			ptr[3 * x + 2] = pixels[x + y * width] >> 16 & 0xFF;
		}
	}
	return img;
}
/**
 *	getIplImageFromIntArray
 *	Java側にある本体のピクセルデータを取得し、IPL画像を作成する
 */
IplImage* getIplImageFromIntArray(JNIEnv* env, jintArray array_data,
		jint width, jint height) {
	int *pixels = env->GetIntArrayElements(array_data, 0);	// int配列array_dataの本体のポインタをpixelsに格納
	if (pixels == 0) {
		LOGE("Error getting int array of pixels.");
		return 0;
	}
	IplImage *image = loadPixels(pixels, width, height);	// IPL画像作成
	env->ReleaseIntArrayElements(array_data, pixels, 0);	// array_dataに内容をコピーし、pixelsのバッファを開放
	if (image == 0) {
		LOGE("Error loading pixel array.");
		return 0;
	}
	return image;
}

#ifdef __cplusplus
}
#endif

