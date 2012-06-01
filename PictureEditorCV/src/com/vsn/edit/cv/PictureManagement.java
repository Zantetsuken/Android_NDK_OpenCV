package com.vsn.edit.cv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.Display;
import android.view.WindowManager;

/**
 * 画像ファイル管理
 * @author
 *
 */
public class PictureManagement {

	/** 描画する画像 */
	private Bitmap bmp = null;
	/** 一時的に表示する */
	private Path virPath = null;
	/** 一時的に表示する */
	private Paint virPaint = null;
	/** 拡縮率 */
	private float zoomRate;
	/** Viewの幅 */
	private int view_w;
	/** Viewの高さ */
	private int view_h;
	/**
	 * コンストラクタ
	 */
	public PictureManagement(Context context) {
		// 画面サイズ取得
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		int width = disp.getWidth();
		int height = disp.getHeight();

		// Viewサイズの描画用Bitmap作成
		bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		virPath = new Path();
		virPaint = new Paint();

	    virPaint.setAntiAlias(false);
	    virPaint.setColor(Color.argb(255, 164, 199, 57));
	}

	/**
	 * bmpのセッタ
	 * @param bmp
	 */
	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}
	/**
	 * bmpのゲッタ
	 * @return
	 */
	public Bitmap getBmp() {
		return bmp;
	}
	/**
	 * virPathのセッタ
	 * @param virPath
	 */
	public void setVirPath(Path virPath) {
		this.virPath = virPath;
	}
	/**
	 * virPathのゲッタ
	 * @return
	 */
	public Path getVirPath() {
		return virPath;
	}
	/**
	 * virPaintのセッタ
	 * @param virPaint
	 */
	public void setVirPaint(Paint virPaint) {
		this.virPaint = virPaint;
	}
	/**
	 * virPaintのゲッタ
	 * @return
	 */
	public Paint getVirPaint() {
		return virPaint;
	}
	/**
	 * zoomRateのゲッタ
	 * @return
	 */
	public float getZoomRate() {
		return zoomRate;
	}
	/**
	 * zoomRateのセッタ
	 * @param d
	 */
	public void setZoomRate(float zoomRate) {
		this.zoomRate = zoomRate;
	}
	/**
	 * view_wのセッタ
	 * @param view_w
	 */
	public void setView_w(int view_w) {
		this.view_w = view_w;
	}
	/**
	 * view_wのゲッタ
	 * @return
	 */
	public int getView_w() {
		return view_w;
	}
	/**
	 * view_hのセッタ
	 * @param view_h
	 */
	public void setView_h(int view_h) {
		this.view_h = view_h;
	}
	/**
	 * view_hのゲッタ
	 * @return
	 */
	public int getView_h() {
		return view_h;
	}
}
