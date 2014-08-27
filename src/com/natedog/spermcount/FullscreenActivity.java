package com.natedog.spermcount;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;

public class FullscreenActivity extends Activity {
	private Camera mCamera;
	private CameraView mPreview;
	private MediaRecorder mMediaRecorder;
	private FrameLayout preview;
	private Button buttonRecordStop;
	private boolean isRecording = false;
	private String fileName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("CameraPage-onCreate", "Called onCreate()");

		setContentView(R.layout.activity_fullscreen);

		buttonRecordStop = (Button) findViewById(R.id.camera_surface_buttonRecordStop);

		buttonRecordStop.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN
						|| event.getAction() == MotionEvent.ACTION_MOVE) {
					AlphaAnimation alpha = new AlphaAnimation(1.0F, 0.3F);
					alpha.setDuration(100);
					alpha.setFillAfter(true); // Tell it to persist after the
												// animation ends
					buttonRecordStop.startAnimation(alpha);
				} else if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_CANCEL) {
					AlphaAnimation alpha = new AlphaAnimation(0.3F, 1.0F);
					alpha.setDuration(100);
					alpha.setFillAfter(true); // Tell it to persist after the
												// animation ends
					buttonRecordStop.startAnimation(alpha);
				}
				return false;
			}
		});

		buttonRecordStop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isRecording) {
					Log.d("CameraPage-buttonRecordStop-onClick", "Start Record");

					getWindow().addFlags(
							WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

					// initialize video camera
					if (prepareVideoRecorder()) {
						v.setVisibility(View.GONE);
						//hello
						 
								
						v.setVisibility(View.VISIBLE);
						isRecording = true;

						// Camera is available and unlocked, MediaRecorder is
						// prepared,
						// now you can start recording
						mMediaRecorder.start();

					} else {
						// prepare didn't work, release the camera
						releaseMediaRecorder();
						// inform user
					}

				} else {
					Log.d("CameraPage-buttonRecordStop-onClick", "Stop Record");

					getWindow().clearFlags(
							WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

					v.setVisibility(View.GONE);
					
					v.setVisibility(View.VISIBLE);
					isRecording = false;

					stopRecording();

					sendBroadcast(new Intent(
							Intent.ACTION_MEDIA_MOUNTED,
							Uri.parse("file://"
									+ Environment.getExternalStorageDirectory())));

				}

			}// end onClick
		});

		mCamera = getCameraInstance();
		preview = (FrameLayout) findViewById(R.id.camera_preview);

	}// end onCreate

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("CameraPage-onResume",
				"Chiamato onResume ripristino le risorse se necessario");

		if (mCamera == null) {
			// Create an instance of Camera
			mCamera = getCameraInstance();

		}

		if (mPreview == null) {
			mPreview = new CameraView(getApplicationContext(), mCamera, this);
			preview.addView(mPreview);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("CameraPage-onPause", "Vado in onPause e rilascio le risorse");

		releaseMediaRecorder(); // if you are using MediaRecorder, release it
								// first
		releaseCamera(); // release the camera immediately on pause event

		preview.removeView(mPreview);
		mPreview = null;
	}

	private void stopRecording() {
		mMediaRecorder.stop();
		releaseMediaRecorder();

		// Restarto la preview della camera
		mCamera.startPreview();
		// mCamera.lock();
	}

	private boolean prepareVideoRecorder() {

		// mCamera = getCameraInstance();
		mMediaRecorder = new MediaRecorder();

		// Stop preview because I have to start the preview for the video
		mCamera.stopPreview();

		// Step 1: Unlock and set camera to MediaRecorder
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);

		// Step 2: Set sources
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		mMediaRecorder.setProfile(CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH));

		// Step 4: Set output file
		setOutputFile();
		mMediaRecorder.setOutputFile(getOutputFile());

		// Step 5: Set the preview output
		mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

		mMediaRecorder.setOrientationHint(FullscreenActivity
				.getCameraDisplayOrientation(this,
						Camera.CameraInfo.CAMERA_FACING_BACK, mCamera));

		// Step 6: Prepare configured MediaRecorder
		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			Log.d("CameraPage-prepareVideoRecorder",
					"IllegalStateException preparing MediaRecorder: "
							+ e.getMessage());
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			Log.d("CameraPage-prepareVideoRecorder",
					"IOException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		}
		return true;
	}

	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			mMediaRecorder.reset(); // clear recorder configuration
			mMediaRecorder.release(); // release the recorder object
			mMediaRecorder = null;
			mCamera.lock(); // lock camera for later use
		}
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}

		if (mPreview != null)
			mPreview.getHolder().removeCallback(mPreview);
	}

	/** A safe way to get an instance of the Camera object. */
	private Camera getCameraInstance() {
		Camera c = null;
		try {
			Log.d("CameraPage-getCameraInstance", "Prendo istanza camera");
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
			Log.d("CameraPage-getCameraInstance",
					"Eccezione durante retrieve istanza camera " + e);
		}
		return c; // returns null if camera is unavailable
	}

	@SuppressLint("SimpleDateFormat")
	private void setOutputFile() {
		String folderPath = Environment.getExternalStorageDirectory().getPath();
		File vims = new File(folderPath + "/ViMS");

		if (!vims.exists())
			vims.mkdir();

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd---HH-mm-ss");
		Date date = new Date();
		String timestamp = "/" + dateFormat.format(date) + ".mp4";

		Log.d("CameraPage-getOutputFile", Environment
				.getExternalStorageDirectory().getPath());

		fileName = vims.getPath() + timestamp;

	}

	private String getOutputFile() {
		return fileName;
	}

	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, android.hardware.Camera camera) {

		int result = FullscreenActivity.getCameraDisplayOrientation(activity, cameraId,
				camera);

		if (android.os.Build.VERSION.SDK_INT <= 14) {
			camera.stopPreview();
			camera.setDisplayOrientation(result);
			camera.startPreview();
		} else {
			camera.setDisplayOrientation(result);
		}

	}// end setCameraDisplayOrientation

	public static int getCameraDisplayOrientation(Activity activity,
			int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}

		return result;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d("CameraPage-onDestroy", "Called onDestroy");

	}

}// end CameraPage
