package com.goodguide.android.app;


import com.goodguide.android.app.R;
import com.goodguide.android.business.GoodGuideBusiness;
import com.goodguide.android.shared.Constants;
import com.goodguide.android.value.Product;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.LayoutInflater.Factory;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ebay.redlasersdk.BarcodeResult;
import com.ebay.redlasersdk.scanner.BarcodeScanActivity;

public class RedLaserSDK extends BarcodeScanActivity {
	
	  private static final String TAG = "RedLaserScan";
	private PopupWindow pw;
	private View layout;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
         /*
    	 * _appCode is your customer SDK code.  "sdk" is for testing
    	 * purposes and has a scanning limit enforced.
    	 * 
    	 * If you are not sure what your SDK code is, it should be in the release e-mail.
    	 * If you still are not sure e-mail support@redlaser.com
    	 */

    	String _appCode = "gg";
        super.onCreate(icicle,_appCode);
        
        
        // Setup the optional scan button names.  To have no buttons pass in 
        // setButtons(null,null,null);

        setButtons(null, null, null);
        
        // To Remove the Title bar from RedLaserSDK Activity, add the NoTitleBar Theme in your Android Manifest.
        
        
        // Remove the Status Bar from this window.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        }
    
    /* Only needed if you want buttons on your overlay. */
	@Override
	protected final void onButton1Click() {
		Log.v("search", "search");
		BarcodeResult result = new BarcodeResult();
		result.IsValid = true;
		result.EAN = Constants.SEARCH_BARCODE_RESULT;
		returnResult(result);
	}

    /* Only needed if you want buttons on your overlay. */
	@Override
	protected final void onButton2Click() {
		Log.v("search", "search");
		BarcodeResult result = new BarcodeResult();
		result.IsValid = true;
		result.EAN = Constants.SEARCH_BARCODE_RESULT;
		returnResult(result);	
	}

    /* Only needed if you want buttons on your overlay. */
	@Override
	protected final void onButton3Click() {
		Log.v("search", "search");
		BarcodeResult result = new BarcodeResult();
		result.IsValid = true;
		result.EAN = Constants.SEARCH_BARCODE_RESULT;
		returnResult(result);
	}

    /* This is where you get your barcode result. */
	@Override
	protected void onBarcodeScanned(BarcodeResult barcode) {
		// To get barcode type use barcode.getBarcodeType();
		Log.v("","barcode type:"+barcode.getBarcodeType());
		returnResult(barcode);
	}

    protected int getLogoResource(){
    	return R.drawable.goodguidescannerlogo;
    }
    protected int getBeepResource(){
    	return R.raw.beep;
    }
    
    // To close the scanner invoke finish()
    // This is taken care of as well by calling returnResult(BarcodeResult result)
}
