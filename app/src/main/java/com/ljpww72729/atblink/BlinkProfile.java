package com.ljpww72729.atblink;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

/**
 * Created by LinkedME06 on 14/08/2017.
 */

public class BlinkProfile {
    private static final String TAG = BlinkProfile.class.getSimpleName();

    /* Blink Service UUID */
    public static UUID BLINK_SERVICE = UUID.fromString("00000101-0000-1000-8000-1989072729ab");
    /* Mandatory Blink Status Characteristic */
    public static UUID BLINK_STATUS_CHAR = UUID.fromString("00000102-0000-1000-8000-1989072729ab");
    /* Mandatory Blink Status Characteristic Config Descriptor */
    public static UUID BLINK_DESC = UUID.fromString("00000103-0000-1000-8000-1989072729ab");

    // Adjustment Flags
    public static final byte ADJUST_NONE = 0x0;
    public static final byte ADJUST_MANUAL = 0x1;
    public static final byte ADJUST_EXTERNAL = 0x2;
    public static final byte ADJUST_TIMEZONE = 0x4;
    public static final byte ADJUST_DST = 0x8;

    /**
     * Return a configured {@link BluetoothGattService} instance for the
     * Blink Service.
     */
    public static BluetoothGattService createBlinkService() {
        BluetoothGattService service = new BluetoothGattService(BLINK_SERVICE,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        // Blink status characteristic
        BluetoothGattCharacteristic blinkStatus = new BluetoothGattCharacteristic(BLINK_STATUS_CHAR,
                //Read/write characteristic, supports notifications
                BluetoothGattCharacteristic.PROPERTY_READ |
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY |
                        BluetoothGattCharacteristic.PROPERTY_WRITE,
                //此处设置权限，若要有写权限，则property及permission都要设置写权限
                BluetoothGattCharacteristic.PERMISSION_READ |
                        BluetoothGattCharacteristic.PERMISSION_WRITE);
        BluetoothGattDescriptor configDescriptor = new BluetoothGattDescriptor(BLINK_DESC,
                //Read/write descriptor
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        blinkStatus.addDescriptor(configDescriptor);

        service.addCharacteristic(blinkStatus);

        return service;
    }
}
