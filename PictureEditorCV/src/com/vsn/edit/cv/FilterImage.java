package com.vsn.edit.cv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class FilterImage {
	private PictureDataManagement data;

	public native void setSourceImage(int[] pixels, int width, int height);
	public native byte[] getSourceImage();
	public native void convertToGrayScale();
	public native void convertToBokashi();
	public native void convertToReverse();
	public native void convertToMosaic();
	/* load our native library */
    static {
        System.loadLibrary("opencv");
    }
    
    /**
     * 
     */
    FilterImage(){
    	data = new PictureDataManagement();
    }
    
    /**
     * 色反転
     * @param bitmap
     */
    public void ReverseFilter(Bitmap bitmap){
    	int width = bitmap.getWidth();
    	int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		copy.getPixels(pixels, 0, width, 0, 0, width, height);
		
		setSourceImage(pixels, width, height);
		convertToReverse();
		byte[] imageData = (byte[])getSourceImage();
		bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
		copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);	// 2011/8/18　追加
		data.setPictureBuffer(copy);	// 2011/8/18　変更
    }

    /**
     * モザイク
     * @param bitmap
     */
    public void MosaicFilter(Bitmap bitmap){
    	int width = bitmap.getWidth();
    	int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		copy.getPixels(pixels, 0, width, 0, 0, width, height);
		
		setSourceImage(pixels, width, height);
		convertToMosaic();
		byte[] imageData = (byte[])getSourceImage();
		bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
		copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);	// 2011/8/18　追加
		data.setPictureBuffer(copy);	// 2011/8/18　変更
    }

    /**
     * モノクロ(グレースケール)
     * @param bitmap
     */
    public void MonochroFilter(Bitmap bitmap){
    	int width = bitmap.getWidth();
    	int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		copy.getPixels(pixels, 0, width, 0, 0, width, height);
		
		setSourceImage(pixels, width, height);
		convertToGrayScale();
		byte[] imageData = (byte[])getSourceImage();
		bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
		copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);	// 2011/8/18　追加
		data.setPictureBuffer(copy);	// 2011/8/18　変更
    }

    /**
     * ぼかし
     * @param bitmap
     */
    public void ShadeFilter(Bitmap bitmap){
    	if(bitmap != null){
	    	int width = bitmap.getWidth();
	    	int height = bitmap.getHeight();
			int[] pixels = new int[width * height];
			Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			copy.getPixels(pixels, 0, width, 0, 0, width, height);
			
			setSourceImage(pixels, width, height);
			convertToBokashi();
			byte[] imageData = (byte[])getSourceImage();
//			for(int i = 0; i < 100; i++){
//				System.out.println(Byte.toString(imageData[i]));
//			}
			bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
			if(bitmap == null)Log.d("bitmap", "null");
			copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);	// 2011/8/18　追加
			data.setPictureBuffer(copy);	// 2011/8/18　変更
    	}else Log.d("shade", "null");
    }

}
