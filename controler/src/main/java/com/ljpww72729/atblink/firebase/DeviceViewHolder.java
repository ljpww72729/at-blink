package com.ljpww72729.atblink.firebase;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ljpww72729.atblink.R;
import com.ljpww72729.atblink.data.Device;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by LinkedME06 on 2017/9/4.
 */

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    public Device device;
    public TextView deviceId;
    public TextView deviceName;
    public TextView last_on_line;
    public TextView connections;

    public DeviceViewHolder(View itemView) {
        super(itemView);
        deviceId = itemView.findViewById(R.id.device_id);
        deviceName = itemView.findViewById(R.id.device_name);
        last_on_line = itemView.findViewById(R.id.last_on_line);
        connections = itemView.findViewById(R.id.connections);
    }

    public void bindToDevice(Context context, Device device) {
        this.device = device;
        deviceId.setText(device.getDeviceId());
        deviceName.setText(device.getDeviceName());
        if (device.isConnections()) {
            connections.setText(context.getString(R.string.online));
            connections.setTextColor(Color.BLUE);
        } else {
            connections.setText(context.getString(R.string.offline));
            connections.setTextColor(Color.RED);
        }
        // HH:mm是24小时制 hh:mm是12小时制
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
        String lastOnlineTime = simpleDateFormat.format(new Date(device.getLastOnline()));
        last_on_line.setText(context.getString(R.string.last_on_line, lastOnlineTime));
    }

}
