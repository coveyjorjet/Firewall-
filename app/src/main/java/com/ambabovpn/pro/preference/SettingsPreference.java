package com.ambabovpn.pro.preference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.ambabovpn.tunnel.config.Setting;
import com.ambabovpn.tunnel.config.SettingsConstants;
import com.ambabovpn.tunnel.logger.ConnectionStatus;
import com.ambabovpn.tunnel.logger.SkStatus;
import com.ambabovpn.pro.R;

public class SettingsPreference extends PreferenceFragmentCompat
	implements Preference.OnPreferenceChangeListener, SettingsConstants,
        SkStatus.StateListener
{
	private Handler mHandler;
	private SharedPreferences mPref;
	
	public static final String
		SSHSERVER_PREFERENCE_KEY = "screenSSHSettings",
		ADVANCED_SCREEN_PREFERENCE_KEY = "screenAdvancedSettings";
		
	private String[] settings_disabled_keys = {
		DNSFORWARD_KEY,
		DNSRESOLVER_KEY,
		UDPFORWARD_KEY,
		UDPRESOLVER_KEY,
		PINGER_KEY,
		AUTO_CLEAR_LOGS_KEY,
		HIDE_LOG_KEY,
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		mHandler = new Handler();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		
		SkStatus.addStateListener(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		
		SkStatus.removeStateListener(this);
	}
	
	
	@Override
    public void onCreatePreferences(Bundle bundle, String root_key) {
        // Load the Preferences from the XML file
        setPreferencesFromResource(R.xml.app_preferences, root_key);

		mPref = getPreferenceManager().getDefaultSharedPreferences(getContext());
		Preference udpForwardPreference = findPreference(UDPFORWARD_KEY);
		udpForwardPreference.setOnPreferenceChangeListener(this);
		Preference dnsForwardPreference = findPreference(DNSFORWARD_KEY);
		dnsForwardPreference.setOnPreferenceChangeListener(this);
		Preference pingForwardPreference = findPreference(PINGFORWARD_KEY);
		pingForwardPreference.setOnPreferenceChangeListener(this);
		Preference clearLogPreference = findPreference(CLEARLOG_KEY);
		clearLogPreference.setOnPreferenceChangeListener(this);
		Preference hideLogPreference = findPreference(HIDE_LOG_KEY);
		hideLogPreference.setOnPreferenceChangeListener(this);

		setRunningTunnel(SkStatus.isTunnelActive());
	}

	private void onChangeUseVpn(boolean use_vpn){
		Preference udpResolverPreference = findPreference(UDPRESOLVER_KEY);
		Preference dnsResolverPreference = findPreference(DNSRESOLVER_KEY);
		Preference pingUrlPreference = findPreference(PINGURL_KEY);
		boolean isUdpForward = mPref.getBoolean(UDPFORWARD_KEY, false);
		boolean isDnsForward = mPref.getBoolean(DNSFORWARD_KEY, false);
		boolean isPingForward = mPref.getBoolean(PINGFORWARD_KEY, false);
		udpResolverPreference.setEnabled(isUdpForward);
		dnsResolverPreference.setEnabled(isDnsForward);
		pingUrlPreference.setEnabled(isPingForward);

		for (String key : settings_disabled_keys){
			getPreferenceManager().findPreference(key)
				.setEnabled(use_vpn);
		}
	}
	
	private void setRunningTunnel(boolean isRunning) {
		if (isRunning) {
			for (String key : settings_disabled_keys){
				getPreferenceManager().findPreference(key)
					.setEnabled(false);
			}
		}
		else {
			onChangeUseVpn(true);
		}
	}
	
	/**
	* Preference.OnPreferenceChangeListener
	* Implementação
	*/
	
	@Override
	public boolean onPreferenceChange(Preference pref, Object newValue) {
		switch (pref.getKey()) {
			case UDPFORWARD_KEY:
				boolean isUdpForward = (boolean) newValue;

				Preference udpResolverPreference = findPreference(UDPRESOLVER_KEY);
				assert udpResolverPreference != null;
				udpResolverPreference.setEnabled(isUdpForward);
				break;

			case DNSFORWARD_KEY:
				boolean isDnsForward = (boolean) newValue;

				Preference dnsResolverPreference = findPreference(DNSRESOLVER_KEY);
				assert dnsResolverPreference != null;
				dnsResolverPreference.setEnabled(isDnsForward);
				break;

			case PINGFORWARD_KEY:
				boolean isPing = (boolean) newValue;

				Preference pingUrl = findPreference(PINGURL_KEY);
				assert pingUrl != null;
				pingUrl.setEnabled(isPing);

			case AUTO_CLEAR_LOGS_KEY:
				boolean isAutoClear = (boolean) newValue;

				new Setting(getContext()).setAutoClearLog(isAutoClear);
				break;

			case HIDE_LOG_KEY:
				boolean isHideLog = (boolean) newValue;

				new Setting(getContext()).setHideLog(isHideLog);
				break;
		}
		return true;
	}

	@Override
	public void updateState(String state, String logMessage, int localizedResId, ConnectionStatus level, Intent intent)
	{
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				setRunningTunnel(SkStatus.isTunnelActive());
			}
		});
	}
}
