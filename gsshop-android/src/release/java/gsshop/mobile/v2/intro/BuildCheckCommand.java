package gsshop.mobile.v2.intro;

import android.app.Activity;
import android.app.Dialog;

import com.google.inject.Inject;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;
import gsshop.mobile.v2.util.ThreadUtils;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.buildcheck.BuildLog;
import gsshop.mobile.v2.support.buildcheck.BuildLogAction;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.util.AppFinishUtils;
import gsshop.mobile.v2.util.LunaUtils;
import gsshop.mobile.v2.util.MarketUtils;
import roboguice.util.Ln;

/**
 * Created by azota on 2016-06-17.
 *
 * 릴리즈 모드인 경우 앱 위변조 체크를 수행한다.
 */
public class BuildCheckCommand extends BaseCommand{

    @Inject
    private BuildLogAction buildLogAction;

    @Override
    public void execute(Activity activity, CommandChain chain) {
        super.injectMembers(activity);

        Ln.i("BuildCheckCommand : release");

        ThreadUtils.INSTANCE.runInBackground(() -> {
                //1. 구글 사인값 취득
                //2. class.dex 값 취득
                //3. 서버로 던짐
                //4. 서버에서 true 로 주면 통과
                //5. 서버에서 false 로 주면 진입 시키지 않음
                // 5-1 웁스에 상태값 전송
                //6. true / false 이외에 모든 값은 통과임

                BuildLog buildLog = null;
                try {
                    buildLog = buildLogAction.sendBuildInfo(activity);
                    Ln.i("BuildCheckCommand : " + buildLog.message);
                }catch (Exception e){
                    //익셥션 발생하면 앱은 정상가동 시킴

                    // 10/19 품질팀 요청
                    // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                    // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                    // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                    Ln.e(e);
                    // 루나 로그 전송
                    //LunaUtils.sendToLuna(activity, e, LunaUtils.PREFIX_BUILD_ERROR);
                }

                if( buildLog != null ) {
                    if("Y".equals(buildLog.result))
                    {
                        // 위변조 체크 이상없음. 진입시킴
                        chain.next(activity);
                    }
                    else if ("I".equals(buildLog.result))
                    {
                        // 루나 로그 전송
                        LunaUtils.sendToLuna(activity, new Exception(), buildLog.message);
                        // 위변조 체크 이상여부 확인 불가. 진입시킴
                        chain.next(activity);
                    }
                    else
                    {
                        // 위변조 체크 결과 위변조 발견
                        // 루나 로그 전송
                        LunaUtils.sendToLuna(activity, new Exception(), buildLog.message);
                        // 진입을 막는다.
                        alertFakeMessage(activity);
                    }
                }
                else
                {
                    // 위변조 체크중 네트웍/서버/결과메시지매핑 등 오류 발생 (위변조 여부 확인 안됨)
                    chain.next(activity);
                }
        });
    }

    private void alertFakeMessage(final Activity activity) {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
                new CustomOneButtonDialog(activity).title(R.string.warning).message(R.string.app_fake_msg)
                        .cancelable(false)
                        .buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                                MarketUtils.goMarket(activity);
                                activity.finish();
                                AppFinishUtils.finishApp(activity, false);
                            }
                        }).show();
            });
    }
}
