package com.mamlambo.artut;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

// TODO: Auto-generated Javadoc
/**
 * The Class CalibrateActivity. Used to find errors in device sensors
 */
public class CalibrateActivity extends FragmentActivity {

    /** The m fragment manager. */
    private FragmentManager mFragmentManager;

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	setContentView(R.layout.calibrate);
	mFragmentManager = getSupportFragmentManager();
	FragmentTransaction t = mFragmentManager.beginTransaction();
	t.add(R.id.screen_content, new CalibrateBeginFragment());
	t.setCustomAnimations(android.R.anim.slide_in_left,
		android.R.anim.slide_out_right);
	t.commit();
    }

    /**
     * The Class CalibrateBeginFragment.
     */
    public static class CalibrateBeginFragment extends Fragment {

	/** The m cancel button. */
	private Button mCancelButton;

	/** The m begin button. */
	private Button mBeginButton;

	/** The m cancel listener. */
	private OnClickListener mCancelListener = new OnClickListener() {

	    public void onClick(View arg0) {
		getActivity().finish();
	    }
	};

	/** The m begin linstener. */
	private OnClickListener mBeginLinstener = new OnClickListener() {

	    public void onClick(View v) {
		FragmentTransaction t = getFragmentManager().beginTransaction();
		t.replace(R.id.screen_content, new CalibrationFragment());
		t.addToBackStack(null);
		t.setCustomAnimations(android.R.anim.slide_out_right,
			android.R.anim.slide_in_left);
		t.commit();
	    }
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    setRetainInstance(true);
	    mCancelButton = (Button) getActivity().findViewById(R.id.cancel);
	    mCancelButton.setOnClickListener(mCancelListener);
	    mBeginButton = (Button) getActivity().findViewById(R.id.next);
	    mBeginButton.setOnClickListener(mBeginLinstener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater
	 * , android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	    return inflater.inflate(R.layout.calibration_begin_fragment, null);
	}

    }

}
