/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.tms;

import android.database.Cursor;

import com.gsshop.mocha.pattern.mvc.Model;
import com.tms.sdk.bean.Msg;

/**
 * Created by azota on 2017-01-06.
 */
@Model
public class InboxMsg extends Msg {
    public int type;

    //사용자 등급정보
    public String custWelcome;
    public String custGrade;
    public String orderText;
    public String prdText;
    public String gradeText;
    public String custExpire;
    public String gradeStateImage;
    public String bottomText;
    public String linkUrl;

    public InboxMsg() {
    }

    public InboxMsg(Cursor pCursor) {
        super(pCursor);
    }
}
