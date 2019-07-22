package com.ljpww72729.atblink;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import android.app.Application;
import android.util.Log;

import com.ljpww72729.atblink.data.RaspberryIotInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by LinkedME06 on 2017/9/7.
 */

public class CustomApp extends Application {

    public static final String TAG = CustomApp.class.getCanonicalName();

    @Override
    public void onCreate() {
        super.onCreate();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        final DatabaseReference connectionRef = database.getReference(RaspberryIotInfo.CONNECTIONSPATH);
        final DatabaseReference lastOnlineRef = database.getReference(RaspberryIotInfo.LASTONLINEPATH);
        final DatabaseReference lastOnlineFormatRef = database.getReference(RaspberryIotInfo.LASTONLINEFORAMTPATH);
        final DatabaseReference connectedRef = database.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot != null) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
//                    DatabaseReference con = connectionRef.push();

                        // when this device disconnects, remove it
                        connectionRef.onDisconnect().removeValue();

                        // when I disconnect, update the last time I was seen online
                        lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                        lastOnlineFormatRef.onDisconnect().setValue(dateFormat.format(new Date()));

                        // add this device to my connections list
                        // this value could contain info about the device or a timestamp too
                        connectionRef.setValue(Boolean.TRUE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i(TAG, "onCancelled: Listener was cancelled at .info/connected");
            }
        });
    }
}
