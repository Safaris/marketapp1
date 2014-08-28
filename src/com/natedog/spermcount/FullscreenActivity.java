package com.natedog.spermcount;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class FullscreenActivity extends Activity {

	private Camera cameraObject;
	private ShowCamera showCamera;
	private FrameLayout preview;

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	private PictureCallback capturedIt = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				// Log.d(TAG,
				// "Error creating media file, check storage permissions: " +
				// e.getMessage());
				return;
			}

			try {
				
				BitmapFactory.Options options = new BitmapFactory.Options();
				  
				  //downsizing image as it throws OutOfMemory Exception for larger
				options.inSampleSize = 4;
				
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
				bitmap = RotateBitmap(bitmap, 90);

				Bitmap dest = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
						Bitmap.Config.ARGB_8888);

				Random r = new Random();
				int randomInt = r.nextInt(1500000);
				String yourText = "Sperm Count: " + randomInt;

				Canvas cs = new Canvas(dest);
				Paint tPaint = new Paint();

				
				tPaint.setTextSize(32);
				tPaint.setColor(Color.WHITE);
				tPaint.setStyle(Style.FILL);
				cs.drawBitmap(bitmap, 0f, 0f, null);
				float height = tPaint.measureText("yY");
				float width = tPaint.measureText(yourText);
				float x_coord = (bitmap.getWidth() - width) / 2;
				cs.drawText(yourText, x_coord, height + 15f, tPaint);
				
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				dest.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(byteArray);
				fos.close();

				MediaScannerConnection.scanFile(getApplicationContext(),
						new String[] { pictureFile.toString() }, null,
						new MediaScannerConnection.OnScanCompletedListener() {
							public void onScanCompleted(String path, Uri uri) {
								Log.i("ExternalStorage", "Scanned " + path
										+ ":");
								Log.i("ExternalStorage", "-> uri=" + uri);
							}
						});
			} catch (FileNotFoundException e) {
				// Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				// Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
			/*
			 * BitmapFactory.Options options = new BitmapFactory.Options();
			 * 
			 * // downsizing image as it throws OutOfMemory Exception for larger
			 * // images options.inSampleSize = 8;
			 * 
			 * // File pictureFile = getOutputMediaFile(); if (bitmap == null) {
			 * Toast.makeText(getApplicationContext(), "not taken",
			 * Toast.LENGTH_SHORT).show(); // return; } else { // what to do
			 * when the user captures a picture
			 * Toast.makeText(getApplicationContext(), "taken",
			 * Toast.LENGTH_SHORT).show(); saveImageToExternalStorage(bitmap);
			 * // bitmap.recycle(); // bitmap = null;
			 */
		 // 15f is //
																	// to // put
																	// // space
																	// //
																	// between
																	// // top //
			// / edge // and // the // text, // if // you // want // to //
			// change // it, // you // can

			//Toast.makeText(getApplicationContext(), path, Toast.LENGTH_SHORT)
				//	.show(); // dest is Bitmap, if you want to preview the final
								// image, // you can display it on screen also
								// before saving

			// }
			// cameraObject.release();
		}
	};

	// called then the app starts
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		cameraObject = getCameraInstance();
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

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MarketAppPictures");

		Log.d("url", mediaStorageDir.toString());
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}
	
	public static Bitmap RotateBitmap(Bitmap source, float angle)
	{
	      Matrix matrix = new Matrix();
	      matrix.postRotate(angle);
	      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.main, menu); return true; }
	 */
}
