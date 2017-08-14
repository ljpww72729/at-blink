package com.ljpww72729.atblink;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

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

        PeripheralManagerService service = new PeripheralManagerService();
        try {
            String pinName = BoardDefaults.getGPIOForLED();
            mLedGpio = service.openGpio(pinName);
            mLedGpio.setDirection(Gpio.DIRECTION_IN);
            mLedGpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
            mLedGpio.registerGpioCallback(gpioCallback);
            Log.i(TAG, "Start blinking LED GPIO pin");

            // Post a Runnable that continuously switch the state of the GPIO, blinking the
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
            return super.onGpioEdge(gpio);
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            super.onGpioError(gpio, error);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the Gpio pin.
        Log.i(TAG, "Closing LED GPIO pin");
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
