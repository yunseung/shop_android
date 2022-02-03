/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.gssuper;

import android.os.Parcel;
import android.os.Parcelable;

import com.gsshop.mocha.pattern.mvc.Model;

/**
 * 매장팝업 정보
 */
@Model
public class GSSuperPopupProductsInfo implements Parcelable {
    GSSuperPopupProductsInfo(String imageUrl, String linkUrl) {
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
    }
    /**
     * 이미지 주소
     */
    public String imageUrl;

    /**
     * 링크 주소
     */
    public String linkUrl;

    protected GSSuperPopupProductsInfo(Parcel in) {
        imageUrl = in.readString();
        linkUrl = in.readString();
    }

    public static final Creator<GSSuperPopupProductsInfo> CREATOR = new Creator<GSSuperPopupProductsInfo>() {
        @Override
        public GSSuperPopupProductsInfo createFromParcel(Parcel in) {
            return new GSSuperPopupProductsInfo(in);
        }

        @Override
        public GSSuperPopupProductsInfo[] newArray(int size) {
            return new GSSuperPopupProductsInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageUrl);
        dest.writeString(linkUrl);
    }
}
