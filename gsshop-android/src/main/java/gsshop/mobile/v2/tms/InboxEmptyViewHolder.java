/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.tms;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by azota on 2017-01-06.
 */

public class InboxEmptyViewHolder extends InboxBaseViewHolder {

    public InboxEmptyViewHolder(View v) {
        super(v);
    }

    @Override
    public void onBindViewHolder(final Context context, int position, ArrayList<InboxMsg> mInfo) {
    }
}
