package com.vsn.edit.cv;

import com.vsn.edit.cv.DrawTool;
import com.vsn.edit.cv.FilterImage;
import com.vsn.edit.cv.PictureDataManagement;
import com.vsn.edit.cv.PictureView;
import com.vsn.edit.cv.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;

public class PictureEditorCVActivity extends Activity  implements OnClickListener {


	private Button menuButton1;			// メニューボタン１
	private ImageButton penButton;		// 線ボタン
	private ImageButton eraserButton;	// 消しゴムボタン
	private ImageButton redoButton;		// 戻るボタン
	private ImageButton undoButton;		// 進むボタン
	private TableRow menuRow1;			// 上メニュー
	private PictureDataManagement pictureData;
    private PictureView view;
    private DrawTool drawTool;
    private FilterImage filter;
	private FlipImage	flipImg;		// 画像反転
    private RotImage rotimg;			// 画像回転
	private ImageResize imgResize;		// リサイズ

    /**
     * Define一覧
     */

	/*------------------*/
	/* メニュー表示用定義	*/
	/*------------------*/
	// MenuID
	private static final int	MENU_ID1	=	Menu.FIRST;
	private static final int	MENU_ID2	=	Menu.FIRST + 1;
	private static final int	MENU_ID3	=	Menu.FIRST + 2;
	private static final int	MENU_ID4	=	Menu.FIRST + 3;
	private static final int	MENU_ID5	=	Menu.FIRST + 4;
	private static final int	MENU_ID6	=	Menu.FIRST + 5;
	private static final int	MENU_ID7	=	Menu.FIRST + 6;
	// Menu項目名
	private static final String	MENU_NAME_1	=	"ファイル";
	private static final String	MENU_NAME_2	=	"太さ";
	private static final String	MENU_NAME_3	=	"色";
	private static final String	MENU_NAME_4	=	"フィルタ";
	private static final String	MENU_NAME_5	=	"反転";
	private static final String	MENU_NAME_6	=	"リサイズ";
	private static final String	MENU_NAME_7	=	"回転";

	/*------------------*/
	/* 反転定義			*/
	/*------------------*/
	// タイトル
	private static final String	FLIP_TITLE		=	"反転";

	// メニュー項目名
	private static final String	FLIP_NAME_TB	=	"上下反転";
	private static final String	FLIP_NAME_LR	=	"左右反転";
	private static final String	FLIP_NAME_TBLR	=	"上下左右反転";

	// 反転モード
	private static final int	FLIP_MODE_TB	=	0;	// 上下反転
	private static final int	FLIP_MODE_LR	=	1;	// 左右反転
	private static final int	FLIP_MODE_TBLR	=	-1;	// 上下左右反転
	// 回転モード格納用
	private int fliptype = 0;

	/*------------------*/
	/* リサイズ項目定義	*/
	/*------------------*/
	// ダイアログ表示項目
	private static final String RESIZE_TITLE = "リサイズ";	// タイトル
	private static final String RESIZE_1 = "1024×768";	// 選択項目
	private static final String RESIZE_2 = "640×480";	// 選択項目
	// リサイズモード通知用項目
	private static final int RESIZE_NORMAL1 = 0;		// 1024×768
	private static final int RESIZE_NORMAL2 = 1;		// 640×480
	// リサイズモード格納用
	private int resizetype = 0;

	/*------------------*/
	/* 回転定義			*/
	/*------------------*/
	// タイトル
	private static final String	ROT_TITLE		=	"回転";
	// メニュー項目名
	private static final String	ROT_NAME_90		=	"90度回転";
	private static final String	ROT_NAME_180	=	"180度回転";
	private static final String	ROT_NAME_270	=	"270度回転";
	private static final String	ROT_NAME_360	=	"360度回転";
	// 回転モード
	private static final int	ROT_MODE_90		=	0;	// 90度回転
	private static final int	ROT_MODE_180	=	1;	// 180度回転
	private static final int	ROT_MODE_270	=	2;	// 270度回転
	private static final int	ROT_MODE_360	=	3;	// 360度回転
	// 回転モード格納用
	private int rotatetype = 0;
	// フィルタ系処理格納用
	private int filtertype = 0;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // ボタン初期化
        menuButton1 = (Button)findViewById(R.id.menuButton1);
        penButton = (ImageButton)findViewById(R.id.penButton);
        eraserButton = (ImageButton)findViewById(R.id.eraserButton);
        redoButton = (ImageButton)findViewById(R.id.redoButton);
        undoButton = (ImageButton)findViewById(R.id.undoButton);

        // レイアウト初期化
        menuRow1 = (TableRow)findViewById(R.id.menuRow1);
        // クリックリスナー
        menuButton1.setOnClickListener(this);
        penButton.setOnClickListener(this);
        eraserButton.setOnClickListener(this);
        redoButton.setOnClickListener(this);
        undoButton.setOnClickListener(this);

        view = (PictureView)findViewById(R.id.view1);
        // その他初期化
        pictureData = new PictureDataManagement();
        drawTool = new DrawTool();
        // 書き込み用キャンバス作成
        drawTool.setImageScreen(view.getImage());
//        drawTool.setPath(new Path());
        filter = new FilterImage();
        flipImg = new FlipImage();
        imgResize = new ImageResize();	// リサイズ処理を行うクラスを生成
        rotimg = new RotImage();

        // リスナー設定
        view.setOnTouchListener(new View.OnTouchListener() {
    		public boolean onTouch(View view, MotionEvent event) {
    			Spinner spinner = (Spinner) findViewById(R.id.Spinner01);
    			if (spinner.getSelectedItemPosition() != 0) {
    				if (event.getAction() == MotionEvent.ACTION_UP) {
    					int x = (int) event.getX();
    					int y = (int) event.getY();
    					((PictureView) view).addPoint(new Point(x-50,y-50),spinner.getSelectedItemPosition());
    					view.invalidate();
    				}
    				return true;
    			}
    			//return false;
    			// falseだとACTION_DOWNしか返さない
    			return TouchEventView(event);
    		}
        });

        /*
        // リスナー設定
        view.setOnTouchListener(new View.OnTouchListener() {
    		public boolean onTouch(View view, MotionEvent event) {
    			// falseだとACTION_DOWNしか返さない
    			return TouchEventView(event);
    		}
        });
        */
    	if(null != pictureData.getPictureBuffer()){
    		Log.d("onCreate", "setBitmap");
    		view.getImage().setBmp(pictureData.getPictureBuffer());
            drawTool.setImageScreen(view.getImage());
    	}
    }

    @Override
    public void onResume( ){
    	super.onResume();
		Log.d("onResume", "getPictureBuffer");
    	if(null != pictureData.getPictureBuffer()){
    		Log.d("onResume", "setBitmap");
//    		view.setBitmap(pictureData.getPictureBuffer());
//            drawTool.createBmpCanvas(view.getBitmap());
    		// 画像を更新
    		view.refreshImage(pictureData.getPictureBuffer());
    		// 描画ツールに設定を反映
    		drawTool.setImageScreen(view.getImage());
    	}
    }

    @Override
    public void onPause(){
    	super.onPause();
		menuRow1.setVisibility(LinearLayout.GONE);
		menuButton1.setVisibility(LinearLayout.VISIBLE);
    }

    @Override
    public void onDestroy(){
    	super.onDestroy();
		menuRow1.setVisibility(LinearLayout.GONE);
		menuButton1.setVisibility(LinearLayout.VISIBLE);
    }

	private Intent intent = new Intent();
	public void onClick(View v) {
		// 応急処置 ボタンが押されたらスピナを"なし"に設定する
		Spinner spinner = (Spinner) findViewById(R.id.Spinner01);
		spinner.setSelection(0);
		if(v == menuButton1){
			menuRow1.setVisibility(LinearLayout.VISIBLE);
			menuButton1.setVisibility(LinearLayout.GONE);
		}else if(v == penButton){
	        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
	        		PictureEditorCVActivity.this);

	        // 表示項目の配列
	        final CharSequence[] line = { "直線", "フリーライン"};
	        // タイトルを設定
	        alertDialogBuilder.setTitle("線");
	        // ダイアログにラジオボタンを設定する
	        alertDialogBuilder.setSingleChoiceItems(line, -1, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	    }
        	});
	        // OKボタン
	        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int item) {
	                 dialog.cancel();
	            }
            });
	        // Cancelボタン
	        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int item) {
	                 dialog.cancel();
	            }
            });
	        // 表示項目とリスナの設定
	        alertDialogBuilder.setItems(line,
	                new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if(line[which]==line[0]){
								drawTool.setToolStatus(DrawTool.ToolStatus.STRAIGHTLINE);
							}else if(line[which]==line[1]){
								drawTool.setToolStatus(DrawTool.ToolStatus.FREELINE);
							}
						}
	                });

	        // back keyを使用不可に設定
	        alertDialogBuilder.setCancelable(false);

	        // ダイアログを表示
	        alertDialogBuilder.create().show();

		}else if(v == eraserButton){
			menuRow1.setVisibility(LinearLayout.GONE);
			menuButton1.setVisibility(LinearLayout.VISIBLE);
			drawTool.setToolStatus(DrawTool.ToolStatus.ERASER);
		}else if(v == redoButton){
			// 戻るボタン(実装予定)
		}else if(v == undoButton){
			// 進むボタン(実装予定)
		}
	}

    /**
     * 描画Viewにタッチされた時の操作
 	 *　@param event タッチ時のアクションイベント
 	 *　@return ドラッグを行う否か
     */
    public boolean TouchEventView(MotionEvent event) {

    	 //Log.d(TAG,"onTouch");

    	boolean flg = false;
        // ツールの処理実行
        flg = drawTool.excuteLauncher(event);

		// 再描画 間接的にonDrawを呼び出す
        view.invalidate();

    	// true: ドラッグする　false: しない
        return flg;
    }

    /**
     * 向き変更時に代わりに呼ばれる
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }

	// Option Menu が最初に表示される時に1度だけ呼び出される
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		boolean ret = super.onCreateOptionsMenu(menu);
		menu.add( Menu.NONE , MENU_ID1 , Menu.NONE , MENU_NAME_1 ).setIcon(R.drawable.folder_icon);
		menu.add( Menu.NONE , MENU_ID2 , Menu.NONE , MENU_NAME_2 ).setIcon(R.drawable.width);
		menu.add( Menu.NONE , MENU_ID3 , Menu.NONE , MENU_NAME_3 ).setIcon(R.drawable.color);
		menu.add( Menu.NONE , MENU_ID4 , Menu.NONE , MENU_NAME_4 ).setIcon(R.drawable.filter);
		menu.add( Menu.NONE , MENU_ID5 , Menu.NONE , MENU_NAME_5 ).setIcon(R.drawable.flip);
		menu.add( Menu.NONE , MENU_ID6 , Menu.NONE , MENU_NAME_6 ).setIcon(R.drawable.icon);
		menu.add( Menu.NONE , MENU_ID7 , Menu.NONE , MENU_NAME_7).setIcon(R.drawable.icon);

		return ret;
	}

	// Option Menu のアイテムが選択された時の動作
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean ret = super.onOptionsItemSelected(item);
		// 選ばれたアイテムの ID を取得
		switch(item.getItemId()) {
		case MENU_ID1:			// ファイル
			// オプションメニューへ移植 11/08/16 k.ishikawa
	        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
	        		PictureEditorCVActivity.this);

	        // 表示項目の配列
	        final CharSequence[] colors = { "読込", "保存"};
	        // タイトルを設定
	        alertDialogBuilder.setTitle("ファイル");
	        // Cancelボタン
	        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                 dialog.cancel();
	            }
            });
	        // 表示項目とリスナの設定
	        alertDialogBuilder.setItems(colors,
	                new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Log.d("floderButton", "getPictureBuffer");
							if(which == 1 && pictureData.getPictureBuffer() == null){
								// 保存時に画像がない時は何もしない
							}else{
								//intent = new Intent();
								intent.setClassName(
									       "com.vsn.edit.cv",
									       "com.vsn.edit.cv.PictureReadActivity");
								intent.putExtra("int Value",which);		// 値を渡す
								Log.d("fileButton", Integer.toString(which));
								startActivity(intent);	// PictureReadActivity開始
							}

						}
	                });

	        // back keyを使用不可に設定
	        alertDialogBuilder.setCancelable(false);

	        // ダイアログを表示
	        alertDialogBuilder.create().show();
			break;

		case MENU_ID2:			// 太さ
			// オプションメニューへ移植 11/08/16 k.ishikawa
	        final AlertDialog.Builder alertDialogBuilder_futosa = new AlertDialog.Builder(
	        		PictureEditorCVActivity.this);

	        // 表示項目の配列
	        final CharSequence[] futosa = { "最小", "小", "中", "大"};
	        // タイトルを設定
	        alertDialogBuilder_futosa.setTitle("太さ");
	        // ダイアログにラジオボタンを設定する
	        alertDialogBuilder_futosa.setSingleChoiceItems(futosa, 1, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	    }
        	});
	        // Cancelボタン
	        alertDialogBuilder_futosa.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                 dialog.cancel();
	            }
            });
	        // 表示項目とリスナの設定
	        alertDialogBuilder_futosa.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						 }
            });
	        // 表示項目とリスナの設定
	        alertDialogBuilder_futosa.setItems(futosa,
	                new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							switch(which){
							case 0:
								drawTool.setWidth(2.0f);
								break;
							case 1:
								drawTool.setWidth(4.0f);
								break;
							case 2:
								drawTool.setWidth(8.0f);
								break;
							case 3:
								drawTool.setWidth(16.0f);
								break;
							default:
								drawTool.setWidth(2.0f);
								break;
							}
						}
	                });
	        // back keyを使用不可に設定
	        alertDialogBuilder_futosa.setCancelable(false);

	        // ダイアログを表示
	        alertDialogBuilder_futosa.create().show();
			break;

		case MENU_ID3:			// 色
			// オプションメニューへ移植 11/08/16 k.ishikawa
	        final AlertDialog.Builder alertDialogBuildercolor = new AlertDialog.Builder(
	        		PictureEditorCVActivity.this);

	        // 表示項目の配列
	        final CharSequence[] color = {
	        		"Red", "Fuchsia", "Lime" ,"Yellow" ,
	        		"Blue", "Aqua", "White" ,"Gray" ,
	        		"Maroon", "Purple", "Green" ,"Olive" ,
	        		"Navy", "Teal", "Silver" ,"Black" ,
	        		};
	        // タイトルを設定
	        alertDialogBuildercolor.setTitle("色");
	        // ダイアログにラジオボタンを設定する
	        alertDialogBuildercolor.setSingleChoiceItems(color, -1, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	    }
        	});
	        // Cancelボタン
	        alertDialogBuildercolor.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                 dialog.cancel();
	            }
            });
	        // OKボタン
	        alertDialogBuildercolor.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
				}
	        });
	        // 表示項目とリスナの設定
	        alertDialogBuildercolor.setItems(color,
	                new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							switch(which){
							case 0:
								drawTool.setColor(0xFF, 0x00, 0x00);
								break;
							case 1:
								drawTool.setColor(0xFF, 0x00, 0xFF);
								break;
							case 2:
								drawTool.setColor(0x00, 0xFF, 0x00);
								break;
							case 3:
								drawTool.setColor(0xFF, 0xFF, 0x00);
								break;
							case 4:
								drawTool.setColor(0x00, 0x00, 0xFF);
								break;
							case 5:
								drawTool.setColor(0x00, 0xFF, 0xFF);
								break;
							case 6:
								drawTool.setColor(0xFF, 0xFF, 0xFF);
								break;
							case 7:
								drawTool.setColor(0x80, 0x80, 0x80);
								break;
							case 8:
								drawTool.setColor(0x80, 0x00, 0x00);
								break;
							case 9:
								drawTool.setColor(0x80, 0x00, 0x80);
								break;
							case 10:
								drawTool.setColor(0x00, 0x80, 0x00);
								break;
							case 11:
								drawTool.setColor(0x80, 0x80, 0x00);
								break;
							case 12:
								drawTool.setColor(0x00, 0x00, 0x80);
								break;
							case 13:
								drawTool.setColor(0x00, 0x80, 0x80);
								break;
							case 14:
								drawTool.setColor(0xC0, 0xC0, 0xC0);
								break;
							case 15:
								drawTool.setColor(0x00, 0x00, 0x00);
								break;
							default:
								drawTool.setColor(0x00, 0x00, 0x00);
								break;
							}
						}
	                });

	        // back keyを使用不可に設定
	        alertDialogBuildercolor.setCancelable(false);

	        // ダイアログを表示
	        alertDialogBuildercolor.create().show();
			break;

		case MENU_ID4:			// フィルタ
			// オプションメニューへ移植 11/08/16 k.ishikawa
	        final AlertDialog.Builder alertDialogBuilderfilter = new AlertDialog.Builder(
	        		PictureEditorCVActivity.this);
	        // 表示項目の配列
	        final CharSequence[] filters = { "色反転", "モノクロ", "モザイク", "ぼかし"};
	        // タイトルを設定
	        alertDialogBuilderfilter.setTitle("フィルタ");
			// 表示項目とリスナの設定
	        alertDialogBuilderfilter.setSingleChoiceItems(filters, -1,
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// リスト選択でダイアログは閉じないため選択されたリサイズのタイプを保持するのみ
							filtertype = which;
						}
					});
	        // Cancelボタン
	        alertDialogBuilderfilter.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                 dialog.cancel();
	            }
            });
	        // OKボタン
	        alertDialogBuilderfilter.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
					    	long time = 0;
					    	long startTime = android.os.SystemClock.uptimeMillis();
							switch(filtertype){
							case 0:
								filter.ReverseFilter(pictureData.getPictureBuffer());
								break;
							case 1:
								filter.MonochroFilter(pictureData.getPictureBuffer());
								break;
							case 2:
								filter.MosaicFilter(pictureData.getPictureBuffer());
								break;
							case 3:
								filter.ShadeFilter(pictureData.getPictureBuffer());
								break;
							}
					    	time = android.os.SystemClock.uptimeMillis() - startTime;
					    	Log.d("reverseColor",Long.toString(time));
							view.refreshImage(pictureData.getPictureBuffer());
				    		// 描画ツールに設定を反映
				    		drawTool.setImageScreen(view.getImage());	// 追加　2011/8/18
				    	}
	    	        });

	        // back keyを使用不可に設定
	        alertDialogBuilderfilter.setCancelable(false);

	        // ダイアログを表示
	        alertDialogBuilderfilter.create().show();
			break;

		case MENU_ID5:			// 反転
			final AlertDialog.Builder alertDialogBuilderFlip =
	        		new AlertDialog.Builder(PictureEditorCVActivity.this);

			// 表示項目の配列
			final CharSequence[] flips = {	FLIP_NAME_TB,		// 上下反転
	        								FLIP_NAME_LR,		// 左右反転
	        								FLIP_NAME_TBLR };	// 上下左右反転

			// タイトルを設定
			alertDialogBuilderFlip.setTitle( FLIP_TITLE );
			// 表示項目とリスナの設定
			alertDialogBuilderFlip.setSingleChoiceItems(flips, -1,
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// リスト選択でダイアログは閉じないため選択されたリサイズのタイプを保持するのみ
							fliptype = which;
						}
					});
	        // Cancelボタン
			alertDialogBuilderFlip.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                 dialog.cancel();
				}
			});
	        // OKボタン
			alertDialogBuilderFlip.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
	                	{
					    	long time = 0;
					    	long startTime = android.os.SystemClock.uptimeMillis();
	                		// 押下したボタンにより処理を切り分ける
							switch(fliptype)
	                		{
							case 0:						// 上下反転
								flipImg.flip( pictureData.getPictureBuffer(),
															FLIP_MODE_TB);
								break;

							case 1:						// 左右反転
								flipImg.flip( pictureData.getPictureBuffer(),
															FLIP_MODE_LR);
						        break;

							case 2:						// 上下左右反転
								flipImg.flip( pictureData.getPictureBuffer(),
															FLIP_MODE_TBLR);
								break;

							default:					// その他
	                			// 上下反転を適用(保護処理)
	                			Log.d("onClick:Flips", "Illgal Param !! [" + which + "]");
	                			flipImg.flip( pictureData.getPictureBuffer(),
															FLIP_MODE_TB);
								break;
							}
					    	time = android.os.SystemClock.uptimeMillis() - startTime;
					    	Log.d("IMGFLIP",Long.toString(time));
							view.refreshImage(pictureData.getPictureBuffer());
				    		// 描画ツールに設定を反映
				    		drawTool.setImageScreen(view.getImage());
				    	}
	        });

	        // back keyを使用不可に設定
			alertDialogBuilderFlip.setCancelable(false);

	        // ダイアログを表示
			alertDialogBuilderFlip.create().show();

			break;

		case MENU_ID6:			// リサイズ
			final AlertDialog.Builder alertDialogBuilderResize = new AlertDialog.Builder(
					PictureEditorCVActivity.this);
			// 表示項目の配列
			final  CharSequence[] resize = {RESIZE_1, RESIZE_2};
			// タイトルを設定
			alertDialogBuilderResize.setTitle(RESIZE_TITLE);

			// 表示項目とリスナの設定
			alertDialogBuilderResize.setSingleChoiceItems(resize, -1,
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// リスト選択でダイアログは閉じないため選択されたリサイズのタイプを保持するのみ
							resizetype = which;
						}
					});

			// OKボタン
			alertDialogBuilderResize.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// 選択されたリストに該当する処理を呼び出す
					long time = 0;
			    	long startTime = android.os.SystemClock.uptimeMillis();
					switch(resizetype){
					case 0:
						// 1024×768
						Log.d("resizeImage", "mode:1024×768");
						imgResize.ResizeImage(pictureData.getPictureBuffer(), RESIZE_NORMAL1);
						break;
					case 1:
						// 640×480
						Log.d("resizeImage", "mode:640×480");
						imgResize.ResizeImage(pictureData.getPictureBuffer(), RESIZE_NORMAL2);
						break;
					}
			    	time = android.os.SystemClock.uptimeMillis() - startTime;
			    	Log.d("resizeImage",Long.toString(time));
			    	view.noMatrix = 1;	// リサイズ後描画用にフラグを立てる
			    	view.refreshImage(pictureData.getPictureBuffer());
		    		// 描画ツールに設定を反映
		    		drawTool.setImageScreen(view.getImage());
				}
			});
			// キャンセルボタン
			alertDialogBuilderResize.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// キャンセル要因でダイアログクローズ
					dialog.cancel();
				}
			});

			// back keyを使用不可に設定
			alertDialogBuilderResize.setCancelable(false);
			// ダイアログを表示
			alertDialogBuilderResize.create().show();
			break;
		case MENU_ID7:			// 回転
			// 11/08/25 追加 k.ishikawa
	        final AlertDialog.Builder alertDialogBuilderRot = new AlertDialog.Builder(
	        		PictureEditorCVActivity.this);
			// 表示項目の配列
			final CharSequence[] rotptn = {	ROT_NAME_90 ,		// 90度回転
											ROT_NAME_180,		// 180度回転
											ROT_NAME_270,		// 270度回転
											ROT_NAME_360	};	// 360度回転
	        // タイトルを設定
	        alertDialogBuilderRot.setTitle(ROT_TITLE);
			// 表示項目とリスナの設定
	        alertDialogBuilderRot.setSingleChoiceItems(rotptn, -1,
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// リスト選択でダイアログは閉じないため選択されたリサイズのタイプを保持するのみ
							rotatetype = which;
						}
					});
	        // Cancelボタン
	        alertDialogBuilderRot.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                 dialog.cancel();
	            }
            });
	        // OKボタン
	        alertDialogBuilderRot.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
				    	long time = 0;
				    	long startTime = android.os.SystemClock.uptimeMillis();
						switch(rotatetype){
						case 0:	// 90度回転
							rotimg.rot(pictureData.getPictureBuffer(), ROT_MODE_90);
							break;
						case 1:	// 180度回転
							rotimg.rot(pictureData.getPictureBuffer(), ROT_MODE_180);
							break;
						case 2:	// 270度回転
							rotimg.rot(pictureData.getPictureBuffer(), ROT_MODE_270);
							break;
						case 3:	// 360度回転
							rotimg.rot(pictureData.getPictureBuffer(), ROT_MODE_360);
							break;
						}
				    	time = android.os.SystemClock.uptimeMillis() - startTime;
				    	Log.d("IMGROT",Long.toString(time));
						view.refreshImage(pictureData.getPictureBuffer());
			    		// 描画ツールに設定を反映
			    		drawTool.setImageScreen(view.getImage());	// 追加　2011/8/18
					}
			});

	        // back keyを使用不可に設定
	        alertDialogBuilderRot.setCancelable(false);

	        // ダイアログを表示
	        alertDialogBuilderRot.create().show();
			break;

		default:
			ret = true;
			break;
		}
		return ret;
	}
}
