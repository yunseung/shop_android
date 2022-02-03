/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.tms;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tms.sdk.bean.Msg;
import com.tms.sdk.common.util.DateUtil;

import java.util.ArrayList;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;

/**
 * Created by azota on 2017-01-06.
 */

public class InboxMsgViewHolder extends InboxBaseViewHolder {

    private TextView txtMap1;
    private TextView txtMap2;
    private TextView txtRegDate;
    private FrameLayout layoutPmsImg;
    private ImageView imgPms;
    private LinearLayout layoutPms;

    /**
     * [1]green [2]pink [3]aqua
     */
    private static final String[] TITLE_COLOR = { "#b6cf00", "#ee1f60", "#00aebd" };

    /**
     * colorMapArray
     */
    private int[] colorMapArray;

    /**
     * inactive & date text에 사용
     */
    private int commonTextColor;

    /**
     * msg active 상태의 color
     */
    private int msgTextActiveColor;

    public InboxMsgViewHolder(View v) {
        super(v);
        txtMap1 = (TextView) v.findViewById(R.id.pms_txt_map1);
        txtMap2 = (TextView) v.findViewById(R.id.pms_txt_map2);
        txtRegDate = (TextView) v.findViewById(R.id.pms_txt_reg_date);
        layoutPmsImg = (FrameLayout) v.findViewById(R.id.pms_img_layout);
        imgPms = (ImageView) v.findViewById(R.id.pms_img);
        layoutPms = (LinearLayout) v.findViewById(R.id.layout_pms);
    }

    @Override
    public void onBindViewHolder(final Context context, int position, ArrayList<InboxMsg> mInfo) {

        Resources r = context.getResources();
        colorMapArray = r.getIntArray(R.array.pms_title_color_map);
        commonTextColor = r.getColor(R.color.pms_text_common_color);
        msgTextActiveColor = r.getColor(R.color.pms_text_active_msg_color);

        final Msg msg = mInfo.get(position);

        // title
        txtMap1.setText(msg.pushTitle);
        txtMap1.setTag(msg); // txtMap1에 data tagging

        // msg
        if (DisplayUtils.isValidString(msg.pushMsg)) {
            txtMap2.setText(msg.pushMsg);
        } else {
            txtMap2.setText("");
        }

        // 읽음 여부에 따라 텍스트 컬러 변경
        if ("Y".equalsIgnoreCase(msg.readYn)) {
            txtMap1.setTextColor(commonTextColor);
            txtMap2.setTextColor(commonTextColor);
        } else { // activie
            // icon name을 id로 해서 text color mapping
            txtMap1.setTextColor(getTitleColor(msg.iconName, msg.msgType));
            txtMap2.setTextColor(msgTextActiveColor);
        }

        // date
        txtRegDate.setText(DateUtil.convertDate(msg.regDate, DateUtil.DEFAULT_FORMAT,
                "yyyy.MM.dd aa hh:mm"));

        // 이미지가 있는 push msg이면 이미지 표시
        if (DisplayUtils.isValidString(msg.pushImg)) {
//            imgPms.setImageUrl(msg.pushImg, MainApplication.mImageLoader);
            ImageUtil.loadImageResize(context, msg.pushImg, imgPms, R.drawable.noimg_tv);
            layoutPmsImg.setVisibility(View.VISIBLE);
        } else {
            layoutPmsImg.setVisibility(View.GONE);
        }

        //아이템 클릭시 링크 이동
        layoutPms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InboxActivity)context).onItemClick(msg);
            }
        });
    }

    /**
     * iconName의 숫자값을 color로 mapping. mapping data는 R.array.pms_title_color_map가 가지고 있고<br>
     * 각 index에 해당하는 color값은 MsgAdapter내에 하드코딩함.
     *
     * @param iconName 아이콘 이름
     * @param msgType 메세지 타입
     * @return 지정된 컬럼값
     */
    private int getTitleColor(String iconName, String msgType) {
        // iconName string을 split해서 숫자값을 가져옴
        String[] nameArray = iconName.split("_");
        int iconIndex = DisplayUtils.getNumberFromString(nameArray[nameArray.length - 1]);

        // 숫자를 index로 color array에 사용할 index도 가져오기
        int colorIndex = 0;
        if (iconIndex < colorMapArray.length) {
            colorIndex = colorMapArray[iconIndex];
        }

        // color index가 invalid 하면 첫 번째 color값 사용
        if (colorIndex == 0) {
            colorIndex = 1;
        }

        // color index는 1부터 시작함
        return Color.parseColor(TITLE_COLOR[colorIndex - 1]);
    }
}
