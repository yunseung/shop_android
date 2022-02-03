package gsshop.mobile.v2.push;

import android.app.Activity;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import java.io.IOException;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.util.Ln;

public class PushFcmRequestTokenCommand extends BaseCommand {
    /**
     * PushFcmRequestTokenCommand execute
     * FCM 토큰 등록 미 저장.
     * @param activity activity
     * @param chain chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
        injectMembers(activity);

        chain.next(activity);

        ThreadUtils.INSTANCE.runInBackground(() -> {
            String token = null;
            //Sender ID 추출
            String senderId = activity.getString(R.string.mc_push_sender_id);
            try {
                token = FirebaseInstanceId.getInstance().getToken(senderId, "FCM");
            } catch (IOException | IllegalStateException e) {
                Ln.e(e.toString());
            }

            // token = FirebaseInstanceId.getInstance().getToken();
            Ln.d("FirebaseInstanceId.getInstance().getToken() : " + token );
            //토큰 로컬에 저장
            if (!TextUtils.isEmpty(token)) {
                PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.TOKEN, Keys.CACHE.TOKEN, token);
            }
        });
    }
}
