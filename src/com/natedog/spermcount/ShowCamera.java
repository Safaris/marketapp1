package com.natedog.spermcount;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {

	   private SurfaceHolder holdMe;//holds the camera preview
	   private Camera theCamera;//reference to the camera from the main activity

	   public ShowCamera(Context context,Camera camera) {
	      super(context);
	      theCamera = camera;
	      theCamera.setDisplayOrientation(90);//rotate the camera 90 degrees
	      
	      holdMe = getHolder();
	      holdMe.setKeepScreenOn(true);//disable the screen from sleeping
	      holdMe.addCallback(this);
	   }

	   @Override
	   public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	   }

	   @Override
	   public void surfaceCreated(SurfaceHolder holder) {
	      try   {
	         theCamera.setPreviewDisplay(holder);
	         theCamera.startPreview(); 
	      } catch (IOException e) {
	      }
	   }

	   @Override
	   public void surfaceDestroyed(SurfaceHolder arg0) {
		   //safely release the camera, IMPORTANT!
		  // theCamera.stopPreview();
		   //theCamera.release();
	   }
}
