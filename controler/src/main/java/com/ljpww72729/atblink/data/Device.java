package com.ljpww72729.atblink.data;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.ljpww72729.atblink.BR;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by LinkedME06 on 2017/9/4.
 */

@IgnoreExtraProperties
public class Device extends BaseObservable implements Parcelable {

    public static final String P_DID = "deviceId";

    private String deviceId;
    private String deviceName;
    private long lastOnline;
    private boolean connections;

    public long getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }

    public boolean isConnections() {
        return connections;
    }

    public void setConnections(boolean connections) {
        this.connections = connections;
    }


    @Bindable
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        notifyPropertyChanged(BR.deviceId);
    }

    @Bindable
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        notifyPropertyChanged(BR.deviceName);
    }

    public Device() {
    }

    /**
     * 判断用户是否填写了信息
     */
    @Exclude
    public boolean objectIsEmpty() {
        return TextUtils.isEmpty(deviceId) && TextUtils.isEmpty(deviceName);
    }

    @Exclude
    public void clearProperties() {
        setDeviceId("");
        setDeviceName("");
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("deviceId", deviceId);
        result.put("deviceName", deviceName);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deviceId);
        dest.writeString(this.deviceName);
        dest.writeLong(this.lastOnline);
        dest.writeByte(this.connections ? (byte) 1 : (byte) 0);
    }

    protected Device(Parcel in) {
        this.deviceId = in.readString();
        this.deviceName = in.readString();
        this.lastOnline = in.readLong();
        this.connections = in.readByte() != 0;
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel source) {
            return new Device(source);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
}
