package com.natedog.spermcount;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class FullscreenActivity extends Activity {

	private Camera cameraObject;
	private ShowCamera showCamera;

	public static Camera isCameraAvailiable() {
		Camera object = null;
		try {
			object = Camera.open();
		} catch (Exception e) {
		}
		return object;
	}

	private PictureCallback capturedIt = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			if (bitmap == null) {
				Toast.makeText(getApplicationContext(), "not taken",
						Toast.LENGTH_SHORT).show();
			} else {
				//what to do when the user captures a picture
				Toast.makeText(getApplicationContext(), "taken",
						Toast.LENGTH_SHORT).show();
				   Bitmap src = bitmap;
				    Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

				    String yourText = "My custom Text adding to Image";

				    Canvas cs = new Canvas(dest);
				    Paint tPaint = new Paint();
				    tPaint.setTextSize(35);
				    tPaint.setColor(Color.BLUE);
				    tPaint.setStyle(Style.FILL);
				    cs.drawBitmap(src, 0f, 0f, null);
				    float height = tPaint.measureText("yY");
				    float width = tPaint.measureText(yourText);
				    float x_coord = (src.getWidth() - width)/2;
				    cs.drawText(yourText, x_coord, height+15f, tPaint); // 15f is to put space between top edge and the text, if you want to change it, you can
				    String path= Environment.getExternalStorageDirectory().getPath();
				    try {
				        dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(path+"/ImageAfterAddingText.jpg")));
				    	Toast.makeText(getApplicationContext(), path,
								Toast.LENGTH_SHORT).show();
				        // dest is Bitmap, if you want to preview the final image, you can display it on screen also before saving
				    
				    } catch (Exception e) {
				        // TODO Auto-generated catch block
				        e.printStackTrace();
				    }
			}
			cameraObject.release();
		}
	};

	//called then the app starts
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		cameraObject = isCameraAvailiable();
		showCamera = new ShowCamera(this, cameraObject);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(showCamera);
	}

	public void snapIt(View view) {
		cameraObject.takePicture(null, null, capturedIt);
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.main, menu); return true; }
	 */
}
