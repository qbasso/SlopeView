package com.mamlambo.artut;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ArDisplayView extends SurfaceView implements
		SurfaceHolder.Callback {
	public static final String DEBUG_TAG = "ArDisplayView Log";
	public Camera mCamera;
	private CameraController mCameraController;
	SurfaceHolder mHolder;
	private Context mContext;
	public Point mSize;

	public ArDisplayView(Context context) {
		super(context);
		mContext = context;
		mHolder = getHolder();
		CameraController.init(mContext);
		mCameraController = CameraController.getController();;
		// This value is supposedly deprecated and set "automatically" when
		// needed.
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mHolder.addCallback(this);
	}

	@SuppressLint({ "NewApi" })
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(DEBUG_TAG, "surfaceCreated");
		try {
			mCameraController.cameraOpen(holder);
			mSize = mCameraController.getCameraResolution();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d(DEBUG_TAG, "surfaceChanged");
		mCameraController.stopPreview();
		mCameraController.changeParams();
		mCameraController.startPreview();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(DEBUG_TAG, "surfaceDestroyed");
		mCameraController.stopPreview();
		mCameraController.releaseCamera();
	}

}
