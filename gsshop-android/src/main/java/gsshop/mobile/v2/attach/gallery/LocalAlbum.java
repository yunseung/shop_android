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

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Video;
import android.provider.MediaStore.Video.VideoColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * LocalAlbum
 *
 * LocalAlbumSet lists all media items in one bucket on local storage.
 * The media items need to be all images or all videos, but not both.
 */
public class LocalAlbum {
    /**
     * String[] COUNT_PROJECTION : "count(*)" 신기 하네 ;;
     */
    private static final String[] COUNT_PROJECTION = { "count(*)" };

    /**
     * INVALID_COUNT
     */
    private static final int INVALID_COUNT = -1;

    /**
     * mOrderClause
     */
    private final String mOrderClause;

    /**
     * mBaseUri
     */
    private final Uri mBaseUri;

    /**
     * mProjection
     */
    private final String[] mProjection;

    /**
     * mApplication
     */
    private final Application mApplication;

    /**
     * mResolver
     */
    private final ContentResolver mResolver;

    /**
     * mIsImage
     */
    private final boolean mIsImage;

    /**
     * INVALID_COUNT
     */
    private int mCachedCount = INVALID_COUNT;

    /**
     * LocalAlbum
     *
     * @param application
     * @param isImage
     */
    public LocalAlbum(Application application, boolean isImage) {

        mApplication = application;
        mResolver = application.getContentResolver();
        mIsImage = isImage;

        if (isImage) {
        	/*
        	Ln.i("ImageColumns.BUCKET_ID ="+ImageColumns.BUCKET_ID);
        	Ln.i("ImageColumns.DATE_TAKEN ="+ImageColumns.DATE_TAKEN);
        	Ln.i("ImageColumns._ID ="+ImageColumns._ID);
        	Ln.i("Images.Media.EXTERNAL_CONTENT_URI ="+Images.Media.EXTERNAL_CONTENT_URI);
        	Ln.i("VideoColumns.BUCKET_ID ="+ImageColumns.BUCKET_ID);
        	Ln.i("VideoColumns.DATE_TAKEN ="+ImageColumns.DATE_TAKEN);
        	Ln.i("VideoColumns._ID ="+ImageColumns._ID);
        	Ln.i("Video.Media.EXTERNAL_CONTENT_URI ="+Video.Media.EXTERNAL_CONTENT_URI);
            */
        	
            mOrderClause = ImageColumns.DATE_TAKEN + " DESC, "
                    + ImageColumns._ID + " DESC";
            mBaseUri = Images.Media.EXTERNAL_CONTENT_URI;
            mProjection = LocalImage.PROJECTION;
        } else {
            mOrderClause = VideoColumns.DATE_TAKEN + " DESC, "
                    + VideoColumns._ID + " DESC";
            mBaseUri = Video.Media.EXTERNAL_CONTENT_URI;
            mProjection = LocalVideo.PROJECTION;
        }

    }

    /**
     * getMediaItem
     * @return List
     */
    public List<LocalMediaItem> getMediaItem() {
        Uri uri = mBaseUri.buildUpon().build();
        ArrayList<LocalMediaItem> list = new ArrayList<LocalMediaItem>();
        // com.georgiecasey.androidgallerylibrary.provider
        //Ln.i("localalbum getmediaitem URI" + uri);
        Cursor cursor = mResolver.query(
                mBaseUri, mProjection, null,
                null,
                mOrderClause);
        if (cursor == null) {
            //Ln.i("query fail: " + uri);
            return list;
        }

        try {
            while (cursor.moveToNext()) {

                LocalMediaItem item= null;
                if(mIsImage) {
                    item = new LocalImage(cursor);
                } else {
                    item = new LocalVideo(cursor);
                }
                list.add(item);
            }
        } finally {
            cursor.close();
        }
        return list;
    }


}