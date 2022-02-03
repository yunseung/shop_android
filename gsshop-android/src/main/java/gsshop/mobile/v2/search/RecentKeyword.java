/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.search;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.annotation.ParametersAreNullableByDefault;

import gsshop.mobile.v2.R;

/**
 * 최근 검색어.
 *
 * keyword가 같으면 동일한 것으로 간주.
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class RecentKeyword {

    public String keyword;

    public InputType type = InputType.DIRECT;

    public RecentKeyword() {
    }

    public RecentKeyword(String keyword, InputType type) {
        this.keyword = keyword;
        this.type = type;
    }

    /**
     * 최근 검색어 종류.
     *
     */
    public static enum InputType {
        /**
         * 직접 검색
         */
        DIRECT(R.drawable.search_type_direct),
        /**
         * 음성 검색
         */
        VOICE(R.drawable.search_type_voice),
        /**
         * QR코드 검색
         */
        QRCODE(R.drawable.search_type_qrcode);

        /**
         * 종류를 나타내는 아이콘 리소스.
         */
        private int icon;

        InputType(int iconRes) {
            this.icon = iconRes;
        }
    }

    // NOTE : hashCode(), equals() 지우지 말것. List에서 비교에 사용됨.
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        RecentKeyword other = (RecentKeyword) obj;
        if (keyword == null) {
            if (other.keyword != null) {
                return false;
            }
        } else if (!keyword.equals(other.keyword)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "RecentKeyword [keyword=" + keyword + ", type=" + type + "]";
    }

}
