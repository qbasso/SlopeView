package com.mamlambo.artut;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class ArDisplayView extends SurfaceView implements
		SurfaceHolder.Callback {
	public static final String DEBUG_TAG = "ArDisplayView Log";
	public Camera mCamera;
	SurfaceHolder mHolder;
	Activity mActivity;
	public byte[] lastPreviewData;
	public Size mSize;
	private Point cameraResolution;
	private Point screenResolution;

	public ArDisplayView(Context context, Activity activity) {
		super(context);

		mActivity = activity;
		mHolder = getHolder();
		// This value is supposedly deprecated and set "automatically" when
		// needed.
		// Without this, the application crashes.
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// callbacks implemented by ArDisplayView
		mHolder.addCallback(this);
	}

	private void initResolutionsFromParams(Camera camera) {
		Camera.Parameters params = camera.getParameters();
		WindowManager manager = (WindowManager) mActivity
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		screenResolution = new Point(dm.widthPixels, dm.heightPixels);
		cameraResolution = findBestPreviewOrPictureSize(
				params.getSupportedPreviewSizes(), screenResolution, false);
	}

	private Point findBestPreviewOrPictureSize(
			List<Size> availablePreviewSizes, Point screenResolution,
			boolean isPicture) {
		int bestX = 0;
		int bestY = 0;
		int diff = Integer.MAX_VALUE;
		for (Size size : availablePreviewSizes) {
			int newX = 0;
			int newY = 0;
			try {
				newX = size.width;
				newY = size.height;
			} catch (NumberFormatException nfe) {
				Log.e("error finding preview", "number format exception for:"
						+ newX + " " + newY);
				continue;
			}
			// if widest dimension is less than 480 we stop searching
			if (isPicture && Math.max(newX, newY) < 480) {
				break;
			}
			// we looking for size that is closest to screen resolution
			int newDiff = Math.abs(newX - screenResolution.x)
					+ Math.abs(newY - screenResolution.y);
			if (newDiff == 0) {
				bestX = newX;
				bestY = newY;
				break;
			} else if (newDiff < diff) {
				bestX = newX;
				bestY = newY;
				diff = newDiff;
			}
		}
		if (bestX > 0 && bestY > 0) {
			return new Point(bestX, bestY);
		}
		return null;
	}

	/**
	 * sets params for camera, default orientation for android is landscape, we
	 * use camera in portrait mode so when we set preview and picture size we
	 * swap x and y values.
	 * 
	 * @param camera
	 *            android camera object
	 */
	private void setBestResolutionParams(Camera camera) {
		Camera.Parameters params = camera.getParameters();
		params.set("jpeg-quality", "100");
		params.setPictureFormat(ImageFormat.JPEG);
		params.setPreviewSize(cameraResolution.x, cameraResolution.y);
		camera.setParameters(params);
		mSize = camera.getParameters().getPreviewSize();
	}

	@SuppressLint({ "NewApi" })
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(DEBUG_TAG, "surfaceCreated");

		// Grab the camera
		mCamera = Camera.open();

		// Set Display orientation
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(CameraInfo.CAMERA_FACING_BACK, info);

		try {
			mCamera.setPreviewDisplay(this.mHolder);
			mCamera.setPreviewCallback(new PreviewCallback() {
				public void onPreviewFrame(byte[] _data, Camera _camera) {
					lastPreviewData = _data;
				}
			});
		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
			Log.e(DEBUG_TAG, "surfaceCreated exception: ", ioe);
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d(DEBUG_TAG, "surfaceChanged");
		initResolutionsFromParams(mCamera);
		setBestResolutionParams(mCamera);
		mCamera.startPreview();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(DEBUG_TAG, "surfaceDestroyed");
		// Shut down camera preview
		mCamera.setPreviewCallback(null);
		mCamera.stopPreview();
		mCamera.release();
	}


}
