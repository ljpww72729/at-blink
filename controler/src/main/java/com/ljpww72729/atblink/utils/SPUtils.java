package com.ljpww72729.atblink.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.TextUtils;

import java.util.Map;
import java.util.Set;

/**
 * Created by LinkedME06 on 2017/9/29.
 */

public class SPUtils {
    private static SharedPreferences getSharedPreferences(@NonNull Context context, String prefsName) {
        if (TextUtils.isEmpty(prefsName)) {
            return ((Activity) context).getPreferences(Context.MODE_PRIVATE);
        } else {
            return context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        }
    }

    private static void apply(SharedPreferences.Editor editor) {
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    /**
     * 存储String类型，若prefsName为null，则会存储到一个默认的SP文件中
     *
     * @param context   Activity Context
     * @param prefsName Preference名
     * @param key       Key
     * @param value     The new value for the preference. Passing null for this argument is
     *                  equivalent to calling remove(String) with this key.
     */
    public static void putString(@NonNull Context context, @Nullable String prefsName, String key, String value) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        apply(editor);
    }

    public static void putString(@NonNull Context context, String key, String value) {
        putString(context, null, key, value);
    }

    public static String getString(@NonNull Context context, String prefsName, String key, String defValue) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        return sp.getString(key, defValue);
    }

    public static String getString(@NonNull Context context, String key, String defValue) {
        return getString(context, null, key, defValue);
    }

    public static void putBoolean(@NonNull Context context, @Nullable String prefsName, String key, boolean value) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        apply(editor);
    }

    public static void putBoolean(@NonNull Context context, String key, boolean value) {
        putBoolean(context, null, key, value);
    }

    public static boolean getBoolean(@NonNull Context context, String prefsName, String key, boolean defValue) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        return sp.getBoolean(key, defValue);
    }

    public static boolean getBoolean(@NonNull Context context, String key, boolean defValue) {
        return getBoolean(context, null, key, defValue);
    }

    public static void putFloat(@NonNull Context context, @Nullable String prefsName, String key, float value) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value);
        apply(editor);
    }

    public static void putFloat(@NonNull Context context, String key, float value) {
        putFloat(context, null, key, value);
    }

    public static float getFloat(@NonNull Context context, String prefsName, String key, float defValue) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        return sp.getFloat(key, defValue);
    }

    public static float getFloat(@NonNull Context context, String key, float defValue) {
        return getFloat(context, null, key, defValue);
    }


    public static void putInt(@NonNull Context context, @Nullable String prefsName, String key, int value) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        apply(editor);
    }

    public static void putInt(@NonNull Context context, String key, int value) {
        putInt(context, null, key, value);
    }

    public static int getInt(@NonNull Context context, String prefsName, String key, int defValue) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        return sp.getInt(key, defValue);
    }

    public static int getInt(@NonNull Context context, String key, int defValue) {
        return getInt(context, null, key, defValue);
    }

    public static void putLong(@NonNull Context context, @Nullable String prefsName, String key, long value) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        apply(editor);
    }

    public static void putLong(@NonNull Context context, String key, long value) {
        putLong(context, null, key, value);
    }

    public static long getLong(@NonNull Context context, String prefsName, String key, long defValue) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        return sp.getLong(key, defValue);
    }

    public static long getLong(@NonNull Context context, String key, long defValue) {
        return getLong(context, null, key, defValue);
    }

    public static void putStringSet(@NonNull Context context, @Nullable String prefsName, String key, Set<String> value) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, value);
        apply(editor);
    }

    public static void putStringSet(@NonNull Context context, String key, Set<String> value) {
        putStringSet(context, null, key, value);
    }

    public static Set<String> getStringSet(@NonNull Context context, String prefsName, String key, Set<String> defValue) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        return sp.getStringSet(key, defValue);
    }

    public static Set<String> getStringSet(@NonNull Context context, String key, Set<String> defValue) {
        return getStringSet(context, null, key, defValue);
    }

    /**
     * 清空，若prefsName为null，则清空默认SP文件，若不为空，则清空指定SP文件
     *
     * @param context   Context
     * @param prefsName SP文件
     */
    public static void clear(@NonNull Context context, String prefsName) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        apply(editor);
    }

    public static void clear(@NonNull Context context) {
        clear(context, null);
    }

    /**
     * 移除某个key
     *
     * @param context   Context
     * @param prefsName SP文件名称
     * @param key       Key
     */
    public static void remove(@NonNull Context context, String prefsName, @NonNull String key) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        apply(editor);
    }

    public static void remove(@NonNull Context context, @NonNull String key) {
        remove(context, null, key);
    }

    /**
     * 判断是否包含某个key
     *
     * @param context   Context
     * @param prefsName SP文件名称
     * @param key       Key
     * @return Boolean true: 包含  false：不包含
     */
    public static boolean contains(@NonNull Context context, String prefsName, @NonNull String key) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        return sp.contains(key);
    }

    public static boolean contains(@NonNull Context context, @NonNull String key) {
        return contains(context, null, key);
    }

    /**
     * 获取全部key-value值
     *
     * @param context   Context
     * @param prefsName SP文件
     */
    public static Map<String, ?> getAll(@NonNull Context context, String prefsName) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        return sp.getAll();
    }

    public static Map<String, ?> getAll(@NonNull Context context) {
        return getAll(context, null);
    }

    /**
     * 注册监听
     *
     * @param context        Context
     * @param prefsName      SP文件
     * @param changeListener OnSharedPreferenceChangeListener
     */
    public static void registerOnSharedPreferenceChangeListener(@NonNull Context context, String prefsName,
                                                                SharedPreferences.OnSharedPreferenceChangeListener changeListener) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        sp.registerOnSharedPreferenceChangeListener(changeListener);
    }

    public static void registerOnSharedPreferenceChangeListener(@NonNull Context context,
                                                                SharedPreferences.OnSharedPreferenceChangeListener changeListener) {
        registerOnSharedPreferenceChangeListener(context, null, changeListener);
    }

    /**
     * 取消监听
     *
     * @param context        Context
     * @param prefsName      SP文件
     * @param changeListener OnSharedPreferenceChangeListener
     */
    public static void unregisterOnSharedPreferenceChangeListener(@NonNull Context context, String prefsName,
                                                                  SharedPreferences.OnSharedPreferenceChangeListener changeListener) {
        SharedPreferences sp = getSharedPreferences(context, prefsName);
        sp.unregisterOnSharedPreferenceChangeListener(changeListener);
    }

    public static void unregisterOnSharedPreferenceChangeListener(@NonNull Context context,
                                                                  SharedPreferences.OnSharedPreferenceChangeListener changeListener) {
        unregisterOnSharedPreferenceChangeListener(context, null, changeListener);
    }
}
