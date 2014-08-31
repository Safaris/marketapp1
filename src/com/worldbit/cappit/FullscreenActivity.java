package com.worldbit.cappit;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

public class FullscreenActivity extends Activity {

	private Camera cameraObject;
	private ShowCamera showCamera;
	private FrameLayout preview;

	public final static String EXTRA_PHOTO_CONTENT = "com.natedog.marketapp1.PHOTO";

	private PictureCallback capturedIt = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			if (data == null) {

				return;
			}

			else {

				BitmapFactory.Options options = new BitmapFactory.Options();

				// downsizing image as it throws OutOfMemory Exception for
				// larger
				options.inSampleSize = 4;

				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length, options);
				bitmap = RotateBitmap(bitmap, 90);

				Bitmap dest = Bitmap.createBitmap(bitmap.getWidth(),
						bitmap.getHeight(), Bitmap.Config.ARGB_8888);

				// Random r = new Random();
				// int randomInt = r.nextInt(1500000);
				// String yourText = "Sperm Count: " + randomInt;

				Phrase p = new Phrase();
				p.generatePhrase();

				String yourText = p.toString();

				Canvas cs = new Canvas(dest);
				Paint tPaint = new Paint();
				Paint sqPaint = new Paint();

				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				float maxW = size.x;
				float maxY = size.y;
				Log.d("maxy:", String.valueOf(maxY));

				tPaint.setTextSize(30);
				tPaint.setFakeBoldText(true);
				tPaint.setAntiAlias(true);

				tPaint.setColor(Color.WHITE);
				tPaint.setStyle(Style.FILL);

				sqPaint.setColor(Color.BLACK);
				sqPaint.setAlpha(100);


				cs.drawBitmap(bitmap, 0f, 0f, null);
				//float height = tPaint.measureText("yY");
				float width = tPaint.measureText(yourText);
				float x_coord = (bitmap.getWidth() - width) / 2;

				float x = 0;
				float y = (maxY - (maxY * .25f));
				cs.drawRect(x, y - tPaint.getTextSize() - 5f, maxW, y + 12f,
						sqPaint);
				cs.drawText(yourText, x_coord, (maxY - (maxY * .25f)), tPaint);


				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				dest.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] byteArray = stream.toByteArray();

				bitmap.recycle();
				bitmap = null;

				Intent intent = new Intent(getApplicationContext(),
						ViewPhoto.class);
				intent.putExtra(EXTRA_PHOTO_CONTENT, byteArray);
				startActivity(intent);
			}


		}
	};

	// called when the app starts
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		cameraObject = getCameraInstance();
		//Parameters params = cameraObject.getParameters();
		//params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
		//cameraObject.setParameters(params);
		showCamera = new ShowCamera(this, cameraObject);
		preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(showCamera);
	}

	public void snapIt(View view) {
		cameraObject.takePicture(null, null, capturedIt);
	}

	protected void onPause() {

		super.onPause();
		try {
			// release the camera immediately on pause event
			// releaseCamera();
			cameraObject.stopPreview();
			cameraObject.setPreviewCallback(null);
			showCamera.getHolder().removeCallback(showCamera);

			cameraObject.release();
			cameraObject = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {

		super.onResume();
		try {
			cameraObject = Camera.open();
			// cameraObject = getCameraInstance();
			// Log.i("TEST onresume", "onResume Runing");
			// Log.i("TEST onresume", "camera " + mCamera);
			cameraObject.setPreviewCallback(null);
			// Log.i("TEST onresume", "camera " + mCamera);
			// mCamera = getCameraInstance();
			// Log.i("TEST onresume", "camera " + mCamera);
			// mCamera.setPreviewCallback(null);
			showCamera = new ShowCamera(this, cameraObject);// set preview

			// Log.i("TEST onresume", "camera " + mCamera);
			// preview = (FrameLayout) findViewById(R.id.camera_preview);

			preview.addView(showCamera);
			// Log.i("TEST onresume", "camera " + mCamera);
			cameraObject.startPreview();
		} catch (Exception e) {
			// Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
		// Log.i("TEST onresume", "camera " + mCamera);
	}

	private Camera getCameraInstance() {

		Camera c = null;
		try {
			// c = Camera.open(); // attempt to get a Camera instance
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		// Log.i("TEST getCameraInstance", "" + c);
		return c; // returns null if camera is unavailable
	}

	public static Bitmap RotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}
	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.main, menu); return true; }
	 */
}
