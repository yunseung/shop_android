/*
 * Copyright (c) 2014 Rex St. John on behalf of AirPair.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package gsshop.mobile.v2.attach;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.gsshop.mocha.pattern.mvc.Model;

/**
 * Used to represent a photo item.
 *
 * Created by Rex St. John (on behalf of AirPair.com) on 3/4/14.
 */
@Model
public class PhotoItem {

	/**
	 * 앨범이미지 - 섬네일
	 */
	public Bitmap thumbnail = null;
	/**
	 * 앨범이미지 전체경로.
	 */
	public Uri fullImageUri = null;
	/**
	 * 앨범이미지 폴더 경로
	 */
	public String pathName = null;
	/**
	 * 상품 이미지
	 */
	public String imageUrl = null;
	/**
	 * thumbnailImageID
	 */
	public int thumbnailImageID;
	/**
	 * 상품코드
	 */
	private String prdCode = "";
	/**
	 * 이미지 구분 코드 (상품이미지/서버에 저장된 이미지를 불러온 경우-P, 앨범이미지/새로 이미지를 선택한 경우-A)
	 */
	public String imgGbCd = "A";

	/**
	 * ImageView iv
	 */
	public ImageView iv;

	/**
	 * sdcard photo
	 *
	 * @param fullImageUri
	 * @param pathName
     */
	public PhotoItem(Uri fullImageUri, String pathName) {
		this.fullImageUri = fullImageUri;
		this.pathName = pathName;
	}

	/**
	 * PhotoItem
	 * thumnail image id
	 *
	 * @param thumbnailImageID
	 * @param fullImageUri
	 * @param pathName
     */
	public PhotoItem(int thumbnailImageID, Uri fullImageUri, String pathName) {
		this.thumbnailImageID = thumbnailImageID;
		this.fullImageUri = fullImageUri;
		this.pathName = pathName;
	}

	/**
	 * PhotoItem
	 * thumbnail bitmap
	 *
	 * @param thumbnail
	 * @param fullImageUri
	 * @param pathName
     */
	public PhotoItem(Bitmap thumbnail, Uri fullImageUri, String pathName) {
		this.thumbnail = thumbnail;
		this.fullImageUri = fullImageUri;
		this.pathName = pathName;
	}

	/**
	 * PhotoItem
	 * imageUrl
	 *
	 * @param imageUrl
	 * @param prdCode
     */
	public PhotoItem(String imageUrl, String prdCode) {
		this.imageUrl = imageUrl;
		this.setPrdCode(prdCode);
	}

	/**
	 * PhotoItem
	 * imageUrl & fullImageUri
	 *
	 * @param imageUrl
	 * @param prdCode
	 * @param fullImageUri
     */
	public PhotoItem(String imageUrl, String prdCode, Uri fullImageUri) {
		this.imageUrl = imageUrl;
		this.setPrdCode(prdCode);
		this.fullImageUri = fullImageUri;
	}
	
	/**
	 * Getters and setters
	 * @param context context
	 * @return Thumbnail
	 */

	public Bitmap getThumbnail(Context context) {
		if (thumbnail == null) {
			thumbnail = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
					thumbnailImageID, MediaStore.Images.Thumbnails.MINI_KIND, null);
		}
		return thumbnail;
	}

	/**
	 * setThumbnail
	 *
	 * @param thumbnail
     */
	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}

	/**
	 * getPrdCode
	 *
	 * @return prdCode
     */
	public String getPrdCode() {
		return prdCode;
	}

	/**
	 * setPrdCode
	 *
	 * @param prdCode
     */
	public void setPrdCode(String prdCode) {
		if (prdCode.length() > 0) {
			// 상품 이미지일경우
			this.imgGbCd = "P";
		}
		this.prdCode = prdCode;
	}

}
