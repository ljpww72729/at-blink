package com.ljpww72729.atblink;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.ljpww72729.atblink.databinding.ActivityScanBinding;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

/**
 * Created by LinkedME06 on 2017/8/31.
 */

public class DeviceScanActivity extends AppCompatActivity implements DeviceScanFragment.OnListFragmentInteractionListener {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityScanBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_scan);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "BLE is supported", Toast.LENGTH_SHORT).show();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, DeviceScanFragment.newInstance(1)).commitNow();
        }

    }

    public static void start(Context context) {
        Intent starter = new Intent(context, DeviceScanActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onListFragmentInteraction(BluetoothDevice device) {

        if (device == null) return;
        final Intent intent = new Intent(this, BlinkControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        DeviceScanFragment fragment = (DeviceScanFragment) fragmentManager.findFragmentById(R.id.container);
        fragment.stopScan();
        startActivity(intent);
    }

}


