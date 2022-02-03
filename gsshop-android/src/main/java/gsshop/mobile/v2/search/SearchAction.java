/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.search;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;

import org.apache.http.NameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedList;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.RestClientUtils;
import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;

/**
 * 검색/카테고리 비즈니스 로직.
 */
@ContextSingleton
public class SearchAction {

	// 쿠키에서 최근검색어 리스트에 사용된 split character
	private static final String SEARCH_COOKIE_SPLIT_CHAR = "@";

	public static final String SEARCH_SPLIT_SEPERATOR = "\\|";

	@Inject
	private RestClient restClient;

	/**
	 * 연관 검색어 목록조회
	 */
	public RelatedKeywordList getRelatedKeywords(String keyword) {
		return RestClientUtils.INSTANCE.get(restClient, ServerUrls.REST.RELATED_KEYWORD_LIST + "?query={query}", RelatedKeywordList.class, keyword);
	}

	/**
	 * 인기 검색어 목록조회
	 */
	public PopularKeywordList getPopularKeywords() {
		return RestClientUtils.INSTANCE.get(restClient, ServerUrls.REST.POPULAR_KEYWORD_LIST, PopularKeywordList.class);
	}

	/**
	 * 검색 A/B 결과 조회
	 * @return
     */
	public String getSearchAB()
	{
		return RestClientUtils.INSTANCE.get(restClient, ServerUrls.REST.SEARCH_AB, String.class);
	}

	public LinkedList<RecentKeyword> getRecentKeywords(Context context) {
		return getKeywordsWithName(context, Keys.COOKIE.SEARCH);
	}

	public LinkedList<RecentKeyword> getRecentDateKeywords(Context context) {
		return getKeywordsWithName(context, Keys.COOKIE.SEARCH_ADD_DATE);
	}

	/**
	 * 최근 검색어 목록 조회. 쿠키에서 가져옴.
	 *
	 * @return 목록이 없으면 null.
	 */
	public LinkedList<RecentKeyword> getKeywordsWithName(Context context, String cookieName) {
		LinkedList<RecentKeyword> keywordsList = null;
		NameValuePair pair = CookieUtils.getWebviewCookie(context, cookieName);

		if (pair != null) {
			String value = pair.getValue();
			try {
				String str = URLDecoder.decode(value, "utf-8");
				String[] strList = str.split(SEARCH_COOKIE_SPLIT_CHAR);
				int length = strList.length;
				if (length > 0) {
					keywordsList = new LinkedList<RecentKeyword>();
					// 쿠키에서는 최근검색어가 뒤에 붙기 때문에 끝에서부터 가져옴.
					for (int i = length - 1; i >= 0; i--) {
						if (!strList[i].isEmpty()) {
							// keyword inputType은 임의로 지정
							keywordsList.add(new RecentKeyword(strList[i],
									RecentKeyword.InputType.DIRECT));
						}
					}
				}
			} catch (UnsupportedEncodingException e) {
				// 10/19 품질팀 요청
				// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
				// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
				// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
				Ln.e(e);
			} catch (IllegalArgumentException ie) {
				//Illegal hex characters in escape (%) pattern 대응
				Ln.e(ie);
			}
		}

		return keywordsList;
	}

	/**
	 * 최근 검색어 한 개 삭제
	 */
	/**
	 * 최근 검색어 한 개 삭제
	 */
	public void deleteRecentKeyword(Context context, RecentKeyword keyword) {
		// 쿠키에서 전체 검색어 리스트를 가져온다
		LinkedList<RecentKeyword> list = getRecentKeywords(context);
		if (list == null) {
			list = new LinkedList<RecentKeyword>();
		}

		// 리스트에서 해당 검색어 삭제
		list.remove(keyword);

		// 쿠키에 다시 반영
		if (list.isEmpty()) {
			deleteAllRecentKeyword(context);
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = list.size() - 1; i >= 0; i--) {
				sb.append(SEARCH_COOKIE_SPLIT_CHAR).append(list.get(i).keyword);
			}

			try {
				String searchString = URLEncoder.encode(sb.toString(), "utf-8");
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
					CookieUtils.resetSearchCookie(context, searchString);
					CookieManager.getInstance().flush();
				} else{
					CookieSyncManager cookieSyncMngr= CookieSyncManager.createInstance(context);
					cookieSyncMngr.startSync();
					CookieUtils.resetSearchCookie(context, searchString);
					cookieSyncMngr.stopSync();
					cookieSyncMngr.sync();
				}

			} catch (UnsupportedEncodingException e) {
				// 실패하면 그냥 검색어 모두 삭제
				deleteAllRecentKeyword(context);

				// 10/19 품질팀 요청
				// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
				// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
				// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
				Ln.e(e);
			}
		}
	}

	public void deleteRecentKeywordAddDate(Context context, RecentKeyword keyword) {
		if (keyword == null) {
			keyword = new RecentKeyword();
		}
		// 쿠키에서 전체 검색어 리스트를 가져온다
		LinkedList<RecentKeyword> list = getRecentDateKeywords(context);
		if (list == null) {
			list = new LinkedList<RecentKeyword>();
		}

		// 검색어가 변경되어 해당 keyword의 스트링 영역이 리스트에 있는경우 조회하여 삭제
		try {
			RecentKeyword deleteKeyword = null;
			for (RecentKeyword item : list) {
				if(item.keyword.split(SEARCH_SPLIT_SEPERATOR)[0].trim().equals(keyword.keyword.trim())) {
					deleteKeyword = item;
					break;
				}
			}

			list.remove(deleteKeyword);
		}
		catch (NullPointerException e) {
			Ln.e(e.getMessage());
		}
		catch (IndexOutOfBoundsException e) {
			Ln.e(e.getMessage());
		}
//		// 리스트에서 해당 검색어 삭제
//		list.remove(keyword);

		// 쿠키에 다시 반영
		if (list.isEmpty()) {
			deleteAllRecentKeywordAddDate(context);
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = list.size() - 1; i >= 0; i--) {
				sb.append(SEARCH_COOKIE_SPLIT_CHAR).append(list.get(i).keyword);
			}

			try {
				String searchString = URLEncoder.encode(sb.toString(), "utf-8");
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
					CookieUtils.resetSearchAddDateCookie(context, searchString);
					CookieManager.getInstance().flush();
				} else{
					CookieSyncManager cookieSyncMngr= CookieSyncManager.createInstance(context);
					cookieSyncMngr.startSync();
					CookieUtils.resetSearchAddDateCookie(context, searchString);
					cookieSyncMngr.stopSync();
					cookieSyncMngr.sync();
				}

			} catch (UnsupportedEncodingException e) {
				// 실패하면 그냥 검색어 모두 삭제
				deleteAllRecentKeywordAddDate(context);

				// 10/19 품질팀 요청
				// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
				// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
				// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
				Ln.e(e);
			}
		}
	}

	/**
	 * 최근 검색어 모두 삭제
	 */
	public void deleteAllRecentKeyword(Context context) {
		// 쿠키에서 전체 검색어 리스트를 가져온다

		StringBuilder sb = new StringBuilder();
		sb.append(SEARCH_COOKIE_SPLIT_CHAR);

		try {
			String searchString = URLEncoder.encode(sb.toString(), "utf-8");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
				CookieUtils.resetSearchCookie(context, searchString);
				CookieManager.getInstance().flush();
			} else{
				CookieSyncManager cookieSyncMngr= CookieSyncManager.createInstance(context);
				cookieSyncMngr.startSync();
				CookieUtils.resetSearchCookie(context, searchString);
				cookieSyncMngr.stopSync();
				cookieSyncMngr.sync();
			}

		} catch (UnsupportedEncodingException e) {
			// 실패하면 그냥 검색어 모두 삭제
			//deleteAllRecentKeyword(context);

			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(e);
		}
	}
	public void deleteAllRecentKeywordAddDate(Context context) {
		// 쿠키에서 전체 검색어 리스트를 가져온다

		StringBuilder sb = new StringBuilder();
		sb.append(SEARCH_COOKIE_SPLIT_CHAR);

		try {
			String searchString = URLEncoder.encode(sb.toString(), "utf-8");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
				CookieUtils.resetSearchAddDateCookie(context, searchString);
				CookieManager.getInstance().flush();
			} else{
				CookieSyncManager cookieSyncMngr= CookieSyncManager.createInstance(context);
				cookieSyncMngr.startSync();
				CookieUtils.resetSearchAddDateCookie(context, searchString);
				cookieSyncMngr.stopSync();
				cookieSyncMngr.sync();
			}

		} catch (UnsupportedEncodingException e) {
			// 실패하면 그냥 검색어 모두 삭제
			//deleteAllRecentKeyword(context);

			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(e);
		}
	}
}
