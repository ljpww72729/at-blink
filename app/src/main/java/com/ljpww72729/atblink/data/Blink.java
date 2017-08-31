package com.ljpww72729.atblink.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LinkedME06 on 19/08/2017.
 */

public class Blink implements Parcelable {
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    private boolean status;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
    }

    public Blink() {
    }

    public Blink(boolean status) {
        this.status = status;
    }

    protected Blink(Parcel in) {
        this.status = in.readByte() != 0;
    }

    public static final Creator<Blink> CREATOR = new Creator<Blink>() {
        @Override
        public Blink createFromParcel(Parcel source) {
            return new Blink(source);
        }

        @Override
        public Blink[] newArray(int size) {
            return new Blink[size];
        }
    };

    // WARNING: 2017/8/31 lipeng 如果使用firebase 实时数据库，该实体类中不能使用非属性的getXXX方法，
    // 会导致数据同步的时候产生问题，会将其作为一个属性get方法对待
    public byte[] obtainBytes() {
        byte[] bytes = new byte[20];
        bytes[0] = status ? (byte) 1 : (byte) 0;
        return bytes;
    }
}
