package com.goodguide.android.app;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.goodguide.android.utilities.ImageDisplayer;
import com.goodguide.android.utilities.ImageReceivedCallback;

public class ImageReceiver extends Thread
{
    String url;
    ImageReceivedCallback callback;
    ImageView view;
 
    public ImageReceiver(String url, ImageReceivedCallback callback, ImageView view)
    {
        this.url = url;
        this.callback = callback;
        this.view = view;
        start();
    }
    
    public class PatchInputStream extends FilterInputStream {
    	  public PatchInputStream(InputStream in) {
    	    super(in);
    	  }
    	  public long skip(long n) throws IOException {
    	    long m = 0L;
    	    while (m < n) {
    	      long _m = in.skip(n-m);
    	      if (_m == 0L) break;
    	      m += _m;
    	    }
    	    return m;
    	  }
    	}

 
    public void run()
    {
        try
        {   
            InputStream in = new java.net.URL(url).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(new PatchInputStream(in));        	
            ImageDisplayer displayer = new ImageDisplayer(view, bitmap);
            callback.onImageReceived(displayer);
        }
        catch (IOException e)
        {
//        	view.setImageResource(R.drawable.image_not_available);
        	ImageDisplayer displayer = new ImageDisplayer(view, null);
            callback.onImageReceived(displayer);
            e.printStackTrace();
        }
    }
 
}
