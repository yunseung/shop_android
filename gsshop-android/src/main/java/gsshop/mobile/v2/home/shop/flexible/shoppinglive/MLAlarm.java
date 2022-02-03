package gsshop.mobile.v2.home.shop.flexible.shoppinglive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.pattern.mvc.Model;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.net.URI;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.shop.schedule.TVScheduleBroadAlarmDialogFragment;
import gsshop.mobile.v2.home.shop.schedule.model.BroadAlarmResult;
import gsshop.mobile.v2.menu.BaseTabMenuActivity;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import roboguice.util.Ln;

import static gsshop.mobile.v2.menu.BaseTabMenuActivity.MOBILE_LIVE_NAVI_ID;

public class MLAlarm {

    private static String mPrdID = null;

    //private static MobileLiveAlarmDialogFragment mUpdateDialog = MobileLiveAlarmDialogFragment.newInstance();
    //private static MobileLiveAlarmAgainDialogFragment mAgainDialog = MobileLiveAlarmAgainDialogFragment.newInstance();
    //private static MobileLiveAlarmCancelDialogFragment mCancelDialog = MobileLiveAlarmCancelDialogFragment.newInstance();

    public static void addQuery (Context context, String caller) {
        addQuery(context, BaseTabMenuActivity.MOBILE_LIVE_NAVI_ID, caller);
    }
    /**
     * 조회 쿼리
     * @param context
     * @param naviID
     */
    public static void addQuery (Context context, String naviID, String caller) {
        String url = ServerUrls.getHttpRoot() + ServerUrls.REST.MOBILE_LIVE_ALARM_ADD_QUERY;
        HttpEntity<Object> requestEntity = makeFormDataMobileLive(
                null, null, null, null, null);
        String type = "query";
        setAlarmController(context, url, requestEntity, type, naviID, caller);
    }

    public static void deleteQuery (Context context, String caller) {
        deleteQuery(context, BaseTabMenuActivity.MOBILE_LIVE_NAVI_ID, caller);
    }
    /**
     * 삭제 쿼리
     * @param context
     * @param naviID
     */
    public static void deleteQuery (Context context, String naviID, String caller) {
        String url = ServerUrls.getHttpRoot() + ServerUrls.REST.MOBILE_LIVE_ALARM_DELETE_QUERY;
        HttpEntity<Object> requestEntity = makeFormDataMobileLive(
                null, null, null, null, null);
        String type = "deleteQuery";
        setAlarmController(context, url, requestEntity, type, naviID, caller);
    }

    public static void add (Context context, boolean isNightAlarm, String caller) {
        add(context, isNightAlarm, BaseTabMenuActivity.MOBILE_LIVE_NAVI_ID, caller);
    }
    /**
     * 추가 실행
     * @param context
     * @param isNightAlarm
     * @param naviID
     */
    public static void add (Context context, boolean isNightAlarm, String naviID, String caller) {
        String url = ServerUrls.getHttpRoot() + ServerUrls.REST.MOBILE_LIVE_ALARM_ADD;
        String strNightAlarm = isNightAlarm? "Y" : "N";
        HttpEntity<Object> requestEntity = makeFormDataMobileLive(
                null, null, "999", "99", strNightAlarm);
        setAlarmController(context, url, requestEntity, "add", naviID, caller);
    }

    public static void delete (Context context, String caller) {
        delete(context, BaseTabMenuActivity.MOBILE_LIVE_NAVI_ID, caller);
    }
    /**
     * 삭제 실행
     * @param context
     * @param naviID
     */
    public static void delete(Context context, String naviID, String caller) {
        delete(context, naviID, mPrdID, caller);
    }
    public static void delete (Context context, String naviID, String prdId, String caller) {
        String url = ServerUrls.getHttpRoot() + ServerUrls.REST.MOBILE_LIVE_ALARM_DELETE;
        HttpEntity<Object> requestEntity = makeFormDataMobileLive(
                prdId, null, null, null, null);
        setAlarmController(context, url, requestEntity, "delete", naviID, caller);
    }

    /**
     * 실제 Controller execute 하는 부분
     * @param context
     * @param url
     * @param requestEntity
     * @param type
     * @param naviId 네비 ID null로 들어오면 default : MOBILE_LIVE_NAVI_ID
     */
    private static void setAlarmController(Context context, String url,
                                           HttpEntity<Object> requestEntity, String type, String naviId, String caller) {
        naviId = naviId == null? BaseTabMenuActivity.MOBILE_LIVE_NAVI_ID : naviId;
        new MobileLiveAlarmUpdateController(context, url, requestEntity, type, naviId, caller).execute();
    }

    /**
     * 모바일라이브 방송알림 API 호출용 FormData를 생성한다.
     * @param prdId   상품아이디
     * @param prdName 상품명
     * @param period  기간
     * @param times   횟수
     * @return HttpEntity<Object>
     */
    private static HttpEntity<Object> makeFormDataMobileLive(String prdId, String prdName, String period, String times, String nightAlarm) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Connection", "Close");
        headers.setContentType(MediaType.APPLICATION_JSON);

        BroadAlarmParam param = new BroadAlarmParam();
        param.type = "TITLE";
        param.prdId = prdId;
        param.prdName = prdName;
        param.period = period;
        param.alarmCnt = times;
        param.nightSndPsblYn = nightAlarm;

        return new HttpEntity<>(param, headers);
    }

    /**
     * 방송알림 API 호출을 위한 POST 파라미터
     */
    @Model
    private static class BroadAlarmParam {
        public String type;
        public String prdId;
        public String prdName;
        public String period;
        public String alarmCnt;
        public String nightSndPsblYn;
    }

    /**
     * 모바일라이브 탭매장 신설 방송알림 API 호출
     */
    private static class MobileLiveAlarmUpdateController extends BaseAsyncController<BroadAlarmResult> {
        private final Context context;
        private String url;
        private HttpEntity<Object> requestEntity;
        private String type;
        private String naviId;
        private String caller;

        @Inject
        private RestClient restClient;

        public MobileLiveAlarmUpdateController(Context activityContext, String url,
                                               HttpEntity<Object> entity, String type, String naviId, String caller) {
            super(activityContext);
            this.context = activityContext;
            this.url = url;
            this.requestEntity = entity;
            this.type = type;
            this.naviId = naviId;
            this.caller = caller;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);
            if (dialog != null) {
                dialog.setCancelable(true);
            }
        }

        @Override
        protected BroadAlarmResult process() throws Exception {
            return restClient.postForObject(new URI(url), requestEntity, BroadAlarmResult.class);
        }

        @Override
        protected void onSuccess(BroadAlarmResult result) throws Exception {
            super.onSuccess(result);

            BroadAlarmResult.ALARM_RESULT_TYPE resultType = BroadAlarmResult.ALARM_RESULT_TYPE.valueOf(result.errMsg);
            try {
                switch (resultType) {
                    case MSG_SUCCESS:
                        if ("query".equals(type)) { //조회 성공했을때
                            //SNS 팝업 띄움
                            MobileLiveAlarmDialogFragment mUpdateDialog = new MobileLiveAlarmDialogFragment();
                            mUpdateDialog.setData(caller, result.imgUrl, result.phoneNo, result.infoTextList);
                            mUpdateDialog.show(((FragmentActivity) context).getSupportFragmentManager(), MobileLiveAlarmDialogFragment.class.getSimpleName());

                            //mUpdateDialog.setData(result.imgUrl, result.phoneNo, result.infoTextList).
                            //        show(((FragmentActivity) context).getSupportFragmentManager(), TVScheduleBroadAlarmDialogFragment.class.getSimpleName());
                            break;
                        } else if ("deleteQuery".equals(type)) { //해제조회 성공했을때
                            mPrdID = result.prdId;

                            MobileLiveAlarmCancelDialogFragment mCancelDialog = new MobileLiveAlarmCancelDialogFragment();
                            mCancelDialog.setData(caller, result.imgUrl, result.infoTextList, result.prdId);
                            mCancelDialog.show(((FragmentActivity) context).getSupportFragmentManager(), MobileLiveAlarmCancelDialogFragment.class.getSimpleName());

                            //mCancelDialog.setData(result.imgUrl, result.infoTextList, result.prdId).
                            //        show(((FragmentActivity) context).getSupportFragmentManager(), TVScheduleBroadAlarmDialogFragment.class.getSimpleName());
                            break;
                        } else if ("delete".equals(type)) { //해제확정 성공했을때제
                            //알림해제 UI업데이트
                            EventBus.getDefault().post(new Events.AlarmUpdatetMLEvent(Events.AlarmUpdatetMLEvent.MAKE_ALARM_CANCEL, false, naviId));
                            break;
                        } else if ("add".equals(type)) { //등록확정 성공했을때
                            //ASIS
                            //mAgainDialog.setData(result.imgUrl, result.phoneNo, result.infoTextList)
                            //.show(((FragmentActivity) context).getSupportFragmentManager(), TVScheduleBroadAlarmDialogFragment.class.getSimpleName());

                            MobileLiveAlarmAgainDialogFragment mAgainDialog = new MobileLiveAlarmAgainDialogFragment();
                            mAgainDialog.setData(result.imgUrl, result.phoneNo, result.infoTextList);
                            mAgainDialog.show(((FragmentActivity) context).getSupportFragmentManager(), MobileLiveAlarmAgainDialogFragment.class.getSimpleName());

                            //새로찾은거
                            //mAgainDialog.setData(result.imgUrl, result.phoneNo, result.infoTextList);
                            //((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(mAgainDialog,MobileLiveAlarmAgainDialogFragment.class.getSimpleName()).commitAllowingStateLoss();

                            //알림등록 UI업데이트
                            EventBus.getDefault().post(new Events.AlarmUpdatetMLEvent(Events.AlarmUpdatetMLEvent.MAKE_ALARM_OK, true, naviId));
                            break;
                        }
                    case ERR_DUPLICATE_MOBILELIVE: //이미 방송알림 등록
                        if ("add".equals(type)) {
                            //이미 등록된 알람이니 UI업데이트만
                            EventBus.getDefault().post(new Events.AlarmUpdatetMLEvent(Events.AlarmUpdatetMLEvent.MAKE_ALARM_OK, true, naviId));
                            break;
                        } else if ("query".equals(type)) {
                            //이미 등록된 알람이니 UI업데이트만
                            EventBus.getDefault().post(new Events.AlarmUpdatetMLEvent(Events.AlarmUpdatetMLEvent.MAKE_ALARM_OK, true, naviId));
                            break;
                        }
                    case ERR_NOT_ALARM_MOBILELIVE: //이미 방송알림 해제
                        if ("query".equals(type)) {
                            //시나리오상 발생할수없는데..
                            break;
                        }
                        if ("delete".equals(type)) {
                            //알림해제 디자인으로 UI업데이트만
                            EventBus.getDefault().post(new Events.AlarmUpdatetMLEvent(Events.AlarmUpdatetMLEvent.MAKE_ALARM_CANCEL, false, naviId));
                            break;
                        } else if ("deleteQuery".equals(type)) {
                            //알림해제 디자인으로 UI업데이트만
                            EventBus.getDefault().post(new Events.AlarmUpdatetMLEvent(Events.AlarmUpdatetMLEvent.MAKE_ALARM_CANCEL, false, naviId));
                            break;
                        } else if ("add".equals(type)) {
                            //시나리오상 발생할 수 없는데..
                            break;
                        }

                        break;
                    case ERR_NOT_LOGIN: //로그인 필요
                        Intent intent = new Intent(Keys.ACTION.LOGIN);
                        //로그인 성공하고 돌아올위치가 모바일라이브로 유지되어야 하니까 모바일라이브의 네비아이디가 필요함
                        //이렇게 한 이유 -> 이걸 실행하면 모바일라이브를 다시 호출한다 그래야 다른데서 방송알림 눌렀었다면 돌아왔을때 반영이 되어야 하니까

                        intent.putExtra(Keys.INTENT.WEB_URL, ServerUrls.WEB.MOVE_SHOP_FROM_TABID_URL + MOBILE_LIVE_NAVI_ID);
                        context.startActivity(intent);
                        break;
                    case ERR_PHONE_EMPTY:   //핸드폰 번호 필요
                    case ERR_MAX_ALARM_SIZE:    //방송알림 최대 등록 개수 초과
                    case ERR_SERVER_SAVE_FAIL:  //방송알림 등록 실패
                    case ERR_SERVER_DELETE_FAIL:    //방송알림 취소 실패
                        
                        new CustomOneButtonDialog((Activity) context).message(result.errMsgText).buttonClick(CustomOneButtonDialog.DISMISS).show();
                        Toast.makeText(context, result.errMsgText, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        //정의되지 않은 에러타입인 경우
                        new CustomOneButtonDialog((Activity) context).message(R.string.undefined_error).buttonClick(CustomOneButtonDialog.DISMISS).show();
                        Toast.makeText(context, R.string.undefined_error, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            catch (ClassCastException | NullPointerException e) {
                Ln.e(e.getMessage());
            }
        }

        @Override
        protected void onError(Throwable e) {
            super.onError(e);
        }
    }
}
