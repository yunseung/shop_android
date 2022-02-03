package gsshop.mobile.v2.home.shop.schedule.model;

import com.gsshop.mocha.pattern.mvc.Model;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.home.shop.schedule_A.ScheduleCate;

/**
 * tv 편셩표 매장 model
 */
@Model
public class TVScheduleModel {

    public String broadType;
    public String refreshYn;
    public String todayYn;
    public String todayDate;
    public String broadDate;
    public String currentTime;
    public String nextBroadTime;

    public List<ScheduleDate> dateList;
    public BroadLink liveBroadInfo;
    public BroadLink dataBroadInfo;
    public BroadLink preBroadInfo;
    public BroadLink shoppyBroadInfo;
    public BroadLink nextBroadInfo;
    public List<ScheduleTimeLineThumb> timeLineList;
    public List<SchedulePrd> broadPrdList;
    public ArrayList<ScheduleCate> cateList;


    /**
     * date
     */
    @Model
    public static final class ScheduleDate {
        public String year;
        public String month;
        public String day;
        public String week;
        public String wiseLogUrl;
        public String yyyyMMdd;
        public String todayYn;
        public String selectedYn;
        public String apiUrl;
        public String apiParm;

        @Override
        public boolean equals(Object o) {
            if(o == null) {
                return false;
            }

            ScheduleDate date = (ScheduleDate) o;
            return month.equals(date.month) && day.equals(date.day) && week.equals(date.week) && todayYn.equals(date.todayYn);
        }
    }


    /**
     * broad link
     */
    @Model
    public static final class BroadLink {
        public String apiUrl;
        public String apiParam;
        public String broadLeftText;
        public String broadRightText;
    }

    /**
     * time line event
     */
    @Model
    public static final class ScheduleTimeLineThumb {
        public String viewType;
        public int pgmId;
        public String broadStartDate;
        public String broadEndDate;
        public String startTime;
        public String specialPgmYn;
        public String specialPgmInfo;
        public String publicBroadYn;
        public String pgmLiveYn;
        public ScheduleTimeLineProduct product;

        public String apiUrl;
        public String apiParam;
    }

    /**
     * time line product
     */
    @Model
    public static final class ScheduleTimeLineProduct {
        public String prdId;
        public String prdName;
        public String subPrdImgUrl;

        /**
         * TV편성표AB2차 때문에 추가됨
         */
        public String brandNm;
    }
}
