package com.malvernapps.appaudit;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Glenn on 23/02/2015.
 */
public class ReadNetworkConnections
{

    String TAG= "ReadNetworkConnections";
    /**
     *
     */
    public void ReadData( Context myContext )
    {
        boolean first = true;

        // create lsit to store data
        List<ConnectionData> connectionData = new ArrayList<ConnectionData>();

        int uidOffset=0;
        int uid;

        int localAddrOffset=0;
        int remoteAddrOffset=0;

        // Get running processes
        ActivityManager manager = (ActivityManager) myContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo item : runningProcesses)
        {
            Log.d(TAG, "ID: " + item.uid + " " + item.processName);
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/net/tcp6"));
            String line;
            while ((line = br.readLine()) != null)
            {
                // process the line.
                String[] array = line.split(" ");
                Log.d( TAG, line + " length: " + array.length );

                if ( first == true )
                {
                    uidOffset = line.indexOf( "uid" );
                    localAddrOffset = line.indexOf("local_address");
                    remoteAddrOffset = line.indexOf("remote_address");
                }
                else
                {
                    String str = line.substring( uidOffset-2, uidOffset + 3 );
                    str = str.trim();
                    uid = Integer.parseInt(str);
                    //Log.d( TAG, "#"+str+"#" );



                    for (ActivityManager.RunningAppProcessInfo item : runningProcesses)
                    {
                        if ( uid == item.uid) {

                            ConnectionData tmp = new ConnectionData();
                            tmp.LocalAddress = "";
                            tmp.RemoteAddress = "";
                            int remoteValue=0;

                            for(int x =3;x>=0;x--)
                            {
                                String lclAddr = line.substring( localAddrOffset+24+ (2*x), localAddrOffset + 26 + (2*x) );
                                tmp.LocalAddress += Integer.parseInt(lclAddr, 16);
                                if ( x != 0 ) tmp.LocalAddress += ".";

                                String rmtAddr = line.substring( remoteAddrOffset+24+ (2*x), remoteAddrOffset + 26 + (2*x) );
                                //Log.d(TAG, rmtAddr );
                                tmp.RemoteAddress += Integer.parseInt(rmtAddr, 16);
                                if ( x != 0 ) tmp.RemoteAddress += ".";

                                remoteValue *= 256;
                                remoteValue += Integer.parseInt(rmtAddr, 16);
                            }

                            Log.d(TAG, "ID: " + item.uid + " " + item.processName + " " + tmp.LocalAddress + " " + tmp.RemoteAddress + " " + remoteValue);
                            tmp.Package = item.processName;

                            break;
                        }
                    }


                }

                first = false;
            }
            br.close();
        }
        catch(FileNotFoundException ex)
        {
            Log.d(TAG, "FileNotFoundException Exception");
        }
        catch (IOException ioex)
        {
            Log.d(TAG, "IO Exception");
        }

    }

}
