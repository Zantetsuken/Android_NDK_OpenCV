package com.vsn.edit.cv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 反転処理メソッド群
 *
 */
public class FlipImage {

	private PictureDataManagement data;

	public native void		flipImg( int flipMode );
	public native void		setSourceImage(int[] pixels, int width, int height);
	public native byte[]	getSourceImage();

	/* load our native library */
    static
	{
        System.loadLibrary("flip");
    }

    /**
     * 
     */
    FlipImage(){
    	data = new PictureDataManagement();
    }

	/**
	 * 画像を反転させる
	 * @param bitmap	反転させる画像
	 * @param flipMode	反転モード
	 */
    public void flip( Bitmap bitmap, int flipMode )
	{
    	int width = bitmap.getWidth();
    	int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		copy.getPixels(pixels, 0, width, 0, 0, width, height);

		setSourceImage(pixels, width, height);
		flipImg( flipMode );
		byte[] imageData = (byte[])getSourceImage();
		bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
		copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		data.setPictureBuffer(copy);
    }
}
