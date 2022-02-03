/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */

package gsshop.mobile.v2.support.permission;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.gsshop.mocha.network.rest.Rest;
import com.gsshop.mocha.network.rest.RestGet;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.DateUtils;
import gsshop.mobile.v2.util.EncryptUtils;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.RestClientUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;


public class UpdatePassActivity extends AbstractBaseActivity {

    private boolean mIsTvLogin = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_pass);

        //배경 DIM 처리
        WindowManager.LayoutParams param = getWindow().getAttributes();
        param.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        param.dimAmount = 0.6f;
        getWindow().setAttributes(param);

        // 클릭리스너 등록
        setClickListener();

        mIsTvLogin = getIntent().getBooleanExtra(Keys.INTENT.ARS_MEMBER_VALUE, false);
        if (mIsTvLogin) {
            TextView txtTitle = (TextView) findViewById(R.id.txt_update_title);
            txtTitle.setText(R.string.update_pass_tv_title_text);
            Button btnUpdate = (Button) findViewById(R.id.btn_update_pass);
            btnUpdate.setText(R.string.update_pass_tv_button_text);
        }
    }

    /**
     * 뷰의 클릭리스너를 세팅한다.
     */
    private void setClickListener() {
        findViewById(R.id.btn_update_pass).setOnClickListener((View v) -> {
                    updatePass();
                }
        );
        findViewById(R.id.btn_not_show).setOnClickListener((View v) -> {
                    hide();
                }
        );
        findViewById(R.id.btn_close).setOnClickListener((View v) -> {
                    close();
                }
        );
    }

    /**
     * 비밀번호 변경하기
     */
    private void updatePass() {
        WebUtils.goWeb(UpdatePassActivity.this, getIntent().getStringExtra(Keys.INTENT.WEB_URL));
        finish();
    }

    /**
     * 30일동안 보지 않기
     */
    private void hide() {
        User user = User.getCachedUser();
        if (user != null) {
            String encCustNo = null;
            if (mIsTvLogin) {
                encCustNo = EncryptUtils.encrypt(user.customerNumber + "_TV_UP", "SHA-256");
            }
            else {
                encCustNo = EncryptUtils.encrypt(user.customerNumber + "_UP", "SHA-256");
            }
            if (encCustNo != null)
                PrefRepositoryNamed.save(MainApplication.getAppContext(), encCustNo, DateUtils.getToday("yyyyMMdd"));

            sendUpdatePass(user.customerNumber);
        }

        finish();
    }

    /**
     * 닫기
     */
    private void close() {
        finish();
    }

    /**
     * 비밀번호변경팝업 그만보기 정보를 전송한다.
     *
     * @param customerNumber 고객번호
     */
    private void sendUpdatePass(String customerNumber) {
        ThreadUtils.INSTANCE.runInBackground(() -> {
            try {
                String result = null;

                if (mIsTvLogin) {
                    result = RestClientUtils.INSTANCE.get(restClient, ServerUrls.REST.NOTICE_UPDATE_TV_MEMBERS_PASS, String.class);
                }
                else {
                    result = RestClientUtils.INSTANCE.get(restClient, ServerUrls.REST.NOTICE_UPDATE_PASS + "?catvId={catvId}", String.class, customerNumber);
                }

                if (result != null && "S".equalsIgnoreCase(result)) {
                    //업데이트 성공
                } else {
                    //업데이트 실패
                }
            } catch (Exception e) {
                Ln.e(e);
            }
        });
    }

    /**
     * 패스워드변경팝업 그만보기 API 커넥터
     */
    @ContextSingleton
    @Rest(resId = R.string.server_http_root)
    public static class UpdatePassConnector {
        @RestGet(ServerUrls.REST.NOTICE_UPDATE_PASS + "?catvId={catvId}")
        public String updatePass(String catvId) {
            return null;
        }
        @RestGet(ServerUrls.REST.NOTICE_UPDATE_TV_MEMBERS_PASS)
        public String updatePassTV() {
            return null;
        }
    }


}