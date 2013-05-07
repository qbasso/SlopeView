package com.mamlambo.artut;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mamlambo.globals.Global;

// TODO: Auto-generated Javadoc
/**
 * The Class ArtutActivity.
 */
@SuppressLint("NewApi")
public class ArtutActivity extends Activity {

    /** The Constant TAG. */
    private static final String TAG = "ArtutActivity";

    /** The Constant DIALOG_YES_NO_MESSAGE. */
    private static final int DIALOG_YES_NO_MESSAGE = 1;

    /** The Constant DIALOG_SINGLE_CHOICE. */
    private static final int DIALOG_SINGLE_CHOICE = 2;

    /** The Constant ACTION_RETRY_GPS_INIT. */
    private static final String ACTION_RETRY_GPS_INIT = "retry_gps_init";

    /** The slope measure type. */
    private int slopeMeasureType = 0;

    /** The ar content. */
    private OverlayView arContent;

    /** The ar display. */
    private ArDisplayView arDisplay;

    /** The m location manager. */
    private LocationManager mLocationManager;

    /** The m alert manager. */
    private AlarmManager mAlertManager;

    /**
     * The m receiver. Receiver to retry init gps, if init fail broadcast is
     * sent to retry in few seconds
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

	@Override
	public void onReceive(Context context, Intent intent) {
	    if (ACTION_RETRY_GPS_INIT.equals(intent.getAction())) {
		initGPS();
	    }
	}
    };

    /**
     * Save screen shot of the camera preview and layout.
     */
    private void saveScreenShot() {
	File dir = new File(Global.ARTUTIMAGE_CAPTURE_PATH);
	dir.mkdirs();
	Bitmap screen;
	OutputStream fout = null;
	String filename;
	filename = String.format("/SlopeView_"
		+ String.valueOf(System.currentTimeMillis()) + ".jpg");
	File imageFile = new File(dir + filename);
	View screenview = (View) findViewById(android.R.id.content);
	screenview.setDrawingCacheEnabled(true);
	screen = Bitmap.createBitmap(screenview.getDrawingCache());
	screenview.setDrawingCacheEnabled(false);
	byte[] data = CameraController.getLastPreviewData();
	if (data != null) {
	    // here we decode preview frame
	    int[] rgbIm = ImageUtils.decodeYUV420SP(data, arDisplay.mSize.x,
		    arDisplay.mSize.y);
	    // and create bitmap from it
	    Bitmap preview = Bitmap.createBitmap(rgbIm, arDisplay.mSize.x,
		    arDisplay.mSize.y, Config.ARGB_8888);
	    // and create result bitmap that will contain preview and layout
	    // drawn on it
	    Bitmap result = Bitmap.createBitmap(preview.getWidth(),
		    preview.getHeight(), Config.ARGB_8888);
	    int previewHeight = preview.getHeight();
	    int screenHeight = screen.getHeight();
	    Canvas c = new Canvas(result);
	    // first we draw preview
	    c.drawBitmap(preview, new Matrix(), null);
	    // if preview is bigger then screen layout we center layout on
	    // preview to look exactly same as on camera preview
	    if (previewHeight - screenHeight > 0) {
		c.drawBitmap(screen, 0, (previewHeight - screenHeight) / 2,
			null);
	    } else {
		c.drawBitmap(screen, new Matrix(), null);
	    }
	    try {
		fout = new FileOutputStream(imageFile);
		result.compress(Bitmap.CompressFormat.JPEG, 100, fout);
		fout.flush();
		fout.close();
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
			Uri.parse("file://"
				+ Environment.getExternalStorageDirectory())));
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    /** The m location listener. */
    private LocationListener mLocationListener = new LocationListener() {

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onProviderDisabled(String provider) {
	}

	public void onLocationChanged(Location location) {
	    if (location != null) {
		arContent.setLocation(location);
	    }
	}
    };

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	initErrorValues();
	setContentView(R.layout.main);
	FrameLayout arViewPane = (FrameLayout) findViewById(R.id.ar_view_pane);
	arDisplay = new ArDisplayView(getApplicationContext());
	arViewPane.addView(arDisplay);
	arContent = new OverlayView(getApplicationContext());
	arViewPane.addView(arContent);
	mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	mAlertManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	if (!initGPS()) {
	    mAlertManager.set(AlarmManager.RTC_WAKEUP,
		    System.currentTimeMillis(), prepareIntent());
	}
	/* Display a radio button group */
	Button slopeButton = (Button) findViewById(R.id.slope);
	slopeButton.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
		showDialog(DIALOG_SINGLE_CHOICE);
	    }
	});

	/* Get ScreenShot */
	Button cameraButton = (Button) findViewById(R.id.camera);
	cameraButton.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
		saveScreenShot();
	    }
	});

	/* Show Agree Dialog */
	Handler handler = new Handler() {
	    public void handleMessage(Message msg) {
		showDialog(DIALOG_YES_NO_MESSAGE);
	    }
	};
	Global.isShowVersionText = true;
	handler.sendEmptyMessageDelayed(0, 1000);
    }

    /**
     * Inits the error values. Here we apply values measured in calibration mode
     */
    private void initErrorValues() {
	SharedPreferences pref = PreferenceManager
		.getDefaultSharedPreferences(this);
	Global.Y_AXIS_CORRECTION = pref.getFloat("y_axis_error", 0);
	Global.Z_AXIS_CORRECTION = pref.getFloat("z_axis_error", 0);
    }

    /**
     * Prepare intent.
     * 
     * @return the pending intent
     */
    private PendingIntent prepareIntent() {
	Intent i = new Intent(ACTION_RETRY_GPS_INIT);
	PendingIntent pi = PendingIntent.getBroadcast(this, 0, i,
		PendingIntent.FLAG_UPDATE_CURRENT);
	return pi;
    }

    /**
     * Inits the gps.
     * 
     * @return true, if successful
     */
    private boolean initGPS() {
	if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	    mLocationManager.requestLocationUpdates(
		    LocationManager.GPS_PROVIDER, 5 * 1000, 10,
		    mLocationListener);
	    return true;
	} else if (mLocationManager
		.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
	    mLocationManager.requestLocationUpdates(
		    LocationManager.NETWORK_PROVIDER, 5 * 1000, 10,
		    mLocationListener);
	    return true;
	}
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
	arContent.onPause();
	mLocationManager.removeUpdates(mLocationListener);
	unregisterReceiver(mReceiver);
	super.onPause();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
	super.onResume();
	arContent.onResume();
	registerReceiver(mReceiver, new IntentFilter(ACTION_RETRY_GPS_INIT));
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateDialog(int)
     */
    @Override
    protected Dialog onCreateDialog(int id) {
	switch (id) {
	case DIALOG_YES_NO_MESSAGE:
	    final ImageView iv = (ImageView) findViewById(R.id.imageview);
	    iv.setVisibility(View.VISIBLE);
	    return new AlertDialog.Builder(ArtutActivity.this)
		    .setIconAttribute(R.drawable.svicon)
		    .setTitle(R.string.alert_dialog_agreetitle)
		    .setMessage(R.string.alert_dialog_agreemsg)
		    .setPositiveButton(R.string.alert_dialog_agree,
			    new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
					int whichButton) {
				    iv.setVisibility(View.INVISIBLE);
				    Global.isShowVersionText = false;
				}
			    })
		    .setNegativeButton(R.string.alert_dialog_disagree,
			    new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
					int whichButton) {
				    iv.setVisibility(View.INVISIBLE);
				    finish();
				}
			    }).create();
	case DIALOG_SINGLE_CHOICE:
	    return new AlertDialog.Builder(ArtutActivity.this)
		    .setIcon(R.drawable.svicon)
		    // .setIconAttribute(android.R.attr.alertDialogIcon)
		    .setTitle(R.string.slope_line_option_title)
		    .setSingleChoiceItems(R.array.slope_line_array, 0,
			    new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
					int whichButton) {
				    slopeMeasureType = whichButton;
				}
			    })
		    .setPositiveButton(R.string.alert_dialog_ok,
			    new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
					int whichButton) {
				    Global.slopeMeasureType = slopeMeasureType;
				}
			    })
		    .setNegativeButton(R.string.alert_dialog_cancel,
			    new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
					int whichButton) {
				}
			    }).create();
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.app.Activity#onConfigurationChanged(android.content.res.Configuration
     * )
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
	// TODO Auto-generated method stub
	super.onConfigurationChanged(newConfig);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater menuInflater = getMenuInflater();
	menuInflater.inflate(R.menu.main, menu);
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
	switch (item.getItemId()) {
	case R.id.menu_calibrate:
	    startActivity(new Intent(this, CalibrateActivity.class));
	    return true;

	default:
	    return super.onMenuItemSelected(featureId, item);
	}
    }

}