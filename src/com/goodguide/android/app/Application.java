/***
	Copyright (c) 2008-2009 CommonsWare, LLC
	
	Licensed under the Apache License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may obtain
	a copy of the License at
		http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package com.goodguide.android.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.cache.SimpleWebImageCache;
import com.commonsware.cwac.thumbnail.ThumbnailBus;
import com.commonsware.cwac.thumbnail.ThumbnailMessage;
import com.goodguide.android.app.R;
import com.goodguide.android.shared.Constants;
import com.urbanairship.push.APIDReceiver;
import com.urbanairship.push.AirMail;
import com.urbanairship.push.PushReceiver;

public class Application extends android.app.Application {
	private static String TAG="CacheDemo";
	private static boolean airmailDialogShown = false;
	private ThumbnailBus bus=new ThumbnailBus();
	private SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache;
    public void onCreate(){
    }
	
	public Application() {
		super();
		//test
		Thread.setDefaultUncaughtExceptionHandler(onBlooey);
	}
	
	void goBlooey(Throwable t) {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		
		builder
			.setTitle(R.string.exception)
			.setMessage(t.toString())
			.setPositiveButton(R.string.ok, null)
			.show();
	}
	
	ThumbnailBus getBus() {
		return(bus);
	}
	
	SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> getCache(int images) {
		if(cache == null) {
			cache = new SimpleWebImageCache<ThumbnailBus, ThumbnailMessage>(null, null, 100, bus);
		}
		return cache;
	}
	
	private Thread.UncaughtExceptionHandler onBlooey=
		new Thread.UncaughtExceptionHandler() {
		public void uncaughtException(Thread thread, Throwable ex) {
			Log.e(TAG, "Uncaught exception", ex);
			goBlooey(ex);
		}
	};
}