package com.vsn.edit.cv;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import com.vsn.edit.cv.PictureManagement;

/**
 * 描画ツール
 * @author
 *
 */
public class DrawTool {

	/**ペンのようなクラス */
    private Paint paint = null;
    /** 画面表示用Paint */
    private Paint virPaint = null;
	/** 画像書き込み用キャンバス */
	private Canvas bmpCanvas = null;

	/** 拡縮率 */
	private float zoomRate;	// 初期値

	/** 座標 */
	private PointF startP = null;	// タッチ開始座標
	private PointF oldP   = null;	// タッチ前回座標
	private PointF endP   = null;	// タッチ終了座標
	private PointF offset   = null;	// 描画オフセット座標

    /**
     *  使用する道具を定義
     * @author 1205483
     *
     */
	public enum ToolStatus {
    	FREELINE,		// フリーライン
    	STRAIGHTLINE,	// 直線
    	ERASER,			// 消しゴム
    	RANGEAREA,		// 範囲指定
    }

	/** ツールが何を指定しているか */
	private ToolStatus toolStatus;
	/** 色 */
	private int myColor;
	/** ペンの幅 */
	private int myPenWidth;
	/** パス */
	private Path path;

	/** デバッグ表示用タグ */
	private static final String TAG = "ToolBox";
	/** 描画する頻度 */
	private static final float TOUCH_TOLERANCE = 4;

    /**
     * コンストラクタ
     */
    public DrawTool(){
        paint = new Paint();
		// ペンツールの色指定：黒
        myColor = Color.BLACK;
//      myColor = Color.BLUE;
		paint.setColor(myColor);	// 色
//        paint.setAntiAlias(true);	// アンチエイリアス
//        paint.setDither(true);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);	// 円描画
        paint.setStrokeWidth(12);	// 太さ

		// フリーライン
		toolStatus = ToolStatus.FREELINE;

		startP = new PointF();	// タッチ開始座標
		oldP   = new PointF();	// タッチ前回座標
		endP   = new PointF();	// タッチ終了座標
		offset   = new PointF();	// タッチ終了座標
    }

    /**
     * 画像管理クラスをセットする
     * @param image
     */
    public void setImageScreen(PictureManagement image) {

        // 書き込み用キャンバス作成
		bmpCanvas = new Canvas(image.getBmp());
		// 各種値を格納
        this.path = image.getVirPath();
        this.virPaint = image.getVirPaint();
        this.zoomRate = image.getZoomRate();
		int w = image.getBmp().getWidth();
		int h = image.getBmp().getHeight();

		float center_w = 0.0f;
		float center_h = 0.0f;
		// 縦と横どちらが大きいか
		if ( w > h ) {
			center_h = ((float)image.getView_h() - (float)h*zoomRate) / 2.0f;
		} else if ( w < h ) {
			center_w = ((float)image.getView_w() - (float)w*zoomRate) / 2.0f;
		} else {
			if ( image.getView_w() < image.getView_h() ) {
				center_h = ((float)image.getView_h() - (float)h*zoomRate) / 2.0f;
			} else {
				center_w = ((float)image.getView_w() - (float)w*zoomRate) / 2.0f;
			}
		}

		// 算出したオフセットを格納
		offset.x = center_w;
		offset.y = center_h;
    }

    /**
     *	現在の道具使用状況に合わせて処理を行う
     */
    public boolean excuteLauncher(MotionEvent event) {
	boolean flg = false;
    	switch (toolStatus) {
    	case FREELINE:		// フリーライン
    		flg = this.drawLine(event);
    		break;
    	case STRAIGHTLINE:	// 直線
    		flg = this.drawStraightLine(event);
    		break;
    	case ERASER:		// 消しゴム
    		flg = this.eraser(event);
    		break;
    	case RANGEAREA:		// 範囲指定
    		this.changeRangeArea(event);
    		flg = true;
    		break;
    	default:			// その他
    		break;
    	}
    	return flg;
    }

    /**
     * フリーライン
     * @return 連続するか
     */
	public boolean drawLine(MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN: //最初のポイント
			// 開始座標の保持
			startP.x = event.getX();
			startP.y = event.getY();
			// 前の座標の保持
			oldP.x = event.getX();
			oldP.y = event.getY();
			break;
		case MotionEvent.ACTION_MOVE: //途中のポイント
            float dx = Math.abs(event.getX() - oldP.x);
            float dy = Math.abs(event.getY() - oldP.y);
            // 指定値をこえていれば
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
    			// 線
    			bmpCanvas.drawLine(
    					(oldP.x - offset.x) / zoomRate,
    					(oldP.y - offset.y) / zoomRate,
    					(event.getX() - offset.x) / zoomRate ,
    					(event.getY() - offset.y) / zoomRate ,
    					paint);
    			// 前の座標の保持
    			oldP.x = event.getX();
    			oldP.y = event.getY();
            }
			break;
		case MotionEvent.ACTION_UP: //最後のポイント
			// 終了座標の保持
			endP.x = event.getX();
			endP.y = event.getY();
        	// 点描画
			bmpCanvas.drawPoint(
					(event.getX() - offset.x) / zoomRate,
					(event.getY() - offset.y) / zoomRate,
					paint);
			break;
		default:
			break;
		}
		return true;
	}
	/**
	 *  直線
	 *  開始座標
	 *  終了座標
	 */
	public boolean drawStraightLine(MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN: //最初のポイント
			// 開始座標の保持
			startP.x = event.getX();
			startP.y = event.getY();

			path.reset();	// パス初期化

			virPaint.setStrokeCap(Paint.Cap.BUTT);	// 線描画
			virPaint.setStyle(Paint.Style.STROKE); // 塗りつぶさない

			virPaint.setColor(paint.getColor());
			virPaint.setStrokeCap(paint.getStrokeCap());
			virPaint.setStrokeWidth(paint.getStrokeWidth());

			break;
		case MotionEvent.ACTION_MOVE: //途中のポイント

		    path.reset();	// パス初期化
			path.moveTo( ( startP.x - offset.x) / zoomRate , (startP.y - offset.y) / zoomRate);
		    path.lineTo( (event.getX() - offset.x) / zoomRate , (event.getY() - offset.y) / zoomRate );


			break;
		case MotionEvent.ACTION_UP: //最後のポイント
			//TODO Viewから出たら描画しない

		    path.reset();	// パス初期化

			// 終了座標の保持
			endP.x = event.getX();
			endP.y = event.getY();
			// 線
			bmpCanvas.drawLine( (startP.x - offset.x) / zoomRate,
					(startP.y - offset.y) / zoomRate,
					(event.getX() - offset.x) / zoomRate,
					(event.getY() - offset.y) / zoomRate, paint);

			break;
		default:
			break;
		}
		return true;
	}
	/**
	 *  消しゴム
	 *  現在座標
	 *  前回座標
	 */
	public boolean eraser(MotionEvent event) {
		paint.setColor(Color.WHITE);
		boolean flg = this.drawLine(event);
		paint.setColor(myColor);
		return flg;
	}

	/**
	 *  色変更
	 * @param color
	 * @return
	 */
	public boolean changePenColor(int color) {
		myColor = color;
		paint.setColor(myColor);
		return false;
	}
	/**
	 * ペンの幅を変更する
	 * @param myPenWidth
	 * @return
	 */
	public boolean  changePenSize(int myPenWidth) {
		this.myPenWidth = myPenWidth;
		return false;
	}
	// 拡大縮小倍率の変更
//	public void changeZoomRate(int zoom) {
//		//TODO 未実装
//		this.bmpCanvas.setDensity(zoom);
//	}

	/**
	 * 範囲指定描画
	 * @param path
	 */
	public void drawRangeArea(Path path) {
		virPaint.setColor(Color.argb(255, 164, 199, 57));
		virPaint.setStrokeCap(Paint.Cap.BUTT);	// 線描画
        virPaint.setStrokeWidth(1);	// 太さ
		virPaint.setStyle(Paint.Style.STROKE); // 塗りつぶさない
		virPaint.setPathEffect(new DashPathEffect(new float[] {10, 5, 5, 5},1));	// 破線

	    path.reset();	// パスを削除
	    path.addRect(new RectF(
	    		(this.startP.x - offset.x) / zoomRate,
	    		(this.startP.y - offset.y) / zoomRate,
	    		(this.oldP.x - offset.x) / zoomRate,
	    		(this.oldP.y - offset.y) / zoomRate),
	    		Path.Direction.CCW);

	}

	/**
	 *  範囲指定
	 *  開始座標
	 *  終了座標
	 */
	public void changeRangeArea(MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN: //最初のポイント
			// 開始座標の保持
			startP.x = event.getX();
			startP.y = event.getY();
			break;
		case MotionEvent.ACTION_MOVE: //途中のポイント
			drawRangeArea(path);
			oldP.x = event.getX();
			oldP.y = event.getY();

			break;
		case MotionEvent.ACTION_UP: //最後のポイント
			// 終了座標の保持
			endP.x = event.getX();
			endP.y = event.getY();
			break;
		default:
			break;
		}
		Log.d(TAG,""+event.getAction());
	}
	/**
	 * toolStatusのゲッタ
	 * @return
	 */
	public ToolStatus getToolStatus() {
		return toolStatus;
	}
	/**
	 * toolStatusのセッタ
	 * @param toolStatus
	 */
	public void setToolStatus(ToolStatus toolStatus) {
		this.toolStatus = toolStatus;
	}
	/**
	 * 線の太さ設定
	 * @param width
	 */
	public void setWidth(float width){
		paint.setStrokeWidth(width);
	}

	/**
	 * 線の太さ設定
	 * @param r
	 * @param g
	 * @param b
	 */
	public void setColor(int r, int g, int b){
		this.myColor = Color.argb(255, r, g, b); //ARGB(255, r, g, b);
		paint.setColor(myColor);
	}
}
