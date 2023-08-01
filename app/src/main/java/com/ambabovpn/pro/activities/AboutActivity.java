package com.ambabovpn.pro.activities;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.ambabovpn.pro.R;
import com.ambabovpn.pro.util.Utils;
import com.ambabovpn.tunnel.tunnel.TunnelUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AboutActivity extends BaseActivity implements OnClickListener {

	private AdView adsBannerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		adsBannerView = (AdView) findViewById(R.id.adView2);
		View changelog = findViewById(R.id.help);
		changelog.setOnClickListener(this);
//		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		PackageInfo pinfo = Utils.getAppInfo(this);
		if (pinfo != null) {
			String version_nome = pinfo.versionName;
			int version_code = pinfo.versionCode;
			String header_text = String.format("%s (%d)", version_nome, version_code);
			TextView app_info_text = (TextView) findViewById(R.id.appVersion);
			app_info_text.setText(header_text);
		}


		if (TunnelUtils.isNetworkOnline(this)) {

			adsBannerView.setAdListener(new AdListener() {
				@Override
				public void onAdLoaded() {
					if (adsBannerView != null) {
						adsBannerView.setVisibility(View.VISIBLE);
					}
				}
			});

			adsBannerView.loadAd(new AdRequest.Builder()
					.build());
		}
	}

	@Override
	public void onClick(View view) {
		// TODO: Implement this method
		int id = view.getId();
		if (id == R.id.help) {
			help();
		} else if (id == R.id.developer) {
			startActivity(new Intent("android.intent.action.VIEW",
					Uri.parse("https://t.me/Ambabo/")));
		}
	}

	private void help() {

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View inflate = inflater.inflate(R.layout.activity_help, (ViewGroup) null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(inflate);
		ImageView iv = inflate.findViewById(R.id.about_image);
		TextView title = inflate.findViewById(R.id.about_text1);
		TextView ms = inflate.findViewById(R.id.about_text2);
		TextView ms2 = inflate.findViewById(R.id.about_text3);
		TextView ms3 = inflate.findViewById(R.id.about_text4);
		TextView ms4 = inflate.findViewById(R.id.about_text5);
		TextView ms5 = inflate.findViewById(R.id.about_text6);
		TextView ms6 = inflate.findViewById(R.id.about_text7);
		TextView ms7 = inflate.findViewById(R.id.about_text8);
		TextView ms8 = inflate.findViewById(R.id.about_text9);
		TextView ms9 = inflate.findViewById(R.id.about_text10);
		TextView ms10 = inflate.findViewById(R.id.about_text11);
		Button ok = inflate.findViewById(R.id.about_button);
		iv.setImageResource(R.drawable.main_icon);
		title.setText("Hello user!");
		ms.setText("Ambabo Vpn is a tool set custom HTTP Header with VPN and proxy support");
		ms2.setText("How to connect?");
		ms3.setText("1. Select your desired server");
		ms4.setText("2. Select your desired payload");
		ms5.setText("3. Tap CONNECT button");
		ms6.setText("How to claim Time?");
		ms7.setText("1. Make sure you have internet connection or Data connection that can browse");
		ms8.setText("2. Click Add + Time");
		ms9.setText("3. And Click Watch Video. Make sure you finish the rewarded video so that you get Two Hours Subscription.");
		ms10.setText("Enjoy Fast and secure VPN");
		ok.setText("Need Help? Contact us!");
		final AlertDialog time = builder.create();
		time.setCanceledOnTouchOutside(false);
		time.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		time.getWindow().setGravity(Gravity.CENTER);
		time.show();
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try
				{
					time.dismiss();
					startActivity(new Intent("android.intent.action.VIEW",
							Uri.parse("https://t.me/+9K2xRT6hdwRmZjRl")));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

			}});

		time.show();
	}
	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();

		if (adsBannerView != null) {
			adsBannerView.resume();
		}
	}

	@Override
	protected void onPause()
	{
		// TODO: Implement this method
		super.onPause();

		if (adsBannerView != null) {
			adsBannerView.pause();
		}
	}

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();

		if (adsBannerView != null) {
			adsBannerView.destroy();
		}
	}

}



