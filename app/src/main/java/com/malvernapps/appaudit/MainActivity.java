package com.malvernapps.appaudit;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements FragmentList.OnPackageItemSelected {

    private Menu main_menu;

    static ListView listView;
    static List<RowItem> rowItems;
    static PackageManager packageManager;

    static PackageInfo MainActivity_packageInfo;

    FragmentList startingFragment;

    static String results="No apk selected";

    /**
     * goes true when message shown
     */
    static boolean messageShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setLogo(R.drawable.title_icon);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if ( messageShown == false ) {
            Toast.makeText(this, "Numbers show the number of permissions used, RED = uses Internet",
                    Toast.LENGTH_LONG).show();
            messageShown = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Resources res = getResources();
        boolean dualPane = res.getBoolean(R.bool.dual_pane);

        if ( dualPane == true  )
        {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main_menu_dual, menu);
        }
        else {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }

        main_menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        MenuItem system_package = main_menu.findItem(R.id.action_is_system_package);
        MenuItem installed_package = main_menu.findItem(R.id.action_is_installed_package);

        startingFragment = (FragmentList) getSupportFragmentManager().findFragmentById(R.id.container);


        int id = item.getItemId();

        switch( id )
        {
            case R.id.action_is_system_package:
                system_package.setChecked(true);
                installed_package.setChecked(false);
                startingFragment.ReDrawDisplay( false );
                return true;

            case R.id.action_is_installed_package:
                system_package.setChecked(false);
                installed_package.setChecked(true);
                startingFragment.ReDrawDisplay( true );
                return true;

            case R.id.action_add_to_clipboard:
                AddToClipboard( FragmentDetail.results );
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Add to clipboard
     */
    private void AddToClipboard( String results)
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("APK Details", results);
        clipboard.setPrimaryClip(clip);
    }

    public void onPackageItemSelected(PackageInfo packageInfo) {
        // need to set the fragment of activity
        //Log.d("Mainact", "Set packet");

        Resources res = getResources();
        boolean dualPane = res.getBoolean(R.bool.dual_pane);

        if ( dualPane == true  )
        {
            FragmentDetail detail = (FragmentDetail) getSupportFragmentManager().findFragmentById(R.id.container2);
            detail.setPackageInfo(packageInfo);
        }
        else
        {
            Intent appInfo = new Intent(this, ApkInfo.class);
            MainActivity.MainActivity_packageInfo = packageInfo;
            startActivity(appInfo);
        }
    }

    @Override
    public void SetItemsInList(int numItems) {

        setTitle("Device Audit, " + numItems + " Apps" );

    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

}
