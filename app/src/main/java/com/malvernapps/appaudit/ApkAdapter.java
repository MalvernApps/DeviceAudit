package com.malvernapps.appaudit;

import java.util.List;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ApkAdapter extends BaseAdapter {

	List<PackageInfo> packageList;
	Activity context;
	PackageManager packageManager;

	public ApkAdapter(Activity context, List<PackageInfo> packageList,
			PackageManager packageManager) {
		super();
		this.context = context;
		this.packageList = packageList;
		this.packageManager = packageManager;

		for(int x=0;x<packageList.size();x++)
		{
			Log.d("NAME:", packageList.get(x).packageName);
		}
	}

	/**
	 * view holder class
	 */
	private class ViewHolder {

		/**
		 * name of the apk
		 */
		TextView apkName;

		/**
		 * Level of risk, number of permissions
		 */
		TextView riskLevel;
	}

	public int getCount() {
		return packageList.size();
	}

	public Object getItem(int position) {
		return packageList.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		//return null;

		LayoutInflater inflater = context.getLayoutInflater();

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.apklist_item, null);
			holder = new ViewHolder();

			holder.apkName = (TextView) convertView.findViewById(R.id.appname);
			holder.riskLevel = (TextView) convertView.findViewById(R.id.risk);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		PackageInfo packageInfo = (PackageInfo) getItem(position);
		Drawable appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
		String appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
		
		appIcon.setBounds(0, 0, 40, 40);
		holder.apkName.setCompoundDrawables(appIcon, null, null, null);
		holder.apkName.setCompoundDrawablePadding(15);
		holder.apkName.setText(appName);

		holder.riskLevel.setBackgroundColor(0xFFFFFFFF);

		if ( packageInfo.requestedPermissions != null ) {
			holder.riskLevel.setText("" + packageInfo.requestedPermissions.length);

			for (int i = 0; i < packageInfo.requestedPermissions.length; i++) {

				if (packageInfo.requestedPermissions[i].contains("android.permission.INTERNET"))
				{
					holder.riskLevel.setBackgroundColor(0xFFFFC0CB);

					final int sdk = android.os.Build.VERSION.SDK_INT;
					if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
						holder.riskLevel.setBackgroundDrawable( context.getResources().getDrawable(R.drawable.rounded_corner_red) );
					} else {
						holder.riskLevel.setBackground( context.getResources().getDrawable(R.drawable.rounded_corner_red));
					}
					break;
				}
			}
		}
		else
		{
			holder.riskLevel.setText("");
		}
		//Log.d("NAME:", appName);

		return convertView;
	}
}