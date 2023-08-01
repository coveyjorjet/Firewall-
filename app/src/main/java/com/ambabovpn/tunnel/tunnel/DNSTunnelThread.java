package com.ambabovpn.tunnel.tunnel;

import android.content.*;

import com.ambabovpn.tunnel.config.Setting;
import com.ambabovpn.tunnel.logger.SkStatus;
import com.ambabovpn.tunnel.tunnel.vpn.VpnUtils;
import com.ambabovpn.tunnel.util.CustomNativeLoader;
import com.ambabovpn.tunnel.util.StreamGobbler;

import java.io.*;

public class DNSTunnelThread extends Thread {

	private Context mContext;
	private static final String DNS_BIN = "libdns";
	private Process dnsProcess;
	private File filedns;
	private Setting mConfig;

	public DNSTunnelThread(Context context) {
		mContext = context;
		mConfig = new Setting(context);

	}

	@Override
	public void run(){
		try {

			SharedPreferences slowprefs = mConfig.getPrefsPrivate();

			String mDns = mConfig.getPrivString(Setting.DNS_KEY);
			String mchave = mConfig.getPrivString(Setting.CHAVE_KEY);
			String mnameserver = mConfig.getPrivString(Setting.NAMESERVER_KEY);
			String chave = slowprefs.getString(Setting.CHAVE_KEY, mDns);
			String nameserver = slowprefs.getString(Setting.NAMESERVER_KEY, mchave);
			String dns = slowprefs.getString(Setting.DNS_KEY, mnameserver);
			StringBuilder cmd1 = new StringBuilder();
			filedns = CustomNativeLoader.loadNativeBinary(mContext, DNS_BIN, new File(mContext.getFilesDir(),DNS_BIN));

			if (filedns == null){
				throw new IOException("DNS bin not found");
			}

			cmd1.append(filedns.getCanonicalPath());
			cmd1.append(" -udp "+ dns + ":53   -pubkey "+ chave + " " + nameserver + " 127.0.0.1:2222");
			dnsProcess = Runtime.getRuntime().exec(cmd1.toString());

			StreamGobbler.OnLineListener onLineListener = new StreamGobbler.OnLineListener(){
				@Override
				public void onLine(String log){
					//SkStatus.logInfo("<b>DNS Client: </b>" + log);
				}
			};
			StreamGobbler stdoutGobbler = new StreamGobbler(dnsProcess.getInputStream(), onLineListener);
			StreamGobbler stderrGobbler = new StreamGobbler(dnsProcess.getErrorStream(), onLineListener);

			stdoutGobbler.start();
			stderrGobbler.start();

			dnsProcess.waitFor();		
		} catch (IOException | InterruptedException e) {
			SkStatus.logInfo("SlowDNS: " + e);
		}

	}

	@Override
	public void interrupt(){
		if (dnsProcess != null)
			dnsProcess.destroy();
		try {
			if (filedns != null)
				VpnUtils.killProcess(filedns);
		} catch (Exception ignored) {}

		dnsProcess = null;
		filedns = null;
		super.interrupt();
	}

}
