package com.cod3rboy.pewdew;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.util.List;

public class AndroidLauncher extends AndroidApplication implements Services {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		config.numSamples = 8;
		config.stencil = 0;
		initialize(new PewDew(), config);
	}

	@Override
	public void share() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.share_msg), getPackageName()));
		startActivity(Intent.createChooser(shareIntent, String.format("Share %s via", getString(R.string.app_name))));
	}

	@Override
	public void rate() {
		final String appId = getPackageName();
		//Log.e("Sharing Game" ,"Package Name or AppId is : " + appId);
		Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("market://details?id=%s", appId)));
		boolean marketFound = false;

		// Find all the applications able to handle our rateIntent
		final List<ResolveInfo> otherApps = getPackageManager().queryIntentActivities(rateIntent, 0);
		for (ResolveInfo otherApp : otherApps) {
			// Look for Google Play Application
			if (otherApp.activityInfo.packageName.contentEquals("com.android.vending")) {
				ActivityInfo otherAppActivity = otherApp.activityInfo;
				ComponentName componentName = new ComponentName(otherAppActivity.applicationInfo.packageName, otherAppActivity.name);
				// Make sure it does NOT open in the stack of my own app activity
				rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// Task reparenting if needed
				rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				// If Google Play was already open in a search result this make sure it still
				// go to this app page
				rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// This make sure only the Google Play app is allowed to intercept the intent
				rateIntent.setComponent(componentName);
				startActivity(rateIntent);
				marketFound = true;
				break;
			}
		}

		// If Google Play app not present in device, open web browser
		if (!marketFound) {
			startActivity(new Intent(
					Intent.ACTION_VIEW,
					Uri.parse(String.format("https://play.google.com/store/apps/details?id=%s", appId))));
		}
	}
}
