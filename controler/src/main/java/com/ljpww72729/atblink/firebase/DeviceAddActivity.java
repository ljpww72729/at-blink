package com.ljpww72729.atblink.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ljpww72729.atblink.R;
import com.ljpww72729.atblink.data.Device;
import com.ljpww72729.atblink.data.RaspberryIotInfo;
import com.ljpww72729.atblink.databinding.DeviceAddBinding;
import com.wilddog.client.SyncError;
import com.wilddog.client.SyncReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LinkedME06 on 2017/9/5.
 */

public class DeviceAddActivity extends FirebaseBaseActivity {

    DatabaseReference deviceFireRef;
    SyncReference deviceWildRef;
    DeviceAddBinding binding;
    Device device;
    private boolean updateDevice = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.device_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (isFirebaseAddress) {
            deviceFireRef = databaseFireRef.child(RaspberryIotInfo.DEVICE);
        } else {
            deviceWildRef = databaseWildRef.child(RaspberryIotInfo.DEVICE);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            updateDevice = true;
            getSupportActionBar().setTitle(R.string.device_update);
            device = bundle.getParcelable(RaspberryIotInfo.DEVICE);
            binding.deviceId.setEnabled(false);
            binding.autoGenerate.setVisibility(View.GONE);
        } else {
            getSupportActionBar().setTitle(R.string.device_add);
            device = new Device();
        }
        binding.setDevice(device);
        binding.autoGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFirebaseAddress) {
                    // 查询线上设备id，自动+1作为新的设备id
                    deviceFireRef.orderByChild(Device.P_DID).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String lastDeviceId = "";
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                lastDeviceId = snapshot.getKey();
                            }
                            autoGenerate(lastDeviceId);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    deviceWildRef.orderByChild(Device.P_DID).limitToLast(1).addListenerForSingleValueEvent(new com.wilddog.client.ValueEventListener() {
                        @Override
                        public void onDataChange(com.wilddog.client.DataSnapshot dataSnapshot) {
                            String lastDeviceId = "";
                            if (dataSnapshot.getChildrenCount() > 0) {
                                com.wilddog.client.DataSnapshot dataSnapshotChildren = (com.wilddog.client.DataSnapshot) dataSnapshot.getChildren().iterator().next();
                                lastDeviceId = dataSnapshotChildren.getKey();
                            }
                            autoGenerate(lastDeviceId);
                        }

                        @Override
                        public void onCancelled(SyncError databaseError) {

                        }
                    });
                }
            }
        });
    }

    /**
     * 自动生成device id
     * @param lastDeviceId
     */
    private void autoGenerate(String lastDeviceId) {
        String prefixIdStr = "lp_iot_";
        String suffixIdStr = "000";
        Pattern pattern = Pattern.compile(".*\\D+(?=(\\d+$))");
        Matcher matcher = pattern.matcher(lastDeviceId);
        if (matcher.find()) {
            prefixIdStr = matcher.group(0);
            suffixIdStr = matcher.group(1);
        }
        String deviceIdSuffix = String.valueOf(Integer.valueOf(suffixIdStr) + 1);
        if (deviceIdSuffix.length() < suffixIdStr.length()) {
            deviceIdSuffix = suffixIdStr.substring(0, suffixIdStr.length() - deviceIdSuffix.length()) + deviceIdSuffix;
        }
        String deviceId = prefixIdStr + deviceIdSuffix;
        device.setDeviceId(deviceId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_add_menu, menu);
        if (updateDevice) {
            menu.removeItem(R.id.clear);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.done:
                // 隐藏键盘
                View currentFocus = getCurrentFocus();
                if (currentFocus != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
                device = binding.getDevice();
                if (TextUtils.isEmpty(device.getDeviceId())) {
                    Snackbar.make(findViewById(R.id.constraint_layout),
                            getString(R.string.device_id) + getString(R.string.notNull),
                            Snackbar.LENGTH_SHORT).show();
                    return false;
                }
                if (TextUtils.isEmpty(device.getDeviceName())) {
                    Snackbar.make(findViewById(R.id.constraint_layout),
                            getString(R.string.device_name) + getString(R.string.notNull),
                            Snackbar.LENGTH_SHORT).show();
                    return false;
                }
                if (isFirebaseAddress) {
                    deviceFireRef.child(device.getDeviceId()).setValue(device);
                }else {
                    deviceWildRef.child(device.getDeviceId()).setValue(device);
                }

                finish();
                return true;
            case R.id.clear:
                device.clearProperties();
                return true;
            case android.R.id.home:
                backLogic();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        backLogic();
    }

    private void backLogic() {
        if (binding.getDevice().objectIsEmpty()) {
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(DeviceAddActivity.this);
            builder.setMessage(R.string.exit_alert_info)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.create().show();

        }
    }

    public static void start(Context context, Bundle bundle) {
        Intent starter = new Intent(context, DeviceAddActivity.class);
        if (bundle != null) {
            starter.putExtras(bundle);
        }
        context.startActivity(starter);
    }

}
