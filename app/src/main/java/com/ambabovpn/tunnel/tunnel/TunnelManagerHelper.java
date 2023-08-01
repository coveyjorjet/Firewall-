package com.ambabovpn.tunnel.tunnel;

import android.content.Intent;
import android.content.Context;
import com.ambabovpn.tunnel.SocksHttpService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class TunnelManagerHelper
{
	public static void startSocksHttp(Context context) {
        Intent startVPN = new Intent(context, SocksHttpService.class);

		TunnelUtils.restartRotateAndRandom();

		context.startForegroundService(startVPN);
	}
	
	public static void stopSocksHttp(Context context) {
		Intent stopTunnel = new Intent(SocksHttpService.TUNNEL_SSH_STOP_SERVICE);
		LocalBroadcastManager.getInstance(context)
			.sendBroadcast(stopTunnel);
	}
}
