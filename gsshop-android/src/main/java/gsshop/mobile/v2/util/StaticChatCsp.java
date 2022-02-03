/*
 * Copyright(C) 2013 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.support.tv.OnCspChatController;
import gsshop.mobile.v2.support.tv.OnCspChatListener;
import gsshop.mobile.v2.user.User;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
import roboguice.RoboGuice;
import roboguice.util.Ln;

/**
 * 모바일라이브 관련 웹소켓 라이브러리
 */
public class StaticChatCsp implements OnCspChatController{

	/**
	 * 메세지 전송 emit
	 * 보낼때의 에밋 이름
	 */
	public static final String GET_EMIT_SEND = "send";

	/**
	 * 메세지 수신 emit
	 */
	public static final String ON_EMIT_MESSAGE = "MSSG";

	/**
	 * 공지리스트 emit
	 */
	public static final String ON_EMIT_NOTI = "NOTI";

	/**
	 * 사람수 emit
	 */
	public static final String ON_EMIT_STAT = "STAT";

	/**
	 * 채팅 화면 설정 emit (컨피그)
	 */
	public static final String ON_EMIT_CONF = "CONF";

	/**
	 * 이전 채팅 목록 가져오는 emit
	 */
	public static final String ON_EMIT_CHASTS = "CHATS";

	/**
	 * 이전 채팅 목록 가져오는 emit
	 */
	public static final String GET_EMIT_CHASTS = "chats";

	private Context mContext;

	/**
	 * - 채팅 용 소켓
	 */
	private Socket mChatSocket;


	/**
	 *  채팅 소켓 케넥션 여부
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
	 * StaticCsp 인스턴스 저장용
	 */
	private static StaticChatCsp mStaticCsp;

	/**
	 * 콜백용
	 */
	private OnCspChatListener callback;

	@Override
	public void setOnChatDataListener(OnCspChatListener callback) {
		if (callback != null) {
			this.callback = callback;
		}
	}

	/**
	 * 인스턴스를 생성 또는 반환한다.
	 *
	 * @param context 컨텍스트
	 * @return StaticCsp
	 */
	public static StaticChatCsp getInstance(Context context) {
		if (mStaticCsp == null) {
			mStaticCsp = new StaticChatCsp(context);
		}
		return mStaticCsp;
	}

	/**
	 * 인스턴스를 반환한다.
	 *
	 * @return StaticCsp
	 */
	public static StaticChatCsp getInstance() {
		return mStaticCsp;
	}

	/**
	 * mCurrentData / mCurrentTabId 생성
	 */
	public StaticChatCsp(Context context){
		this.mContext = context;
		RoboGuice.getInjector(context).injectMembers(this);
	}


	/**
	 * 소켓 초기화
	 *
	 * 고개번호가 이미 글로벌(User.getCachedUser()) 이기 때문에 여기서 하는것이 맞는것 같다
	 *
	 * 공통이 필요 하면 만들짜
	 *
	 * 고객번호가 없으면 접속이 안된다.
	 * 고객번호가 없으면 PCID 상품 번
	 *
	 */
	public void initSocket(String liveNo)
	{
		try {

			//http://cspdev.innolab.us/chat?A=GSSHOP&U=7534298&P=HOME&S=10
			//- A : GSSHOP ( 고정 )
			//- V : 버전 1.0
			// - U : 고개번호 피씨아이디 없으면 안되는데..
			// - P : 현재 TAB 위치 채팅에서는 상품코드 또는 방송 번호 유니큳

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
			opts.query = "A=GSSHOP&U="+custNo +"&P="+ liveNo +"&C="+ "mobilelive" + "&V=" + "1.0";
			String url = ServerUrls.WS.CSP_CHAT_SERVICE_URL;

			mChatSocket = IO.socket(url,opts);
			mChatSocket.connect();
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
		Ln.i("globalSocketOff");
		if(mChatSocket != null)
		{
			mChatSocket.off();
			mChatSocket.close();
			mChatSocket=null;
			mgbSocketConnect = false;
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
		Ln.i("globalSocketOn");
		//Socket.EVENT 각각의 이벤트의 따른 리스너 등
		if(mChatSocket != null) {
			mChatSocket.on(Socket.EVENT_CONNECT, onConnect);
			mChatSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
			mChatSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
			mChatSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeOut);
		}
	}



	/**
	 * Socket.EVENT_CONNECT
	 */
	private Emitter.Listener onConnect = new Emitter.Listener() {
		@Override
		public void call(Object... args) {

			Ln.i("onConnect");

			mgbSocketConnect = true;
			//사용자 이벤트 message
			if(mChatSocket != null) {
				//소켓이 열결되면 메세지 받을 준비를 한다.
				mChatSocket.on(ON_EMIT_MESSAGE, onMessageReceived);
				//소켓이 열결되면 공지 리스트 받을 준비를 한다.
				mChatSocket.on(ON_EMIT_NOTI, onNotiListReceived);
				//소켓이 열결되면 사람수 또는 스테이트 받을 준비를 한다.
				mChatSocket.on(ON_EMIT_STAT, onStateReceived);
				//소켓이 열결되면 채팅 설정 상태를 받을 준비를 한다 ( ex)채팅 잠금 유무 같은.)
				mChatSocket.on(ON_EMIT_CONF, onConfigReceived);

				//소켓을 열렸다는 가저하여 달라 메세지 , JSONObject 내부 구성으로 가장 오래 된것의 TS(타임스탬프)를 넘기면 더 전꺼를 준다. 100ㅆ
				mChatSocket.emit(GET_EMIT_CHASTS,new JSONObject());

				//소켓이 연결되면 과거의 채팅 목록을 받을 준비를 한다.
				mChatSocket.on(ON_EMIT_CHASTS,onChatsReceived);


			}
		}
	};

	/**
	 * Socket.EVENT_DISCONNECT
	 */
	private Emitter.Listener onDisconnect = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			Ln.i("onDisconnect");
			globalSocketOff();
		}
	};
	/**
	 * Socket.EVENT_CONNECT_ERROR
	 */

	private Emitter.Listener onConnectError = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			Ln.i("onConnectError");
			globalSocketOff();
		}
	};

	/**
	 * Socket.EVENT_CONNECT_TIMEOUT
	 */
	private Emitter.Listener onConnectTimeOut = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			Ln.i("onConnectTimeOut");
			globalSocketOff();
		}
	};

	/**
	 *  onNotiListReceived 리스너
	 */
	private Emitter.Listener onNotiListReceived = new Emitter.Listener() {
		@Override
		public void call(Object... args) {

			Ln.i("onNotiListReceived : " + args.toString());
			//다이렉트로 왔기 때문에
			//args[0] : ex) message
			//args[1] : ex) json
			try{
				JSONObject receivedData = (JSONObject) args[0];

				//message 여야만 한다.
				//mCurrentMessageData
				//onMessageDoing(receivedData);
				callback.onNotied(receivedData);
			}catch (Exception e)
			{
				//죽지마!!
				Ln.e(e);
			}

		}
	};

	/**
	 * onStateReceived 리스너
	 */
	private Emitter.Listener onStateReceived = new Emitter.Listener() {
		@Override
		public void call(Object... args) {

			Ln.i("onStateReceived : " + args.toString());
			//다이렉트로 왔기 때문에
			//args[0] : ex) message
			//args[1] : ex) json
			try{
				JSONObject receivedData = (JSONObject) args[0];

				//message 여야만 한다.
				//mCurrentMessageData
				//onMessageDoing(receivedData);
				callback.onStated(receivedData);
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

			Ln.i("onMessageReceived : " + args.toString());
			//다이렉트로 왔기 때문에
			//args[0] : ex) message
			//args[1] : ex) json
			try{
				JSONObject receivedData = (JSONObject) args[0];

				//message 여야만 한다.
				//mCurrentMessageData
				//onMessageDoing(receivedData);
				callback.onMessaged(receivedData);
			}catch (Exception e)
			{
				//죽지마!!
				Ln.e(e);
			}
		}
	};

	/**
	 * onConfigReceived 리스너
	 */
	private Emitter.Listener onConfigReceived = new Emitter.Listener() {
		@Override
		public void call(Object... args) {

			Ln.i("onMessageReceived : " + args.toString());
			//다이렉트로 왔기 때문에
			//args[0] : ex) message
			//args[1] : ex) json
			try{
				JSONObject receivedData = (JSONObject) args[0];

				//message 여야만 한다.
				//mCurrentMessageData
				//onMessageDoing(receivedData);
				callback.onReceivedConfig(receivedData);
			}catch (Exception e)
			{
				//죽지마!!
				Ln.e(e);
			}
		}
	};

	/**
	 * onChatsReceived 리스너
	 *
	 */
	private Emitter.Listener onChatsReceived = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			Ln.i("onChatsReceived : " + args.toString());
			try
			{
				JSONObject receivedData = (JSONObject) args[0];
				callback.onReceivedChats(receivedData);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	};

	/**
	 *
	 * 이미 User 에 대한 정보가 글로벌이기 때문에
	 * 운영을 위해서 라고 처리 로직은 이곳에 넣는것이 맞다?
	 *
	 * var nick = // *처리된 로그인 ID
	 * var custNo = 고객번호
	 * var message = 메시지 텍스트
	 * var dt = { 'UO' : { 'U':  custNo, 'NI': nick }, 'MO' : { 'MG': message, 'EM': 이모티콘 코드 } }
	 * var param = { 'DT' : dt, 'NM' : 'MSSG' }
	 * socket.emit( 'send', param )
	 *
	 * @param message
	 */
	public void messageSend(String message)
	{
		ThreadUtils.INSTANCE.runInBackground(() -> {
			Ln.i( "messageSend : " + message);

			JSONObject data = new JSONObject();
			JSONObject UO = new JSONObject();
			JSONObject MO = new JSONObject();

			/**
			 * 최종 전송되어야 하는 데이터
			 */
			JSONObject DT = new JSONObject();

			try {
				String custNo = User.getCachedUser() != null ? User.getCachedUser().customerNumber : "";
				String pcId = CookieUtils.getPcId(mContext);
				String nick = User.getCachedUser() != null ? MaskingUtil.maskLoginIdForShoppyLive(User.getCachedUser().loginId, true) : "";

				//고객 번호가 비어져 있으면
				if(custNo != null && custNo.isEmpty())
				{
					//고객번호 대신 pcid ex) 15264500165345248215353
					custNo = pcId;
					//그것도 없으면 비우자 서버 로직에 따라 커넥션 실패가 되거나 허용되겟지
					if(custNo.isEmpty())
						custNo = "";
				}

				//닉 또는 고객 번호가 비어져 있으면 문제가 있는 상황인데..
				// TODO: 2019. 2. 15. mslee
				//혹시라도 문제가 있으면 예외처리를 하자
				if(nick != null && nick.isEmpty()) {
					//마스킹 처리를 하자
					// TODO: 2019. 2. 15. mslee
					// 마스킹 처리
					nick = "****";
				}


				UO.put("U", custNo);
				UO.put("NI", nick);

				MO.put("MG", message);
				MO.put("EM", "");

				data.put("UO",UO);
				data.put("MO",MO);

				DT.put("DT",data);
				DT.put("NM",ON_EMIT_MESSAGE);


				if(mgbSocketConnect) {
					try{
						mChatSocket.emit(GET_EMIT_SEND,DT);
					}catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else{

				}

			} catch(JSONException e) {
				e.printStackTrace();
			}
		});
	}
}
