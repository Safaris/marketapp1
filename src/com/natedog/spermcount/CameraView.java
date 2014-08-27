package com.natedog.spermcount;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback
{
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Activity activity;

    public CameraView(Context context)
    {
        super(context);
        Log.d("CameraView-default constructor",
                "Chiamato uno dei due default constructors");
    }

    public CameraView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        Log.d("CameraView-default constructor",
                "Chiamato uno dei due default constructors");
    }

    public CameraView(Context context, Camera camera, Activity activity)
    {
        super(context);
        Log.d("CameraView-constructor",
                "Inizializzo la classe con la camera e il context");

        this.activity = activity;

        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.d("CameraView-surfaceCreated",
                "La superfice è stata creata, setto la preview della camera sulla superficie");

        // The Surface has been created, now tell the camera where to draw the preview.
        try
        {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e)
        {
            Log.d("CameraView-surfaceCreated", "Error setting camera preview: "
                    + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.d("CameraView-surfaceDestroyed",
                "Distrutta superficie della preview");
        // Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        Log.d("CameraView-surfaceChanged",
                "Gestisco i cambiamenti nella superficie che ospita la camera");
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null)
        {
            // preview surface does not exist
            Log.d("CameraView-surfaceChanged", "Preview surface doesn't exist");
            return;
        }

        try
        {
            FullscreenActivity.setCameraDisplayOrientation(activity,
                    Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
        } catch (Exception e)
        {
            Log.d("CameraView-surfaceChanged",
                    "Exception reorienting the camera " + e);
        }

    }// end surfaceChanged
}// end CameraView