package com.ljpww72729.atblink.module.gpio;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import android.util.ArrayMap;
import android.util.Log;

import com.ljpww72729.atblink.data.Blink;

import java.io.IOException;
import java.util.Iterator;

import static android.content.ContentValues.TAG;

/**
 * Created by LinkedME06 on 20/08/2017.
 */

public class GpioServer {

    private PeripheralManagerService service;
    // TODO: 22/08/2017 lipeng 待完成
    private ArrayMap<String, Gpio> gpioArrayMap = new ArrayMap<>();

    public GpioServer() {
        service = new PeripheralManagerService();
//            List<String> gpioList = service.getGpioList();
//            for (int i = 0; i < gpioList.size(); i++) {
//                Log.i(TAG, "gpio 引脚: " + gpioList.get(i));
//            }
    }


    public Gpio initGpio(String pinName) {
        if (service == null) {
            Log.e(TAG, "GpioServer is not created.");
            return null;
        }
        if (gpioArrayMap.get(pinName) != null) {
            return gpioArrayMap.get(pinName);
        }
        try {
            Gpio mLedGpio = service.openGpio(pinName);
            gpioArrayMap.put(pinName, mLedGpio);
            Log.i(TAG, "Start blinking LED GPIO pin");
            return mLedGpio;
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
        return null;
    }

    public Gpio getGpid(String pinName) {
        if (gpioArrayMap.get(pinName) != null) {
            return gpioArrayMap.get(pinName);
        }
        return null;
    }

    /**
     * Close the Gpio pin.
     */
    public void closeGpio() {
        try {
            Iterator<String> iterator = gpioArrayMap.keySet().iterator();
            while (iterator.hasNext()) {
                String pinName = iterator.next();
                gpioArrayMap.get(pinName).close();
            }
            gpioArrayMap.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyBlinkDataChanged(String pinName, Blink blink) {
        // Toggle the GPIO state
        try {
            if (gpioArrayMap.get(pinName) != null) {
                gpioArrayMap.get(pinName).setValue(blink.isStatus());
                Log.d(TAG, "State set to " + blink.isStatus());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
