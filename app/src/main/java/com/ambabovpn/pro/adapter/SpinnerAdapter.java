package com.ambabovpn.pro.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ambabovpn.pro.R;
import java.io.InputStream;
import java.util.ArrayList;
import org.json.JSONObject;

public class SpinnerAdapter extends ArrayAdapter<JSONObject> {

	private int spinner_id;

	public SpinnerAdapter(Context context, int spinner_id, ArrayList<JSONObject> list) {
		super(context, R.layout.spinner_item, list);
		this.spinner_id = spinner_id;
	}

	@Override
	public JSONObject getItem(int position) {
		return super.getItem(position);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return view(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return view(position, convertView, parent);
	}

	private View view(int position, View convertView, ViewGroup parent) {
		View v = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
		TextView tv = v.findViewById(R.id.itemName);
		ImageView im = v.findViewById(R.id.itemImage);
		TextView info = v.findViewById(R.id.tvInfo);
		TextView country = v.findViewById(R.id.tvCountry);
		try {
			tv.setText(getItem(position).getString("Name"));
			if (spinner_id == R.id.serverSpinner) {
				getServerIcon(position, im, info, country);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return v;
	}

	private void getServerIcon(int position, ImageView im, TextView info, TextView country) throws Exception {
		InputStream inputStream = getContext().getAssets().open("flags/" + getItem(position).getString("FLAG") +  ".png" );
		im.setImageDrawable(Drawable.createFromStream(inputStream, getItem(position).getString("FLAG") +  ".png" ));
		if (inputStream != null) {
			inputStream.close();
		}
		int tun = getItem(position).getInt(("TunnelType"));
		String sInfo = getItem(position).getString("Info");
		country.setText(sInfo);
		switch (tun) {
			case 0:
				if (sInfo.toLowerCase().contains("legit")) {
					info.setText("Direct");
				} else {
					info.setText("WS/HTTP");
				}
				break;
			case 1:
				info.setText("SSH/SSL");
				break;
			case 3:
				info.setText("WS/SSL+Payload");
				break;
				
			case 4:
				info.setText("WS/SSL+Payload");
				break;
			case 5:
				info.setText("WS/SSL+Payload");
				break;	
				
		}
	}

}
