package com.ambabovpn.pro;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.ambabovpn.pro.util.ToastUtil;
import com.ambabovpn.tunnel.SocksHttpCore;
import com.ambabovpn.tunnel.config.ExceptionHandler;
import com.ambabovpn.pro.util.Utils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.onesignal.OneSignal;

import java.util.Date;

public class AmbaboVpnProApp extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver
{
	ToastUtil toastUtil;
	private static final String TAG = AmbaboVpnProApp.class.getSimpleName();
	public static final String PREFS_GERAL = "SocksHttpGERAL";
	//Test Ads
	public static final String ADS_UNITID_INTERSTITIAL_MAIN = "ca-app-pub-3940256099942544/1033173712";
	public static final String ADS_UNITID_BANNER_MAIN = "ca-app-pub-2245398304049642/3185447877";
	public static final String ADS_UNITID_BANNER_SOBRE = "ca-app-pub-2245398304049642/3185447877";
	public static final String ADS_UNITID_BANNER_TEST = "ca-app-pub-2245398304049642/3185447877";
	public static final String APP_FLURRY_KEY = "RQQ8J9Q2N4RH827G32X9";
	public static final int dakm2901wdqir32rpj3209 = 2*3600*1000;
	private AmbaboVpnProApp mApp;
	private AppOpenAdManager appOpenAdManager;
	private Activity currentActivity;
	public interface OnShowAdCompleteListener {
		void onShowAdComplete();
	}
	private static final String ONESIGNAL_APP_ID = "83176381-914e-4967-94f6-4b6e18709051";

	@Override
	public void onCreate()
	{
		super.onCreate();
		mApp = this;
		SocksHttpCore.init(this);
		this.registerActivityLifecycleCallbacks(this);
		MobileAds.initialize(
				this,
				new OnInitializationCompleteListener() {
					@Override
					public void onInitializationComplete(InitializationStatus initializationStatus) {}
				});
		ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
		appOpenAdManager = new AppOpenAdManager();
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		// Enable verbose OneSignal logging to debug issues if needed.
		OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
		// OneSignal Initialization
		OneSignal.initWithContext(this);
		OneSignal.setAppId(ONESIGNAL_APP_ID);
		// promptForPushNotifications will show the native Android notification permission prompt.
		// We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
		OneSignal.promptForPushNotifications();

		Utils.overrideFont(getApplicationContext(), "SERIF", "Zag Bold.otf");
	}

	private class AppOpenAdManager {
		private static final String LOG_TAG = "AppOpenAdManager";
		private static final String AD_UNIT_ID = "ca-app-pub-2905188701168289/8526766860";

		private AppOpenAd appOpenAd = null;
		private boolean isLoadingAd = false;
		private boolean isShowingAd = false;
		private long loadTime = 0;

		/** Constructor. */
		public AppOpenAdManager() {}

		public void loadAd(Context context) {
			// Do not load ad if there is an unused ad or one is already loading.
			if (isLoadingAd || isAdAvailable()) {
				return;
			}

			isLoadingAd = true;
			AdRequest request = new AdRequest.Builder().build();
			AppOpenAd.load(
					context, AD_UNIT_ID, request,
					AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
					new AppOpenAd.AppOpenAdLoadCallback() {
						@Override
						public void onAdLoaded(AppOpenAd ad) {
							// Called when an app open ad has loaded.
							//Log.d(LOG_TAG, "Ad was loaded.");
							appOpenAd = ad;
							isLoadingAd = false;
							loadTime = (new Date()).getTime();
						}

						@Override
						public void onAdFailedToLoad(LoadAdError loadAdError) {
							// Called when an app open ad has failed to load.
							//Log.d(LOG_TAG, loadAdError.getMessage());
							isLoadingAd = false;
						}
					});
		}
		private void showAdIfAvailable(@NonNull final Activity activity) {
			showAdIfAvailable(
					activity,
					new OnShowAdCompleteListener() {
						@Override
						public void onShowAdComplete() {
							// Empty because the user will go back to the activity that shows the ad.
						}
					});
		}

		public void showAdIfAvailable(
				@NonNull final Activity activity,
				@NonNull OnShowAdCompleteListener onShowAdCompleteListener){
			// If the app open ad is already showing, do not show the ad again.
			if (isShowingAd) {
				//Log.d(LOG_TAG, "The app open ad is already showing.");
				return;
			}

			// If the app open ad is not available yet, invoke the callback then load the ad.
			if (!isAdAvailable()) {
				//toastUtil.showErrorToast("The interstitial ad is Loading.");
				Log.d(LOG_TAG, "The app open ad is not ready yet.");
				onShowAdCompleteListener.onShowAdComplete();
				loadAd(activity);
				return;
			}

			appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
				@Override
				public void onAdClicked() {
					super.onAdClicked();
				}

				@Override
				public void onAdDismissedFullScreenContent() {
					// Called when fullscreen content is dismissed.
					// Set the reference to null so isAdAvailable() returns false.
					//Log.d(LOG_TAG, "Ad dismissed fullscreen content.");
					appOpenAd = null;
					isShowingAd = false;

					onShowAdCompleteListener.onShowAdComplete();
					loadAd(activity);
				}

				@Override
				public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
					// Called when fullscreen content failed to show.
					// Set the reference to null so isAdAvailable() returns false.
					//Log.d(LOG_TAG, adError.getMessage());
					appOpenAd = null;
					isShowingAd = false;

					onShowAdCompleteListener.onShowAdComplete();
					loadAd(activity);
				}

				@Override
				public void onAdImpression() {
					super.onAdImpression();
				}

				@Override
				public void onAdShowedFullScreenContent() {
					// Called when fullscreen content is shown.
					//Log.d(LOG_TAG, "Ad showed fullscreen content.");
				}
			});
			isShowingAd = true;
			appOpenAd.show(activity);
		}
		private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
			long dateDifference = (new Date()).getTime() - this.loadTime;
			long numMilliSecondsPerHour = 3600000;
			return (dateDifference < (numMilliSecondsPerHour * numHours));
		}
		/** Check if ad exists and can be shown. */
		private boolean isAdAvailable() {
			return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
		}
	}

	/** ActivityLifecycleCallback methods. */
	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

	@Override
	public void onActivityStarted(Activity activity) {
		// Updating the currentActivity only when an ad is not showing.
		if (!appOpenAdManager.isShowingAd) {
			currentActivity = activity;
		}
	}

	@Override
	public void onActivityResumed(Activity activity) {}

	@Override
	public void onActivityStopped(Activity activity) {}

	@Override
	public void onActivityPaused(Activity activity) {}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

	@Override
	public void onActivityDestroyed(Activity activity) {}

	/** LifecycleObserver method that shows the app open ad when the app moves to foreground. */
	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	protected void onMoveToForeground() {
		// Show the ad (if available) when the app moves to foreground.
		appOpenAdManager.showAdIfAvailable(currentActivity);
	}

}
