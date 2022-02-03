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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.os.Environment;

import java.io.File;

public class ImageCache {
    private static final String TAG = "ImageCache";

    /**
     * oops 에서 확인한 내용
     * Null Pointer에 대한 예외처리 추가 07/26일 반영 08/04 일 배포 예정
     * @param context
     * @param uniqueName
     * @return
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        try {
            cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !isExternalStorageRemovable() || getExternalCacheDir(context) != null ? getExternalCacheDir(context).getPath() : context.getCacheDir().getPath();

            if (cachePath != null) {
                return new File(cachePath + File.separator + uniqueName);
            }
        }catch(Exception e){
        }
        return null;
    }


    @TargetApi(VERSION_CODES.GINGERBREAD)
    public static boolean isExternalStorageRemovable() {
        if (Utils.hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    @TargetApi(VERSION_CODES.FROYO)
    public static File getExternalCacheDir(Context context) {
        if (Utils.hasFroyo()) {
            return context.getExternalCacheDir();
        }

        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }


}
