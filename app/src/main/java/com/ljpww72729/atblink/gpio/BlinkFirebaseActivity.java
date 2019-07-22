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
import com.google.android.things.pio.GpioCallback;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ljpww72729.atblink.HttpServerService;
import com.ljpww72729.atblink.R;
import com.ljpww72729.atblink.data.Blink;
import com.ljpww72729.atblink.data.GPIO;
import com.ljpww72729.atblink.data.RaspberryIotInfo;
import com.ljpww72729.atblink.module.BoardDefaults;
import com.ljpww72729.atblink.module.gpio.GpioServer;

import java.io.IOException;

/**
 * Sample usage of the Gpio API that blinks an LE.
 *
 * Some boards, like Intel Edison, have onboard LEDs linked to specific GPIO pins. The preferred
 * GPIO pin to use on each board is in the {@link BoardDefaults} class.
 */
public class BlinkFirebaseActivity extends Activity {
    private static final String TAG = BlinkFirebaseActivity.class.getSimpleName();
    private GpioServer gpioServer;
    // 默认开启状态
    private boolean mLedState = true;
    private DatabaseReference selfGpioRef;

    //test touch switch
    private GPIO gpioLed;
    private Gpio gpioInput;
    private Intent httpServerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_blink);
        // 设置时间
        try {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am.setTimeZone("Asia/Shanghai");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "Starting BlinkActivity");
        gpioServer = new GpioServer();
        // Write a message to the database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        selfGpioRef = database.getReference(RaspberryIotInfo.SELFGPIO);
        // Read from the database
        Query selfDeviceQuery = selfGpioRef.orderByKey();
        selfDeviceQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                childBlinkChanged(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                childBlinkChanged(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        httpServerService = new Intent(this, HttpServerService.class);
        httpServerService.putExtra(HttpServerService.REALTIME_DATA_TYPE, HttpServerService.REALTIME_DATA_TYPE_WILD);
        startService(httpServerService);
    }

    private void childBlinkChanged(DataSnapshot dataSnapshot) {
        GPIO gpioEntry = dataSnapshot.getValue(GPIO.class);
        if (gpioEntry != null && gpioEntry.getGpio().toUpperCase().startsWith("BCM")) {
            Log.i(TAG, "childBlinkChanged: " + gpioEntry.getGpio() + "," + gpioEntry.getStatus());
            try {
                Gpio gpio = gpioServer.initGpio(gpioEntry.getGpio());
                gpio.setDirection(gpioEntry.getDirection());
                gpio.setActiveType(gpioEntry.getActive());
                gpio.setEdgeTriggerType(gpioEntry.getEdge());
                if (gpioEntry.getGpio().equals("BCM2")) {
                    gpioLed = gpioEntry;
                }
                if (gpioEntry.getDirection() != Gpio.DIRECTION_IN) {
                    Blink blink = new Blink();
                    blink.setStatus(gpioEntry.getStatus());
                    gpioServer.notifyBlinkDataChanged(gpioEntry.getGpio(), blink);
                } else {
                    gpioInput = gpio;
                    gpio.registerGpioCallback(mGpioCallback);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private GpioCallback mGpioCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            // Read the active high pin state
            try {
                if (gpio.getValue()) {
                    // Pin is HIGH
                    selfGpioRef.child(gpioLed.getGpioId()).child(GPIO.P_STATUS).setValue(true);
                } else {
                    // Pin is LOW
                    selfGpioRef.child(gpioLed.getGpioId()).child(GPIO.P_STATUS).setValue(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Continue listening for more interrupts
            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            Log.w(TAG, gpio + ": Error event " + error);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gpioInput.unregisterGpioCallback(mGpioCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gpioServer.closeGpio();
        if (httpServerService != null) {
            stopService(httpServerService);
        }
    }

}
