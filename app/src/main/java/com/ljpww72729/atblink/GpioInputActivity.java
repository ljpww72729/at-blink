package com.ljpww72729.atblink;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ljpww72729.atblink.gpio.BlinkActivity;
import com.ljpww72729.atblink.module.BoardDefaults;

import java.io.IOException;

/**
 * Created by LinkedME06 on 02/06/2017.
 */

public class GpioInputActivity extends Activity {
    private static final String TAG = BlinkActivity.class.getSimpleName();
    private Gpio mLedGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting GpioInputActivity");

        PeripheralManager peripheralManager = PeripheralManager.getInstance();
        try {
            String pinName = BoardDefaults.getGPIOForLED();
            mLedGpio = peripheralManager.openGpio(pinName);
            mLedGpio.setDirection(Gpio.DIRECTION_IN);
            mLedGpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
            mLedGpio.registerGpioCallback(gpioCallback);
            Log.i(TAG, "Start blinking LED GPIOModule pin");

            // Post a Runnable that continuously switch the state of the GPIOModule, blinking the
            // corresponding LED
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }

    GpioCallback gpioCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                Log.i(TAG, "onGpioEdge: " + gpio.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the Gpio pin.
        Log.i(TAG, "Closing LED GPIOModule pin");
        try {
            mLedGpio.unregisterGpioCallback(gpioCallback);
            mLedGpio.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        } finally {
            mLedGpio = null;
        }
    }
}
