package com.vsn.edit.cv;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * 画像データ管理
 * @author 1202542
 *
 */
public class PictureDataManagement {
	private static Bitmap pictureBuffer = null;		// 画像のピクセルデータ

	/**
	 *
	 * @param pixelBuffer
	 */
	public void setPictureBuffer(Bitmap pixelBuffer){
		pictureBuffer = pixelBuffer;
		Log.d("pictureData", "pixelBuffer in");
	}

	/**
	 *
	 * @param pixelBuffer
	 */
	public Bitmap getPictureBuffer(){
		Log.d("pictureData", "pixelBuffer out");
		return pictureBuffer;
	}

	/**
	 *
	 */
	public int[] toPixel(){
		int[] pixel = new int[pictureBuffer.getWidth()*pictureBuffer.getHeight()];

		return pixel;
	}
}
