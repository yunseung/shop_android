/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.tms;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

/**
 * Created by azota on 2017-01-06.
 */

public class InboxGradeViewHolder extends InboxBaseViewHolder {

    private TextView txtGradeTitle;
    private TextView txtGradeOrder;
    private TextView txtGradeReview;
    private TextView txtGradeComment;
    private TextView txtGradeDuedate;
    private TextView txtGradeMore;

    private ImageView ivBackGrade;
    private ImageView ivGradeArrow;

    /**
     * 등급이 없는 경우 기본 폰트색
     */
    private static final String DEFAULT_COLOR = "#111111";

    /**
     * 등급 및 폰트색 정의
     */
    public enum CUSTOMER_GRADE {
        무등급(DEFAULT_COLOR),
        브론즈("#de7c3e"),
        실버("#666666"),
        골드("#f4b400"),
        VIP("#6d43b6"),
        VVIP("#2a1f19");

        private String color = "";

        CUSTOMER_GRADE(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

    public InboxGradeViewHolder(View v) {
        super(v);
        txtGradeTitle = (TextView) v.findViewById(R.id.txt_grade_title);
        txtGradeOrder = (TextView) v.findViewById(R.id.txt_grade_order);
        txtGradeReview = (TextView) v.findViewById(R.id.txt_grade_review);
        txtGradeComment = (TextView) v.findViewById(R.id.txt_grade_comment);
        txtGradeDuedate = (TextView) v.findViewById(R.id.txt_grade_duedate);
        txtGradeMore = (TextView) v.findViewById(R.id.txt_grade_more);

        ivBackGrade = (ImageView) v.findViewById(R.id.iv_back_grade);
        ivGradeArrow = (ImageView) v.findViewById(R.id.iv_grade_arrow);

        //중앙 화살표 이미지는 패드에서 너무 작게 보이므로 스케일링 해서 보여준다.
        DisplayUtils.resizeAtViewToScreen(MainApplication.getAppContext(), ivGradeArrow);
    }

    @Override
    public void onBindViewHolder(final Context context, int position, ArrayList<InboxMsg> mInfo) {
        final InboxMsg inboxGrade = mInfo.get(position);

        String title = inboxGrade.custWelcome;
        SpannableStringBuilder sBuilder = new SpannableStringBuilder();
        sBuilder.append(title);

        String grade = inboxGrade.custGrade;
        if (!TextUtils.isEmpty(grade)) {
            int sIdx = title.indexOf(grade);
            int eIdx = sIdx + grade.length();
            if (sIdx != -1) {
                //title에 일치하는 grade가 있는 경우만 볼드 및 색변경 처리
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor(getGradeInfo(grade).color));
                sBuilder.setSpan(new StyleSpan(Typeface.BOLD), sIdx, eIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                sBuilder.setSpan(colorSpan, sIdx, eIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        txtGradeTitle.setText(sBuilder);
        if (!TextUtils.isEmpty(inboxGrade.orderText)) {
            txtGradeOrder.setText(inboxGrade.orderText);
        } else {
            txtGradeOrder.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(inboxGrade.prdText)) {
            txtGradeReview.setText(inboxGrade.prdText);
        } else {
            txtGradeReview.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(inboxGrade.gradeText)) {
            txtGradeComment.setText(inboxGrade.gradeText);
        } else {
            txtGradeComment.setVisibility(View.GONE);
        }
        txtGradeDuedate.setText(inboxGrade.custExpire);
        ivGradeArrow.setImageResource(getGradeInfo(grade).resourceId);
        txtGradeMore.setText(inboxGrade.bottomText);

        //등급이미지 로딩
        if (DisplayUtils.isValidString(inboxGrade.gradeStateImage)) {
            ImageUtil.loadImageResize(context, inboxGrade.gradeStateImage, ivBackGrade, R.drawable.inbox_grade_default);
        }

        //리얼멤버쉽 자세히 보기
        txtGradeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).finish();
                WebUtils.goWeb(context, inboxGrade.linkUrl);
            }
        });
    }

    /**
     * 등급별 텍스트색과 화살표이미지 리소스를 반환한다.
     *
     * @param grade 등급
     * @return GradeInfo
     */
    private GradeInfo getGradeInfo(String grade) {
        GradeInfo gradeInfo = new GradeInfo();
        CUSTOMER_GRADE customerGrade;
        try {
            if (TextUtils.isEmpty(grade)) {
                throw new IllegalArgumentException();
            }
            customerGrade = CUSTOMER_GRADE.valueOf(grade.toUpperCase());
        }catch (IllegalArgumentException e) {
            //정의되지 않은 등급정보가 내려올 경우 디폴트정보 사용
            gradeInfo.color = DEFAULT_COLOR;
            gradeInfo.resourceId = R.drawable.inbox_grade_arrow_default;
            return gradeInfo;
        }

        switch (customerGrade) {
            case 무등급:
                gradeInfo.color = customerGrade.getColor();
                gradeInfo.resourceId = R.drawable.inbox_grade_arrow_nograde;
                break;
            case 브론즈:
                gradeInfo.color = customerGrade.getColor();
                gradeInfo.resourceId = R.drawable.inbox_grade_arrow_bronze;
                break;
            case 실버:
                gradeInfo.color = customerGrade.getColor();
                gradeInfo.resourceId = R.drawable.inbox_grade_arrow_silver;
                break;
            case 골드:
                gradeInfo.color = customerGrade.getColor();
                gradeInfo.resourceId = R.drawable.inbox_grade_arrow_gold;
                break;
            case VIP:
                gradeInfo.color = customerGrade.getColor();
                gradeInfo.resourceId = R.drawable.inbox_grade_arrow_vip;
                break;
            case VVIP:
                gradeInfo.color = customerGrade.getColor();
                gradeInfo.resourceId = R.drawable.inbox_grade_arrow_vvip;
                break;
        }

        return gradeInfo;
    }

    /**
     * 등급별 텍스트색과 화살표이미지 리소스 정보 저장
     */
    private class GradeInfo {
        /**
         * 등급 텍스트색
         */
        public String color;

        /**
         * 등급 화살표 이미지 리소스
         */
        public int resourceId;
    }
}
