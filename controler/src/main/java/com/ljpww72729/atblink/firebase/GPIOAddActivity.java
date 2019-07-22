package com.ljpww72729.atblink.firebase;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ljpww72729.atblink.R;
import com.ljpww72729.atblink.data.Device;
import com.ljpww72729.atblink.data.GPIO;
import com.ljpww72729.atblink.data.RaspberryIotInfo;
import com.ljpww72729.atblink.databinding.GpioAddBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;


/**
 * Created by LinkedME06 on 2017/9/6.
 */

public class GPIOAddActivity extends FirebaseBaseActivity {

    DatabaseReference gpioDeviceFireRef;
    GpioAddBinding binding;
    GPIO gpio;
    private String operate = RaspberryIotInfo.ADD;
    private String deviceId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.gpio_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            deviceId = bundle.getString(RaspberryIotInfo.DID);
            operate = bundle.getString(RaspberryIotInfo.OPERATE, RaspberryIotInfo.ADD);
            switch (operate) {
                case RaspberryIotInfo.UPDATE:
                    if (bundle.getParcelable(RaspberryIotInfo.GPIO) != null) {
                        getSupportActionBar().setTitle(R.string.gpio_update);
                        gpio = bundle.getParcelable(RaspberryIotInfo.GPIO);
                    }
                    break;
                case RaspberryIotInfo.QUERY:
                    if (bundle.getParcelable(RaspberryIotInfo.GPIO) != null) {
                        getSupportActionBar().setTitle(R.string.gpio_query);
                        gpio = bundle.getParcelable(RaspberryIotInfo.GPIO);
                    }
                    break;
                default://默认为add添加
                    getSupportActionBar().setTitle(R.string.gpio_add);
                    gpio = new GPIO();
                    break;
            }
        }
        if (isFirebaseAddress) {
            gpioDeviceFireRef = databaseFireRef.child(RaspberryIotInfo.GPIO).child(deviceId);
        }
        binding.setGpio(gpio);
        binding.autoGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 查询线上设备id，自动+1作为新的设备id
                if (isFirebaseAddress) {
                    gpioDeviceFireRef.orderByChild(Device.P_DID).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String lastGpioId = "";
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                lastGpioId = snapshot.getKey();
                            }
                            autoGenerate(lastGpioId);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    /**
     * 自动生成 gpio id
     */
    private void autoGenerate(String lastGpioId) {
        String prefixIdStr = deviceId;
        String suffixIdStr = "00";
        Pattern pattern = Pattern.compile(".*\\D+(?=(\\d+$))");
        Matcher matcher = pattern.matcher(lastGpioId);
        if (matcher.find()) {
            prefixIdStr = matcher.group(0);
            suffixIdStr = matcher.group(1);
        }
        String gpioIdSuffix = String.valueOf(Integer.valueOf(suffixIdStr) + 1);
        if (gpioIdSuffix.length() < suffixIdStr.length()) {
            gpioIdSuffix = suffixIdStr.substring(0, suffixIdStr.length() - gpioIdSuffix.length()) + gpioIdSuffix;
        }
        String deviceId = prefixIdStr + gpioIdSuffix;
        gpio.setGpioId(deviceId);
    }

    private void changeEditEnable(boolean enable) {
        binding.gpioId.setEnabled(false);
        binding.gpio.setEnabled(enable);
        binding.alias.setEnabled(enable);
        binding.function.setEnabled(enable);
        binding.status.setEnabled(enable);
        binding.direction.setEnabled(enable);
        binding.active.setEnabled(enable);
        binding.edge.setEnabled(enable);
        binding.autoGenerate.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_add_menu, menu);
        switch (operate) {
            case RaspberryIotInfo.UPDATE:
                menu.removeItem(R.id.clear);
                menu.findItem(R.id.update).setVisible(false);
                changeEditEnable(true);
                break;
            case RaspberryIotInfo.QUERY:
                menu.removeItem(R.id.clear);
                menu.removeItem(R.id.done);
                menu.findItem(R.id.update).setVisible(true);
                changeEditEnable(false);
                break;
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
                gpio = binding.getGpio();
                if (!validEditValue()) {
                    return false;
                }
                Map<String, Object> gpioValues = gpio.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                if (isFirebaseAddress) {
                    // zzbpw()获取gpioDeviceFireRef引用路径
                    childUpdates.put(gpioDeviceFireRef.toString() + "/" + gpio.getGpioId(), gpioValues);
                    childUpdates.put("/" + RaspberryIotInfo.DEVICE + "/" + deviceId + "/changed", 1);
                    databaseFireRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Snackbar.make(findViewById(R.id.constraint_layout), R.string.operate_succeed, Snackbar.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Snackbar.make(findViewById(R.id.constraint_layout),
                                        getString(R.string.operate_failed_msg, databaseError.getMessage()),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                return true;
            case R.id.clear:
                gpio.clearProperties();
                return true;
            case R.id.update:
                operate = RaspberryIotInfo.UPDATE;
                invalidateOptionsMenu();
                return true;
            case android.R.id.home:
                backLogic();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private boolean validEditValue() {
        if (TextUtils.isEmpty(gpio.getGpioId())) {
            Snackbar.make(findViewById(R.id.constraint_layout),
                    getString(R.string.gpio_id) + getString(R.string.notNull),
                    Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(gpio.getGpio())) {
            Snackbar.make(findViewById(R.id.constraint_layout),
                    getString(R.string.gpio_gpio) + getString(R.string.notNull),
                    Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(gpio.getFunction())) {
            Snackbar.make(findViewById(R.id.constraint_layout),
                    getString(R.string.gpio_function) + getString(R.string.notNull),
                    Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        backLogic();
    }

    private void backLogic() {
        if (TextUtils.equals(operate, RaspberryIotInfo.QUERY)) {
            finish();
            return;
        }
        if (binding.getGpio().objectIsEmpty()) {
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(GPIOAddActivity.this);
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
        Intent starter = new Intent(context, GPIOAddActivity.class);
        if (bundle != null) {
            starter.putExtras(bundle);
        }
        context.startActivity(starter);
    }

}
