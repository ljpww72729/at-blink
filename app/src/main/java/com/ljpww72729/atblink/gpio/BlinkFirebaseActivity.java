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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.ljpww72729.atblink.R;
import com.ljpww72729.atblink.data.Blink;
import com.ljpww72729.atblink.module.BoardDefaults;
import com.ljpww72729.atblink.module.gpio.GpioServer;

/**
 * Sample usage of the Gpio API that blinks an LE.
 *
 * Some boards, like Intel Edison, have onboard LEDs linked to specific GPIO pins.
 * The preferred GPIO pin to use on each board is in the {@link BoardDefaults} class.
 */
public class BlinkFirebaseActivity extends Activity {
    private static final String TAG = BlinkFirebaseActivity.class.getSimpleName();
    private GpioServer gpioServer;
    // 默认开启状态
    private boolean mLedState = true;
    String pinName = "BCM6";
    Blink blink = new Blink();
    private DatabaseReference databaseReference;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_blink);
        Log.i(TAG, "Starting BlinkActivity");
        blink.setStatus(mLedState);
        gpioServer = new GpioServer();

        gpioServer.initGpio(pinName, Gpio.DIRECTION_OUT_INITIALLY_HIGH);
        gpioServer.notifyBlinkDataChanged(pinName, blink);
        // Write a message to the database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        databaseReference.child("gpio").child(pinName).setValue(blink);
//        databaseReference.setValue(blink.isStatus());
        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Blink value = dataSnapshot.child("gpio").child(pinName).getValue(Blink.class);
//                blink.setStatus((Boolean) dataSnapshot.getValue());
                gpioServer.notifyBlinkDataChanged(pinName, value);
                Log.i(TAG, "Value is: " + value.isStatus());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        handler.post(mRandomRunnable);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gpioServer.closeGpio();
    }

    private Runnable mRandomRunnable = new Runnable() {
        @Override
        public void run() {
            blink.setStatus(!blink.isStatus());
            databaseReference.child("gpio").child(pinName).setValue(blink);
//            databaseReference.setValue(blink.isStatus());
            handler.postDelayed(mRandomRunnable, 2000);
        }
    };

}
