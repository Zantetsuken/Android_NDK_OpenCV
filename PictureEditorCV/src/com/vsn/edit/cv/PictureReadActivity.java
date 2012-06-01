package com.vsn.edit.cv;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 読込画像指定画面
 * @author 1202542
 *
 */
public class PictureReadActivity extends Activity{

	private static final int REQUEST_PICK_CONTACT = 0;
	protected static final String TAG = null;
	private PictureDataManagement pictureData;
	private EditText edit;
	private String file="/";
	private Button writeButton;

	/**
	 * onCreate
	 * Activity起動時に呼ばれる
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pictureData = new PictureDataManagement();
        Intent getIntent = getIntent();		// 値取得用Intent
        int select = getIntent.getIntExtra("int Value", -1);

        Log.d("ReadOnCreate", "select:"+Integer.toString(select));
        switch(select){
        case 0:
        	Log.d("ReadOnCreate", "PictureRead");
			// インテント設定
	    	Intent intent = new Intent(Intent.ACTION_PICK);
	        // とりあえずストレージ内の全イメージ画像を対象
	        intent.setType("image/*");
	        // ギャラリー表示
	      	startActivityForResult(intent, REQUEST_PICK_CONTACT);
	      	break;
        case 1:
            setContentView(R.layout.write);	// view
    		edit = (EditText)findViewById(R.id.editText);
    		writeButton = (Button)findViewById(R.id.write_button);
    		writeButton.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
	        		SpannableStringBuilder sb = (SpannableStringBuilder)edit.getText();
	                Bitmap src = pictureData.getPictureBuffer();
	        		file += sb.toString();
	                Log.d("ReadActivity", sb.toString());
	                file += ".png";
	                Log.d("ReadActivity", file);
	            	try {
						PictureReadActivity.this.saveDataToPicture(src, file);
	    			} catch (Exception e) {
		                Log.d("ReadActivity", "Exception");
	    			}
    			}
    		});
			break;
    	default:
            finish();
    		break;
        }
	}

    @Override
    /**
     * 標準ギャラリーから戻り時に呼ばれるイベント
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v("onActivityResult:requestCode",String.valueOf(requestCode));
		Log.v("onActivityResult:resultCode",String.valueOf(resultCode));
		if (requestCode == REQUEST_PICK_CONTACT && data != null) {
			final Intent intent = data;
		    // 画像URIを取得
		    Uri photoUri = intent.getData();
		    /*
		    intent.setData(photoUri);
		    startActivityForResult(intent, REQUEST_PICK_CONTACT);
		    */
		    // 画像を取得
		    ContentResolver conReslv = getContentResolver();
		    if (photoUri != null) {
		    	try {
					// ビットマップ画像を取得
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(conReslv, photoUri);
					Bitmap copybmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);	// 編集可能な32bitビットマップにコピー
					pictureData.setPictureBuffer(copybmp);

		       } catch (Exception e) {
					Log.e(TAG, e.getMessage().toString());
					e.printStackTrace();
		       }
		    }
		}
//		Intent intent = new Intent();
//		intent.setClassName(
//			       "com.vsn.edit.cv",
//			       "com.vsn.edit.cv.PictureEditorActivity");
//		Log.d("ReadActivity", "startActivity");
//		startActivity(intent);	// PictureReadActivity開始
//		Log.d("ReadActivity", "finish");
		finish();
  }

    /**
     * 画像を保存する
     * @param data   byte[]   画像データ
     * @param dataName   String   保存パス
     * @return   boolean
     * @throws Exception
     */
    private boolean saveDataToPicture(Bitmap src, String dataName) throws Exception {
    	if (src == null)
    		return false;
    	FileOutputStream fileOutputStream = null;
    	try {
    		// BitmapをPngのByte データに変換
    		ByteArrayOutputStream output = new ByteArrayOutputStream();
    		src.compress( Bitmap.CompressFormat.PNG, 100, output);
    		byte[] data = output.toByteArray();
			// editディレクトリ作成処理
			String spath = Environment.getExternalStorageDirectory().toString() + "/edit";

			File file = new File(spath);

			try{
				if(!file.exists()){
					// editディレクトリが存在しない場合新規作成
					file.mkdir();
					Log.i("Error", "dir.mkdir:Success");
				}
			}catch(SecurityException ex){
				// 例外発生時の処理
				Log.i("Error", "dir.mkdir:Failed");
			}

			Log.v("1:saveDir", String.valueOf(spath));
			try{
				//String saveDir = spath.getPath();
				// 指定保存先に保存する
				fileOutputStream = new FileOutputStream((spath + dataName));
				fileOutputStream.write(data);
				Log.v("filesave!!",String.valueOf((spath + dataName)));
			}catch (Exception e) {
				Log.e(TAG, e.getMessage().toString());
			}finally {
				if (fileOutputStream != null) {
				Log.v("fileclose!!",String.valueOf((spath + dataName)));
				fileOutputStream.close();
				fileOutputStream = null;
	          }
			}
			Log.v("2:saveDir", String.valueOf(spath));
			// ギャラリーに保存した画像を追加
			MediaScannerConnection.scanFile(getApplicationContext(),
					new String[]{(spath + dataName)}, new String[]{"image/png"},
					ScanCompletedListener);
			// 選択された画像PATHを共有領域にセットする
//			SharedPreferences preference = getSharedPreferences("PREF_PICT", MODE_PRIVATE);
//			SharedPreferences.Editor editor = preference.edit();
//			Log.v("3:saveDir", String.valueOf(spath));
//			editor.putString("select_pict", (spath + "/select.png"));
//			editor.commit();
//			Log.v("4:saveDir", String.valueOf(spath));

		} catch (Exception e) {
			Log.e(TAG, e.getMessage().toString());
			e.printStackTrace();
		}
		finish();	// Acitvity終了
		return true;
    }

    /**
     * SDカードに保存された画像を認識させる
     */
    OnScanCompletedListener ScanCompletedListener = new OnScanCompletedListener() {
	    public void onScanCompleted(String path, Uri uri) {
	        Log.d("MediaScannerConnection", "Scanned " + path + ":");
	        Log.d("MediaScannerConnection", "-> uri=" + uri);
	    }
	};
//    /**
//     * ビットマップ画像をバイトデータに変換する
//     * @param src      Bitmap
//     * @param format   Bitmap.CompressFormat
//     * @param quality   int
//     * @return         byte[]
//     */
//    private static byte[] chngBmpToData(Bitmap src, Bitmap.CompressFormat format, int quality) {
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        Log.v("TAG", "IN_chngBmpToData");
//        src.compress(format, quality, output);
//        Log.v("TAG", "OUT_chngBmpToData");
//        return output.toByteArray();
//
//     }
//    /**
//     * 画像を保存する
//     * @param data   byte[]   画像データ
//     * @param dataName   String   保存パス
//     * @return   boolean
//     * @throws Exception
//     */
//    private void saveDataToStorage(byte[] data, String dataName) throws Exception {
//       FileOutputStream fileOutputStream = null;
//       try {
//          // 指定保存先に保存する
//          fileOutputStream = new FileOutputStream(dataName);
//          fileOutputStream.write(data);
//          Log.v("filesave!!",String.valueOf(dataName));
//       } catch (Exception e) {
//           Log.e(TAG, e.getMessage().toString());
//       } finally {
//          if (fileOutputStream != null) {
//             Log.v("fileclose!!",String.valueOf(dataName));
//             fileOutputStream.close();
//             fileOutputStream = null;
//          }
//       }
//    }

}
