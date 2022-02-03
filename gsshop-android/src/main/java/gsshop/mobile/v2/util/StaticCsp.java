/*
 * Copyright(C) 2013 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util;

import android.content.Context;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.support.wiselog.WiseLogAction;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.web.WebUtils;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
import roboguice.RoboGuice;
import roboguice.util.Ln;

public class StaticCsp {

	/**
	 * join emit 나 이탭을 봤다 !
	 */
	public static final String EMIT_JOIN = "join";
	/**
	 * activity emit (클릭 또는 뷰 효율을 위해서)
	 */
	public static final String EMIT_ACTIVITY = "activity";

	/**
	 * 소켓 이벤트 콜백을 받기위한 이벤트명 message
	 */
	public static final String SOCKET_EVENT_MESSAGE = "message";
	/**
	 * 소켓 이벤트 콜백을 받기위한 이벤트명 banner
	 */
	public static final String SOCKET_EVENT_BANNER = "banner";

	/**
	 * OBJECT(던져질 Json) 탭정보
	 */
	public static final String OBJECT_TAB = "P";
	/**
	 * OBJECT(던져질 Json) 캠페인의 각 페이즈 번호 ( 내려줄때마다 다름 )
	 */
	public static final String OBJECT_AID = "AID";
	/**
	 * OBJECT(던져질 Json) C / V (클릭 또는 보여줌 구분)
	 */
	public static final String OBJECT_MSEQ = "TP";

	/**
	 * OBJECT_MSEQ_VIEW TP = V 뷰
	 */
	public static final String OBJECT_MSEQ_VIEW = "V";
	/**
	 * OBJECT_MSEQ_VIEW TP = C 클릭
	 */
	public static final String OBJECT_MSEQ_CLICK = "C";

	public boolean isServiceVisible = false;

	private Context mContext;

	private Handler mHandler;

	private FrameLayout layoutCsp;
	private ImageButton btnCsp;
	private TextView textview_csp;

	public static final int CLOSE_MESSAGE = 1000;
	public static final int CLOSEALL_MESSAGE = 2000;

	public static final int MESSAGE_SHOW_TIME = 7000;


	/**
	 * 컨런트 데이터 하나
	 */
	private JSONObject mQJSONObject = null;

	/**
	 * 커런트 데이터 유무
	 * 항시 큐를 체크해야 되는데 귀찮다.
	 */
	private boolean mQFlag = false;

	/**
	 * - 글로벌로 푸사, 특정 탭 본것
	 * - 서버에서 이사람의 current 이벤트가 달라졌다면 처리 해줘야 한다. 나는 나갔다를 명확하게 할수 없다.
	 * - 다른 이벤트가 곧 다른 행위를 햇다이다 들어왔다<->나갔다가 한씬이 아니다
	 */
	private Socket mGlobalSocket;

	/**
	 *  소켓 케넥션 여부
	 */
	private boolean mgbSocketConnect = false;

	/**
	 * 끊어져 있으면 false
	 * 연결되어 있으면 true
	 * @return 연결 유 상태
	 */
	public boolean isSocketConnect()
	{
		return mgbSocketConnect;
	}


	/**
	 * 현재 보관하고 있는 message 타입 큐 커런트 데이터
	 */
	private CspMessageModel mCurrentMessageData;

	/**
	 * 현재 보관하고 있는 banner 타입 큐 커런트 데이터
	 */
	private CspBannerModel mCurrentBannerData;

	/**
	 *  케넥트 할경우에 쓰기될 현재 보고 있는 탭 아이디
	 */
	private String mCurrentTabId;

	private WiseLogAction wiseLog;

	/**
	 * StaticCsp 인스턴스 저장용
	 */
	private static StaticCsp mStaticCsp;

	public boolean isServiceVisible()
	{
		return isServiceVisible;
	}
	public void setServiceVisible(boolean visible)
	{
		isServiceVisible = visible;
	}

	/**
	 * 인스턴스를 생성 또는 반환한다.
	 *
	 * @param context 컨텍스트
	 * @param wiseLog 와이즈로그
	 * @param handler 핸들러
	 * @param layoutCsp 레이아웃
	 * @param btnCsp 버튼
	 * @param textview_csp 텍스트뷰
	 * @return StaticCsp
	 */
	public static StaticCsp getInstance(Context context, WiseLogAction wiseLog, Handler handler, FrameLayout layoutCsp, ImageButton btnCsp, TextView textview_csp) {
		if (mStaticCsp == null) {
			mStaticCsp = new StaticCsp(context, wiseLog, handler, layoutCsp, btnCsp, textview_csp);
		}
		return mStaticCsp;
	}

	/**
	 * 인스턴스를 반환한다.
	 *
	 * @return StaticCsp
	 */
	public static StaticCsp getInstance() {
		return mStaticCsp;
	}

	/**
	 * mCurrentData / mCurrentTabId 생성
	 */
	public StaticCsp(Context context, WiseLogAction wiseLog, Handler handler, FrameLayout layoutCsp, ImageButton btnCsp, TextView textview_csp){

		RoboGuice.getInjector(context).injectMembers(this);

		this.mContext = context;

		this.mHandler = handler;

		this.wiseLog = wiseLog;

		//message Data
		this.mCurrentMessageData = new CspMessageModel();

		//banner Data
		this.mCurrentBannerData = new CspBannerModel();

		this.mCurrentTabId = "";
		this.mQJSONObject = null;
		this.mQFlag = false;

		//View
		this.layoutCsp = layoutCsp;
		this.btnCsp = btnCsp;
		this.textview_csp = textview_csp;
	}

	public void setCurrentTabId(String tabId)
	{
		mCurrentTabId = tabId;

	}
	public String getCurrentTabId()
	{
		return mCurrentTabId;
	}

	public void messagePush(String tab)
	{
		//Log.e("MSLEE",  "messagePush : tab " + tab);
		JSONObject data = new JSONObject();
		try{
			data.put(OBJECT_TAB, tab);
			mQJSONObject = data;
			mQFlag = true;
		}catch (Exception e)
		{

		}

	}

    /**
     * CSP 뷰홀더에 이벤트 전달
     * 뷰홀더는 생성이후에 이벤트를 전달받을 수 있기 때문에 딜레이를 줌
     *
     * @param cspEvent CspEvent
     */
	private void sendEvent(Events.CspEvent cspEvent) {
		ThreadUtils.INSTANCE.runInUiThread(() -> EventBus.getDefault().postSticky(cspEvent), 500);
	}

	public void messagePop(String tab)
	{
		//Log.e("MSLEE",  "messagePop : tab " + tab);
		JSONObject data = new JSONObject();
		try {

			String custNo = User.getCachedUser() != null ? User.getCachedUser().customerNumber : "";
			String pcId = CookieUtils.getPcId(mContext);

			//고객 번호가 비어져 있으면
			if(custNo.isEmpty())
			{
				//고객번호 대신 pcid ex) 15264500165345248215353
				custNo = pcId;
				//그것도 없으면 ex) ****
				if(custNo.isEmpty())
					custNo = "****";
			}

			mHandler.removeMessages(StaticCsp.CLOSEALL_MESSAGE);
			mHandler.sendEmptyMessageDelayed(CLOSEALL_MESSAGE,0);
			data.put(OBJECT_TAB, tab);

			if(mgbSocketConnect) {
				//Log.e("MSLEE", "messagePop true");
				try{
					mGlobalSocket.emit(EMIT_JOIN,data);
				}catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else{
				//연결되어 잇지 않으면 큐에 넣어 둔다.
				//Log.e("MSLEE","messagePop false");
				mQJSONObject = data;
				mQFlag = true;
			}

		} catch(JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 소켓 초기화
	 */
	public void initSocket()
	{
		//Log.e("MSLEE","initSocket " + mCurrentTabId );
		//globalSocketOff();
		try {

			//http://cspdev.innolab.us/global?A=GSSHOP&U=7534298&P=HOME&S=10
			//- A : GSSHOP ( 고정 )
			//- S : 10 ( 고정 )
			// - U : 고객번호
			// - P : 현재 TAB 위치. (영문)

			String custNo = User.getCachedUser() != null ? User.getCachedUser().customerNumber : "";
			String pcId = CookieUtils.getPcId(mContext);

			//고객 번호가 비어져 있으면
			if(custNo.isEmpty())
			{
				//고객번호 대신 pcid ex) 15264500165345248215353
				custNo = pcId;
				//그것도 없으면 ex) ****
				if(custNo.isEmpty())
					custNo = "";
			}

			IO.Options opts = new IO.Options();
			opts.forceNew = true;
			opts.reconnection = false;
			//이걸빼면 SSL안된다
			opts.transports = new String[]{WebSocket.NAME};

			// 이것좀 정리 해야 하는데.. opts.query ? 처리를 하지 못한다
			//opts.query = "A=zeroapp&U=ceo01&D=web&C=sockettest&S=10";
			opts.query = "A=GSSHOP&U="+custNo +"&P="+ mCurrentTabId + "&S=10" + "&V=" + "1.0";
			String url = ServerUrls.WS.CSP_SERVICE_URL;
			//Log.e("MSLEE", "URL :" + url +"?"+ opts.query);

			mGlobalSocket = IO.socket(url,opts);
			mGlobalSocket.connect();
			globalSocketOn();

		} catch(URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 소켓 종료
	 * homeActiviy 가 pause 또는 부서졌을대.
	 */
	public void globalSocketOff()
	{
		//Log.e("MSLEE","globalSocketOff");
		if(mGlobalSocket != null)
		{
			//Log.e("MSLEE","globalSocketOff SUCC");
			mGlobalSocket.off();
			mGlobalSocket.close();
			mGlobalSocket=null;
			mgbSocketConnect = false;
			//소켓이 종료 되도 큐가 살려준다.
		}
	}
	/**
	 * 글로벌 소켓에서 ON
	 *
	 * notifyme 휠컴 형태의 최초 메인 진입에 대한 이벤트 - 의미가 있을까?
	 * message join (각탭에 진입시 던지는 이벤트)
	 *
	 *
	 */
	private void globalSocketOn()
	{
		//Log.e("MSLEE","globalSocketOn");
		//Socket.EVENT 각각의 이벤트의 따른 리스너 등
		if(mGlobalSocket != null) {
			mGlobalSocket.on(Socket.EVENT_CONNECT, onConnect);
			mGlobalSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
			mGlobalSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
			mGlobalSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeOut);
		}
	}



	/**
	 * Socket.EVENT_CONNECT
	 */
	private Emitter.Listener onConnect = new Emitter.Listener() {
		@Override
		public void call(Object... args) {

			//Log.e("MSLEE","onConnect");
			mgbSocketConnect = true;
			//사용자 이벤트 message
			if(mGlobalSocket != null) {
				//
				mGlobalSocket.on(SOCKET_EVENT_MESSAGE, onMessageReceived);
				mGlobalSocket.on(SOCKET_EVENT_BANNER, onBannerReceived);
			}

			/**
			 * mQFlag 현재 "join" 탭이동에 대한 정보가 남아 있으면 던진다
			 */
			if(mQFlag)
			{
				//큐 바로 소진 및 해제
				mQFlag = false;
				if(mQJSONObject != null) {
					if( mGlobalSocket != null ) {
						//mGlobalSocket.emit("join", mQJSONObject, onAckReceived);
						mGlobalSocket.emit(EMIT_JOIN, mQJSONObject);
					}
				}
				//무조건 해제
				mQJSONObject=null;
			}
		}
	};

	/**
	 * Socket.EVENT_DISCONNECT
	 * 주
	 */
	private Emitter.Listener onDisconnect = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			//Log.e("MSLEE","onDisconnect");
			globalSocketOff();
		}
	};
	/**
	 * Socket.EVENT_CONNECT_ERROR
	 */

	private Emitter.Listener onConnectError = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			//Log.e("MSLEE","onConnectError");
			globalSocketOff();
		}
	};

	/**
	 * Socket.EVENT_CONNECT_TIMEOUT
	 */
	private Emitter.Listener onConnectTimeOut = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			//Log.e("MSLEE","onConnectTimeOut");
			globalSocketOff();
		}
	};

	/**
	 *
	 */
	private Emitter.Listener onBannerReceived = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			//다이렉트로 왔기 때문에
			//args[0] : ex) banner
			//args[1] : ex) json
			try{
				//Log.e("MSLEE","onBannerReceived");
				JSONObject receivedData = (JSONObject) args[0];


				//이 콜백을 받았다는건.. 이미 배너 타입
				//mCurrentBannerData
				//banner여야만 한다.
				onBannerDoing(receivedData);
			}catch (Exception e)
			{
				//죽지마!!
				Ln.e(e);
			}
		}
	};

	/**
	 * message onMessageEventReceived 리스너
	 */
	private Emitter.Listener onMessageReceived = new Emitter.Listener() {
		@Override
		public void call(Object... args) {

			//다이렉트로 왔기 때문에
			//args[0] : ex) message
			//args[1] : ex) json
			try{
				//Log.e("MSLEE","onMessageReceived");
				JSONObject receivedData = (JSONObject) args[0];

				//message 여야만 한다.
				//mCurrentMessageData
				onMessageDoing(receivedData);
			}catch (Exception e)
			{
				//죽지마!!
				Ln.e(e);
			}

		}
	};


	/**
	 *
	 * @param aid		지금 받은 놈의 아이디 ( 받을때마다 달라짐 )
	 * @param action	C / V 이 두개만 있다.
	 * @return 정상 유무 ( 서버에서 받은거 아님 )
	 */
	public boolean setEmitActivity(String aid,String action)
	{
		try {
			JSONObject data = new JSONObject();
			data.put(OBJECT_AID, aid);
			data.put(OBJECT_MSEQ, action);

			//당연히 연결되어 있겟지만.
			if (mgbSocketConnect) {
				//Log.e("MSLEE", "activity emit");
				try {
					mGlobalSocket.emit(EMIT_ACTIVITY, data);
				} catch (Exception e) {

				}
			}

			return true;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * onBannerReceived
	 * 꼭 UI 스레드 사용
	 * @param receivedData
	 */
	public final void onBannerDoing(JSONObject receivedData)
	{
		if( receivedData != null )
		{
			ThreadUtils.INSTANCE.runInUiThread(() -> {
				//Log.e("MSLEE","onBannerDoing " + receivedData.toString());
				try{
					mCurrentBannerData = new Gson().fromJson(receivedData.toString(), CspBannerModel.class);

					//배너 데이터가 수신시 이벤트버스 호출
					if("Y".equals(mCurrentBannerData.VI))
					{
						//그리고
						sendEvent(new Events.CspEvent(mCurrentBannerData.P, mCurrentBannerData.I, mCurrentBannerData.LN, mCurrentBannerData.AID));
						//이벤트 발생시 V 스크립되는 바인드가 시점이 아니다
						//setEmitActivity(mCurrentBannerData.AID,"V");
					}
				}catch (Exception e)
				{
					Ln.e(e);

				}
			});
		}
	}

	/**
	 * onMessageReceived 사용자 메세지 발생시 수행할 부분
	 * 꼭 UI 스레드 사용
	 * @param receivedData
	 */
	public final void onMessageDoing(JSONObject receivedData)
	{
		if( receivedData != null )
		{
			ThreadUtils.INSTANCE.runInUiThread(() -> {
				//Log.e("MSLEE","onAckDoing " + receivedData.toString());
				try{
					if(mCurrentMessageData != null) {

						mCurrentMessageData = new Gson().fromJson(receivedData.toString(), CspMessageModel.class);

						if("Y".equals(mCurrentMessageData.VI))
						{
							showCspButton(View.VISIBLE);
						}
						else
						{
							showCspButton(View.INVISIBLE);
						}
					}
				}catch (Exception e)
				{
					Ln.e(e);

				}
				showCspButton(View.VISIBLE);
			});
		}
	}

	/**
	 * Message 타입에서만 사용되는 코드
	 * @param type
	 */
	private void showCspButton(int type)
	{
		if(layoutCsp != null && btnCsp != null && textview_csp != null) {
			if(type == View.VISIBLE) {
				if(mCurrentMessageData != null)
				{
					//그릴 아이콘
					//
					//TP : view     눈
					//TP : save     돈
					//TP : tip      티비
					//TP : alert    종

					//btn_csp
					if("view".equals(mCurrentMessageData.TP))
						btnCsp.setBackgroundResource(R.drawable.ic_eye_android);
					else if("save".equals(mCurrentMessageData.TP))
						btnCsp.setBackgroundResource(R.drawable.ic_benepit_android);
					else if("tip".equals(mCurrentMessageData.TP))
						btnCsp.setBackgroundResource(R.drawable.ic_tip_android);
					else
						btnCsp.setBackgroundResource(R.drawable.ic_alarm_android);

					layoutCsp.setVisibility(type);
					btnCsp.setVisibility(type);

					//그릴 메세지
					int lenthM0 = 0;
					int lenthM1 = 0;

					//절대 색갈
					int color = 0xFFE2FF3A;

					//글자가 없으면? 각각의 경우수...
					//M0 없는 경우
					//M1 이 없는 경우
					//M2 가 없는 경우
					//M0, M1 이 없는 경우
					//M0, M2 가 없는 경우
					//M1, M2 가 없는 경우
					//M0, M1 M2 가 없는 경우
					if(mCurrentMessageData.M0 != null )
						lenthM0 = mCurrentMessageData.M0.length();
					if(mCurrentMessageData.M1 != null )
						lenthM1 = mCurrentMessageData.M1.length();

					if(lenthM0 < 0)
						lenthM0 = 0;
					if(lenthM1 < 0)
						lenthM1 = 0;

					//글자를 합치고
					String temp = mCurrentMessageData.M0 + mCurrentMessageData.M1 + mCurrentMessageData.M2;

					//링크가 있으면 > 붙이기
					if(mCurrentMessageData.LN != null && mCurrentMessageData.LN.length() > 3)
					{
						temp += " >";
					}

					if(lenthM1 > 0) //절대 색이 에 길자 길이가 0보다 큰경우 색갈을 칠함
					{
						//글자를 빌더에넣고
						SpannableStringBuilder builder = new SpannableStringBuilder(temp);
						//변경할 생각을 찾고 , lenthM0 길이의 끝 부터 ~ lenthM1 끝까지
						builder.setSpan(new ForegroundColorSpan(color), lenthM0, lenthM0+lenthM1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						textview_csp.setText(builder);
					}
					else{
						textview_csp.setText(temp);
					}

					textview_csp.setVisibility(View.GONE);
					textview_csp.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							//LN 예외처리
							WebUtils.goWeb(mContext,mCurrentMessageData.LN);
						}
					});

					//보여주는 씨나리오
					textview_csp.setVisibility(View.VISIBLE);
					Animation a = AnimationUtils.loadAnimation(mContext, R.anim.anim_guide_right_out);
					a.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							textview_csp.setVisibility(View.VISIBLE);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});
					a.reset();
					textview_csp.clearAnimation();
					textview_csp.startAnimation(a);

					//나올때는 0의 위치로
					ViewHelper.setTranslationY(layoutCsp, 0);

					//핸들러 param 을  처리하자 계속 5초 되도록
					mHandler.removeMessages(CLOSE_MESSAGE);
					mHandler.sendEmptyMessageDelayed(CLOSE_MESSAGE,MESSAGE_SHOW_TIME);

					btnCsp.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

							textview_csp.setVisibility(View.VISIBLE);
							Animation a = AnimationUtils.loadAnimation(mContext, R.anim.anim_guide_right_out);
							a.setAnimationListener(new Animation.AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {

								}

								@Override
								public void onAnimationEnd(Animation animation) {
									textview_csp.setVisibility(View.VISIBLE);
								}

								@Override
								public void onAnimationRepeat(Animation animation) {
								}
							});
							a.reset();
							textview_csp.clearAnimation();
							textview_csp.startAnimation(a);

							//핸들러 param 을  처리하자 계속 5초 되도록
							mHandler.removeMessages(CLOSE_MESSAGE);
							mHandler.sendEmptyMessageDelayed(CLOSE_MESSAGE,MESSAGE_SHOW_TIME);
						}
					});

				}
				else{
					if(mHandler!=null)
						mHandler.sendEmptyMessage(CLOSEALL_MESSAGE);
				}
			}
			else{
				if(mHandler!=null)
					mHandler.sendEmptyMessage(CLOSEALL_MESSAGE);
			}
		}
	}
}
