/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.google.inject.Inject;
import com.gsshop.mocha.device.AppInfo;
import com.gsshop.mocha.network.rest.RestClient;

import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import gsshop.mobile.v2.ApptimizeExpManager;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.CACHE;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.intro.ApptimizeCommand;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import roboguice.inject.ContextSingleton;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * HomeGroupInfoAction
 */
@ContextSingleton
public class HomeGroupInfoAction {
    private final static String AOS = "AOS";
    private final static String OS = "os";
    public final static String APPVER = "appver";

    //영훈매니저님께 앱티마이저에서 받은 상태값을 전달해줄때 사용
    private final static String ABINFO = "abinfo";

    @Inject
    private RestClient restClient;

    /**
     * 로컬 캐쉬 데이터의 유효성 체크
     * @param context context
     * @return boolean
     * @throws IOException
     */
    public boolean getHomeGroupInfoCheck(Context context) throws IOException {
    	
    	HomeGroupInfo homeGroupInfo = null;    
        
    	try {
            homeGroupInfo = getCachedHomeGroupInfo(context);
        } catch (Exception e) {
            homeGroupInfo = null;
        }
    	
    	// 캐쉬가 없거나, 캐쉬에 실제 List 없거나, List 는 있지만 내부 내용이 없는 경우 재 요청  
        if (homeGroupInfo == null || homeGroupInfo.groupSectionList == null
                || homeGroupInfo.groupSectionList.size() < 0) {
            //Ln.i("Cache 사용 하지 않음 - Cache Data 오류");
        	return false;

        } else {
            return true;
        }	
    }

    /**
     * Home Menu 조회.(앱 시작시 사용됨)
     *
     * @param context context
     * @param nonCacheFlag true 네트워크를 항상 탄다. / false 캐쉬를 가져와본다음에 태울지 안태울지 확인
     * @return HomeGroupInfo
     * @throws IOException IOException
     * @throws URISyntaxException URISyntaxException
     * @throws RestClientException RestClientException
     */
    public HomeGroupInfo getHomeGroupInfo(Context context, boolean nonCacheFlag)
            throws IOException, URISyntaxException, RestClientException {

        //로컬 캐쉬 확인 - 2014/10/26 by leems
        HomeGroupInfo homeGroupInfo;
        long currentTime = System.currentTimeMillis();
        long tempTime = 0;

        String url = ServerUrls.getHttpRoot() + ServerUrls.REST.getHomeNavigation();

//        url = "http://10.52.164.237/test/app/testdata.jsp";
//        url = "http://10.52.214.181/app/navigation?version=2.9";

        try{
            //sm21에서만 동작 하도록 수정, openDate 전용 빌드일때는 N로 설정함
            String openDateMenuFlag = MainApplication.getAppContext().getString(R.string.openDateMenuFlag);
            if("Y".equals(openDateMenuFlag)) {
                //openDate 처리 uti
                String openDate = getCacheHomeNaviOpenDate();
                //openDate 존재하면 ""포함 , 기존 URL openDate= 없을때만 사용자가 정의한 openDate를 붙인다.
                if (!TextUtils.isEmpty(openDate) && !url.contains(ServerUrls.REST.OPEN_DATE_FORMAT)) {
                    //항상 ? 포함된 상태라고 판단 navigation?version=4.0 항상 있으니까
                    url = url + "&" + openDate;// + abTestParam;
                }
            }
        }
        catch (Exception e) {

        }

        try {
            MainApplication.apptimizeNaviFlag = false; //네비호출 전이니까 false

            ApptimizeCommand.ABINFO_VALUE="";
            for(int i = 0; i< ApptimizeExpManager.resultArray.size() ; i++){
                ApptimizeCommand.ABINFO_VALUE += ApptimizeExpManager.resultArray.get(i).getAll() + ",";
            }
            PackageInfo pi = AppInfo.getPackageInfo(MainApplication.getAppContext());
            url = (url  + "&" + OS + "=" + AOS + "&" + APPVER + "=" + AppInfo.getAppVersionCode(pi) + "&" + ABINFO + "=" + ApptimizeCommand.ABINFO_VALUE).trim();

            MainApplication.apptimizeNaviFlag = true; //네비호출 후니까 true
        }catch (Exception e){

        }



        /**
         * cache 를 태우지 않음
         */

        if (nonCacheFlag) {
            //사용자별로 디폴트 매장 추천을 위해 wa_pcid값이 필요하여, 웹뷰쿠키를 restClient에 복사하여 API를 호출함
            homeGroupInfo = (HomeGroupInfo) CookieUtils.syncAndcopyWebViewCookiesToRestClient(context, restClient, url, HomeGroupInfo.class);

            if (homeGroupInfo != null) {
                homeGroupInfo.saveTime = System.currentTimeMillis();
                cacheHomeGroupInfo(context, homeGroupInfo);
            }
            return sortHomeGroupInfo(homeGroupInfo);
        }

        try {
            homeGroupInfo = getCachedHomeGroupInfo(context);            
        } catch (Exception e) {
            homeGroupInfo = null;
        }
        /*
         * 캐쉬가 있고, 캐쉬의 저장 시간이 0 보다 크고, 현재 시간이 0보다 큰경우
         * 캐쉬가 된 시간과 현재 시간의 텀의 구한다. 
         */
        if (homeGroupInfo != null && homeGroupInfo.saveTime > 0 && currentTime > 0) {
            tempTime = currentTime - homeGroupInfo.saveTime;
        }
        /*
         * 초 * 1000
         * 캐쉬가 된 시간과 현재 시간의 텀이 지정된 시간 보다 작고, 0보다 클 때 캐쉬를 사용한다.
         * to do : 30초는 디파인 필요
         */
        if (tempTime < Keys.CACHETIMENAVIGATION && tempTime > 0) {
            // 캐쉬가 없거나, 캐쉬에 실제 List 없거나, List 는 있지만 내부 내용이 없는 경우 재 요청  
            if (homeGroupInfo == null || homeGroupInfo.groupSectionList == null
                    || homeGroupInfo.groupSectionList.size() < 0) {
                //Ln.i("캐쉬 미사용");

                homeGroupInfo = (HomeGroupInfo) CookieUtils.syncAndcopyWebViewCookiesToRestClient(context, restClient, url, HomeGroupInfo.class);
                if (homeGroupInfo != null) {
                    homeGroupInfo.saveTime = System.currentTimeMillis();
                    cacheHomeGroupInfo(context, homeGroupInfo);
                }

            } else {
                //Ln.i("캐쉬 사용");
            }
        } else {
            //Ln.i("캐쉬 시간 지남");
            homeGroupInfo = (HomeGroupInfo) CookieUtils.syncAndcopyWebViewCookiesToRestClient(context, restClient, url, HomeGroupInfo.class);
            if (homeGroupInfo != null) {
                homeGroupInfo.saveTime = System.currentTimeMillis();
                cacheHomeGroupInfo(context, homeGroupInfo);
            }
        }
        return sortHomeGroupInfo(homeGroupInfo);
    }

    /**
     * HomeGroupInfo를 캐시.
     *
     * @param context context
     * @param homeInfo homeInfo
     */
    public void cacheHomeGroupInfo(Context context, HomeGroupInfo homeInfo) {
        //navigation version 추가
        homeInfo = addNavigationVersion(sortHomeGroupInfo(homeInfo));

        PrefRepositoryNamed.save(MainApplication.getAppContext(), CACHE.HOME_GROUP_INFO, homeInfo);
        //
        //        MainApplication mainApp = (MainApplication) context.getApplicationContext();
        //
        //        mainApp.removeHomeGroupInfo(CACHE.HOME_GROUP_INFO);
        //        mainApp.save(CACHE.HOME_GROUP_INFO, homeInfo);
    }

    /**
     * 고정매장뒤에 개인매장이 위치하도록 정렬한다.
     *
     * @param homeInfo HomeGroupInfo
     * @return 정렬된 HomeGroupInfo
     */
    private HomeGroupInfo sortHomeGroupInfo(HomeGroupInfo homeInfo) {
        if (isEmpty(homeInfo) || isEmpty(homeInfo.groupSectionList)) {
            return homeInfo;
        }

        //매장정렬
        ArrayList<TopSectionList> sectionListNew = new ArrayList<>();
        ArrayList<TopSectionList> sectionListPub = new ArrayList<>();
        ArrayList<TopSectionList> sectionListPrv = new ArrayList<>();
        ArrayList<TopSectionList> sectionList = homeInfo.groupSectionList.get(0).sectionList;
        if (isEmpty(sectionList)) {
            return homeInfo;
        }
        for (int i = 0; i < sectionList.size(); i++) {
            if (sectionList.get(i).isPublicSection) {
                sectionListPub.add(sectionList.get(i));
            } else {
                sectionListPrv.add(sectionList.get(i));
            }
        }
        sectionListNew.addAll(sectionListPub);
        sectionListNew.addAll(sectionListPrv);
        homeInfo.groupSectionList.get(0).sectionList = sectionListNew;

        return homeInfo;
    }

    /**
     * 방송갱신 API 주소에 파라미터 추가
     *
     * @param homeGroupInfo navigation api 호출결과 객체
     * @return HomeGroupInfo
     */
    private HomeGroupInfo addNavigationVersion(HomeGroupInfo homeGroupInfo) {
        if (isNotEmpty(homeGroupInfo) && isNotEmpty(homeGroupInfo.appUseUrl)) {
            final String key = "version";
            Uri.Builder builder;
            if (isNotEmpty(homeGroupInfo.appUseUrl.tvLiveUrl)) {
                Uri uri = Uri.parse(homeGroupInfo.appUseUrl.tvLiveUrl);
                String apiVerParam = uri.getQueryParameter(key);
                if (isEmpty(apiVerParam)) {
                    //파라미터 없는 경우만 추가
                    builder = uri.buildUpon();
                    builder.appendQueryParameter(key, ServerUrls.REST.getNavigationVer());
                    homeGroupInfo.appUseUrl.tvLiveUrl = builder.build().toString();
                }
            }
            if (isNotEmpty(homeGroupInfo.appUseUrl.homeLiveBrodUrl)) {
                Uri uri = Uri.parse(homeGroupInfo.appUseUrl.homeLiveBrodUrl);
                String apiVerParam = uri.getQueryParameter(key);
                if (isEmpty(apiVerParam)) {
                    builder = uri.buildUpon();
                    builder.appendQueryParameter(key, ServerUrls.REST.getNavigationVer());
                    homeGroupInfo.appUseUrl.homeLiveBrodUrl = builder.build().toString();
                }
            }
            if (isNotEmpty(homeGroupInfo.appUseUrl.homeDataBrodUrl)) {
                Uri uri = Uri.parse(homeGroupInfo.appUseUrl.homeDataBrodUrl);
                String apiVerParam = uri.getQueryParameter(key);
                if (isEmpty(apiVerParam)) {
                    builder = uri.buildUpon();
                    builder.appendQueryParameter(key, ServerUrls.REST.getNavigationVer());
                    homeGroupInfo.appUseUrl.homeDataBrodUrl = builder.build().toString();
                }
            }
            if (isNotEmpty(homeGroupInfo.appUseUrl.liveBrodUrl)) {
                Uri uri = Uri.parse(homeGroupInfo.appUseUrl.liveBrodUrl);
                String apiVerParam = uri.getQueryParameter(key);
                if (isEmpty(apiVerParam)) {
                    builder = uri.buildUpon();
                    builder.appendQueryParameter(key, ServerUrls.REST.getNavigationVer());
                    homeGroupInfo.appUseUrl.liveBrodUrl = builder.build().toString();
                }
            }
            if (isNotEmpty(homeGroupInfo.appUseUrl.dataBrodUrl)) {
                Uri uri = Uri.parse(homeGroupInfo.appUseUrl.dataBrodUrl);
                String apiVerParam = uri.getQueryParameter(key);
                if (isEmpty(apiVerParam)) {
                    builder = uri.buildUpon();
                    builder.appendQueryParameter(key, ServerUrls.REST.getNavigationVer());
                    homeGroupInfo.appUseUrl.dataBrodUrl = builder.build().toString();
                }
            }
        }

        return homeGroupInfo;
    }

    /**
     * 오픈데이트 값 가져오기
     * @return
     */
    public String getCacheHomeNaviOpenDate()
    {
        return PrefRepositoryNamed.get(MainApplication.getAppContext(), CACHE.HOME_NAVI_OPEN_DATE, String.class);
    }

    /**
     * 로컬에 캐시된 정보를 조회한다.
     * @param context context
     * @return HomeGroupInfo
     */
    public HomeGroupInfo getCachedHomeGroupInfo(Context context) {
        MainApplication mainApp = (MainApplication) context.getApplicationContext();

        //return PrefRepositoryNamed.get(CACHE.HOME_GROUP_INFO, HomeGroupInfo.class);
        return mainApp.getHomeGroupInfo(CACHE.HOME_GROUP_INFO);
    }

    /**
     * 로컬에 저장된 메인과 매장 정보 가져옴 (앱구동시마다 API 호출하여 저장된 정보, 캐시 아님)
     *
     * @return HomeGroupInfo
     */
    public static HomeGroupInfo getHomeGroupInfo() {
        return PrefRepositoryNamed.get(MainApplication.getAppContext(), Keys.CACHE.HOME_GROUP_INFO, HomeGroupInfo.class);
    }
}
