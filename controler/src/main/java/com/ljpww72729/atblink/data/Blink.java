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

    public byte[] readBytes() {
        byte[] bytes = new byte[20];
        bytes[0] = status ? (byte) 1 : (byte) 0;
        return bytes;
    }

    public void writeBytes(byte[] bytes) {
        this.setStatus(bytes[0] == 1);
    }
}
