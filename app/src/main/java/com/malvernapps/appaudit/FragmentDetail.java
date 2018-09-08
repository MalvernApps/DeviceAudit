package com.malvernapps.appaudit;



import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FragmentDetail extends Fragment {
	
	TextView appLabel, packageName, version, features, activity_list;
    TextView permissions, andVersion, installed, lastModify, path, services, uidlabel;
    PackageInfo packageInfo;
    PackageManager pm;
    static String results="No apk selected";


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_detail, container, false);
    
    appLabel = (TextView) view.findViewById(R.id.applabel);
    packageName = (TextView) view.findViewById(R.id.package_name);
      uidlabel = (TextView) view.findViewById(R.id.uidlabel);
    version = (TextView) view.findViewById(R.id.version_name);
    features = (TextView) view.findViewById(R.id.req_feature);
    permissions = (TextView) view.findViewById(R.id.req_permission);
      activity_list = (TextView) view.findViewById(R.id.activities_list) ;
    andVersion = (TextView) view.findViewById(R.id.andversion);
    path = (TextView) view.findViewById(R.id.path);
    installed = (TextView) view.findViewById(R.id.insdate);
    lastModify = (TextView) view.findViewById(R.id.last_modify);
    services = (TextView) view.findViewById(R.id.req_services);
    
    return view;
  }
  
  public void setPackageInfo( PackageInfo newPackageInfo )
  {
      if ( getActivity() == null ) return;
      pm = getActivity().getPackageManager();
	  packageInfo = newPackageInfo;
	  setValues();  	  
  }

    private void setValues() {

        results = "Application Name:\n";
        // APP name
        appLabel.setText(pm.getApplicationLabel(
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

  private void setValuesOld() {
	  
	  if ( pm == null ) return;
      // APP name
      appLabel.setText(pm.getApplicationLabel(
              packageInfo.applicationInfo));

      // package name
      packageName.setText(packageInfo.packageName);

      // set uid
      uidlabel.setText( "" + packageInfo.applicationInfo.uid);

      // version name
      version.setText(packageInfo.versionName);

      // target version
      andVersion.setText(Integer
              .toString(packageInfo.applicationInfo.targetSdkVersion));

      // path
      path.setText(packageInfo.applicationInfo.sourceDir);

      // first installation
      installed.setText(setDateFormat(packageInfo.firstInstallTime));

      // last modified
      lastModify.setText(setDateFormat(packageInfo.lastUpdateTime));

      // features
      if (packageInfo.reqFeatures != null)
          features.setText(getFeatures(packageInfo.reqFeatures));
      else
          features.setText("-");

      // uses-permission
      if (packageInfo.requestedPermissions != null)
          permissions
                  .setText(getPermissions(packageInfo.requestedPermissions));
      else
          permissions.setText("-");

      activity_list.setText(getActivities());
      
      // uses-services
      if (packageInfo.services != null)
      	services
                  .setText(getServices(packageInfo.services));
      else
      	services.setText("-");
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
            actFeatures = pm.getPackageInfo(packageInfo.packageName, PackageManager.GET_ACTIVITIES).activities;
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



} 