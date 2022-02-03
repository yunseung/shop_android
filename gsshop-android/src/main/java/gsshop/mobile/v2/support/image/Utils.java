/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gsshop.mobile.v2.support.image;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import com.gsshop.mocha.device.SystemInfo;

import java.io.File;

/**
 * 디스크 캐시용 유틸
 */
public class Utils {
    public static final int IO_BUFFER_SIZE = 8 * 1024;

    private Utils() {
    }

    public static void disableConnectionReuseIfNecessary() {
        if (hasHttpConnectionBug()) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    @SuppressLint("NewApi")
    public static int getBitmapSize(Bitmap bitmap) {
        if (SystemInfo.getSDKVersion() >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    @SuppressLint("NewApi")
    public static boolean isExternalStorageRemovable() {
        if (SystemInfo.getSDKVersion() >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    @SuppressLint("NewApi")
    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }

        final String cacheDir = "/Android/data/" + context.getPackageName()
                + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath()
                + cacheDir);
    }

    @SuppressLint("NewApi")
    public static long getUsableSpace(File path) {
        if (SystemInfo.getSDKVersion() >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    public static int getMemoryClass(Context context) {
        return ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    }

    public static boolean hasHttpConnectionBug() {
        return SystemInfo.getSDKVersion() < Build.VERSION_CODES.FROYO;
    }

    public static boolean hasExternalCacheDir() {
        return SystemInfo.getSDKVersion() >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasActionBar() {
        return SystemInfo.getSDKVersion() >= Build.VERSION_CODES.HONEYCOMB;
    }

	/*@TargetApi(VERSION_CODES.HONEYCOMB)
    public static void enableStrictMode() {
        if (Utils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            if (Utils.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
                //vmPolicyBuilder.setClassInstanceLimit(ImageGridActivity.class, 1).setClassInstanceLimit(ImageDetailActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }*/

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return SystemInfo.getSDKVersion() >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return SystemInfo.getSDKVersion() >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasGingerbreadMR1() {
        return SystemInfo.getSDKVersion() >= Build.VERSION_CODES.GINGERBREAD_MR1;
    }

    public static boolean hasHoneycomb() {
        return SystemInfo.getSDKVersion() >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return SystemInfo.getSDKVersion() >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasIceCreamSandwich() {
        return SystemInfo.getSDKVersion() >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }


    public static boolean hasJellyBean() {
        return SystemInfo.getSDKVersion() >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasJellyBeanMR1() {
        return SystemInfo.getSDKVersion() >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean hasJellyBeanMR2() {
        return SystemInfo.getSDKVersion() >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean hasKitKat() {
        return SystemInfo.getSDKVersion() >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLolliPop() {
        return SystemInfo.getSDKVersion() >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasMarshmallow() {
        return SystemInfo.getSDKVersion() >= 23;
    }

}