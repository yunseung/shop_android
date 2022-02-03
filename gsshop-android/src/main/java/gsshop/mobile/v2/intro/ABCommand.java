/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ai.evolv.AscendAction;
import ai.evolv.AscendAllocationStore;
import ai.evolv.AscendClientFactory;
import ai.evolv.AscendConfig;
import ai.evolv.AscendParticipant;
import ai.evolv.HttpClient;
import ai.evolv.httpclients.OkHttpClient;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.DeviceUtils;
import roboguice.util.Ln;

/**
 * 어샌드 AB 테스트 초기화 커맨드
 */
public class ABCommand extends BaseCommand {

    public final static String Tag = "ABCommand";

    //POC MainApplication.client.emitEvent("tab_shown_478");
    //MainApplication.GS_CHOICE_TITLE = tabTitle;
    //private final static String tabTitle_preFix = "tab.title_";

    //poc product_selected_323 효율 및 expandABState
    //private final static String pocTvFolded = "tv.folded";

    private final static String tabTitle_preFix = "tab.title_";

    //내일 TV 편성 정보..컬러 변경 AB
    //NAVI_TOMOROWTV_492 = "492";
    //public final static String tv492_Sch_Info_Color_AB = "tv.sch_text_" + NaviIdManager.NAVI_TOMOROWTV_492;
    //내일 TV 편성 디폴트 색갈..
    //public final static String tv492_Sch_Info_Color_Defalut = "#666666";

    //내일 TV 플레이 버튼 AB
    //NAVI_TOMOROWTV_492 = "492";
    //public final static String tv492_play_image_AB = "tv.play_image_" + NaviIdManager.NAVI_TOMOROWTV_492;

    //내일 TV 플레이  “상품보기”  버튼 노출 AB
    //NAVI_TOMOROWTV_492 = "492";
    //public final static String tv492_controlview_goprd_AB = "tv.controlview_goprd_" + NaviIdManager.NAVI_TOMOROWTV_492;


    //상품 진입 골에 대한 preFix
    public final static String product_Selected_PreFix = "product_selected_";
    //재생버튼 클릭 골에 대한 preFix
    //public final static String prd_PlayClicked_PreFix = "prd_play_clicked_";
    //메인 탭 방문 골에 대한 preFIx
    public final static String tab_Shown_PreFix= "tab_shown_";


    private Activity mActivity;

    class storedAllocation {
        String uid;
    }
    /**
     *  execute
     * @param activity activity
     * @param chain chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
        injectMembers(activity);

        mActivity = activity;

        //일단 흘려 보내고
        chain.next(activity);

        try {
            //gsshop customer unique key(login user customerNumber)
            //allways gsshop customer pcid

            //String pcId = CookieUtils.getPcId(activity);
            String gsuuid = DeviceUtils.getGsuuid(activity);

            AscendAllocationStore store = new CustomAllocationStore(activity,gsuuid);
            //store.put(pcId, new JsonParser().parse(myStoredAllocation).getAsJsonArray());

            HttpClient httpClient = new OkHttpClient(TimeUnit.MILLISECONDS, 2500);

            // build config with custom timeout and custom allocation store
            // STG : 4a5722e0fd product :6f4529be9b
            AscendConfig config = AscendConfig.builder(MainApplication.getAppContext().getString(R.string.asend_env_id), httpClient).setAscendAllocationStore(store).build();
            //AscendConfig config = AscendConfig.builder("6f4529be9b", httpClient).setAscendAllocationStore(store).build();

            // initialize the client with a stored user
            // gsshop cookie -> setUserId(pcId);
            MainApplication.client = AscendClientFactory.init(config,  AscendParticipant.builder().setUserId(gsuuid).build());

            MainApplication.client.confirm();

        } catch (Exception e) {
            Ln.e(e);
        }
    }

    public class CustomAllocationStore implements AscendAllocationStore {

        private String TAG = "ABCommand";
        private Map<String, JsonArray> allocations = new HashMap<String, JsonArray>();
        private SharedPreferences pref;
        private String prfKey = "_ab_prf_key";

        CustomAllocationStore(Context context,String pcid) {
            String jsonArrayString = null;
            pref = context.getSharedPreferences(prfKey, Activity.MODE_PRIVATE);

            if(pref != null)
                jsonArrayString = pref.getString(prfKey, (String)null);

            if(jsonArrayString != null)
            {
                try{
                    this.allocations.put(pcid,new JsonParser().parse(jsonArrayString).getAsJsonArray());
                }catch (Exception e)
                {
                    pref.edit().remove(prfKey).commit();
                    Ln.e(e);
                }
            }
            if(this.allocations.get(pcid) != null)
                Log.d(TAG,"ABCommand : "+ this.allocations.get(pcid).toString());
        }

        @Override
        public JsonArray get(String userId) {
            return allocations.get(userId);
        }

        @Override
        public void put(String userId, JsonArray jsonArray) {

            //Log.i(TAG,"put : "+ jsonArray.toString());
            this.allocations.clear();
            this.allocations.put(userId, jsonArray);

            //Log.i(TAG,"put : "+this.allocations.get(userId).toString());
            //memory map put after, prfsave
            saveJsonArray(prfKey,this.allocations.get(userId).toString());
        }

        /**
         *
         * @param key
         * @param jsonArrayString
         */
        public void saveJsonArray(String key, String jsonArrayString)
        {
            if( key != null && jsonArrayString != null && pref != null)
            {
                pref.edit().putString(key, jsonArrayString.toString()).commit();
            }
        }
    }
}
