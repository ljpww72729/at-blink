/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ljpww72729.atblink.gpio;

import com.google.android.things.pio.Gpio;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.ljpww72729.atblink.GattServerService;
import com.ljpww72729.atblink.data.Blink;
import com.ljpww72729.atblink.module.BoardDefaults;
import com.ljpww72729.atblink.module.gpio.GpioServer;

import java.io.IOException;
import java.util.Random;

/**
 * Sample usage of the Gpio API that blinks an LE.
 *
 * Some boards, like Intel Edison, have onboard LEDs linked to specific GPIO pins.
 * The preferred GPIO pin to use on each board is in the {@link BoardDefaults} class.
 */
public class LedRGBActivity extends Activity {
    private static final String TAG = LedRGBActivity.class.getSimpleName();
    private GpioServer gpioServer;
    // 默认开启状态
    private boolean mLedState = false;
    String pinNameR = "BCM22";
    String pinNameB = "BCM4";
    String pinNameG = "BCM17";
    Blink blinkR = new Blink();
    Blink blinkB = new Blink();
    Blink blinkG = new Blink();

    private static final int INTERVAL_BETWEEN_BLINKS_MS = 2000;

    private Handler mHandler = new Handler();

    private GattServerService mBluetoothLeService;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((GattServerService.GattServerBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // 自动开启GattServer服务
            mBluetoothLeService.startGattServer();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (GattServerService.ACTION_GATT_CONNECTED.equals(action)) {

            } else if (GattServerService.ACTION_GATT_DISCONNECTED.equals(action)) {

            } else if (GattServerService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

            } else if (GattServerService.ACTION_DATA_AVAILABLE.equals(action)) {
//                blink = intent.getParcelableExtra(GattServerService.EXTRA_DATA);
//                gpioServer.notifyBlinkDataChanged(blink);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting BlinkActivity");
        blinkR.setStatus(false);
        blinkB.setStatus(false);
        blinkG.setStatus(false);
        gpioServer = new GpioServer();

        try {
            gpioServer.initGpio(pinNameR).setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            gpioServer.notifyBlinkDataChanged(pinNameR, blinkR);

            gpioServer.initGpio(pinNameB).setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            gpioServer.notifyBlinkDataChanged(pinNameB, blinkB);

            gpioServer.initGpio(pinNameG).setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            gpioServer.notifyBlinkDataChanged(pinNameG, blinkG);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Intent gattServiceIntent = new Intent(this, GattServerService.class);
        gattServiceIntent.putExtra(GattServerService.CURRENT_BLINK_DATA, blinkR);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        mHandler.post(mBlinkRunnable);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            mBluetoothLeService.startGattServer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gpioServer.closeGpio();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GattServerService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(GattServerService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(GattServerService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(GattServerService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private Runnable mBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            // Exit Runnable if the GPIO is already closed
            if (gpioServer.getGpid(pinNameR) == null) {
                return;
            }
            // Toggle the GPIO state
            mLedState = !mLedState;

            int random = new Random().nextInt(4);
            switch (random) {
                case 1:
                    blinkR.setStatus(mLedState);
                    gpioServer.notifyBlinkDataChanged(pinNameR, blinkR);
                    blinkB.setStatus(mLedState);
                    gpioServer.notifyBlinkDataChanged(pinNameB, blinkR);
                    break;
                case 2:
                    blinkR.setStatus(mLedState);
                    gpioServer.notifyBlinkDataChanged(pinNameR, blinkR);
                    blinkG.setStatus(mLedState);
                    gpioServer.notifyBlinkDataChanged(pinNameG, blinkR);
                    break;
                case 3:
                    blinkB.setStatus(mLedState);
                    gpioServer.notifyBlinkDataChanged(pinNameB, blinkR);
                    blinkG.setStatus(mLedState);
                    gpioServer.notifyBlinkDataChanged(pinNameG, blinkR);
                    break;
                default:
                    blinkR.setStatus(mLedState);
                    gpioServer.notifyBlinkDataChanged(pinNameR, blinkR);
                    break;
            }
            Log.d(TAG, "State set to " + mLedState);
            mHandler.postDelayed(mBlinkRunnable, INTERVAL_BETWEEN_BLINKS_MS);
        }
    };
}
