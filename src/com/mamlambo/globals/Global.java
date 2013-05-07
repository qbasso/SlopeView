package com.mamlambo.globals;

import android.os.Environment;

// TODO: Auto-generated Javadoc
/**
 * The Class Global.
 */
public class Global {

	/** The is debug. */
	public static boolean isDebug = false;

	/** The device type. */
	public static int deviceType = 0; // 0 degree correction : Slopeview 1.1 -
										// Model GT-P3113
	// public static int deviceType = 1; // 4 degree correction : Slopeview 1.1
	// - Model GT-P6210

	/** The device type name. */
										public static String deviceTypeName[] = { "Slopeview 1.2 - Model GT-P3113",
			"Slopeview 1.2 - Model GT-P6210" };

	/** The slope measure type. */
	public static int slopeMeasureType = 0;
	
	/** The slope measure meter. */
	public static float slopeMeasureMeter[] = { 7.0f, 20.0f, 34.0f, 40.0f,
			50.0f, 62.5f };

	/** The slope height of mesure type. */
	public static float slopeHeightOfMesureType[] = { 135.0f, 0.0f, 0.0f, 0.0f,
			0.0f, 0.0f }; // Samsung Tablet 1024 * 600

	/** The slope base measure meter. */
	public static float slopeBaseMeasureMeter = 7.0f;
	
	/** The slope base height of mesure type1. */
	public static float slopeBaseHeightOfMesureType1 = 135.0f; // Samsung Tablet
																// 1024 * 600,
																// hFOV == 54.8f
																// vFOV == 42.5f
																// , GT-P3113
	/** The slope base height of mesure type2. */
																public static float slopeBaseHeightOfMesureType2 = 100.0f; // Samsung Tablet
																// 1024 * 600,
																// hFOV == 59.6f
																// vFOV == 46.3f
																// , GT-P6210
	/** The slope base height of mesure type3. */
																public static float slopeBaseHeightOfMesureType3 = 168.0f; // ASUS Tablet
																// 1280 * 800,
																// hFOV == 60.0f
																// vFOV == 60.0f

	/** The slope base offer height1. */
																public static float slopeBaseOfferHeight1 = 0.0f; // Samsung Tablet 1024 *
														// 600, hFOV == 54.8f
														// vFOV == 42.5f
	/** The slope base offer height2. */
														public static float slopeBaseOfferHeight2 = 42.0f; // Samsung Tablet 1024 *
														// 600, hFOV == 59.6f
														// vFOV == 46.3f
	/** The slope base offer height3. */
														public static float slopeBaseOfferHeight3 = 0.0f; // ASUS Tablet 1280 * 800,
														// hFOV == 60.0f vFOV ==
														// 60.0f

	/** The Constant ARTUTIMAGE_CAPTURE_PATH. */
														public static final String ARTUTIMAGE_CAPTURE_PATH = Environment
			.getExternalStorageDirectory() + "/SlopeView";
	
	/** The y axis correction. */
	public static float Y_AXIS_CORRECTION = 0f;
	
	/** The z axis correction. */
	public static float Z_AXIS_CORRECTION = 0f;
	
	/** The is show version text. */
	public static boolean isShowVersionText = true;

	/** The slope line meter. */
	public static float slopeLineMeter = 0.0f;
}
