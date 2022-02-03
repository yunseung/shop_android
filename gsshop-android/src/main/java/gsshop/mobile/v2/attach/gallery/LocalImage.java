/*
 * Copyright (C) 2010 The Android Open Source Project
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

package gsshop.mobile.v2.attach.gallery;

import android.database.Cursor;
import android.provider.MediaStore.Images.ImageColumns;

/**
 * LocalImage
 * LocalImage represents an image in the local storage.
 */
public class LocalImage extends LocalMediaItem {

    /**
     * Must preserve order between these indices and the order of the terms in
       the following PROJECTION array.
     */
    private static final int INDEX_ID = 0;
    private static final int INDEX_CAPTION = 1;
    private static final int INDEX_MIME_TYPE = 2;
    private static final int INDEX_LATITUDE = 3;
    private static final int INDEX_LONGITUDE = 4;
    private static final int INDEX_DATE_TAKEN = 5;
    private static final int INDEX_DATE_ADDED = 6;
    private static final int INDEX_DATE_MODIFIED = 7;
    private static final int INDEX_DATA = 8;
    private static final int INDEX_ORIENTATION = 9;
    private static final int INDEX_BUCKET_ID = 10;
    private static final int INDEX_BUCKET_DISPLAY_NAME = 11;
    private static final int INDEX_SIZE_ID = 12;
    private static final int INDEX_WIDTH = 13;
    private static final int INDEX_HEIGHT = 14;

    static final String[] PROJECTION =  {
            ImageColumns._ID,           // 0
            ImageColumns.TITLE,         // 1
            ImageColumns.MIME_TYPE,     // 2
            ImageColumns.LATITUDE,      // 3
            ImageColumns.LONGITUDE,     // 4
            ImageColumns.DATE_TAKEN,    // 5
            ImageColumns.DATE_ADDED,    // 6
            ImageColumns.DATE_MODIFIED, // 7
            ImageColumns.DATA,          // 8
            ImageColumns.ORIENTATION,   // 9
            ImageColumns.BUCKET_ID,     // 10
            ImageColumns.BUCKET_DISPLAY_NAME,     // 11
            ImageColumns.SIZE,          // 12
            // These should be changed to proper names after they are made public.
            "width", // ImageColumns.WIDTH,         // 13
            "height", // ImageColumns.HEIGHT         // 14
    };

    /**
     * rotation
     */
    public int rotation;

    /**
     * width
     */
    public int width;

    /**
     * height
     */
    public int height;

    /**
     * LocalImage cursor
     * @param cursor loadFromCursor(cursor)
     */
    public LocalImage(Cursor cursor) {
        loadFromCursor(cursor);
    }


    /**
     * loadFromCursor
     *
     * @param cursor
     */
    private void loadFromCursor(Cursor cursor) {
        id = cursor.getInt(INDEX_ID);
        caption = cursor.getString(INDEX_CAPTION);
        mimeType = cursor.getString(INDEX_MIME_TYPE);
        latitude = cursor.getDouble(INDEX_LATITUDE);
        longitude = cursor.getDouble(INDEX_LONGITUDE);
        dateTakenInMs = cursor.getLong(INDEX_DATE_TAKEN);
        filePath = cursor.getString(INDEX_DATA);
        rotation = cursor.getInt(INDEX_ORIENTATION);
        bucketId = cursor.getInt(INDEX_BUCKET_ID);
        bucketDisplayName = cursor.getString(INDEX_BUCKET_DISPLAY_NAME);
        fileSize = cursor.getLong(INDEX_SIZE_ID);
        width = cursor.getInt(INDEX_WIDTH);
        height = cursor.getInt(INDEX_HEIGHT);
    }

}
