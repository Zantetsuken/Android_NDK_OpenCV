package com.vsn.edit.cv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


/**
 * リサイズ処理
 * 
 * リサイズは、2倍と1/2のみ可能
 * 	※変更の可能性あり
 * 
 */
public class ImageResize{
	
	private PictureDataManagement picture;
	
	/**
	 * 画像をリサイズするネイティブコード
	 * @param	mode	リサイズの種類
	 */
	public native void resizeImage(int mode);
	
	/**
	 * Bitmap形式に変換する画像を設定
	 * @param	pixels	リサイズする画像のピクセル
	 * 			width	リサイズする画像の幅
	 * 			height	リサイズする画像の高さ
	 */
	public native void setSourceImage(int[] pixels, int width, int height);
	
	/**
	 * Bitmap形式に変換した画像を取得
	 */
	public native byte[] getSourceImage();
	
	/* load our native library */
    static {
        System.loadLibrary("resize");
    }
    
    /**
     * コンストラクタ
     */
    ImageResize(){
    	picture = new PictureDataManagement();
    }
    
    /**
     * 画像をリサイズ
     * @param	bitmap　	リサイズする画像
     * 			mode	リサイズパターン
     */
    public void ResizeImage(Bitmap bitmap, int mode){
    	if(bitmap != null){
			Log.d("bitmap", "not null");
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int[] pixels = new int[width * height];
			// Bitmap画像をIplImage画像へ変換
			Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			copy.getPixels(pixels, 0, width, 0, 0, width, height);
			setSourceImage(pixels, width, height);
			// リサイズ処理
			resizeImage(mode);
			// IplImage画像をBitmap画像へ変換
			byte[] imageData = getSourceImage();
			bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
			copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			picture.setPictureBuffer(copy);
		}
    }
}