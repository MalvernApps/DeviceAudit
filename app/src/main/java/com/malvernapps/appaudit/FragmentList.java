package com.malvernapps.appaudit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentList extends Fragment {

	Context myContext;
	View rootView;
	static ListView listView;
	static List<RowItem> rowItems;
	static PackageManager packageManager;
	
	public interface OnPackageItemSelected 
	{
	    public void onPackageItemSelected(PackageInfo packageInfo);
	    
	    public void SetItemsInList(int numItems);
	  }
	
	OnPackageItemSelected listener;

	public FragmentList() {
	}

	public void ReDrawDisplay(boolean drawInstalledPackages) {
		UpdateScreen(rootView, drawInstalledPackages);
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_list, container, false);

		myContext = this.getActivity().getApplicationContext();

		UpdateScreen(rootView, true);

		return rootView;
	}

	private void UpdateScreen(View rootView, boolean drawInstalledPackages) {
		listView = (ListView) rootView.findViewById(R.id.list);

		packageManager = this.getActivity().getPackageManager();
		List<PackageInfo> packageList = packageManager
				.getInstalledPackages(PackageManager.GET_PERMISSIONS | PackageManager.GET_SERVICES );
				//.getInstalledPackages(PackageManager.GET_ACTIVITIES | PackageManager.GET_PERMISSIONS | PackageManager.GET_SERVICES );

        String [] libNames = packageManager.getSystemSharedLibraryNames();

        for( String str: libNames )
        {
           // Log.d("LIB: ", str );
        }

		List<PackageInfo> packageList1 = new ArrayList<PackageInfo>();

		for (PackageInfo pi : packageList) {
			boolean b = isSystemPackage(pi);
			if (b == !drawInstalledPackages) {
				packageList1.add(pi);
			}
		}

		Collections.sort(packageList1, new Comparator() {
			@Override
			public int compare(Object softDrinkOne, Object softDrinkTwo) {
				
				// use instanceof to verify the references are indeed of the
				// type in question
				return ((packageManager
						.getApplicationLabel(((PackageInfo) softDrinkOne).applicationInfo)
						.toString()).compareTo((packageManager
						.getApplicationLabel(((PackageInfo) softDrinkTwo).applicationInfo)
						.toString())));

			}
		});
		
		listener.SetItemsInList(packageList1.size());

		ApkAdapter adapter = new ApkAdapter(this.getActivity(), packageList1,
				packageManager);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				//Log.d("HERE", "clicked: " + position);
				

				PackageInfo packageInfo = (PackageInfo) parent
						.getItemAtPosition(position);
				Intent appInfo = new Intent(myContext, ApkInfo.class);
				
				listener.onPackageItemSelected(packageInfo);

				//MainActivity.MainActivity_packageInfo = packageInfo;
				//startActivity(appInfo);

				// getActivity().overridePendingTransition(R.anim.push_up_in,
				// R.anim.push_up_out);
			}
		});
	}

	private boolean isSystemPackage(PackageInfo pkgInfo) {
		return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
				: false;
	}
	
	@Override
	public void onAttach(Activity activity) 
	{
		super.onAttach(activity);
		
		if (activity instanceof OnPackageItemSelected) {
	        listener = (OnPackageItemSelected) activity;
	      } else {
	        throw new ClassCastException(activity.toString()
	            + " must implemenet MyListFragment.OnItemSelectedListener");
	      }		
	}

}
