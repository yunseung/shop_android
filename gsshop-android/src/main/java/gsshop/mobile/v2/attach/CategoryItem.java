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

import android.graphics.Bitmap;
import android.net.Uri;

import com.gsshop.mocha.pattern.mvc.Model;

/**
 * Used to represent a photo item.
 *
 * Created by Rex St. John (on behalf of AirPair.com) on 3/4/14.
 */
@Model
public class CategoryItem extends PhotoItem {

	/**
	 * count
	 */
	public int count;
	/**
	 * pathName
	 */
	public String pathName;

	/**
	 * CategoryItem
	 * @param thumbnail
	 * @param fullImageUri
	 * @param name
     * @param cnt
     */
	public CategoryItem(Bitmap thumbnail, Uri fullImageUri, String name, int cnt) {
		super(thumbnail, fullImageUri, name);
		// NOTE Auto-generated constructor stub
		pathName = name;
		count = cnt;
	}

}
