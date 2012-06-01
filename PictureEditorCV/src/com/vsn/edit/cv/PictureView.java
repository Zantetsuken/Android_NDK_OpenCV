package com.vsn.edit.cv;


import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class PictureView extends View {

	// スタンプの画像
	private Bitmap[] mStamps;
	// スタンプの位置
	private static ArrayList<TypePoint> mPoints;

	public static class TypePoint {
		Point xy;
		int type;

		TypePoint(Point p, int type) {
			this.xy = p;
			this.type = type;
		}
	}
	/**
	 * ローカルフィールド変数宣言
	 */
	/** 画像データ管理クラス */
	private PictureManagement image = null;
	/** 表示用行列 */
	private Matrix matrix = null;

	/** デバッグ表示用タグ */
	private static final String TAG = "DrawView";

	/** リサイズ用 */
	public int noMatrix = 0;

	/**
	 * コンストラクタ
	 * @param context
	 */
	public PictureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		setFocusableInTouchMode(true);

		// 画像管理クラスのインスタンスを生成
		image = new PictureManagement(context);
		image.setView_w(this.getWidth());
		image.setView_h(this.getHeight());

		// 単位行列の生成
		matrix = new Matrix();
		mPoints = new ArrayList<TypePoint>();
		// スタンプの画像を読み込む
		mStamps = new Bitmap[] {
		        BitmapFactory.decodeResource(getContext().getResources(),
		                R.drawable.icon) };
	}
	/**
	 * ウィンドウサイズに画像を調整する
	 * @param c
	 */
	public void setMatrix(Canvas c) {
		int w = image.getBmp().getWidth();	// 画像ファイルの幅
		int h = image.getBmp().getHeight();	// 画像ファイルの高さ
		float center_w = 0.0f;
		float center_h = 0.0f;
		float zoomRate = this.getImage().getZoomRate();

		// 縦と横どちらが大きいか
		if ( w > h ) {
			center_h = (this.getImage().getView_h() - h*zoomRate) / 2.0f;
		} else if ( w < h ) {
			center_w = (this.getImage().getView_w() - w*zoomRate) / 2.0f;
		} else {
			if ( this.getWidth() < this.getHeight() ) {
				center_h = (this.getImage().getView_h() - h*zoomRate) / 2.0f;
			} else {
				center_w = (this.getImage().getView_w() - w*zoomRate) / 2.0f;
			}
		}

		matrix.reset();		// 行列を初期化
//		Log.d(TAG,"zoomRate="+zoomRate);
		// 行列を作成する。
//		matrix.postRotate(frameCount); // 回転
		matrix.postScale(zoomRate, zoomRate); // 画面サイズに拡縮

		matrix.postTranslate(center_w, center_h); // 画面中央寄せ

		matrix.postConcat(c.getMatrix()); // 元の描画位置を加算する

		// matrixをセットする
		c.setMatrix(matrix);
	}

	/**
	 * リサイズ後の画像をウィンドウに設定する
	 */
	public void setResizeImage(Canvas c) {
		int w = image.getBmp().getWidth();	// 画像ファイルの幅
		int h = image.getBmp().getHeight();	// 画像ファイルの高さ
		float center_w = 0.0f;
		float center_h = 0.0f;
		float zoomRate = this.getImage().getZoomRate();

		int canvasW = c.getWidth();
		int canvasH = c.getHeight();

		// 描画領域と画像どちらが大きいか
		if ( canvasW > w ) {
			// 画像幅より描画領域のほうが大きい場合
			center_w = (canvasW / 2) - (w / 2);
		}

		if(canvasH > h){
			// 画像高さより描画領域のほうが大きい場合
			center_h = (canvasH / 2) - (h / 2);
		}

		matrix.reset();		// 行列を初期化
		matrix.postTranslate(center_w, center_h); // 画面中央寄せ

		// matrixをセットする
		c.setMatrix(matrix);

		// 各値をリセット
		noMatrix = 0;
	}

	/**
	 *  描画処理
	 */
	@Override
	public void onDraw(Canvas c) {
		if(noMatrix == 0){
			// ウィンドウサイズに合わせる
			this.setMatrix(c);
		}else{
			// リサイズ後の画像をそのまま設定
			this.setResizeImage(c);
		}

		// bitmapを画面に描画
		c.drawBitmap(image.getBmp(), 0, 0, null);
		// Pathを画面に描画
	    c.drawPath(image.getVirPath(), image.getVirPaint());
		// スタンプの位置がある場合は、画像に描画する
		if (mPoints.size() > 0) {
			for (TypePoint point : mPoints) {
				if (point.type > 0) {
					Bitmap bmp = mStamps[point.type - 1];
					c.drawBitmap(bmp, point.xy.x, point.xy.y, null);

//					offScreen.drawBitmap(image.getBmp(), null, null);
//					offScreen.drawBitmap(bmp, null, null);
//					c.drawBitmap(offScreen, 0, 0, null);
				}
			}
		}

	}
	/**
	 * 画像データを更新
	 * @param bmp
	 */
	public void refreshImage(Bitmap bmp){
		image.setBmp(bmp);
		if(image.getBmp() != null){
			image.setView_w(this.getWidth());
			image.setView_h(this.getHeight());
			int w = image.getBmp().getWidth();	// 画像の幅
			int h = image.getBmp().getHeight();	// 画像の高さ

			float zoomRate_w = ((float)this.getWidth() / (float)image.getBmp().getWidth());
			float zoomRate_h = ((float)this.getHeight() / (float)image.getBmp().getHeight());

			// 大きい方に合わせる
			if ( w > h ) {
				image.setZoomRate( zoomRate_w );
			} else if (w < h) {
				image.setZoomRate( zoomRate_h );
			} else {
				if ( zoomRate_w < zoomRate_h ) {
					image.setZoomRate( zoomRate_w );
				} else {
					image.setZoomRate( zoomRate_h );
				}
			}
			this.invalidate();
		}
	}
	/**
	 * imageのセッタ
	 * @param image
	 */
	public void setImage(PictureManagement image) {
		this.image = image;
	}
	/**
	 * imageのゲッタ
	 * @return
	 */
	public PictureManagement getImage() {
		return image;
	}
	/***
	 * スタンプの位置を設定する
	 *
	 * @param point
	 */
	public void addPoint(Point point, int type) {
		mPoints.add(new TypePoint(point, type));
	}

	/***
	 * スタンプの位置を返却する
	 *
	 * @return
	 */
	public ArrayList<TypePoint> getPoints() {
		return mPoints;
	}

	/***
	 * スタンプをリセットする
	 *
	 */
	public void resetPoint() {
		mPoints.clear();
	}

}
