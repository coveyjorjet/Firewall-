package com.ambabovpn.pro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
//import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ambabovpn.pro.activities.HostChecker;
import com.ambabovpn.pro.util.RetrieveData;
import com.ambabovpn.pro.util.StoredData;
import com.ambabovpn.pro.view.DataTransferGraph;
import com.ambabovpn.pro.view.GraphHelper;
import com.ambabovpn.tunnel.SocksHttpService;
import com.ambabovpn.tunnel.StatisticGraphData;
import com.ambabovpn.tunnel.config.ConfigParser;
import com.ambabovpn.tunnel.config.ExceptionHandler;
import com.ambabovpn.tunnel.config.Setting;
import com.ambabovpn.tunnel.logger.ConnectionStatus;
import com.ambabovpn.tunnel.logger.SkStatus;
import com.ambabovpn.tunnel.tunnel.TunnelManagerHelper;
import com.ambabovpn.tunnel.tunnel.TunnelUtils;
import com.ambabovpn.pro.activities.AboutActivity;
import com.ambabovpn.pro.activities.BaseActivity;
import com.ambabovpn.pro.activities.ConfigGeneralActivity;
import com.ambabovpn.pro.adapter.LogsAdapter;
import com.ambabovpn.pro.adapter.SpinnerAdapter;
import com.ambabovpn.pro.util.AESCrypt;
import com.ambabovpn.pro.util.ConfigUpdate;
import com.ambabovpn.pro.util.ConfigUtil;
import com.ambabovpn.pro.util.ToastUtil;
import com.ambabovpn.pro.util.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class AmbaboVpnProActivity extends BaseActivity
implements OnClickListener, SkStatus.StateListener {
	private FirebaseAnalytics mFirebaseAnalytics;

	public static final String app_name = new String(new byte[]{70, 105, 114, 101, 119, 97, 108, 108, 32, 73, 110, 106, 101, 99, 116, 32, 86, 112, 110});
	public static final String app_base = new String(new byte[]{99, 111, 109, 46, 97, 109, 98, 97, 98, 111, 118, 112, 110, 46, 112, 114, 111});

	private ToastUtil toastutil;
	private boolean isConnected  = true;
    private CoordinatorLayout coordinatorLayout;
	private boolean mShown, mShown2;
	public String versionName;
	private View view6;private View view5;private View view4;private View view3;private View view2;private View view1;
    private InterstitialAd mInterstitialAd;
	private GuideView mGuideView;
    public static int PICK_FILE = 1;
	private static final String DNS_BIN = "libdns";
	private Process dnsProcess;
	private File filedns;
	private static final String UPDATE_VIEWS = "MainUpdate";
	public static LogsAdapter mAdapter;
	private Setting mConfig;
	private Toolbar toolbar_main;
	private Handler mHandler;
	private Button starterButton;
	private Button inspectAds;
	private ImageButton btnMenu;
    private DrawerLayout drawerLayout;
	private AdView adsBannerView;
	private ConfigUtil config;
	private TextView status;
	private Spinner serverSpinner;
	private SpinnerAdapter serverAdapter;
	private ArrayList<JSONObject> serverList;
	private SweetAlertDialog sDialog;
	private RecyclerView logList;
	private BottomSheetBehavior<View> bottomSheetBehavior;
	private View bshl;
	private SharedPreferences sp;
	private static final int START_VPN_PROFILE = 2002;
	private TextView bytesIn;
	private TextView bytesOut;
	private String[] sniffingList = new String[]{
			"com.minhui.networkcapture.pro",
			"com.minhui.networkcapture",
			"app.greyshirts.sslcapture",
			"com.emanuelef.remote_capture",
			"jp.co.taosoftware.android.packetcapture",
			"com.evbadroid.proxymon",
			"tech.httptoolkit.android.v1",
			"de.feuerbergsoftware.ssl_checker",
			"com.evbadroid.ceromon",
			"com.guoshi.httpcanary",
			"com.guoshi.httpcanary.premium"
	};
	private String[] torrentList = new String[] {
		"com.termux",
		"com.tdo.showbox",
		"com.nitroxenon.terrarium",
		"com.pklbox.translatorspro",
		"com.xunlei.downloadprovider",
		"com.epic.app.iTorrent",
		"hu.bute.daai.amorg.drtorrent",
		"com.mobilityflow.torrent.prof",
		"com.brute.torrentolite",
		"com.nebula.swift",
		"tv.bitx.media",
		"com.DroiDownloader",
		"bitking.torrent.downloader",
		"org.transdroid.lite",
		"com.mobilityflow.tvp",
		"com.gabordemko.torrnado",
		"com.frostwire.android",
		"com.vuze.android.remote",
		"com.akingi.torrent",
		"com.utorrent.web",
		"com.paolod.torrentsearch2",
		"com.delphicoder.flud.paid",
		"com.teeonsoft.ztorrent",
		"megabyte.tdm",
		"com.bittorrent.client.pro",
		"com.mobilityflow.torrent",
		"com.utorrent.client",
		"com.utorrent.client.pro",
		"com.bittorrent.client",
		"torrent",
		"com.AndroidA.DroiDownloader",
		"com.indris.yifytorrents",
		"com.delphicoder.flud",
		"com.oidapps.bittorrent",
		"dwleee.torrentsearch",
		"com.vuze.torrent.downloader",
		"megabyte.dm",
		"com.asantos.vip",
		"com.fgrouptech.kickasstorrents",
		"com.jrummyapps.rootbrowser.classic",
		"com.bittorrent.client",
		"com.x.gdf",
		"co.we.torrent"
	};
	private String[] dnsList = {
			"208.67.222.222",
			"1.1.1.1",
			"8.26.56.26",
			"8.8.8.8",
			"9.9.9.9",
			"185.225.168.168",
			"76.76.19.19",
			"64.6.64.6",
			"216.87.84.211",
			"77.88.8.88",
			"84.200.69.80",
			"209.244.0.3",
			"216.146.35.35",
			"91.239.100.100",
			"156.154.70.5",
			"195.46.39.39",
			"74.82.42.42",
	};
	private Random r = new Random();
	boolean isNight;

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	private RewardedAd mRewardedAd;

	//Ad Time Implementation
	private SweetAlertDialog sDialogAd;
	private TextView timeCountdown;
	private Button addTime;
	private CountDownTimer mCountDownTimer;
	private boolean mTimerRunning;
	private long mStartTimeInMillis;
	private long mTimeLeftInMillis;
	private long mEndTime;
	private long saved_ads_time;
	private boolean mTimerEnabled;

	private LineChart mChart;
	private GraphHelper graph;
	private Thread dataThread;
	private Button showGraphBtn;
	private ImageView iconImgV;

	private String rewardedAdId = "ca-app-pub-2905188701168289/4432690752";
	private String adError;

	@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
		//SplashScreen Api
		SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
		loadRewarded();
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
		developerYarn();
		toastutil = new ToastUtil(this);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		mHandler = new Handler();
		mConfig = new Setting(this);
		MobileAds.initialize(this, new OnInitializationCompleteListener() {
			@Override
			public void onInitializationComplete(InitializationStatus initializationStatus) {
				//loadRewarded();
			}
		});
		loadInterstitial();
		doLayout();
		checkSniffingtool();
		checkTorrentApps();
		initBytesInAndOut();
		doUpdateLayout();
	}

    @SuppressLint("CutPasteId")
	private void doLayout() {
        setContentView(R.layout.drawer_layout);

		SharedPreferences sharedPreferences = getSharedPreferences(AmbaboVpnProApp.PREFS_GERAL, Context.MODE_PRIVATE);
		SwitchCompat switchCompat = findViewById(R.id.switchDayNight);
		isNight = sharedPreferences.getBoolean("night_mode", false);
		if(isNight){
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("night_mode",true);
			editor.commit();
			switchCompat.setChecked(true);
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		}
		switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked){
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putBoolean("night_mode",true);
						editor.commit();
						switchCompat.setChecked(true);
						AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
						restart_app();

					}else {
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putBoolean("night_mode",false);
						editor.commit();
						switchCompat.setChecked(false);
						AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
						restart_app();
					}
				}
			});
//        toolbar_main = (Toolbar) findViewById(R.id.toolbar_main);
//
//        setSupportActionBar(toolbar_main);

		// set ADS
        adsBannerView = (AdView) findViewById(R.id.adBannerMainView);
		adsBannerView.loadAd(new AdRequest.Builder().build());
		adsBannerView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				if (adsBannerView != null) {
				adsBannerView.setVisibility(View.VISIBLE);
				}
			}
		});

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		navListener();
		view1 = findViewById(R.id.serverSpinner);
        view2 = findViewById(R.id.connection_status);
        view3 = findViewById(R.id.bottom_sheet);
        view4 = findViewById(R.id.activity_starterButtonMain);
        view5 = findViewById(R.id.bytes_in);
        view6 = findViewById(R.id.bytes_out);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
		btnMenu = (ImageButton) findViewById(R.id.btnMenu);
		btnMenu.setOnClickListener(this);
		starterButton = (Button) findViewById(R.id.activity_starterButtonMain);
		starterButton.setOnClickListener(this);
		//inspectAds = (Button) findViewById(R.id.inspectAds);
		//inspectAds.setOnClickListener(this);
		status = (TextView) findViewById(R.id.connection_status);
		config = new ConfigUtil(this);
		serverSpinner = (Spinner) findViewById(R.id.serverSpinner);
		serverList = new ArrayList<>();
		serverAdapter = new SpinnerAdapter(this, R.id.serverSpinner, serverList);
		serverSpinner.setAdapter(serverAdapter);
		loadServer();
		updateConfig(true);
		SharedPreferences sPrefs = mConfig.getPrefsPrivate();
		sPrefs.edit().putBoolean(Setting.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
		sPrefs.edit().putInt(Setting.TUNNELTYPE_KEY, Setting.bTUNNEL_TYPE_SSH_PROXY).apply();
		TextView configVer = (TextView) findViewById(R.id.config_v);
		configVer.setText(config.getVersion());
		TextView appVer = (TextView) findViewById(R.id.appVersion);
		appVer.setText(String.valueOf(BuildConfig.VERSION_CODE));
		@SuppressLint("CutPasteId") View bottomSheet = findViewById(R.id.bottom_sheet);
		this.bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
		this.bshl = findViewById(R.id.bshl);
		serverSpinner.setSelection(sPrefs.getInt("LastSelectedServer", 0));
        serverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
                    SharedPreferences sPrefs = mConfig.getPrefsPrivate();
                    SharedPreferences.Editor edit = sPrefs.edit();
                    edit.putInt("LastSelectedServer", p3).apply();
					showInterstitial();
                }

                @Override
                public void onNothingSelected(AdapterView<?> p1) {
                }
            });

        View persistentbottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);

        final BottomSheetBehavior behavior = BottomSheetBehavior.from(persistentbottomSheet);
		behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
				@Override
				public void onStateChanged(@NonNull View view, int i) {
					switch (i) {
						case BottomSheetBehavior.STATE_HIDDEN:
							break;
						case BottomSheetBehavior.STATE_EXPANDED:
							break;
						case BottomSheetBehavior.STATE_COLLAPSED:
							break;
						case BottomSheetBehavior.STATE_DRAGGING:
							break;
						case BottomSheetBehavior.STATE_SETTLING:
							behavior.setHideable(false);
							break;
						case BottomSheetBehavior.STATE_HALF_EXPANDED:
							break;
					}
				}
				@Override
				public void onSlide(@NonNull View view, float slideOffset) {

				}
			});

		bshl.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        //btnBottomSheet.setText(R.string.close);
                    }
                    else
                    {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        //btnBottomSheet.setText(R.string.expand);
                    }
                }
            });

		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		mAdapter = new LogsAdapter(layoutManager, this);
		logList = (RecyclerView) findViewById(R.id.recyclerLog);
		logList.setAdapter(mAdapter);
		logList.setLayoutManager(layoutManager);
		mAdapter.scrollToLastPosition();
		boolean isRunning = SkStatus.isTunnelActive();
        if (isRunning) {
			//this.graph.start();
            serverSpinner.setEnabled(false);
        } else {
			//this.graph.stop();
            serverSpinner.setEnabled(true);
        }


		SharedPreferences sharedPreferences1 = getSharedPreferences("Saved_Time", Context.MODE_PRIVATE);
		timeCountdown = (TextView) findViewById(R.id.timerTextView);
		long defTime = sharedPreferences1.getLong("remaining_Time", AmbaboVpnProApp.dakm2901wdqir32rpj3209);
		setTime(defTime);
		//timeCountdown.setText();
		addTime = (Button) findViewById(R.id.btnAddTime);
		addTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadingAds();
				showRewarded();
			}
		});

		//Graph
		iconImgV = (ImageView) findViewById(R.id.iconImgView);
		mChart = (LineChart) findViewById(R.id.chart1);
		mChart.setVisibility(View.GONE);
		graph = GraphHelper.getHelper().with(this).color(Color.parseColor(getString(R.color.colorPrimary))).chart(mChart);
		if(!StoredData.isSetData){
			StoredData.setZero();
		}
		showGraphBtn = (Button) findViewById(R.id.showHide);
		showGraphBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//mChart.setVisibility(View.VISIBLE);
				if(mChart.getVisibility() == View.GONE){
					iconImgV.setVisibility(View.GONE);
					mChart.setVisibility(View.VISIBLE);
				}
				else if (mChart.getVisibility() == View.VISIBLE){
					iconImgV.setVisibility(View.VISIBLE);
					mChart.setVisibility(View.GONE);
				}
			}
		});

		/**Anti Sniff Implementation-by Ambabo**/
		//Detect Root
		SharedPreferences prefsPrivate = new Setting(this).getPrefsPrivate();
		if (prefsPrivate.getBoolean(Setting.BLOQUEAR_ROOT_KEY, false) ||
				ConfigParser.isDeviceRooted(this)) {
			starterButton.setEnabled(false);
			alertApp("Root Detected in your App", getString(R.string.root_detected), "root");
		}
	}
	private void doUpdateLayout() {
		SharedPreferences prefs = mConfig.getPrefsPrivate();
		//AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
		boolean isRunning = SkStatus.isTunnelActive();
		int tunnelType = prefs.getInt(Setting.TUNNELTYPE_KEY, Setting.bTUNNEL_TYPE_SSH_DIRECT);

		setStarterButton(starterButton, this);

		TextView publicIp = (TextView) findViewById(R.id.publicIp);
		publicIp.setText("Public Ip : " + getIpPublic());

        if (isRunning) {
			iconImgV.setVisibility(View.GONE);
			mChart.setVisibility(View.VISIBLE);
			this.graph.start();
            serverSpinner.setEnabled(false);
        } else {
			iconImgV.setVisibility(View.VISIBLE);
			mChart.setVisibility(View.GONE);
			this.graph.stop();
            serverSpinner.setEnabled(true);
        }
	}

	//Load all the data needed
	private synchronized void doSaveData() {
		try {
			SharedPreferences prefs = mConfig.getPrefsPrivate();
			SharedPreferences.Editor edit = prefs.edit();

			edit.apply();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void loadServerData() {
		try {
			SharedPreferences prefs = mConfig.getPrefsPrivate();
			SharedPreferences.Editor edit = prefs.edit();
			int pos1 = serverSpinner.getSelectedItemPosition();
		//	int tun = config.getServersArray().getJSONObject(pos1).getInt("TunnelType");

			String ssh_server = config.getServersArray().getJSONObject(pos1).getString("ServerIP");
			String remote_proxy = config.getServersArray().getJSONObject(pos1).getString("ProxyIP");
			String proxy_port = config.getServersArray().getJSONObject(pos1).getString("ProxyPort");
            String ssh_user = config.getServersArray().getJSONObject(pos1).getString("ServerUser");
            String ssh_password = config.getServersArray().getJSONObject(pos1).getString("ServerPass");
			String ssh_port = config.getServersArray().getJSONObject(pos1).getString("ServerPort");
			String ssl_port = config.getServersArray().getJSONObject(pos1).getString("SSLPort");
			String payload = config.getServersArray().getJSONObject(pos1).getString("Payload");
			String sni = config.getServersArray().getJSONObject(pos1).getString("SNI");
			String chaveKey = config.getServersArray().getJSONObject(pos1).getString("Slowchave");
			String serverNameKey = config.getServersArray().getJSONObject(pos1).getString("Nameserver");
			String dnsKey = config.getServersArray().getJSONObject(pos1).getString("Slowdns");


            edit.putString(Setting.USUARIO_KEY, ssh_user);
            edit.putString(Setting.SENHA_KEY, ssh_password);
			edit.putString(Setting.SERVIDOR_KEY, ssh_server);
			edit.putString(Setting.PROXY_IP_KEY, remote_proxy);
			edit.putString(Setting.PROXY_PORTA_KEY, proxy_port);


			boolean sslType = config.getServersArray().getJSONObject(pos1).getBoolean("isSSL");

			boolean sslpayload = config.getServersArray().getJSONObject(pos1).getBoolean("isPayloadSSL");

			boolean inject = config.getServersArray().getJSONObject(pos1).getBoolean("isInject");

			boolean direct = config.getServersArray().getJSONObject(pos1).getBoolean("isDirect");

			boolean slow = config.getServersArray().getJSONObject(pos1).getBoolean("isSlow");


            //SSH DIRECT
			if (direct)
			{
				prefs.edit().putBoolean(Setting.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
				prefs.edit().putInt(Setting.TUNNELTYPE_KEY, Setting.bTUNNEL_TYPE_SSH_DIRECT).apply();

				prefs.edit().putString(Setting.SERVIDOR_KEY, ssh_server).apply();
				prefs.edit().putString(Setting.SERVIDOR_PORTA_KEY, ssh_port).apply();

				prefs.edit().putString(Setting.PROXY_IP_KEY, remote_proxy).apply();
				prefs.edit().putString(Setting.PROXY_PORTA_KEY, proxy_port).apply();
				prefs.edit().putString(Setting.CUSTOM_PAYLOAD_KEY, payload).apply();
			}

             //SSH PROXY
			if (inject)
			{
				prefs.edit().putBoolean(Setting.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
				prefs.edit().putInt(Setting.TUNNELTYPE_KEY, Setting.bTUNNEL_TYPE_SSH_PROXY).apply();

				prefs.edit().putString(Setting.SERVIDOR_KEY, ssh_server).apply();
				prefs.edit().putString(Setting.SERVIDOR_PORTA_KEY, ssh_port).apply();

				prefs.edit().putString(Setting.PROXY_IP_KEY, remote_proxy).apply();
				prefs.edit().putString(Setting.PROXY_PORTA_KEY, proxy_port).apply();
				prefs.edit().putString(Setting.CUSTOM_PAYLOAD_KEY, payload).apply();
			}


            //SSH SSL
			if (sslType)
			{
				prefs.edit().putBoolean(Setting.PROXY_USAR_DEFAULT_PAYLOAD, true).apply();
				prefs.edit().putInt(Setting.TUNNELTYPE_KEY, Setting.bTUNNEL_TYPE_SSH_SSL).apply();

				prefs.edit().putString(Setting.SERVIDOR_KEY, ssh_server).apply();
				prefs.edit().putString(Setting.SERVIDOR_PORTA_KEY, ssl_port).apply();

				prefs.edit().putString(Setting.PROXY_IP_KEY, remote_proxy).apply();
				prefs.edit().putString(Setting.PROXY_PORTA_KEY, proxy_port).apply();

				prefs.edit().putString(Setting.CUSTOM_PAYLOAD_KEY, payload).apply();
				prefs.edit().putString(Setting.CUSTOM_SNI, sni).apply();

			}
			//SSL PAYLOAD
			if (sslpayload)
			{
				prefs.edit().putBoolean(Setting.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
				prefs.edit().putInt(Setting.TUNNELTYPE_KEY, Setting.bTUNNEL_TYPE_SSL_PAYLOAD).apply();

				prefs.edit().putString(Setting.SERVIDOR_KEY, ssh_server).apply();
				prefs.edit().putString(Setting.SERVIDOR_PORTA_KEY, ssl_port).apply();

				prefs.edit().putString(Setting.CUSTOM_PAYLOAD_KEY, payload).apply();
				prefs.edit().putString(Setting.CUSTOM_SNI, sni).apply();

			}


			//SLOW DIRECT
			if (slow)
			{

				prefs.edit().putString(Setting.CHAVE_KEY, chaveKey).apply();

				prefs.edit().putString(Setting.NAMESERVER_KEY, serverNameKey).apply();
				prefs.edit().putString(Setting.DNS_KEY, dnsKey).apply();

				prefs.edit().putString(Setting.SERVIDOR_KEY, ssh_server).apply();
				prefs.edit().putString(Setting.SERVIDOR_PORTA_KEY, ssh_port).apply();

				prefs.edit().putBoolean(Setting.PROXY_USAR_DEFAULT_PAYLOAD, true).apply();
				prefs.edit().putInt(Setting.TUNNELTYPE_KEY, Setting.bTUNNEL_TYPE_SLOWDNS).apply();
			}

			edit.apply();

		} catch (Exception e) {
			SkStatus.logInfo(e.getMessage());
		}
	}
	private void loadServer() {
		try {
			if (serverList.size() > 0) {
				serverList.clear();
				serverAdapter.notifyDataSetChanged();
			}
			for (int i = 0; i < config.getServersArray().length(); i++) {
				JSONObject obj = config.getServersArray().getJSONObject(i);
				serverList.add(obj);
				serverAdapter.notifyDataSetChanged();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//update data transfer
	void initBytesInAndOut() {
		bytesIn = (TextView) findViewById(R.id.bytes_in);
		bytesOut = (TextView) findViewById(R.id.bytes_out);
		StatisticGraphData.getStatisticData().setDisplayDataTransferStats(true);
	}
	private void updateHeaderCallback() {
		StatisticGraphData.DataTransferStats dataTransferStats = StatisticGraphData.getStatisticData().getDataTransferStats();
		bytesIn.setText(Utils.byteCountToDisplaySize(dataTransferStats.getTotalBytesReceived(), false));
		bytesOut.setText(Utils.byteCountToDisplaySize(dataTransferStats.getTotalBytesSent(), false));
	}

	//Config Update Notification builder
	private void confUpdateNotif(){

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder notification = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(getPackageName() + "ConfigUpdate");
            createNotification(notificationManager, getPackageName() + "ConfigUpdate");
        }

        notification.setContentTitle(getString(R.string.app_name))
            .setContentText(("Config Updated"))
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.main_icon))
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(Notification.PRIORITY_HIGH)
            .setShowWhen(true)
            .setSmallIcon(R.drawable.main_icon);
        notificationManager.notify(4130,notification.getNotification());
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
	private void createNotification(NotificationManager notificationManager, String id) {
		NotificationChannel mNotif = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			mNotif = new NotificationChannel(id, "ConfigUpdate", NotificationManager.IMPORTANCE_HIGH);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			mNotif.setShowBadge(true);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			notificationManager.createNotificationChannel(mNotif);
		}
    }

	//Config Update Checker
	private void updateConfig(final boolean isOnCreate) {
		new ConfigUpdate(this, new ConfigUpdate.OnUpdateListener() {
			@Override
			public void onUpdateListener(String result) {
				try {
					if (!result.contains("Error on getting data")) {
						String json_data = AESCrypt.decrypt(config.PASSWORD, result);
						if (isNewVersion(json_data)) {
							newUpdateDialog(result);
						} else {
							if (!isOnCreate) {
								noUpdateDialog();
							}
						}
					} else if(result.contains("Error on getting data") && !isOnCreate){
						errorUpdateDialog(result);
					}
				} catch (Exception e) {
					SkStatus.logInfo(e.getMessage());
				}
			}
		}).start(isOnCreate);
	}
	private boolean isNewVersion(String result) {
		try {
			String current = config.getVersion();
			String update = new JSONObject(result).getString("Version");
			return config.versionCompare(update, current);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	private void newUpdateDialog(final String result) throws JSONException, GeneralSecurityException{

		String json = AESCrypt.decrypt(config.PASSWORD, result);
		String releasenotes = new JSONObject(json).getString("ReleaseNotes");

		sDialog = new SweetAlertDialog(AmbaboVpnProActivity.this, SweetAlertDialog.SUCCESS_TYPE);

		sDialog.setTitleText("Update Available!!!");
		sDialog.setContentText(releasenotes);
		sDialog.setCancelText("CANCEL");
		sDialog.setConfirmText("OK");
		sDialog.showCancelButton(true);
		sDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog sDialog) {
				sDialog.dismiss();
			}
		});
		sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog sDialog) {
				try {
					sDialog.dismiss();
					confUpdateNotif();
					File file = new File(getFilesDir(), "Config.json");
					OutputStream out = new FileOutputStream(file);
					out.write(result.getBytes());
					out.flush();
					out.close();
					restart_app();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		sDialog.show();
    }
	private void noUpdateDialog() {
		sDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);

		sDialog.setTitleText("No Update Available");
		sDialog.setContentText("There is a no new update found.\nYou are using the latest Config Patch.\nIf the Servers are not Working, Contact me at the Following;\n Telegram : https://t.me/Ambabo");
		sDialog.setConfirmText(getString(R.string.ok));
		sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				sDialog.dismiss();
			}
		});
		sDialog.show();
	}
	private void errorUpdateDialog(String error) {
		sDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);

		sDialog.setTitleText("Error While Checking for Update");
		sDialog.setContentText("There is an error occurred when checking for update.\n Please contact the Developer.\n Telegram : https://t.me/Ambabo");
		sDialog.setConfirmText(getString(R.string.ok));
		sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				sDialog.dismiss();
			}
		});
		sDialog.show();

	}

    private String importer(Uri uri) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try
        {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));

            String line = "";
            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }
            reader.close();
        }
        catch (IOException e) {e.printStackTrace();}
        return builder.toString();
    }

	public void startOrStopTunnel(Activity activity) {
		if (SkStatus.isTunnelActive()) {
            SharedPreferences prefs = mConfig.getPrefsPrivate();
			TunnelManagerHelper.stopSocksHttp(activity);
		}
		else {
			launchVPN();
		}
	}
	private void launchVPN() {
		Intent intent = VpnService.prepare(this);

        if (intent != null) {
            SkStatus.updateStateString("USER_VPN_PERMISSION", "", R.string.state_user_vpn_permission,
									   ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
            // Start the query
            try {
                startActivityForResult(intent, START_VPN_PROFILE);
            } catch (ActivityNotFoundException ane) {
                SkStatus.logError(R.string.no_vpn_support_image);
            }
        } else {
            onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);
        }
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_FILE) {
			if (resultCode == RESULT_OK) {
				try {
					Uri uri = data.getData();
					String intentData = importer(uri);
					//String cipter = AESCrypt.decrypt(ConfigUtil.PASSWORD, intentData);
					File file = new File(getFilesDir(), "Config.json");
					OutputStream out = new FileOutputStream(file);
					out.write(intentData.getBytes());
					out.flush();
					out.close();
					restart_app();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (requestCode == START_VPN_PROFILE) {
			if (resultCode == Activity.RESULT_OK) {
				SharedPreferences prefs = mConfig.getPrefsPrivate();

				if (!TunnelUtils.isNetworkOnline(this)) {
					toastutil.showErrorToast("No Internet Connection");
				} else
					TunnelManagerHelper.startSocksHttp(this);
			}
		}
		if(resultCode == Activity.RESULT_OK){
			try {
				startStop(true);
			}catch (Exception e){
				toastutil.showErrorToast(e.getMessage());
			}
		}
	}
	@Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
	@Override
	public void onClick(View p1) {
		switch (p1.getId()) {
			case R.id.activity_starterButtonMain:
				doSaveData();
				loadServerData();
				startOrStopTunnel(this);
				mShown = false;
				mShown2 = false;
				break;
			case R.id.btnMenu:
				showMenu();
				break;
			/*case R.id.inspectAds:
				inspectAds.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						MobileAds.openAdInspector(AmbaboVpnProActivity.this, new OnAdInspectorClosedListener() {
							public void onAdInspectorClosed(@Nullable AdInspectorError error) {
								// Error will be non-null if ad inspector closed due to an error.
								toastutil.showErrorToast(error.toString());
							}
						});
					}
				});
				break;*/
		}
	}
	@Override
	public void updateState(final String state, String msg, int localizedResId, final ConnectionStatus level, Intent intent)
	{
		mHandler.post(new Runnable() {
				@Override
				public void run() {
					doUpdateLayout();
					if(SkStatus.isTunnelActive()){
						iconImgV.setVisibility(View.GONE);
						mChart.setVisibility(View.VISIBLE);
						graph.start();
						if(level.equals(ConnectionStatus.LEVEL_CONNECTED)) {
							start();
							if (!mShown){
								toastutil.showSuccessToast("Connected");
								mShown = true;
							}
							status.setText(R.string.state_connected);
                            status.setTextColor(Color.parseColor("#FF9800"));

							if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
								bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
							}

						/* ImageView bg = (ImageView)findViewById(R.id.kantot);
							bg.setImageResource(R.drawable.antenna_3);*/

						}
						if(level.equals(ConnectionStatus.LEVEL_NOTCONNECTED)){

							status.setText(R.string.state_disconnected);
						}
						if(level.equals(ConnectionStatus.LEVEL_CONNECTING_SERVER_REPLIED)){
							status.setText( R.string.state_auth);

							/*ImageView bg = (ImageView)findViewById(R.id.kantot);
							bg.setImageResource(R.drawable.antenna_2);*/

						}
						if(level.equals(ConnectionStatus.LEVEL_CONNECTING_NO_SERVER_REPLY_YET)){
							status.setText(R.string.state_connecting);
							status.setTextColor(Color.parseColor("#ff00c5d6"));


							if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED && !mConfig.getHideLog()) {
								bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
							}


						/*	ImageView bg = (ImageView)findViewById(R.id.kantot);
							bg.setImageResource(R.drawable.antenna_1);*/

						}
						if(level.equals(ConnectionStatus.UNKNOWN_LEVEL)){
							stop();
							if (!mShown2){
								toastutil.showErrorToast("Disconnected");
								mShown2 = true;
							}
							status.setText(R.string.state_disconnected);
                            status.setTextColor(Color.parseColor("#FFFF0000"));



							if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
								bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

							}
						/**	ImageView bg = (ImageView)findViewById(R.id.kantot);
							bg.setImageResource(R.drawable.antenna_disconnect);*/

							//bg.setImageTintList(getResources().getColorStateList(R.color.colorOpposite));

						}

					}
					if(level.equals(ConnectionStatus.LEVEL_NONETWORK)){
						iconImgV.setVisibility(View.VISIBLE);
						mChart.setVisibility(View.GONE);
						graph.stop();
						status.setText(R.string.state_nonetwork);
					}
					if(level.equals(ConnectionStatus.LEVEL_AUTH_FAILED)){
						iconImgV.setVisibility(View.VISIBLE);
						mChart.setVisibility(View.GONE);
						graph.stop();
						status.setText(R.string.state_auth_failed);
					}
				}
			});
		switch (state) {
			case SkStatus.SSH_CONECTADO:
				// carrega ads banner
				if (adsBannerView != null) {
					adsBannerView.setAdListener(new AdListener() {
							@Override
							public void onAdLoaded() {
								if (adsBannerView != null && !isFinishing()) {
									adsBannerView.setVisibility(View.VISIBLE);
								}
							}
						});
					adsBannerView.postDelayed(new Runnable() {
							@Override
							public void run() {
								// carrega ads interestitial
								//AdsManager.newInstance(getApplicationContext())
								//	.loadAdsInterstitial();
								// ads banner
								showInterstitial();
								if (adsBannerView != null && !isFinishing()) {
									adsBannerView.loadAd(new AdRequest.Builder()
														 .build());
								}
							}
						}, 5000);
				}
				break;
		}
	}


	/**
	 * Recebe locais Broadcast
	 */

	private BroadcastReceiver mActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            if (action.equals(UPDATE_VIEWS) && !isFinishing()) {
				doUpdateLayout();
			}
        }
    };


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.configUpdate:
				updateConfig(false);
				break;
			case R.id.miExit:
				if (Build.VERSION.SDK_INT >= 16) {
					finishAffinity();
				}

				System.exit(0);
				break;
		}

		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onBackPressed() {
		showInterstitial();
		showExitDialog();
	}
	@Override
    public void onResume() {
        super.onResume();
		SharedPreferences sPrefs = mConfig.getPrefsPrivate();
        int server = sPrefs.getInt("LastSelectedServer", 0);
		serverSpinner.setSelection(server);

        if (adsBannerView != null) {
            adsBannerView.resume();
        }
		SkStatus.addStateListener(this);
		if (adsBannerView != null) {
			adsBannerView.resume();
		}
		new Timer().schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								updateHeaderCallback();
							}
						});
				}
			}, 0,1000);
    }
	@Override
	protected void onPause() {
		super.onPause();
		doSaveData();
		SkStatus.removeStateListener(this);
		if (adsBannerView != null) {
			adsBannerView.pause();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();

		SharedPreferences prefs = mConfig.getPrefsPrivate();
        SharedPreferences.Editor edit = prefs.edit();
        int server = serverSpinner.getSelectedItemPosition();
        edit.putInt("LastSelectedServer", server);
        edit.apply();

		if (adsBannerView != null) {
			adsBannerView.destroy();
		}
	}

	private void geoLocation(){
		new UpdateCore(AmbaboVpnProActivity.this, "http://ip-api.com/json", new UpdateCore.Listener() {
			@Override
			public void onLoading()
			{
				sDialog = new SweetAlertDialog(AmbaboVpnProActivity.this, SweetAlertDialog.PROGRESS_TYPE);
				sDialog.setTitleText("Searching for Your IP Location");
				sDialog.show();
				sDialog.setCancelable(true);
			}
			@Override
			public void onCompleted(String config) throws Exception {
				//dismiss previous dialog mga tsong : ung progress type
				sDialog.dismiss();
				JSONObject geo = new JSONObject(config);
				StringBuffer sb = new StringBuffer();
				sb.append("<br>").append("ISP: ").append(geo.getString("isp"));
				sb.append("<br>").append("Time Zone: ").append(geo.getString("timezone"));
				sb.append("<br>").append("Country Code: ").append(geo.getString("countryCode"));
				sb.append("<br>").append("Country: ").append(geo.getString("country"));
				sb.append("<br>").append("Region: ").append(geo.getString("regionName"));
				sb.append("<br>").append("City: ").append(geo.getString("city"));

				sDialog = new SweetAlertDialog(AmbaboVpnProActivity.this, SweetAlertDialog.SUCCESS_TYPE);

				sDialog.setTitle("IP Check Location");
				sDialog.setContentText(sb.toString());
				sDialog.setConfirmText(getString(R.string.ok));
				sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
							sDialog.dismiss();
					}
				});
				sDialog.show();
			}
			@Override
			public void onCancelled() {
				sDialog.dismiss();
			}
			@Override
			public void onException(String ex) {
				sDialog.dismiss();
				SweetAlertDialog sDialog1;
				sDialog1 = new SweetAlertDialog(AmbaboVpnProActivity.this, SweetAlertDialog.ERROR_TYPE);

				sDialog1.setTitleText("Error While Getting Location");
				sDialog1.setContentText("There is an error occurred when checking for your Geo Location.<br>Please contact the Developer.<br>Telegram : https://t.me/Ambabo");
				sDialog1.setConfirmText(getString(R.string.ok));
				sDialog1.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						sDialog1.dismiss();
					}
				});
				sDialog1.show();

				toastutil.showErrorToast(ex);
			}
		}).execute();
	}

	private void about(){
        Intent aboutIntent = new Intent(this, AboutActivity.class);
		startActivity(aboutIntent);
    }
	private void clearData() {
		sDialog = new SweetAlertDialog(AmbaboVpnProActivity.this, SweetAlertDialog.WARNING_TYPE);
		sDialog.setTitleText("Warning !!");
		sDialog.setContentText("Are you sure to clear Firewall Inject application data including config updates?Click OK to Proceed");
		sDialog.setCancelText("CANCEL");
		sDialog.setConfirmText("OK");
		sDialog.showCancelButton(true);
		sDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
				@Override
				public void onClick(SweetAlertDialog sDialog) {
					AmbaboVpnProActivity.this.sDialog.dismiss();
				}
			});
		sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
				@Override
				public void onClick(SweetAlertDialog sDialog) {
					try {
						// clearing app data
						String packageName = getApplicationContext().getPackageName();
						Runtime runtime = Runtime.getRuntime();
						runtime.exec("pm clear "+packageName);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		sDialog.show();
	}
	public void Changelogs() {
        this.sDialog =  new SweetAlertDialog(AmbaboVpnProActivity.this, SweetAlertDialog.NORMAL_TYPE);
        this.sDialog.setTitleText("Release Notes");
        this.sDialog.setContentText(this.config.getReleaseNotes());
        this.sDialog.show();

    }
	public void supportDeveloper(){

		sDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
		sDialog.setTitleText("Support Developer");
		sDialog.setContentText("Please support my work by Watching Ads, or Donating in my Digital Wallets.<br>Paypal: @cjdeluna1999 <br>Paymaya: @coveyjorjet13 <br>Gcash: 09630249483");
		sDialog.setConfirmText("Watch Ads");
		sDialog.setCancelText("Donate");
		sDialog.setCancelable(true);
		sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener(){
			@Override
			public void onClick(SweetAlertDialog sDialog) {
				sDialog.dismiss();
				loadingAds();
				//loadRewarded();

			}
		});
		sDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				String url4 = "https://www.facebook.com/cjdeluna.1999";
				Intent intent4 = new Intent(Intent.ACTION_VIEW, Uri.parse(url4));
				intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent4);
			}
		});
		sDialog.show();
	}

    public void openDrawer(View v) {
        drawerLayout.openDrawer(GravityCompat.START);
	}
	public void showExitDialog() {
		sDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);

		sDialog.setTitleText(getString(R.string.attention));
		sDialog.setContentText(getString(R.string.alert_exit));
		sDialog.setConfirmText("EXIT");
		sDialog.setCancelText("MINIMIZE");
		sDialog.setCanceledOnTouchOutside(true);
		sDialog.setCancelable(true);
		sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener(){
			@Override
			public void onClick(SweetAlertDialog sDialog) {
				Utils.exitAll(AmbaboVpnProActivity.this);
			}
		});
		sDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog sDialog) {
				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME);
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(startMain);
			}
		});
		sDialog.show();

	}
	protected String getIpPublic() {

		ConnectivityManager connMgr;
		connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo network = connMgr
				.getActiveNetworkInfo();

		if (network != null && network.isConnectedOrConnecting()) {
			return TunnelUtils.getLocalIpAddress();
		}
		else {
			return "127.0.0.1";
		}
	}
	public void settings(View v) {
		Intent intentSettings = new Intent(AmbaboVpnProActivity.this, ConfigGeneralActivity.class);
		//intentSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intentSettings);
	}

	public void setStarterButton(Button starterButton, Activity activity) {
		String state = SkStatus.getLastState();
		boolean isRunning = SkStatus.isTunnelActive();

		if (starterButton != null) {
			int resId;

			SharedPreferences prefsPrivate = new Setting(activity).getPrefsPrivate();

			if (ConfigParser.isValidadeExpirou(prefsPrivate
					.getLong(Setting.CONFIG_VALIDADE_KEY, 0))) {
				resId = R.string.expired;
				starterButton.setEnabled(false);

				if (isRunning) {
					startOrStopTunnel(activity);
				}
			}
			else if (prefsPrivate.getBoolean(Setting.BLOQUEAR_ROOT_KEY, false) &&
					ConfigParser.isDeviceRooted(activity)) {
				resId = R.string.blocked;
				starterButton.setEnabled(false);
				toastutil.showErrorToast(getString(R.string.error_root_detected));
				if (isRunning) {
					startOrStopTunnel(activity);
				}
			}
			else if (SkStatus.SSH_INICIANDO.equals(state)) {
				resId = R.string.stop;
				starterButton.setEnabled(false);
			}
			else if (SkStatus.SSH_PARANDO.equals(state)) {
				resId = R.string.state_stopping;
				starterButton.setEnabled(false);
				StatisticGraphData.getStatisticData().getDataTransferStats().stop();
			}
			else {
				resId = isRunning ? R.string.stop : R.string.start;
				starterButton.setEnabled(true);

			}

			starterButton.setText(resId);
		}
	}
	void showMenu() {
		PopupMenu popup = new PopupMenu(this, btnMenu);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.main_menu, popup.getMenu());
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem p1)
			{
				switch (p1.getItemId()) {
					case R.id.configUpdate:
						guides();
						break;
					case R.id.miExit:
						showExitDialog();
						break;
					case R.id.tips:
						noBrowse();
						showInterstitial();
						break;
					case R.id.tips1:
						clearData();
						break;
				}
				return true;
			}
		});
		popup.show();
	}
	private void restart_app() {
		Context context = getApplicationContext();
		PackageManager packageManager = context.getPackageManager();
		Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
		ComponentName componentName = intent.getComponent();
		Intent mainIntent = Intent.makeRestartActivityTask(componentName);
		context.startActivity(mainIntent);
		Runtime.getRuntime().exit(0);
	}

	//GuideView
	public void guides() {
		new GuideView.Builder(AmbaboVpnProActivity.this)
				.setTitle("ServerSpinner")
				.setContentText("Select server you wish to connect ")
				.setTargetView(view1)
				.setGravity(Gravity.center)
				.setDismissType(DismissType.outside) //optional - default dismissible by TargetView
				.setGuideListener(new GuideListener() {
					@Override
					public void onDismiss(View view) {
						//TODO ...
						new GuideView.Builder(AmbaboVpnProActivity.this)
								.setTitle("Connection Status")
								.setContentText("It shows the status of your Connection")
								.setTargetView(view2)
								.setGravity(Gravity.center)
								.setDismissType(DismissType.outside) //optional - default dismissible by TargetView
								.setGuideListener(new GuideListener() {
									@Override
									public void onDismiss(View view) {
										//TODO ...
										new GuideView.Builder(AmbaboVpnProActivity.this)
												.setTitle("Logs")
												.setContentText("It shows the logs of your connection")
												.setTargetView(view3)
												.setGravity(Gravity.center)
												.setDismissType(DismissType.outside) //optional - default dismissible by TargetView
												.setGuideListener(new GuideListener() {
													@Override
													public void onDismiss(View view) {
														//TODO ...
														new GuideView.Builder(AmbaboVpnProActivity.this)
																.setTitle("Downloaded Data")
																.setContentText("Recived bytes using pro connection")
																.setTargetView(view5)
																.setGravity(Gravity.center)
																.setDismissType(DismissType.outside) //optional - default dismissible by TargetView
																.setGuideListener(new GuideListener() {

																	@Override
																	public void onDismiss(View view) {
																		//TODO ...
																		new GuideView.Builder(AmbaboVpnProActivity.this)
																				.setTitle("Uploaded Data")
																				.setContentText("Sent bytes using pro connection")
																				.setTargetView(view6)
																				.setGravity(Gravity.center)
																				.setDismissType(DismissType.outside) //optional - default dismissible by TargetView
																				.setGuideListener(new GuideListener() {

																					@Override
																					public void onDismiss(View view) {
																						//TODO ...
																						new GuideView.Builder(AmbaboVpnProActivity.this)
																								.setTitle("Connect Button")
																								.setContentText("Tap here to Connect")
																								.setTargetView(view4)
																								.setGravity(Gravity.center)
																								.setDismissType(DismissType.outside) //optional - default dismissible by TargetView
																								.setGuideListener(new GuideListener() {

//ari nimu e solod ang bag o


																									@Override
																									public void onDismiss(View view) {
																										//TODO ...

																									}
																								})
																								.build()
																								.show();

																					}
																				})
																				.build()
																				.show();

																	}

																})
																.build()
																.show();

													}
												})
												.build()
												.show();
									}
								})
								.build()
								.show();
					}
				})
				.build()
				.show();
		updatingForDynamicLocationViews();
	}
	private void updatingForDynamicLocationViews() {
		view4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				mGuideView.updateGuideViewLocation();
			}
		});}
	private void noBrowse() {
		sDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);

		sDialog.setTitle("Connection Problem?");
		sDialog.setContentText("Reset DNS randomly from <br>DNS List: "+dnsList[0]+"<br>"+dnsList[1]+"<br>"+dnsList[2]+"<br>"+dnsList[3]+"<br>"+dnsList[4]+"<br>"+dnsList[5]+"<br>"+dnsList[6]+"<br>"+dnsList[7]+"<br>"+dnsList[8]+"<br>"+dnsList[9]+"<br>"+dnsList[10]+"<br>"+dnsList[11]+"<br>"+dnsList[12]+"<br>"+dnsList[13]+"<br>"+dnsList[14]+"<br>"+dnsList[15]+"<br>"+dnsList[16]);
		sDialog.setConfirmText("Reset DNS");
		sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				sDialog.dismiss();
				if(!mConfig.getVpnDnsForward()) {
					mConfig.setVpnDnsForward(true);
				}
				mConfig.setVpnDnsResolver(dnsList[r.nextInt(dnsList.length)]);
				SweetAlertDialog pdialog = new SweetAlertDialog(AmbaboVpnProActivity.this, SweetAlertDialog.SUCCESS_TYPE);

				pdialog.setTitleText("Success!");
				pdialog.setContentText("DNS has been change to <br>" + mConfig.getVpnDnsResolver());
				pdialog.setConfirmText("OK");
				pdialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						sweetAlertDialog.dismiss();
					}
				});
				pdialog.show();
			}
		});
		sDialog.show();

	}

	//Google Ads
	private void loadInterstitial(){
		AdRequest adRequest = new AdRequest.Builder().build();
		InterstitialAd.load(this,"ca-app-pub-2905188701168289/1092310578", adRequest,
				new InterstitialAdLoadCallback() {
					@Override
					public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
						// The mInterstitialAd reference will be null until
						// an ad is loaded.
						mInterstitialAd = interstitialAd;
						//Log.i(TAG, "onAdLoaded");
						mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
							@Override
							public void onAdClicked() {
								// Called when a click is recorded for an ad.
								//Log.d(TAG, "Ad was clicked.");
							}

							@Override
							public void onAdFailedToShowFullScreenContent(AdError adError) {
								// Called when ad fails to show.
								//Log.e(TAG, "Ad failed to show fullscreen content.");
								mInterstitialAd = null;
							}

							@Override
							public void onAdShowedFullScreenContent() {
								// Called when ad is shown.
								//Log.d(TAG, "Ad showed fullscreen content.");
							}
						});
					}

					@Override
					public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
						// Handle the error
						//Log.d(TAG, loadAdError.toString());
						//toastutil.showErrorToast(loadAdError.toString());
						mInterstitialAd = null;
					}
				});

	}
	private void showInterstitial(){
		if (mInterstitialAd != null) {
			mInterstitialAd.show(this);
		} else {
			//toastutil.showErrorToast("The interstitial ad is Loading.");
			loadInterstitial();
			//Log.d("TAG", "The interstitial ad wasn't ready yet.");
		}
	}
	private void loadRewarded(){
		AdRequest adRequest = new AdRequest.Builder().build();
		RewardedAd.load(this, "ca-app-pub-2905188701168289/4432690752",
				adRequest, new RewardedAdLoadCallback() {
					@Override
					public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
						// Handle the error.
						//Log.d(TAG, loadAdError.toString());
						adError = loadAdError.toString();
						mRewardedAd = null;
					}

					@Override
					public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
						mRewardedAd = rewardedAd;
						mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
							@Override
							public void onAdClicked() {
								// Called when a click is recorded for an ad.
								//Log.d(TAG, "Ad was clicked.");
							}

							@Override
							public void onAdDismissedFullScreenContent() {
								// Called when ad is dismissed.
								// Set the ad reference to null so you don't show the ad a second time.
								//Log.d(TAG, "Ad dismissed fullscreen content.");
								mRewardedAd = null;
							}

							@Override
							public void onAdFailedToShowFullScreenContent(AdError adError) {
								// Called when ad fails to show.
								//Log.e(TAG, "Ad failed to show fullscreen content.");
								mRewardedAd = null;
							}

							@Override
							public void onAdImpression() {
								// Called when an impression is recorded for an ad.
								//Log.d(TAG, "Ad recorded an impression.");
							}

							@Override
							public void onAdShowedFullScreenContent() {
								// Called when ad is shown.
								//Log.d(TAG, "Ad showed fullscreen content.");
							}
						});
						//Log.d(TAG, "Ad was loaded.");
					}
				});
	}
	private void showRewarded(){
		if (mRewardedAd != null) {
			sDialogAd.dismiss();
			Activity activityContext = AmbaboVpnProActivity.this;
			mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
				@Override
				public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
					// Handle the reward.
					//Log.d(TAG, "The user earned the reward.");
					int rewardAmount = rewardItem.getAmount();
					String rewardType = rewardItem.getType();
				}
			});
		} else {
			sDialogAd.dismiss();
			showError(adError);
			loadRewarded();
			//Log.d(TAG, "The rewarded ad wasn't ready yet.");
		}
	}

	private void developerYarn(){

		if (!(((String) getPackageManager().getApplicationLabel(getApplicationInfo())).equals(AmbaboVpnProActivity.app_name) && getPackageName().equals(AmbaboVpnProActivity.app_base))) {
			sDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);

			sDialog.setTitleText("Warning!");
			sDialog.setContentText("It looks like you've tried to modify this app.\n Plese Reinstall the original Version of this app.");
			sDialog.setConfirmText("Exit");
			sDialog.setCancelable(false);
			sDialog.setCanceledOnTouchOutside(false);
			sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
				@Override
				public void onClick(SweetAlertDialog sweetAlertDialog) {
					sDialog.dismiss();
					if (Build.VERSION.SDK_INT >= 21) {
						finishAndRemoveTask();
					} else {
						android.os.Process.killProcess(android.os.Process.myPid());
					}
					System.exit(0);
				}
			});
			sDialog.show();
		}
	}


	private void start() {

		if (saved_ads_time == 0) {

			//Toast.makeText(AmbaboVpnActivity.this, "Your time is expiring soon, please click ADD TIME to renew access!", Toast.LENGTH_LONG).show();

			long millisInput = 1000 * 500;

			setTime(millisInput);
		}

		if (!mTimerRunning) {
			startTimer();
		}

	}
	private void stop() {
		if (mTimerRunning) {
			pauseTimer();
		}

	}
	private void pauseTimer() {
		mCountDownTimer.cancel();
		mTimerRunning = false;

	}
	private void updateCountDownText() {

		long days = TimeUnit.MILLISECONDS.toDays(mTimeLeftInMillis);
		long daysMillis = TimeUnit.DAYS.toMillis(days);

		long hours = TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis - daysMillis);
		long hoursMillis = TimeUnit.HOURS.toMillis(hours);

		long minutes = TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis - daysMillis - hoursMillis);
		long minutesMillis = TimeUnit.MINUTES.toMillis(minutes);

		long seconds = TimeUnit.MILLISECONDS.toSeconds(mTimeLeftInMillis - daysMillis - hoursMillis - minutesMillis);

		String resultString = (days < 10 ? "0"+days: days) + ":" + (hours < 10 ? "0"+hours: hours)+":"+(minutes < 10 ? "0"+minutes: minutes)+":"+ (seconds < 10 ? "0"+seconds: seconds);

		timeCountdown.setText(resultString);
	}
	private void setTime(long milliseconds) {

		saved_ads_time = mTimeLeftInMillis + milliseconds;

		mTimeLeftInMillis = saved_ads_time;
		updateCountDownText();

		SharedPreferences sharedPreferences = getSharedPreferences("Saved_ime", Context.MODE_PRIVATE);
		sharedPreferences.getLong("remainig_Time", 2*3600*1000);
		SharedPreferences.Editor sedit = sharedPreferences.edit();
		sedit.putLong("remaining_Time", mTimeLeftInMillis);
		sedit.apply();


	}
	private void addTime(long time){

		setTime(time);

		if (mTimerRunning){
			pauseTimer();
		}

		//startTimer();
	}
	private void saveTime(long time) {
		SharedPreferences mTime = getSharedPreferences("Saved_Time", Context.MODE_PRIVATE);

		SharedPreferences.Editor time_edit = mTime.edit();
		time_edit.putLong("remaining_Time", time).apply();
	}
	private void addTime(){
		long added_time = 2 * 3600 * 1000;
		if (mTimerRunning){
			addTime(added_time);
		}else{
			setTime(added_time);
		}
		saveTime(mTimeLeftInMillis);
	}
	private void resumeTime() {

		SharedPreferences mTime = getSharedPreferences("Saved_Time", Context.MODE_PRIVATE);

		long saved_time = mTime.getLong("remaining_Time", 2*3600*1000);
		setTime(saved_time);

		// Use this code to continue time if app close accidentally while connected

		 /*String state = SkStatus.getLastState();

		 if (SkStatus.SSH_CONECTADO.equals(state)) {
			 if (!mTimerRunning){
			 	startTimer();
			 }
		 }*/

		mTimerEnabled = true;
	}
	private void startTimer() {
		mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
		mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {


			@Override
			public void onTick(long millisUntilFinished) {
				mTimeLeftInMillis = millisUntilFinished;
				saveTime(mTimeLeftInMillis);
				updateCountDownText();
			}
			@Override
			public void onFinish() {
				mTimerRunning = false;
				pauseTimer();
				saved_ads_time = 0;

				// Code for auto stop vpn (sockshtttp)

				Intent stopVPN = new Intent(SocksHttpService.TUNNEL_SSH_STOP_SERVICE);
				LocalBroadcastManager.getInstance(AmbaboVpnProActivity.this)
						.sendBroadcast(stopVPN);
				toastutil.showErrorToast("Time expired! Click Add + Time to renew access!");
			}
		}.start();
		mTimerRunning = true;
	}

	private void showError(String error){
		sDialog = new SweetAlertDialog(AmbaboVpnProActivity.this, SweetAlertDialog.ERROR_TYPE);
		sDialog.setTitleText("Error");
		sDialog.setContentText(error);
		sDialog.setConfirmText("OK");
		sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				sDialog.dismiss();
			}
		});
		sDialog.show();

	}

	private void loadingAds(){
		sDialogAd = new SweetAlertDialog(AmbaboVpnProActivity.this, SweetAlertDialog.PROGRESS_TYPE);
		sDialogAd.setTitleText("Requesting Rewarded AD");
		sDialogAd.setContentText("Please Wait While Requesting Rewarded AD...");
		sDialogAd.show();
		sDialogAd.setCancelable(true);
		sDialogAd.setCanceledOnTouchOutside(false);
	}

	final class MyThreadClass implements Runnable{
		//   int service_id;

		//  MyThreadClass(/*int service_id*/)
		//{
		//  this.service_id = service_id;

		// }
		@Override
		public void run() {
			int i = 0;
			synchronized (this) {

				while (dataThread.getName() == "showDataGraph") {
					//  Log.e("insidebroadcast", Integer.toString(service_id) + " " + Integer.toString(i));
					getData();
					try {
						wait(1000);
						i++;
					}
					catch (InterruptedException e) {
						//  sshMsg(e.getMessage());
					}

				}
				//stopSelf(service_id);
			}

		}
	}
	public void getData() {
		List<Long> allData;

		//if (!network_status.equals("no_connection")) {
		//receiveData = RetrieveData.findData();
		allData = RetrieveData.findData();

		Long mDownload, mUpload;
		long upload = DataTransferGraph.upload;
		long download = DataTransferGraph.download;


		download = allData.get(0);
		upload = allData.get(1);

		//receiveData = mDownload + mUpload;
		//storedData(mDownload, mUpload);
		storedData(download, upload);
	}
	public void storedData(Long mDownload, Long mUpload) {
		StoredData.downloadSpeed = mDownload;
		StoredData.uploadSpeed = mUpload;

		if (StoredData.isSetData)
		{
			StoredData.downloadList.remove(0);
			StoredData.uploadList.remove(0);

			StoredData.downloadList.add(mDownload);
			StoredData.uploadList.add(mUpload);
		}

		// Log.e("storeddata","test "+toString().valueOf(StoredData.downloadList.size()));

	}
	public void startStop(boolean z) {
		Intent intent = new Intent(AmbaboVpnProActivity.this, SocksHttpService.class);
		if (z)
		{
			dataThread = new Thread(new MyThreadClass());
			dataThread.setName("showDataGraph");
			dataThread.start();
		}
		else
		{
			dataThread = new Thread(new MyThreadClass());
			dataThread.setName("stopDataGraph");
			dataThread.start();
		}
	}

	private void checkSniffingtool(){
		for (String list: sniffingList) {
			if(checkIn(list)){
				alertApp("Sniffing Tool Found!", getString(R.string.sniff_detected), list);
				break;
			}
		}
	}
	private void checkTorrentApps(){
		for (String list: torrentList) {
			if(checkIn(list)){
				alertApp("Torrent Application Found", getString(R.string.torrent_detected), list);
				break;
			}
		}
	}
	private boolean checkIn(String item){
		List<ApplicationInfo> packages;
		PackageManager pm;

		pm = getPackageManager();
		packages = pm.getInstalledApplications(0);
		for (ApplicationInfo packageInfo : packages) {
			if(packageInfo.packageName.equals(item))
				return true;
		}
		return false;
	}
	private void alertApp(String title, String content, String item){
		sDialog = new SweetAlertDialog(AmbaboVpnProActivity.this, SweetAlertDialog.ERROR_TYPE);
		sDialog.setTitle(title);
		sDialog.setContentText(content+"<br> This Error is Caused by : "+item);
		sDialog.setConfirmText("OK");
		sDialog.setCanceledOnTouchOutside(false);
		sDialog.setCancelable(false);
		sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				Utils.exitAll(AmbaboVpnProActivity.this);
			}
		});
		sDialog.show();
	}
	private void navListener(){
		NavigationView navigationView = (NavigationView) findViewById(R.id.shitstuff);
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			// This method will trigger on item Click of navigation menu
			@Override
			public boolean onNavigationItemSelected(MenuItem menuItem)
			{
				switch (menuItem.getItemId())
				{
					case R.id.notif:
						showInterstitial();
						drawerLayout.closeDrawers();
						Changelogs();
						return true;
					case R.id.checkupdate:
						showInterstitial();
						drawerLayout.closeDrawers();
						updateConfig(false);
						return true;
					case R.id.geolocation:
						showInterstitial();
						drawerLayout.closeDrawers();
						geoLocation();
						return true;
					case R.id.appsettings:
						Intent intentSettings = new Intent(AmbaboVpnProActivity.this, ConfigGeneralActivity.class);
						intentSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intentSettings);
						showInterstitial();
						return true;
					case R.id.developer:
						String url1 = "https://t.me/Ambabo";
						Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url1));
						intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(Intent.createChooser(intent1, getText(R.string.open_with)));
						showInterstitial();
						break;
					case R.id.telegramchannel:
						String url2 = "https://t.me/AmbaboVpnChannel";
						Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(url2));
						intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(Intent.createChooser(intent2, getText(R.string.open_with)));
						showInterstitial();
						break;
					case R.id.telegramgroup:
						String url3 = "https://t.me/+9K2xRT6hdwRmZjRl";
						Intent intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(url3));
						intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(Intent.createChooser(intent3, getText(R.string.open_with)));
						showInterstitial();
						break;
					case R.id.donate:
						drawerLayout.closeDrawers();
						showInterstitial();
						supportDeveloper();
						break;
					case R.id.speedtest:
						Intent speedtestintent = new Intent(AmbaboVpnProActivity.this, SpeedTest.class);
						startActivity(speedtestintent);
						break;
					case R.id.about:
						Intent miAboutintent = new Intent(AmbaboVpnProActivity.this, AboutActivity.class);
						startActivity(miAboutintent);
						break;
					case R.id.hostChecker:
						Intent hostCheckerIntent = new Intent(AmbaboVpnProActivity.this, HostChecker.class);
						startActivity(hostCheckerIntent);
						break;
					default:
						//    snack("Coming Soon!");
						return true;

				}
				return false;
			}
		});
	}
}

