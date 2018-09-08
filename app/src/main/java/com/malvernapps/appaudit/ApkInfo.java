package com.malvernapps.appaudit;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent.OnFinished;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
 
public class ApkInfo extends ActionBarActivity {
 
    TextView appLabel, packageName, version, features, activity_list;
    TextView permissions, andVersion, installed, lastModify, path, services, uidlabel;
    PackageInfo packageInfo;

    String results="No apk selected";
 
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apkinfo);
        
		getSupportActionBar().setLogo(R.drawable.title_icon);		
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
 
        findViewsById();
       
        //AppData appData = (AppData) getApplicationContext();
        packageInfo = MainActivity.MainActivity_packageInfo;
 
       setValues();
 
    }

    /**
     * Create the menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_data_menu, menu);
        return true;
    }

    /**
     * Handle menu actions
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_to_clipboard:
                AddToClipboard();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void AddToClipboard()
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("APK Details", results);
        clipboard.setPrimaryClip(clip);
    }

    private void findViewsById() {
        appLabel = (TextView) findViewById(R.id.applabel);
        uidlabel = (TextView) findViewById(R.id.uidlabel);
        packageName = (TextView) findViewById(R.id.package_name);
        version = (TextView) findViewById(R.id.version_name);
        features = (TextView) findViewById(R.id.req_feature);
        permissions = (TextView) findViewById(R.id.req_permission);
        activity_list = (TextView) findViewById(R.id.activities_list) ;
        andVersion = (TextView) findViewById(R.id.andversion);
        path = (TextView) findViewById(R.id.path);
        installed = (TextView) findViewById(R.id.insdate);
        lastModify = (TextView) findViewById(R.id.last_modify);
        services = (TextView) findViewById(R.id.req_services);
    }
 
    private void setValues() {

        results = "Application Name:\n";
        // APP name
        appLabel.setText(getPackageManager().getApplicationLabel(
                packageInfo.applicationInfo));

        results += appLabel.getText();
        results += "\n\n";

        // set uid
        results += "UID:\n";
        uidlabel.setText( "" + packageInfo.applicationInfo.uid);
        results += uidlabel.getText();
        results += "\n\n";
 
        // package name
        results += "Package Name:\n";
        packageName.setText(packageInfo.packageName);
        results += packageName.getText();
        results += "\n\n";
 
        // version name
        results += "Version:\n";
        version.setText(packageInfo.versionName);
        results += version.getText();
        results += "\n\n";

        // features
        results += "Required Features:\n";
        if (packageInfo.reqFeatures != null)
            features.setText(getFeatures(packageInfo.reqFeatures));
        else
            features.setText("-");
        results += features.getText();
        results += "\n\n";

        // uses-permission
        results += "Permissions:\n";
        if (packageInfo.requestedPermissions != null)
            permissions
                    .setText(getPermissions(packageInfo.requestedPermissions));
        else
            permissions.setText("-");
        results += permissions.getText();
        results += "\n\n";

        results += "Activities:\n";
        activity_list.setText(getActivities());
        results += activity_list.getText();
        results += "\n\n";

        // path
        results += "Path Info:\n";
        path.setText(packageInfo.applicationInfo.sourceDir);
        results += path.getText();
        results += "\n\n";

        // target version
        results += "Target SDK Version:\n";
        andVersion.setText(Integer
                .toString(packageInfo.applicationInfo.targetSdkVersion));
        results += andVersion.getText();
        results += "\n\n";
 
        // first installation
        results += "First Installed:\n";
        installed.setText(setDateFormat(packageInfo.firstInstallTime));
        results += installed.getText();
        results += "\n\n";
 
        // last modified
        results += "Last Modified:\n";
        lastModify.setText(setDateFormat(packageInfo.lastUpdateTime));
        results += lastModify.getText();
        results += "\n\n";

        // services
        results += "Services:\n";
        if (packageInfo.services != null)
        	services
                    .setText(getServices(packageInfo.services));
        else
        	services.setText("-");
        results += services.getText();
        results += "\n\n";
    }
 
    @SuppressLint("SimpleDateFormat")
    private String setDateFormat(long time) {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String strDate = formatter.format(date);
        return strDate;
    }
 
    // Convert string array to comma separated string
    private String getPermissions(String[] requestedPermissions) {
        String permission = "";
        for (int i = 0; i < requestedPermissions.length; i++) {
            permission = permission + requestedPermissions[i] + ",\n";
        }
        return permission;
    }
    
    // Convert string array to comma separated string
    private String getServices(ServiceInfo[] requestedServices) {
        String permission = "";
        for (int i = 0; i < requestedServices.length; i++) {
            permission = permission + requestedServices[i].name + ",\n";
        }
        return permission;
    }
 
    // Convert string array to comma separated string
    private String getFeatures(FeatureInfo[] reqFeatures) {
        String features = "";
        for (int i = 0; i < reqFeatures.length; i++) {
            features = features + reqFeatures[i] + ",\n";
        }
        return features;
    }

    // Convert string array to comma separated string
    private String getActivities(){

        ActivityInfo [] actFeatures = null;
        try {
            actFeatures = getPackageManager().getPackageInfo(packageInfo.packageName, PackageManager.GET_ACTIVITIES).activities;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String activities = "";

        if ( actFeatures != null ) {
            for (int i = 0; i < actFeatures.length; i++) {
                activities = activities + actFeatures[i].name + ",\n";
            }
        }
        else
        {
           activities = "-";
        }

        return activities;
    }
    
   @Override
public void onBackPressed() {
	// TODO Auto-generated method stub
	super.onBackPressed();
	
	//overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
}
}
