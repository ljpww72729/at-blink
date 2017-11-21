package com.ljpww72729.atblink;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.ljpww72729.atblink.data.RaspberryIotInfo;
import com.wilddog.client.ChildEventListener;
import com.wilddog.client.DataSnapshot;
import com.wilddog.client.SyncError;
import com.wilddog.client.SyncReference;
import com.wilddog.client.ValueEventListener;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * http服务，用于esp8266设备连接，获取数据是否更新状态
 *
 * Created by LinkedME06 on 2017/11/2.
 */

public class HttpServerService extends Service {
    private static final String TAG = "HttpServerService";
    private static final int HTTPSERVERPORT = 8888;
    public static final String REALTIME_DATA_TYPE = "realtime_data_type";
    public static final String WILD_SYNC_URL = "wild_sync_url";
    public static final int REALTIME_DATA_TYPE_FIREBASE = 0;
    public static final int REALTIME_DATA_TYPE_WILD = 1;
    private volatile boolean quit = false;
    private SyncReference wildRef;
    ServerSocket httpServerSocket;
    private int realtimeDataType = REALTIME_DATA_TYPE_FIREBASE;
    private ConcurrentMap<String, BlockingQueue> changedQueueMap;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({REALTIME_DATA_TYPE_FIREBASE, REALTIME_DATA_TYPE_WILD})
    public @interface RealtimeDataType {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        changedQueueMap = new ConcurrentHashMap<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ipAddress=" + getIpAddress());
        realtimeDataType = intent.getIntExtra(REALTIME_DATA_TYPE, REALTIME_DATA_TYPE_FIREBASE);
        if (realtimeDataType == REALTIME_DATA_TYPE_WILD) {
            //野狗数据源
            WilddogOptions options = new WilddogOptions.Builder().setSyncUrl(RaspberryIotInfo.WILD_SYNC_URL).build();
            WilddogApp.initializeApp(this, options);
            wildRef = WilddogSync.getInstance().getReference();
            wildRef.child(RaspberryIotInfo.DEVICE).orderByKey().addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    dataChangedListener(dataSnapshot.getKey());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
//                    dataRemovedListener(dataSnapshot.getKey());
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(SyncError syncError) {

                }
            });

        } else {
            // TODO: 2017/11/6 lipeng  firebase 数据源

        }

        HttpServerThread httpServerThread = new HttpServerThread();
        httpServerThread.start();
        return START_STICKY;
    }

    /**
     * 数据状态监听
     */
    private void dataChangedListener(final String dId) {
        SyncReference gpioRef = wildRef.child(RaspberryIotInfo.GPIO + "/" + dId);
        final SyncReference changedRef = wildRef.child(RaspberryIotInfo.DEVICE + "/" + dId + "/" + RaspberryIotInfo.CHANGED);
        SyncReference connectionsRef = wildRef.child(RaspberryIotInfo.DEVICE + "/" + dId + "/" + RaspberryIotInfo.CONNECTIONS);
        final SyncReference lastOnlineRef = wildRef.child(RaspberryIotInfo.DEVICE + "/" + dId + "/" + RaspberryIotInfo.LASTONLINE);
        // Read from the database
        gpioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                wildRef.child(RaspberryIotInfo.DEVICE + "/" + dId + "/" + RaspberryIotInfo.CHANGED)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int changed = (int) dataSnapshot.getValue(Integer.class);
                                // 判断是否只更改了引脚状态，并没有设置changed的值为1，但这也属于数据值更改了，
                                // 应该通知子模块数据更新了，但是这里changed是否获取的是最新的值，无法确定，其实
                                // 这里违背了数据更改的统一性，但是为了兼容可在任何地方更改引脚状态来通知子模块
                                // 数据已更新，则只能这么处理了
                                if (changed != 1) {
                                    Log.i(TAG, "gpio changed value is changed");
                                    if (changedQueueMap.containsKey(dId)) {
                                        changedQueueMap.get(dId).offer("1");
                                    } else {
                                        BlockingQueue blockingQueue = new ArrayBlockingQueue(10);
                                        blockingQueue.offer("1");
                                        changedQueueMap.put(dId, blockingQueue);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(SyncError syncError) {

                            }
                        });

                Log.i(TAG, "gpio value is changed");
            }

            @Override
            public void onCancelled(SyncError syncError) {

            }
        });
        changedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int changed = (int) dataSnapshot.getValue(Integer.class);
                if (changedQueueMap.containsKey(dId)) {
                    changedQueueMap.get(dId).offer(String.valueOf(changed));
                } else {
                    BlockingQueue blockingQueue = new ArrayBlockingQueue(10);
                    blockingQueue.offer(String.valueOf(changed));
                    changedQueueMap.put(dId, blockingQueue);
                }
                Log.i(TAG, "changed value is: " + changed);
            }

            @Override
            public void onCancelled(SyncError syncError) {

            }

        });
        connectionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connections = (boolean) dataSnapshot.getValue(Boolean.class);
                Log.i(TAG, "connections value is: " + connections);
                if (connections) {
                    lastOnlineRef.setValue(new Date().getTime());
                }
            }

            @Override
            public void onCancelled(SyncError syncError) {

            }

        });
    }

//    private void dataRemovedListener(String dId) {
//
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        quit = true;
        if (httpServerSocket != null) {
            try {
                httpServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getIpAddress() {
        StringBuilder ip = new StringBuilder();
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip.append("SiteLocalAddress: ").append(inetAddress.getHostAddress()).append("\n");
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip.append("Something Wrong! ").append(e.toString()).append("\n");
        }

        return ip.toString();
    }

    private class HttpServerThread extends Thread {
        @Override
        public void run() {
            Log.d(TAG, "run: HttpServerThread is Start");
            Socket socket;
            final BlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(50);
            HttpResponseRunnable httpResponseRunnable = new HttpResponseRunnable(arrayBlockingQueue);
            Thread thread = new Thread(httpResponseRunnable);
            thread.start();
            try {
                httpServerSocket = new ServerSocket(HTTPSERVERPORT);
                while (!quit) {
                    try {
                        socket = httpServerSocket.accept();
                        arrayBlockingQueue.offer(socket);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class HttpResponseRunnable implements Runnable {

        private final BlockingQueue queue;

        HttpResponseRunnable(BlockingQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                while (!quit) {
                    Socket socket = (Socket) queue.take();
                    httpResponse(socket);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        void httpResponse(Socket socket) {
            BufferedReader bufferedReader;
            PrintWriter os;
            String request;
            String resp = null;

            try {
                // 此处设置缓存大小是防止内存占用过大，导致不停地GC
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()), 100);
                request = bufferedReader.readLine();
                if (TextUtils.isEmpty(request)) {
                    return;
                }
                String httpString = request.split(" ")[1];
                Uri httpUri = Uri.parse(httpString);
                String dId = httpUri.getQueryParameter("dId");
                Log.d(TAG, "run: did is " + dId);
                if (TextUtils.isEmpty(dId)) {
                    return;
                }
                BlockingQueue blockingQueue = changedQueueMap.get(dId);
                if (blockingQueue != null) {
                    resp = (String) blockingQueue.poll();
                }
                if (resp == null) {
                    resp = "0";
                }
                if (TextUtils.equals(resp, "1")) {
                    // 重置数据更改状态
                    if (realtimeDataType == REALTIME_DATA_TYPE_WILD) {
                        // 更新野狗数据
                        SyncReference changedRef = wildRef.child(RaspberryIotInfo.DEVICE + "/" + dId + "/" + RaspberryIotInfo.CHANGED);
                        changedRef.setValue(0);
                    } else {
                        // TODO: 2017/11/6 lipeng  更新firebase数据
                    }
                }

                Log.d(TAG, "run: resp = " + resp);

                os = new PrintWriter(socket.getOutputStream(), true);

                String response = resp;

                os.print("HTTP/1.0 200" + "\r\n");
                os.print("Content type: text/html" + "\r\n");
                os.print("Content length: " + response.length() + "\r\n");
                os.print("\r\n");
                os.print(response + "\r\n");
                os.flush();

                // 以下为紧缺资源不要使用finally来释放
                os.close();
                bufferedReader.close();
                Log.i(TAG, "Request of " + request
                        + " from " + socket.getInetAddress().toString() + "\n");
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }


}
