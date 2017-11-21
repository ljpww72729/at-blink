package com.ljpww72729.atblink.data;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by LinkedME06 on 2017/9/4.
 */

public class RaspberryIotInfo {

    public static final String DEVICE = "device";
    public static final String CHANGED = "changed";

    public static final String CONNECTIONS = "connections";
    public static final String LASTONLINE = "lastOnline";


    public static final String DID = "lp_iot_001";

    public static final String GPIO = "gpio";

    public static final String PIN = "pin";

    // 以下是当前设备路径
    public static final String SELFDEVICE = RaspberryIotInfo.DEVICE + "/" + RaspberryIotInfo.DID;
    public static final String CONNECTIONSPATH = SELFDEVICE + "/" + CONNECTIONS;
    public static final String LASTONLINEPATH = SELFDEVICE + "/" + LASTONLINE;

    public static final String SELFGPIO = RaspberryIotInfo.GPIO + "/" + RaspberryIotInfo.DID;

    // 野狗服务器路径
    public static final String WILD_SYNC_URL = "https://wd8078052585upgyqs.wilddogio.com";

    // 增删改查状态
    public static final String OPERATE = "operate";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ADD, DELETE, UPDATE, QUERY})
    public @interface Operate {

    }

    public static final String ADD = "add";
    public static final String DELETE = "delete";
    public static final String UPDATE = "update";
    public static final String QUERY = "query";

}
