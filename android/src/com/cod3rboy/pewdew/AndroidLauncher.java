package com.cod3rboy.pewdew;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

import de.golfgl.gdxgamesvcs.GpgsClient;
import de.golfgl.gdxgamesvcs.IGameServiceListener;

// @todo fix play services crash on some devices
public class AndroidLauncher extends AndroidApplication implements Services {

    private InterstitialAd interstitialAd;
    private final String AD_APP_ID = "ca-app-pub-3288414679602977~3658835069";
    private final String INTERSTITIAL_UNIT_ID = "ca-app-pub-3288414679602977/4651642095";
    private GpgsClient googlePlayGamesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;
        config.numSamples = 8;
        config.stencil = 0;
        googlePlayGamesClient = new GpgsClient().initialize(this, false);
        PewDew.gsClient = googlePlayGamesClient;
        googlePlayGamesClient.setListener(new IGameServiceListener() {
            @Override
            public void gsOnSessionActive() {
                PewDew.signedIn = true;
                PewDew.playerName = googlePlayGamesClient.getPlayerDisplayName();
                if(BuildConfig.DEBUG) Log.d("Android Launcher", "Google Play Games - OnSessionActive");
            }

            @Override
            public void gsOnSessionInactive() {
                PewDew.signedIn = false;
                PewDew.playerName = "";
                if(BuildConfig.DEBUG) Log.d("Android Launcher", "Google Play Games - OnSessionInactive");
            }

            @Override
            public void gsShowErrorToUser(GsErrorType et, String msg, Throwable t) {
                if(et == GsErrorType.errorLoginFailed)
                    Toast.makeText(getApplicationContext(), "Login failed. Try Again ..", Toast.LENGTH_SHORT).show();
                else if(et == GsErrorType.errorLogoutFailed)
                    Toast.makeText(getApplicationContext(), "Logout failed. Try Again ..", Toast.LENGTH_SHORT).show();
                else if(et == GsErrorType.errorServiceUnreachable) ;
                    Toast.makeText(getApplicationContext(), "Cannot reach play games services ..", Toast.LENGTH_SHORT).show();
                PewDew.signedIn = false;
                PewDew.playerName = "";
                if(BuildConfig.DEBUG) Log.d("Android Launcher", "Google Play Games - ShowErrorToUser");
            }
        });
        MobileAds.initialize(this, AD_APP_ID);
        initializeApp(new PewDew(), config);
    }

    private void initializeApp(ApplicationListener listener, AndroidApplicationConfiguration config) {
        // Do stuff to setup view correctly
        // Create Layout
        RelativeLayout layout = new RelativeLayout(this);
        // Do the stuff that initialize would do when setting up view by itself
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        } catch (Exception ex) {
            log("AndroidApplication", "Content already displayed, cannot request FEATURE_NO_TITLE", ex);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // Manually create view and set it the contentView of the Activity
        // Get game view
        View gameView = initializeForView(listener, config);
        // Create ad view
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(INTERSTITIAL_UNIT_ID);
        if (BuildConfig.DEBUG) {
            // Debug Interstitial Ads in Debug Build
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    Log.d("PewDew Game", "Interstitial Ad closed");
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    Log.d("PewDew Game", "Interstitial Ad failed to load");
                }

                @Override
                public void onAdOpened() {
                    Log.d("PewDew Game", "Interstitial Ad opened");
                }

                @Override
                public void onAdLoaded() {
                    Log.d("PewDew Game", "Interstitial Ad loaded");
                }

                @Override
                public void onAdClicked() {
                    Log.d("PewDew Game", "Interstitial Ad clicked");
                }
            });
        }
        // Add views to layout in stack order
        layout.addView(gameView);
		/*RelativeLayout.LayoutParams adParams =
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);*/
        // Hook it all up
        setContentView(layout);
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

    @Override
    public void loadInterstitialAd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Get and load ad request
                if (!interstitialAd.isLoaded()) {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    interstitialAd.loadAd(adRequest);
                }
            }
        });
    }

    @Override
    public void showInterstitialAd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(googlePlayGamesClient != null)
            googlePlayGamesClient.onGpgsActivityResult(requestCode, resultCode, data);
    }
}
