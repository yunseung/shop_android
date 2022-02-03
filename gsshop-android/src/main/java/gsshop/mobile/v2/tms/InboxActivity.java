/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.tms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.tms.sdk.TMS;
import com.tms.sdk.api.request.NewMsg;
import com.tms.sdk.api.request.ReadMsg;
import com.tms.sdk.bean.APIResult;
import com.tms.sdk.bean.Msg;
import com.tms.sdk.common.util.TMSUtil;

import org.json.JSONArray;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.menu.RunningActivityManager;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.ServerUrls.REST.CUSTOMER_GRADE;

/**
 * TMS SDK 커스텀 마이징이 가능하도록 풀어서 프로젝트에 넣은 로직
 * 수정은 가능하지만 애매한 부분이 있으면 휴머스온에 문의가 필요 할수 있다.
 */
public class InboxActivity extends AbstractBaseActivity{

    /**
     * 컨텍스트
     */
    private Context mContext;

    /**
     * tms
     */
    private TMS tms;

    /**
     * tmsApi
     */
    private TMSApi TMSApi;

    /**
     * 읽지않은 메시지 수
     */
    private int tmsNewMsgCnt = 0;

    /**
     * 외부링크로 이동할때 비교할 Host
     */
    private final static String EXTERNAL_HEADER = "http";

    /**
     * tms 커서
     */
    private Cursor pCursor;

    /**
     * 상단 가이드 문구 guideText
     */
    private final String[] guideText = new String[2];

    /**
     * 상단 가이드 문구 switcher
     */
    private TextSwitcher switcher;

    /**
     * 공통 인덱스
     */
    private int gIndex = 0;

    /**
     * 리사이클러뷰
     */
    private RecyclerView mRecyclerView;

    /**
     * 리사이클러뷰 어댑터
     */
    private InboxAdapter mAdapter;

    /**
     * 리사이클러뷰에 노출할 데이타 배열
     */
    private ArrayList<InboxMsg> inboxMsgs = new ArrayList<InboxMsg>();

    /**
     * 등급정보 저장변수
     */
    private InboxMsg inboxMsg = null;

    /**
     * 뷰타입 정의 (등급영역)
     */
    public static final int VIEWHOLDER_TYPE_GRADE = 1;

    /**
     * 뷰타입 (메시지영역)
     */
    public static final int VIEWHOLDER_TYPE_MSG = 2;

    /**
     * 뷰타입 (메시지없음 알림 영역)
     */
    public static final int VIEWHOLDER_TYPE_EMPTY = 3;

    /**
     * 본화면 최초 로딩시에 발생하는 tms.RECEIVER_REQUERY 브로드케스트를 생략하기 위한 플래그
     * 왜냐하면 최초 로딩시에는 newMsg()를 통해 동일한 로직이 수행되기 때문 (중복수행 방지)
     */
    private boolean isSkipRequery = true;

    /**
     * 쇼핑알림 onCreate
     * 나타날때 효과
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pms_inbox_activity);
        setHeaderView(R.string.pms_title, true);

        mContext = this;

        mRecyclerView = (RecyclerView) findViewById(R.id.pms_lst);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        switcher = (TextSwitcher) findViewById(R.id.switcher);

        RunningActivityManager.addActivity(this);

        tms = TMS.getInstance(mContext);
        TMSApi = new TMSApi(mContext);
        registReceiver();

        //상단 가이드문구 교체 관련
        guideText[0] = getString(R.string.pms_notice2);
        guideText[1] = getString(R.string.pms_notice3);
        switcher.setFactory(mFactory);
        switcher.setInAnimation(this, android.R.anim.fade_in);
        switcher.setOutAnimation(this, android.R.anim.fade_out);
        switcher.setText(guideText[gIndex]);

        new InboxGradeController(this).execute();
    }

    /**
     * tms API를 통해 가져온 메시지를 로컬DB에 저장하고
     * 로컬DB에서 데이타를 읽어 어댑터를 세팅한다.
     *
     * @param grade 등급정보
     */
    private void newMsg(final InboxMsg grade) {
        inboxMsg = grade;

//        PushInfo info = getPushInfo();

        TMSApi.newMsg(new NewMsg.Callback() {
            @Override
            public void response(APIResult apiResult, ArrayList<Msg> arrayList) {
                tmsNewMsgCnt = tms.selectNewMsgCnt();
                getMultiMsgList(inboxMsg);
                mAdapter = new InboxAdapter(InboxActivity.this);
                mAdapter.setInfo(inboxMsgs);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    /**
     * 로컬DB에서 데이타를 읽어 어뎁터에서 사용할 데이타를 생성한다.
     * 이때, 어뎁터용 데이타의 첫번째 인덱스에는 등급정보를 세팅한다.
     *
     * @param grade 등급정보
     */
    private void getMultiMsgList(InboxMsg grade) {
        inboxMsgs.clear();

        //tms Cursor
        pCursor = tms.selectMsgList(1, 9999);
        if (pCursor != null && pCursor.getCount() != 0) {
            if (pCursor.moveToFirst()) {
                do {
                    InboxMsg inboxMsg = new InboxMsg(pCursor);
                    inboxMsg.type = VIEWHOLDER_TYPE_MSG;
                    inboxMsgs.add(inboxMsg);

                } while (pCursor.moveToNext());
            }
        } else {
            //tms메시지가 없는 경우 "최근에 받은알림이 없습..." 표시
            InboxMsg inboxMsg = new InboxMsg();
            inboxMsg.type = VIEWHOLDER_TYPE_EMPTY;
            inboxMsgs.add(inboxMsg);
        }

        //10/12 null체크 이민수
        if( pCursor != null ) {
            pCursor.close();
        }

        Collections.sort(inboxMsgs, regdateComparator);
        Collections.reverse(inboxMsgs);

        //등급정보 배열 첫번째 인덱스에 세팅
        //custWelcome 값이 없는 경우 등급영역을 그리지 않음
        if (grade != null
                && !TextUtils.isEmpty(grade.custWelcome)) {
            inboxMsgs.add(0, grade);
        }
    }

    /**
     * 최근 등록 날짜 순으로 정령하기 위한 비교자
     */
    private final static Comparator<Msg> regdateComparator = new Comparator<Msg>() {
        private final Collator collator = Collator.getInstance();
        /**
         *
         * @param obj1 obj1
         * @param obj2 obj1
         * @return compare 비교 결과
         */
        @Override
        public int compare(Msg obj1, Msg obj2) {
            return collator.compare(obj1.regDate, obj2.regDate);
        }
    };

    /**
     * regist receiver
     */
    private void registReceiver() {
        this.mContext.registerReceiver(pushReceiver, new IntentFilter(TMS.RECEIVER_PUSH));
        this.mContext.registerReceiver(requeryReceiver, new IntentFilter(TMS.RECEIVER_REQUERY));
        this.mContext.registerReceiver(closeMsgBoxReceiver, new IntentFilter(TMS.RECEIVER_CLOSE_INBOX));
    }

    /**
     * clean receiver
     */
    private void cleanReceiver() {
        this.mContext.unregisterReceiver(pushReceiver);
        this.mContext.unregisterReceiver(requeryReceiver);
        this.mContext.unregisterReceiver(closeMsgBoxReceiver);
    }

    /**
     * push receiever
     */
    private final BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        /**
         * BroadcastReceiver onReceive()
         * @param context context
         * @param intent intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
//            PushInfo info = getPushInfo();
            TMSApi.newMsg((apiResult, arrayList) -> {
//                Ln.d("hklim newMsg result : " + apiResult.toString());
            });
        }
    };

    /**
     * requert receiver
     */
    private final BroadcastReceiver requeryReceiver = new BroadcastReceiver() {

        /**
         * BroadcastReceiver onReceive
         * @param context context
         * @param intent intent
         */
        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isSkipRequery) {
                getMultiMsgList(inboxMsg);
                tmsNewMsgCnt = tms.selectNewMsgCnt();
                mAdapter.notifyDataSetChanged();
            }
            isSkipRequery = false;
        }
    };

    private final BroadcastReceiver closeMsgBoxReceiver = new BroadcastReceiver() {
        /**
         * BroadcastReceiver onReceive
         *
         * @param context context
         * @param intent intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    /**
     * 리스트뷰 아이템 클릭시 읽음 처리 후 해당 링크로 이동한다.
     *
     * @param msg 메시지객체
     */
    // 쓰는 데가 없음.

    public void onItemClick(Msg msg) {
        // 읽음처리
        if (!"Y".equalsIgnoreCase(msg.readYn)) {

            new ReadMsg(mContext).request(msg.msgId, apiResult -> {
                //Ln.d("hklim ReadMsg 2 result : " + apiResult.toString());
            });

            // 읽음확인 처리를 해도 tms 내부에서 new msg count가 변하는데 시간이 걸려서
            // activity단에서 따로 new count 처리를 함
            if (tmsNewMsgCnt > 0) {
                --tmsNewMsgCnt;

                // 리스트의 모든 msg를 확인했으면 notificatio icon 숨기기
//                if (tmsNewMsgCnt == 0) {
//                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                    notificationManager.cancel(PushReceiver.NOTIFICATION_ID);
//                }
            }
        }

        if (msg.appLink != null
                && msg.appLink.startsWith(EXTERNAL_HEADER)) {
            //외부링크("http")로 띄워야 하는 경우
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(msg.appLink));
            startActivity(intent);
        } else {
            // native 영역에서 appLink로 이동할 때는 scheme 빼고 url만 사용
            String url = Uri.parse(msg.appLink).getEncodedQuery();
            Intent intent = new Intent();
            intent.putExtra(Keys.INTENT.CAN_CAUSE_REDIRECTING, true);
            intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.MYSHOP);
            WebUtils.goWeb(this, url, intent, false, false);
        }

        finish();
    }

    /**
     * onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * onPause
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * finish
     */
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * onDestroy
     */
    @Override
    protected void onDestroy() {
        cleanReceiver();
        super.onDestroy();
    }

    /**
     * TextSwitcher에서 사용할 TextView를 생성한다.
     */
    private final ViewFactory mFactory = new ViewFactory() {

        /**
         * ViewFactory makeView
         * @return view
         */
        @Override
        public View makeView() {
            TextView t = new TextView(InboxActivity.this);
            t.setTextAppearance(InboxActivity.this, R.style.InboxGuideText);
            return t;
        }
    };

    /**
     * getPushInfo
     * @return PushInfo
     */
    /*
    private PushInfo getPushInfo(){
        PushInfo info = new PushInfo();
        Prefs pref = new Prefs(mContext);

        if(pref.getBoolean("pref_app_first")) {
            info.setMaxUserMsgId(pref.getString(TMS.PREF_MAX_USER_MSG_ID));
            info.setType(TMS.TYPE_NEXT);
        }else{
            info.setMaxUserMsgId("-1");
            info.setType(TMS.TYPE_PREV);
            pref.putBoolean("pref_app_first", true);
        }

        return info;
    }
    */
    /**
     * 푸시정보 이너 클래스
     */
    public class PushInfo {
        private String maxUserMsgId;
        private String type;
        /**
         * @return the maxUserMsgId
         */
        public String getMaxUserMsgId() {
            return maxUserMsgId;
        }
        /**
         * @param maxUserMsgId the maxUserMsgId to set
         */
        public void setMaxUserMsgId(String maxUserMsgId) {
            this.maxUserMsgId = maxUserMsgId;
        }
        /**
         * @return the type
         */
        public String getType() {
            return type;
        }
        /**
         * @param type the type to set
         */
        public void setType(String type) {
            this.type = type;
        }

    }

    /**
     * 사용자 등급정보를 가져온다.
     */
    public class InboxGradeController extends BaseAsyncController<InboxMsg> {
        @Inject
        private RestClient restClient;

        public InboxGradeController(Context context) {
            super(context);
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);
        }

        @Override
        protected InboxMsg process() throws Exception {
            String catvId = User.getCachedUser() != null ? User.getCachedUser().customerNumber : "";
            return (InboxMsg) DataUtil.getData(context, restClient, InboxMsg.class,
                    false, false, ServerUrls.getHttpRoot() + CUSTOMER_GRADE + "?catvId=" + catvId);
        }

        @Override
        protected void onSuccess(final InboxMsg result) throws Exception {
            if (isNotEmpty(result)) {
                result.type = VIEWHOLDER_TYPE_GRADE;
            }
            newMsg(result);
        }

        @Override
        protected void onError(Throwable e) {
            newMsg(null);
            Ln.e(e);
        }
    }
}