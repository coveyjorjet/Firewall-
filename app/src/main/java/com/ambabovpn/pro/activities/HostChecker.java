package com.ambabovpn.pro.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.ambabovpn.pro.R;
import com.ambabovpn.pro.util.Fetch;
import com.ambabovpn.pro.util.ToastUtil;
import com.ambabovpn.pro.util.WorkerAction;
import com.ambabovpn.tunnel.tunnel.TunnelUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.net.SocketFactory;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


@SuppressLint("SetTextI18n")
public class HostChecker extends BaseActivity {
    private static final String TAG = "HostChecker";
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private Button btnSubmit;
    private EditText bugHost;
	private CheckBox useProxy;
	private String ipProxy;
	private String portProxy;
    private EditText proxy;
    private SharedPreferences sp;
    private Spinner spinner, methodSpinner;
	View proxyView;
	ToastUtil toastutil;
	String Exception;
	private Boolean urlException = false;


	@Override
	protected void onStart()
	{
		super.onStart();
//		checkInternetConn();
	}
	@Override
    public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.activity_hostchecker);
		toastutil = new ToastUtil(this);
		ImageButton btnMenu = findViewById(R.id.btnMenu);
		btnMenu.setOnClickListener(v -> finish());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		ListView list = findViewById(R.id.listLogs);
		arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.activity_hostchecker_logtext, arrayList);
        list.setAdapter(adapter);
        bugHost = findViewById(R.id.editTextUrl);
        proxy = findViewById(R.id.editTextProxy);
		proxyView = (findViewById(R.id.proxyLayout));
		proxyView.setVisibility(View.GONE);
        spinner = findViewById(R.id.spinnerRequestMethod);
		methodSpinner = findViewById(R.id.method);
		useProxy = findViewById(R.id.checkBoxDirectRequest);
        useProxy.setOnClickListener(view -> {
			SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			if (useProxy.isChecked()) {
				Editor	edit = sharedPreferences1.edit();
				edit.putBoolean("use_proxy", true);
				edit.apply();
				proxyView.setVisibility(View.VISIBLE);
				return;
			}
			Editor edit = sharedPreferences1.edit();
			edit.putBoolean("use_proxy", false);
			edit.apply();
			proxyView.setVisibility(View.GONE);
		});
		btnSubmit = findViewById(R.id.buttonSearch);
		btnSubmit.setOnClickListener(v -> {
			if (!TunnelUtils.isNetworkOnline(HostChecker.this)) {
				toastutil.showErrorToast("No Internet Connection");
			} else if (bugHost.getText().toString().equals("")) {
				toastutil.showErrorToast("Please Fill The URL");
			} else if (useProxy.isChecked()&&proxy.getText().toString().equals("")) {
				toastutil.showErrorToast("Fill-in the proxy ip:port to start");
			} else {
				urlException = false;
				adapter.clear();
				start();
			}
		});
        if (sharedPreferences.getBoolean("use_proxy", false)) {
            useProxy.setChecked(true);
            proxyView.setVisibility(View.VISIBLE);
            return;
        }
        useProxy.setChecked(false);
        proxyView.setVisibility(View.GONE);
    }
	@Override
	public void onPause() {
		super.onPause();
//		checkInternetConn();
		Log.v(TAG, "onPause()");
		savePreferences("hostChecker", bugHost.getText().toString().trim());
		savePreferences("proxyChecker", proxy.getText().toString().trim());
	}

	@Override
	public void onResume() {
		super.onResume();
		if (sp == null) {
			sp = PreferenceManager.getDefaultSharedPreferences(this);
		}
		bugHost.setText(sp.getString("hostChecker", ""));
		proxy.setText(sp.getString("proxyChecker", ""));
//		checkInternetConn();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed(){
		super.onBackPressed();
	}

    private void savePreferences(String str, String str2) {
        if (sp == null) {
            sp = PreferenceManager.getDefaultSharedPreferences(this);
        }
        Editor edit = sp.edit();
        edit.putString(str, str2);
        edit.apply();
    }

	public void start() {
		String editable = bugHost.getText().toString();
		String obj = spinner.getSelectedItem().toString();
		String mobj = methodSpinner.getSelectedItem().toString();
		btnSubmit.setEnabled(false);
		btnSubmit.setText("Stop");
		StringBuilder sb = new StringBuilder();
		adapter.notifyDataSetChanged();

		String trim = proxy.getText().toString().trim();
		if (trim.contains(":")) {
			String[] split = trim.split(":");
			ipProxy = split[0];
			portProxy = split[1];
		} else{
			ipProxy = trim;
			portProxy = "80";
		}
		if(mobj.contains("Head")){
			if (useProxy.isChecked()) {
				arrayList.add(obj + " - " + "URL: https://" + editable);
				arrayList.add(editable + " - " + ipProxy);
			} else {
				arrayList.add(obj + " - " + "URL: https://" + editable);
				arrayList.add(editable + " - " + "Direct");
			}
		}
		else if(mobj.contains("SNI")){
			arrayList.add("\bSetting SNI Hostname " + editable);
		}
		else if(mobj.contains("Ip")){
			arrayList.add("\bUrl: "+ editable);
		}
		else if(mobj.contains("CDN")){
			arrayList.add("\bWebsite CDN: " + editable);
		}
		else
		{
			toastutil.showErrorToast("Select Request Method");
		}

		//Extended Asynctask for permisson purposes
		new Fetch(new WorkerAction() {
			String response = "";
			final StringBuilder sb = new StringBuilder();
			@Override
			public void runFirst() {
				try {
					if(mobj.contains("Header")){
						Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(ipProxy, Integer.parseInt(portProxy)));
						HttpURLConnection conn;
						if (useProxy.isChecked()) {
							conn = (HttpURLConnection) new URL("https://" + editable).openConnection(proxy);
						} else {
							conn = (HttpURLConnection) new URL("https://" + editable).openConnection();
						}
						conn.setRequestMethod(obj);
						conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.130 Safari/537.36");
						conn.setReadTimeout(2000);
						conn.setConnectTimeout(3000);
						conn.setDoInput(true);
						conn.connect();
						for (Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
							if (entry.getKey() == null) {
								response = sb.append(entry.getValue().toString().replace("[", "").replace("]", "")).append("\n").toString();
							} else {
								response = sb.append(entry.getKey()).append(" : ").append(entry.getValue().toString().replace("[", "").replace("]", "")).append("\n").toString();
							}
						}
						if (response.contains("\n")) {
							String[] split = response.split("\n");
							arrayList.add("");
							arrayList.addAll(Arrays.asList(split));
							/*for (String add : split) {
								arrayList.add(add);
							}*/
						}
					}
					else if(mobj.contains("SNI")){
						//doSSLHandshake(host, editable, port);
						if(useProxy.isChecked()){
							adapter.notifyDataSetChanged();
							doSSLHandshakeProxy(editable, ipProxy, Integer.parseInt(portProxy));
							return;
						}
						doSSLHandshakeDirect(editable);
					}
					else if(mobj.contains("Ip")){
						adapter.notifyDataSetChanged();
						hostToIp(editable);
					}
					else if(mobj.contains("CDN")){
						//TODO Implement CDN FINDER
					}
					else {
						//toastutil.showErrorToast("May mali ka brader hahaha.");
					}
				} catch (Exception e) {
					adapter.notifyDataSetChanged();
					urlException = true;
					Exception = e.toString();
					e.printStackTrace();
				}
			}

			@Override
			public void runLast() {
				if (urlException) {
					adapter.notifyDataSetChanged();
					int pos;
					String exception;
					pos = Exception.indexOf("Exception:");
					exception = Exception.substring(pos);
					toastutil.showErrorToast(exception);
					arrayList.add("");
					arrayList.add(exception);
					//arrayList.add(sb.append("Exception: Unable to resolve host ").append(qoute).append(editable).append(qoute).append(". No address associated with the hostname").toString());
					arrayList.add("Stopped");
					btnSubmit.setEnabled(true);
					btnSubmit.setText("Check");
					urlException = false;
					//adapter.notifyDataSetChanged();
				} else {
					adapter.notifyDataSetChanged();
					arrayList.add("");
					arrayList.add("Stopped");
					toastutil.showSuccessToast("Success");
					btnSubmit.setEnabled(true);
					btnSubmit.setText("Check");
					//adapter.notifyDataSetChanged();
				}
			}
		}).execute();
		adapter.notifyDataSetChanged();
    }

	private void doSSLHandshakeDirect(String sni) throws IOException {

		SocketFactory factory = SSLSocketFactory.getDefault();
		try (Socket connection = factory.createSocket(sni, 443)) {
			((SSLSocket) connection).getEnabledCipherSuites();
			((SSLSocket) connection).getEnabledProtocols();

			SSLParameters sslParams = new SSLParameters();
			sslParams.setEndpointIdentificationAlgorithm("HTTPS");
			((SSLSocket) connection).setSSLParameters(sslParams);

			BufferedReader input = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

			((SSLSocket) connection).getSession().getCipherSuite();

			arrayList.add("");
			arrayList.add("<b>Established " + ((SSLSocket) connection).getSession().getProtocol() + " connection with " +sni+ ":443 using " + ((SSLSocket) connection).getSession().getCipherSuite() + "</b>");
			arrayList.add("");
			arrayList.add("Using protocol " + ((SSLSocket) connection).getSession().getProtocol());
			arrayList.add("Using cipher" + ((SSLSocket) connection).getSession().getCipherSuite());
			arrayList.add(Arrays.toString(((SSLSocket) connection).getSession().getPeerCertificateChain()));
			//((SSLSocket) connection).addHandshakeCompletedListener(new HostChecker.HandshakeTunnelCompletedListener(connection));
			//connection.addHandshakeCompletedListener(new HostChecker.HandshakeTunnelCompletedListener(socket));
			//return input.readLine();
		}

	}

	private void doSSLHandshakeProxy(String sni, String proxyHost, int proxyPort) throws IOException {

		final StringBuilder sb = new StringBuilder();

		SocketFactory factory = SSLSocketFactory.getDefault();
		try (Socket connection = factory.createSocket(proxyHost, proxyPort)) {
			connection.getClass().getMethod("setHostname", String.class).invoke(connection, sni);

			((SSLSocket) connection).getEnabledCipherSuites();
			((SSLSocket) connection).getEnabledProtocols();

			SSLParameters sslParams = new SSLParameters();
			sslParams.setEndpointIdentificationAlgorithm("HTTPS");
			((SSLSocket) connection).setSSLParameters(sslParams);

			BufferedReader input = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

			arrayList.add(input.toString());
			arrayList.add(Arrays.toString(((SSLSocket) connection).getSession().getPeerCertificates()));

		} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
			e.printStackTrace();
		}

	}

	private void hostToIp(String host){
		String ipAdress, ipAdresses;
		int pos,pos2;

		InetAddress inetAddress = null;
		InetAddress[] inetAddresses = null;

		try {
			inetAddress = InetAddress.getByName(host);
			inetAddresses = InetAddress.getAllByName(host);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ipAdress = String.valueOf(inetAddress);
		ipAdresses = Arrays.toString(inetAddresses);
		arrayList.add("\bIP:");
		if(inetAddresses!=null){

			for (InetAddress arrays : inetAddresses){
				ipAdresses = String.valueOf(arrays);
				pos = ipAdresses.indexOf("/");
//				pos2 = ipAdresses.indexOf(",");
				ipAdresses = ipAdresses.substring(pos+1);
				arrayList.add(ipAdresses);
			}
//				for(int a = 0; a<Objects.requireNonNull(inetAddresses).length; a++ ){
//				pos = ipAdresses.indexOf("/");
//				pos2 = ipAdresses.indexOf(",");
//				ipAdresses = ipAdresses.substring(pos+1, pos2);
//				arrayList.add(ipAdresses);
//			}
		}
		else {
			pos = ipAdress.indexOf("/");
			ipAdress = ipAdress.substring(pos+1);
			arrayList.add(ipAdress);
		}

	}
}
