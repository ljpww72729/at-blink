package com.ljpww72729.atblink;

import android.app.Application;

import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

/**
 * Created by LinkedME06 on 2017/9/7.
 */

public class CustomApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化
        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://wd8078052585upgyqs.wilddogio.com").build();
        WilddogApp.initializeApp(this, options);

//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        final DatabaseReference connectionRef = database.getReference(RaspberryIotInfo.CONNECTIONSPATH);
//        final DatabaseReference lastOnlineRef = database.getReference(RaspberryIotInfo.LASTONLINEPATH);
//        final DatabaseReference connectedRef = database.getReference(".info/connected");
//        connectedRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                boolean connected = snapshot.getValue(Boolean.class);
//                if (connected) {
////                    DatabaseReference con = connectionRef.push();
//
//                    // when this device disconnects, remove it
//                    connectionRef.onDisconnect().removeValue();
//
//                    // when I disconnect, update the last time I was seen online
//                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
//
//                    // add this device to my connections list
//                    // this value could contain info about the device or a timestamp too
//                    connectionRef.setValue(Boolean.TRUE);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                System.err.println("Listener was cancelled at .info/connected");
//            }
//        });
    }
}
