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


/**
 * LocalMediaItem
 *
 * LocalMediaItem is an abstract class captures those common fields
 * in LocalImage and LocalVideo.
 */
public abstract class LocalMediaItem {

    /**
     * INVALID_LATLNG
     * NOTE : fix default value for latlng and change this.
     */
    public static final double INVALID_LATLNG = 0f;


    // database fields
    /**
     * id
     */
    public int id;
    /**
     * caption
     */
    public String caption;
    /**
     * mimeType
     */
    public String mimeType;
    /**
     * fileSize
     */
    public long fileSize;
    /**
     * latitude
     */
    public double latitude = INVALID_LATLNG;
    /**
     * longitude
     */
    public double longitude = INVALID_LATLNG;
    /**
     * dateTakenInMs
     */
    public long dateTakenInMs;
    /**
     * dateAddedInSec
     */
    public long dateAddedInSec;
    /**
     * dateModifiedInSec
     */
    public long dateModifiedInSec;
    /**
     * filePath
     */
    public String filePath;
    /**
     * bucketId
     */
    public int bucketId;
    /**
     * bucketDisplayName
     */
    public String bucketDisplayName;

}
