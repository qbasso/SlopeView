package com.mamlambo.artut;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

// TODO: Auto-generated Javadoc
/**
 * The Class ArDisplayView. Used to display camera preview
 */
public class ArDisplayView extends SurfaceView implements
	SurfaceHolder.Callback {

    /** The Constant DEBUG_TAG. */
    public static final String DEBUG_TAG = "ArDisplayView Log";

    /** The m camera. */
    public Camera mCamera;

    /** The m camera controller. */
    private CameraController mCameraController;

    /** The m holder. */
    SurfaceHolder mHolder;

    /** The m context. */
    private Context mContext;

    /** The m size. */
    public Point mSize;

    /**
     * Instantiates a new ar display view. Here we have camera controller
     * initialization as well. It wraps standard Android camera handling flow
     * 
     * @param context
     *            the context
     */
    public ArDisplayView(Context context) {
	super(context);
	mContext = context;
	mHolder = getHolder();
	CameraController.init(mContext);
	mCameraController = CameraController.getController();
	;
	// This value is supposedly deprecated and set "automatically" when
	// needed.
	mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	mHolder.addCallback(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder
     * )
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder
     * , int, int, int)
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
	    int height) {
	Log.d(DEBUG_TAG, "surfaceChanged");
	mCameraController.stopPreview();
	mCameraController.changeParams();
	mCameraController.startPreview();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.
     * SurfaceHolder)
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
	Log.d(DEBUG_TAG, "surfaceDestroyed");
	mCameraController.stopPreview();
	mCameraController.releaseCamera();
    }

}
