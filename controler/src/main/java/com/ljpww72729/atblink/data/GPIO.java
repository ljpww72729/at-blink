package com.ljpww72729.atblink.data;

import com.google.firebase.database.Exclude;

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

public class GPIO extends BaseObservable implements Parcelable {

    public static final String P_DID = "deviceId";
    public static final String P_STATUS = "status";

    public static final int ACTIVE_HIGH = 1;
    public static final int ACTIVE_LOW = 0;
    public static final int DIRECTION_IN = 0;
    public static final int DIRECTION_OUT_INITIALLY_HIGH = 1;
    public static final int DIRECTION_OUT_INITIALLY_LOW = 2;
    public static final int EDGE_BOTH = 3;
    public static final int EDGE_FALLING = 2;
    public static final int EDGE_NONE = 0;
    public static final int EDGE_RISING = 1;


    /**
     * gpioId : lp_iot_00003
     * gpio : BCM3
     * function : 控制1号LED灯
     * status : true 开；false:关
     * emitCheck : 1
     * reportCheck : 1
     */

    private String gpioId;
    private String gpio;
    private String alias;
    private String function;
    private boolean status;
    private int direction = DIRECTION_IN;
    private int active = ACTIVE_LOW;
    private int edge = EDGE_NONE;

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getEdge() {
        return edge;
    }

    public void setEdge(int edge) {
        this.edge = edge;
    }

    @Bindable
    public String getGpio() {
        return gpio;
    }

    public void setGpio(String gpio) {
        this.gpio = gpio;
        notifyPropertyChanged(BR.gpio);
    }

    @Bindable
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
        notifyPropertyChanged(BR.alias);
    }

    @Bindable
    public String getGpioId() {
        return gpioId;
    }

    public void setGpioId(String gpioId) {
        this.gpioId = gpioId;
        notifyPropertyChanged(BR.gpioId);
    }

    @Bindable
    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
        notifyPropertyChanged(BR.function);
    }

    @Bindable
    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    public GPIO() {
    }

    /**
     * 判断用户是否填写了信息
     */
    @Exclude
    public boolean objectIsEmpty() {
        return TextUtils.isEmpty(gpioId) && TextUtils.isEmpty(gpio) &&
                TextUtils.isEmpty(alias) && TextUtils.isEmpty(function);
    }

    @Exclude
    public void clearProperties() {
        setGpioId("");
        setGpio("");
        setAlias("");
        setFunction("");
        setDirection(0);
        setActive(0);
        setEdge(0);
        setStatus(false);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("gpioId", gpioId);
        result.put("gpio", gpio);
        result.put("alias", alias);
        result.put("function", function);
        result.put("status", status);
        result.put("direction", direction);
        result.put("active", active);
        result.put("edge", edge);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.gpioId);
        dest.writeString(this.gpio);
        dest.writeString(this.alias);
        dest.writeString(this.function);
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
        dest.writeInt(this.direction);
        dest.writeInt(this.active);
        dest.writeInt(this.edge);
    }

    protected GPIO(Parcel in) {
        this.gpioId = in.readString();
        this.gpio = in.readString();
        this.alias = in.readString();
        this.function = in.readString();
        this.status = in.readByte() != 0;
        this.direction = in.readInt();
        this.active = in.readInt();
        this.edge = in.readInt();
    }

    public static final Creator<GPIO> CREATOR = new Creator<GPIO>() {
        @Override
        public GPIO createFromParcel(Parcel source) {
            return new GPIO(source);
        }

        @Override
        public GPIO[] newArray(int size) {
            return new GPIO[size];
        }
    };
}
