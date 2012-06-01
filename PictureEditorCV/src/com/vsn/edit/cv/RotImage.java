package com.vsn.edit.cv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 回転処理メソッド群
 *
 */
public class RotImage {

	private PictureDataManagement data;

	public native void		rotImg( int rotMode );
	public native void		setSourceImage(int[] pixels, int width, int height);
	public native byte[]	getSourceImage();

	/* load our native library */
    static
	{
        System.loadLibrary("rotate");
    }

    /**
     *
     */
    RotImage(){
    	data = new PictureDataManagement();
    }

	/**
	 * 画像を回転させる
	 * @param bitmap 回転させる画像
	 */
    public void rot( Bitmap bitmap, int rotMode )
	{
    	int width = bitmap.getWidth();
    	int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		copy.getPixels(pixels, 0, width, 0, 0, width, height);

		setSourceImage(pixels, width, height);
		rotImg( rotMode );
		byte[] imageData = (byte[])getSourceImage();
		bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
		copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		data.setPictureBuffer(copy);
    }
}
