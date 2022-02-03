/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.bestshop.MenuGiftRecommendFixVH;
import gsshop.mobile.v2.home.shop.department.DepartmentFpcPVH;
import gsshop.mobile.v2.home.shop.department.PdvVH;
import gsshop.mobile.v2.home.shop.flexible.beauty.BanBeaytyNoData;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.BanMLPrd2;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.BanMLPrdAlarmMobileLive;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.BanMLPrdBest;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.BanMLPrdMobileLive;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.BanMLPrdNoticed;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.BanMLPrdTitleSubAddedMobileLive;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.BanSldMobileLiveSmallVH;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.BanSldMobileLiveBroadVH;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.TitleMobileLiveVH;
import gsshop.mobile.v2.home.shop.flexible.vip.BanVIPBeneGbaVH;
import gsshop.mobile.v2.home.shop.flexible.vip.BanVIPCardGbaVH;
import gsshop.mobile.v2.home.shop.flexible.vip.BanVIPGbaVH;
import gsshop.mobile.v2.home.shop.flexible.vip.BanVIPImgGbaVH;
import gsshop.mobile.v2.home.shop.flexible.vip.GrPrd1VIPListGbaVH;
import gsshop.mobile.v2.home.shop.flexible.vip.GrPrd1VIPListGbbVH;
import gsshop.mobile.v2.home.shop.flexible.vip.GrPrd1VipListGbcVH;
import gsshop.mobile.v2.home.shop.flexible.vip.GrPrd2VIPListGbaVH;
import gsshop.mobile.v2.home.shop.flexible.vip.GrPrd2VIPListGbbVH;
import gsshop.mobile.v2.home.shop.flexible.vip.PrdCB1VIPGbaVH;
import gsshop.mobile.v2.home.shop.flexible.wine.BanTitleWineVH;
import gsshop.mobile.v2.home.shop.ltype.BanTxtImgLnkGbaVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.BanMoreGbaVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.BanNoPrdVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.BanTxtCstGBAVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.MolocoPrdCSqVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.PmoT1xxxVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.PmoT2xxxVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.PrdCB1VH;
import gsshop.mobile.v2.home.shop.renewal.flexible.PrdCCstSqVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.PrdCSqVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.PrdPasSqVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.RtsPrdCCstSqVH;
import gsshop.mobile.v2.home.shop.renewal.publicusage.Prd1_640VH;
import gsshop.mobile.v2.home.shop.renewal.publicusage.Prd1_ListVH;
import gsshop.mobile.v2.home.shop.renewal.publicusage.Prd2VH;
import gsshop.mobile.v2.home.shop.retail.PmoGsrCGba;
import gsshop.mobile.v2.home.shop.todayopen.TodayOpenAdViewHolder;
import gsshop.mobile.v2.home.shop.vod.BanImgSldGbaVH;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;

/**
 *
 */
public class FlexibleShopAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    protected ShopInfo mInfo;
    protected final Context mContext;

    //롤리팝 버전코드
    private final int VERSION_CODES_LOLLIPOP = 21;
    //롤리팝 여부 플래그
    private boolean isLollipop = false;

    /**
     * 뷰타입 define
     */
    private String PRD_C_CST_SQ_VIEWTYPE = "PRD_C_CST_SQ";

    public FlexibleShopAdapter(Context context) {
        this.mContext = context;
        //롤리팝 이상에서만 25,50...번째 아이템 노출에 대한 GTM 로깅 적용
        if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES_LOLLIPOP) {
            isLollipop = true;
        }
    }

    public void setInfo(ShopInfo info) {
        mInfo = info;
    }

    /**
     * 컨텐츠 정보를 업데이트한다.
     *
     * @param item 컨텐츠정보
     * @param uKey 컨텐츠 리스트에서 해당 컨텐츠를 찾기 위한 고유키
     */
    public synchronized void updateInfo(SectionContentList item, long uKey) {
        int pos = getItemPosition(uKey);
        if (pos == -1) {
            return;
        }

        if (item != null) {
            mInfo.contents.get(pos).sectionContent = item;
            notifyItemChanged(pos);
        } else {
            mInfo.contents.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    /**
     * PRD_C_CST_SQ 아이템을 목록에 추가
     *
     * @param item 추가할 아이템
     * @param pos  추가될 위치
     */
    public synchronized void addInfoDT(SectionContentList item, int pos) {
        int minPos = 0; //최소위치

        if (mInfo != null && mInfo.contents != null) {
            if (pos < 0 || pos > mInfo.contents.size() || item == null) {
                return;
            }
            if (item != null) {
                if (PRD_C_CST_SQ_VIEWTYPE.equals(item.viewType)) {
                    for (int i = 0; i < mInfo.contents.size(); i++) {
                        if (ViewHolderType.BANNER_TYPE_HOME_TV_LIVE == (mInfo.contents.get(i).type)) {
                            minPos++;
                        }
                        if (ViewHolderType.BANNER_TYPE_HOME_TV_DATA == (mInfo.contents.get(i).type)) {
                            minPos++;
                        }
                        if (ViewHolderType.BANNER_TYPE_MOBILE_LIVE == (mInfo.contents.get(i).type)) {
                            minPos++;
                        }
                    }

                    //생방영역 추가시 생방영역 밑에 추가되도록
                    if (pos < 3) {
                        pos = minPos;
                    }

                    //타이틀 바로 밑에
                    if (ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBB == (mInfo.contents.get(pos - 1).type)) {
                        pos++;
                    }
                    //실시간 인기검색어
                    if (ViewHolderType.VIEW_TYPE_BAN_TXT_EXP_GBA == (mInfo.contents.get(pos).type)) {
                        pos++;
                    }
                    //빠른 배송 서비스
                    if (ViewHolderType.VIEW_TYPE_BAN_IMG_C5_GBA == (mInfo.contents.get(pos).type)) {
                        pos++;
                    }

                    if (pos < mInfo.contents.size()) {
                        ShopInfo.ShopItem shopItem = new ShopInfo.ShopItem();
                        shopItem.sectionContent = item;
                        shopItem.type = ViewHolderType.VIEW_TYPE_PRD_C_CST_SQ;

                        mInfo.contents.add(pos, shopItem);
                        notifyItemInserted(pos);
                    }
                }
            }
        }
    }

    /**
     * PRD_C_CST_SQ 아이템을 목록에서 제거
     *
     * @param pos
     * @return 지워진 아이템 반환
     */
    public synchronized ShopInfo.ShopItem removeInfoDT(int pos) {
        if (mInfo != null && mInfo.contents != null) {
            if (pos == -1 || pos > mInfo.contents.size()) {
                return null;
            }
            ShopInfo.ShopItem removedItem = mInfo.contents.get(pos);
            mInfo.contents.remove(pos);
            notifyItemRemoved(pos);
            return removedItem;
        }
        return null;
    }

    /**
     * 컨텐츠 리스트에서 컨텐츠의 위치를 찾는다.
     *
     * @param uKey 컨텐츠 리스트에서 해당 컨텐츠를 찾기 위한 고유키
     * @return 컨텐츠 리스트에서 해당 컨텐츠의 위치
     */
    private int getItemPosition(long uKey) {
        int pos = -1;
        for (int i = 0; i < mInfo.contents.size(); i++) {
            if (mInfo.contents.get(i).timestamp == uKey) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    public ShopInfo getInfo() {
        return mInfo;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getItemViewType(int position) {
        return mInfo.contents.get(position).type;
    }

    /**
     * @return int
     */
    @Override
    public int getItemCount() {
        return isEmpty(mInfo) ? 0 : mInfo.contents.size();
    }

    public Object getItem(int position) {
        if (mInfo == null || mInfo.contents == null) {
            return null;
        }
        return mInfo.contents.get(position);
    }


    /**
     *
     */
    @Override
    public void onBindViewHolder(BaseViewHolder viewHolder, int position) {
        //Ln.i("FlexibleShopAdapter onBindViewHolder - position : " + position);

        String action = GTMEnum.GTM_NONE;
        String label = GTMEnum.GTM_NONE;
        // String tab = GTMEnum.GTM_NONE;
        String sectionName = GTMEnum.GTM_NONE;
        String productCode = GTMEnum.GTM_NONE;

        /**
         * PRD_POSITION의 배수인 경우 GTM에 이벤트를 전달한다. (0 포함안함)
         * - 주입이 안들어간 관계로 try catch 묶음
         * - Flexibles(리사이클) 구조가 들어간곳의 Impression
         * - SRL 타입이 노출되었는지 알수 있을까??
         */
        if (isLollipop) {
            if (position != 0 && position % Keys.PRD_POSITION == 0) {
                //GTM 클릭이벤트 전달
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {
                    try {
                        String tempAction = String.format("%s_%s_%s_%s",
                                GTMEnum.GTM_ACTION_HEADER, GTMEnum.GTM_ACTION_SERVICE_APP, mInfo.sectionList.sectionName, GTMEnum.GTM_IMPRESSION_TAIL);
                        GTMAction.sendEvent(mContext, GTMEnum.GTM_AREA_CATEGORY,
                                tempAction, String.valueOf(position));
                    } catch (Exception e) {
                        // 10/19 품질팀 요청
                        // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                        // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                        // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                        Ln.e(e);
                    }
                }
            }
        }
        switch (viewHolder.getItemViewType()) {
            case ViewHolderType.BANNER_TYPE_RENEWAL_PMO_T3_IMAGE_AB:
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {

                    if (mInfo.contents != null && mInfo.contents.get(position) != null) {
                        SectionContentList temp = mInfo.contents.get(position).sectionContent;
                        if (!TextUtils.isEmpty(temp.dealNo) && !"0".equals(temp.dealNo)) {
                            productCode = temp.dealNo;
                        } else if (!TextUtils.isEmpty(temp.prdid) && !"0".equals(temp.prdid)) {
                            productCode = temp.prdid;
                        }

                        // 서버 매장에 대한 처리
                        if (mInfo.sectionList.subMenuList != null
                                && !mInfo.sectionList.subMenuList.isEmpty()) {
                            if (mInfo.sectionList.subMenuList.get(mInfo.tabIndex) != null
                                    && mInfo.sectionList.subMenuList
                                    .get(mInfo.tabIndex).sectionName != null) {
                                sectionName = String.format("%s_%s", mInfo.sectionList.sectionName,
                                        mInfo.sectionList.subMenuList.get(mInfo.tabIndex).sectionName);
                            }
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                productCode);
                        label = String.format("%s_%s", Integer.toString(position), temp.productName);
                    }
                }
                ((FlexibleBannerImageViewHolder_AB) viewHolder).onBindViewHolder(mContext, position, mInfo, action, label, null);
                break;

            case ViewHolderType.VIEW_TYPE_L:
                // 단품 기본형.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {

                    if (mInfo.contents != null && mInfo.contents.get(position) != null) {
                        SectionContentList temp = mInfo.contents.get(position).sectionContent;
                        if (!TextUtils.isEmpty(temp.dealNo) && !"0".equals(temp.dealNo)) {
                            productCode = temp.dealNo;
                        } else if (!TextUtils.isEmpty(temp.prdid) && !"0".equals(temp.prdid)) {
                            productCode = temp.prdid;
                        }

                        // 서버 매장에 대한 처리
                        if (mInfo.sectionList.subMenuList != null
                                && !mInfo.sectionList.subMenuList.isEmpty()) {
                            if (mInfo.sectionList.subMenuList.get(mInfo.tabIndex) != null
                                    && mInfo.sectionList.subMenuList
                                    .get(mInfo.tabIndex).sectionName != null) {
                                sectionName = String.format("%s_%s", mInfo.sectionList.sectionName,
                                        mInfo.sectionList.subMenuList.get(mInfo.tabIndex).sectionName);
                            }
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                productCode);
                        label = String.format("%s_%s", Integer.toString(position), temp.productName);
                    }
                }

                ((LVH) viewHolder).onBindViewHolder(mContext, position, mInfo,
                        action, label, null);
                break;

            case ViewHolderType.VIEW_TYPE_PRD_1_640:
                // 단품 기본형.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {

                    if (mInfo.contents != null && mInfo.contents.get(position) != null) {
                        SectionContentList temp = mInfo.contents.get(position).sectionContent;
                        if (!TextUtils.isEmpty(temp.dealNo) && !"0".equals(temp.dealNo)) {
                            productCode = temp.dealNo;
                        } else if (!TextUtils.isEmpty(temp.prdid) && !"0".equals(temp.prdid)) {
                            productCode = temp.prdid;
                        }

                        // 서버 매장에 대한 처리
                        if (mInfo.sectionList.subMenuList != null
                                && !mInfo.sectionList.subMenuList.isEmpty()) {
                            if (mInfo.sectionList.subMenuList.get(mInfo.tabIndex) != null
                                    && mInfo.sectionList.subMenuList
                                    .get(mInfo.tabIndex).sectionName != null) {
                                sectionName = String.format("%s_%s", mInfo.sectionList.sectionName,
                                        mInfo.sectionList.subMenuList.get(mInfo.tabIndex).sectionName);
                            }
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                productCode);
                        label = String.format("%s_%s", Integer.toString(position), temp.productName);
                    }
                }

                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null);
                break;

            case ViewHolderType.VIEW_TYPE_PRD_1_LIST:   //홈 하단 개인화구좌 (편성표 부상품 형태)
            case ViewHolderType.VIEW_TYPE_PRD_2:           // 지금 베스트 체크박스 배너
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null);
                break;

            case ViewHolderType.VIEW_TYPE_FXCLIST:
                // 카테고리 배너형.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {
                    // 내가 어떤탭인지
                    action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                            mInfo.sectionList.sectionName, GTMEnum.GTM_ACTION_3DEPTH_TAIL);
                    label = String.format("%s", Integer.toString(position));
                }
                ((FxclistVH) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;
            case ViewHolderType.VIEW_TYPE_SUB_PRD_LIST_TEXT:
                // 카테고리 배너형.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {
                    // 내가 어떤탭인지
                    action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                            mInfo.sectionList.sectionName, GTMEnum.GTM_ACTION_3DEPTH_TAIL);
                    label = String.format("%s", Integer.toString(position));
                }
                ((SubPrdListTextVH) viewHolder).onBindViewHolder(mContext, -1, mInfo, action, label, null);
                break;

            case ViewHolderType.VIEW_TYPE_B_SIC:
            case ViewHolderType.BANNER_TYPE_ROLL_IMAGE_NEW_GBA:
            case ViewHolderType.BANNER_TYPE_ROLL_IMAGE_NEW_GBH:
            case ViewHolderType.VIEW_TYPE_PMO_T2_IMG_C:
            case ViewHolderType.VIEW_TYPE_BAN_SLD_GBB:
                // 롤링 이미지 배너형.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {
                    if (mInfo.contents.get(position) != null
                            && mInfo.contents.get(position).sectionContent != null) {
                        // 서버 매장에 대한 처리
                        if (mInfo.sectionList.subMenuList != null
                                && !mInfo.sectionList.subMenuList.isEmpty()) {
                            if (mInfo.sectionList.subMenuList.get(mInfo.tabIndex) != null
                                    && mInfo.sectionList.subMenuList
                                    .get(mInfo.tabIndex).sectionName != null) {
                                sectionName = String.format("%s_%s", mInfo.sectionList.sectionName,
                                        mInfo.sectionList.subMenuList.get(mInfo.tabIndex).sectionName);
                            }
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }

                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                mInfo.contents.get(position).sectionContent.viewType);
                        label = String.format("%s", Integer.toString(position));
                    }
                }
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null);
                break;
            case ViewHolderType.BANNER_TYPE_BRAND_ROLL_IMAGE:
                // 롤링 이미지 배너형.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {
                    if (mInfo.contents.get(position) != null
                            && mInfo.contents.get(position).sectionContent != null) {
                        //브랜드쇼룸의 첫번째 아이템 brandBanner의 경우 viewType값이 없어 "NONE"로 세팅
                        String viewType = mInfo.contents.get(position).sectionContent.viewType;
                        viewType = TextUtils.isEmpty(viewType) ? GTMEnum.GTM_NONE : viewType;
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, mInfo.sectionList.sectionName,
                                viewType);
                        label = String.format("%s", Integer.toString(position));
                    }
                }
                ((FlexibleBannerBrandRollImageViewHolder) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;

            case ViewHolderType.BANNER_TYPE_TV_ITEM:
                // 롤링배너 TVShoping.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {
                    if (mInfo.contents.get(position) != null
                            && mInfo.contents.get(position).sectionContent != null) {
                        //TV 쇼핑탭의 tvlivebanner는 뷰타입이 없으니 다른곳에 복사해서 쓰면 안됨
                        // 서버 매장에 대한 처리
                        if (mInfo.sectionList.subMenuList != null
                                && !mInfo.sectionList.subMenuList.isEmpty()) {
                            if (mInfo.sectionList.subMenuList.get(mInfo.tabIndex) != null
                                    && mInfo.sectionList.subMenuList
                                    .get(mInfo.tabIndex).sectionName != null) {
                                sectionName = String.format("%s_%s", mInfo.sectionList.sectionName,
                                        mInfo.sectionList.subMenuList.get(mInfo.tabIndex).sectionName);
                            }
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                GTMEnum.GTM_ACTION_ONAIR_BANNER_TAIL);
                        label = String.format("%s", Integer.toString(position));
                    }
                }
                ((FlexibleBannerTvShopRollImageViewHolder) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;
            case ViewHolderType.VIEW_TYPE_SE:
                // 롤링 이미지 배너형 - 왼쪽/오른쪽 미리보기 포함.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {
                    if (mInfo.contents.get(position) != null
                            && mInfo.contents.get(position).sectionContent != null) {
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, mInfo.sectionList.sectionName,
                                mInfo.contents.get(position).sectionContent.viewType);
                        label = String.format("%s", Integer.toString(position));
                    }
                }
                ((SeVH) viewHolder).onBindViewHolder(mContext,
                        position, mInfo, action, label, null);
                break;
            case ViewHolderType.BANNER_TYPE_BAND:    // 띠배너.
            case ViewHolderType.BANNER_TYPE_IMAGE: // "I" "B_IS""B_IM" "B_IL" "B_IXL" "B_IM440"
            case ViewHolderType.VIEW_TYPE_B_ISS: // "B_ISS"
            case ViewHolderType.BANNER_TYPE_BRAND_CONTENT:
            case ViewHolderType.BANNER_TYPE_ROLL_ONE_IMAGE:
            case ViewHolderType.BANNER_TYPE_BAND_NO_PADDING:
                // 이미지 배너형.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {
                    if (mInfo.contents.get(position) != null
                            && mInfo.contents.get(position).sectionContent != null) {
                        // 서버 매장에 대한 처리
                        if (mInfo.sectionList.subMenuList != null
                                && !mInfo.sectionList.subMenuList.isEmpty()) {
                            if (mInfo.sectionList.subMenuList.get(mInfo.tabIndex) != null
                                    && mInfo.sectionList.subMenuList
                                    .get(mInfo.tabIndex).sectionName != null) {
                                String tabMenu;
                                if (viewHolder.getItemViewType() == ViewHolderType.BANNER_TYPE_BRAND_CONTENT) {
                                    //브랜드쇼룸 상품의 경우 상품이 어떤 탭에 포함되어 있는지 확인한다.
                                    tabMenu = mInfo.sectionList.subMenuList.get(getTabmenuIndex(position)).sectionName;
                                } else {
                                    tabMenu = mInfo.sectionList.subMenuList.get(mInfo.tabIndex).sectionName;
                                }
                                sectionName = String.format("%s_%s", mInfo.sectionList.sectionName,
                                        tabMenu);
                            }
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }
                        String linkUrl = mInfo.contents.get(position).sectionContent.linkUrl;
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                mInfo.contents.get(position).sectionContent.viewType);
                        label = String.format("%s_%s", Integer.toString(position),
                                TextUtils.isEmpty(linkUrl) ? label : linkUrl);
                    }
                }
                if (viewHolder.getItemViewType() == ViewHolderType.VIEW_TYPE_B_ISS) {
                    ((BIssVH) viewHolder).onBindViewHolder(mContext, position, mInfo,
                            action, label, null);
                } else {
                    ((FlexibleBannerImageViewHolder) viewHolder).onBindViewHolder(mContext, position, mInfo,
                            action, label, null);
                }
                break;

            case ViewHolderType.VIEW_TYPE_MAP_CX_TXT_GBA:
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {
                    if (mInfo.contents.get(position) != null
                            && mInfo.contents.get(position).sectionContent != null) {

                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }
                        String linkUrl = mInfo.contents.get(position).sectionContent.linkUrl;
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                mInfo.contents.get(position).sectionContent.viewType);
                        label = String.format("%s_%s", Integer.toString(position),
                                TextUtils.isEmpty(linkUrl) ? label : linkUrl);
                    }
                }
                ((MapCxTxtGbaVH) viewHolder).onBindViewHolder(mContext, position, mInfo,
                        action, label, null);
                break;

            case ViewHolderType.BANNER_TYPE_BRAND_CATEGORY:
                ((FlexibleBannerBrandCategoryViewHolder) viewHolder).onBindViewHolder(mContext, position, mInfo,
                        action, label, null);
                break;
            case ViewHolderType.BANNER_TYPE_BRAND_DIVIDER:
                ((FlexibleBannerBrandDividerViewHolder) viewHolder).onBindViewHolder(mContext, position, mInfo,
                        action, label, null);
                break;

            case ViewHolderType.VIEW_TYPE_SL:
                // todays hot 배너.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {
                    if (mInfo.contents.get(position) != null
                            && mInfo.contents.get(position).sectionContent != null) {
                        // 서버 매장에 대한 처리
                        if (mInfo.sectionList.subMenuList != null
                                && !mInfo.sectionList.subMenuList.isEmpty()) {
                            if (mInfo.sectionList.subMenuList.get(mInfo.tabIndex) != null
                                    && mInfo.sectionList.subMenuList
                                    .get(mInfo.tabIndex).sectionName != null) {
                                sectionName = String.format("%s_%s", mInfo.sectionList.sectionName,
                                        mInfo.sectionList.subMenuList.get(mInfo.tabIndex).sectionName);
                            }
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                mInfo.contents.get(position).sectionContent.viewType);
                        label = String.format("%s", Integer.toString(position));
                    }
                }
                ((SlVH) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;
            case ViewHolderType.VIEW_TYPE_TSL:
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {
                    if (mInfo.contents.get(position) != null
                            && mInfo.contents.get(position).sectionContent != null) {
                        // 서버 매장에 대한 처리
                        if (mInfo.sectionList.subMenuList != null
                                && !mInfo.sectionList.subMenuList.isEmpty()) {
                            if (mInfo.sectionList.subMenuList.get(mInfo.tabIndex) != null
                                    && mInfo.sectionList.subMenuList
                                    .get(mInfo.tabIndex).sectionName != null) {
                                sectionName = String.format("%s_%s", mInfo.sectionList.sectionName,
                                        mInfo.sectionList.subMenuList.get(mInfo.tabIndex).sectionName);
                            }
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                mInfo.contents.get(position).sectionContent.viewType);
                        label = String.format("%s", Integer.toString(position));
                    }
                }
                ((TslVH) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;
            case ViewHolderType.BANNER_TYPE_TITLE:
            case ViewHolderType.BANNER_MART_TYPE_TITLE:
                // 타이틀 배너.
                ((BTscVH) viewHolder).onBindViewHolder(mContext, position, mInfo,
                        null, null, null);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBB:
                // 타이틀 배너2.
                ((BanTxtImgLnkGbbVH) viewHolder).onBindViewHolder(mContext, position, mInfo,
                        null, null, null);
                break;
            case ViewHolderType.VIEW_TYPE_B_MIA:
                // HOT 배너.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {
                    if (mInfo.contents.get(position) != null
                            && mInfo.contents.get(position).sectionContent != null) {
                        // 서버 매장에 대한 처리
                        if (mInfo.sectionList.subMenuList != null
                                && !mInfo.sectionList.subMenuList.isEmpty()) {
                            if (mInfo.sectionList.subMenuList.get(mInfo.tabIndex) != null
                                    && mInfo.sectionList.subMenuList
                                    .get(mInfo.tabIndex).sectionName != null) {
                                sectionName = String.format("%s_%s", mInfo.sectionList.sectionName,
                                        mInfo.sectionList.subMenuList.get(mInfo.tabIndex).sectionName);
                            }
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                mInfo.contents.get(position).sectionContent.viewType);
                        label = String.format("%s", Integer.toString(position));
                    }
                }
                ((BMiaVH) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;
            case ViewHolderType.VIEW_TYPE_DSL_X:
                // no1 deal 배너.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {
                    // 베스트딜에 있는 Data 구조는 플렉서블 하지 않다. View 타입이 존재 하지 않는다.
                    action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                            mInfo.sectionList.sectionName, mInfo.contents.get(position).sectionContent.viewType);
                    label = String.format("%s", Integer.toString(position));
                }
                ((DslXVH) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;
            case ViewHolderType.VIEW_TYPE_DSL_A2:
                // no1 deal 배너.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {
                    // 베스트딜에 있는 Data 구조는 플렉서블 하지 않다. View 타입이 존재 하지 않는다.
                    action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                            mInfo.sectionList.sectionName, mInfo.contents.get(position).sectionContent.viewType);
                    label = String.format("%s", Integer.toString(position));
                }
                viewHolder.onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;
            case ViewHolderType.BANNER_TYPE_TV_LIVE:
                // tv live 배너.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null && mInfo.tvLiveBanner != null) {
                    // 베스트딜에 있는 Data 구조는 플렉서블 하지 않다. View 타입이 존재 하지 않는다.
                    // if( mInfo.contents.get(position) != null &&
                    // mInfo.contents.get(position).sectionContent != null )
                    {
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                                mInfo.sectionList.sectionName, GTMEnum.GTM_ACTION_LIVE_TAIL);
                        label = String.format("%s", mInfo.tvLiveBanner.productName);
                        sectionName = mInfo.sectionList.sectionName;
                    }
                }
                ((BestDealBannerTVLiveViewHolder) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, sectionName);
                break;
            case ViewHolderType.VIEW_TYPE_SUB_SEC_LINE:
            case ViewHolderType.VIEW_TYPE_API_SUB_SEC_LINE:
                // event item banner
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {
                    action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                            mInfo.sectionList.sectionName, GTMEnum.GTM_ACTION_3DEPTH_TAIL);
                    label = String.format("%s", Integer.toString(position));
                }
                ((SubSecLineVH) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;
            case ViewHolderType.BANNER_TYPE_FOOTER:
                // 풋터 배너
                // 하지마
                ((FlexibleBannerFooterViewHolder) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, null, null, null);
                break;
            case ViewHolderType.VIEW_TYPE_SRL:
            case ViewHolderType.VIEW_TYPE_API_SRL:
                // 추천딜.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {

                    /**
                     * 추천딜 영역에 대한 imp..  노출 트래킹 추가 == SRL 타입
                     * 20151115 이벤트 태킹 너무 많은데 ;;
                     */
                    try {
                        String tempAction = String.format("%s_%s_%s_%s",
                                GTMEnum.GTM_ACTION_HEADER, GTMEnum.GTM_ACTION_SERVICE_APP, mInfo.sectionList.sectionName, GTMEnum.GTM_IMPRESSION_SRL_TAIL);
                        GTMAction.sendEvent(mContext, GTMEnum.GTM_AREA_CATEGORY,
                                tempAction, String.valueOf(position));
                    } catch (Exception e) {
                        // 10/19 품질팀 요청
                        // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                        // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                        // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                        Ln.e(e);
                    }

                    if (mInfo.contents != null && mInfo.contents.get(position) != null) {
//                        SectionContentList temp = mInfo.contents.get(position).sectionContent;
//                        if (!TextUtils.isEmpty(temp.dealNo) && !"0".equals(temp.dealNo)) {
//                            productCode = temp.dealNo;
//                        } else if (!TextUtils.isEmpty(temp.prdid) && !"0".equals(temp.prdid)) {
//                            productCode = temp.prdid;
//                        }

                        // 서버 매장에 대한 처리
                        if (mInfo.sectionList.subMenuList != null
                                && !mInfo.sectionList.subMenuList.isEmpty()) {
                            if (mInfo.sectionList.subMenuList.get(mInfo.tabIndex) != null
                                    && mInfo.sectionList.subMenuList
                                    .get(mInfo.tabIndex).sectionName != null) {
                                sectionName = String.format("%s_%s", mInfo.sectionList.sectionName,
                                        mInfo.sectionList.subMenuList.get(mInfo.tabIndex).sectionName);
                            }
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }

                        //_뷰타입 추가
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                mInfo.contents.get(position).sectionContent.viewType);
                        //상품명 없음
                        label = String.format("%s", Integer.toString(position));
                    }
                }
//                ((SrlVH) viewHolder).onBindViewHolder(mContext, position, mInfo,
//                        action, label, null);
                if (viewHolder.getItemViewType() == ViewHolderType.VIEW_TYPE_SRL) {
                    ((SrlVH) viewHolder).onBindViewHolder(mContext, position, mInfo,
                            action, label, null);
                } else if (viewHolder.getItemViewType() == ViewHolderType.VIEW_TYPE_API_SRL) {
                    ((PrdCSqVH) viewHolder).onBindViewHolder(mContext, position, mInfo,
                            action, label, null);
                }

                break;
            case ViewHolderType.VIEW_TYPE_B_HIM:
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {

                    if (mInfo.contents != null && mInfo.contents.get(position) != null) {
//                        SectionContentList temp = mInfo.contents.get(position).sectionContent;
//                        if (!TextUtils.isEmpty(temp.dealNo) && !"0".equals(temp.dealNo)) {
//                            productCode = temp.dealNo;
//                        } else if (!TextUtils.isEmpty(temp.prdid) && !"0".equals(temp.prdid)) {
//                            productCode = temp.prdid;
//                        }

                        // 서버 매장에 대한 처리
                        if (mInfo.sectionList.subMenuList != null
                                && !mInfo.sectionList.subMenuList.isEmpty()) {
                            if (mInfo.sectionList.subMenuList.get(mInfo.tabIndex) != null
                                    && mInfo.sectionList.subMenuList
                                    .get(mInfo.tabIndex).sectionName != null) {
                                sectionName = String.format("%s_%s", mInfo.sectionList.sectionName,
                                        mInfo.sectionList.subMenuList.get(mInfo.tabIndex).sectionName);
                            }
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }

                        //_뷰타입 추가
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                mInfo.contents.get(position).sectionContent.viewType);
                        //상품명 없음
                        label = String.format("%s", Integer.toString(position));
                    }
                }
                ((BHimVH) viewHolder).onBindViewHolder(mContext, position, mInfo,
                        action, label, null);
                break;

            case ViewHolderType.VIEW_TYPE_PC:
            case ViewHolderType.VIEW_TYPE_B_DHS:
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {


                    if (mInfo.contents != null && mInfo.contents.get(position) != null) {
//                        SectionContentList temp = mInfo.contents.get(position).sectionContent;
//                        if (!TextUtils.isEmpty(temp.dealNo) && !"0".equals(temp.dealNo)) {
//                            productCode = temp.dealNo;
//                        } else if (!TextUtils.isEmpty(temp.prdid) && !"0".equals(temp.prdid)) {
//                            productCode = temp.prdid;
//                        }

                        // 서버 매장에 대한 처리
                        if (mInfo.sectionList.subMenuList != null
                                && !mInfo.sectionList.subMenuList.isEmpty()) {
                            if (mInfo.sectionList.subMenuList.get(mInfo.tabIndex) != null
                                    && mInfo.sectionList.subMenuList
                                    .get(mInfo.tabIndex).sectionName != null) {
                                sectionName = String.format("%s_%s", mInfo.sectionList.sectionName,
                                        mInfo.sectionList.subMenuList.get(mInfo.tabIndex).sectionName);
                            }
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }

                        //_뷰타입 추가
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                mInfo.contents.get(position).sectionContent.viewType);
                        //상품명 없음
                        label = String.format("%s", Integer.toString(position));
                    }
                }
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null);
                break;

            case ViewHolderType.VIEW_TYPE_SPL:
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {


                    if (mInfo.contents != null && mInfo.contents.get(position) != null) {
//                        SectionContentList temp = mInfo.contents.get(position).sectionContent;
//                        if (!TextUtils.isEmpty(temp.dealNo) && !"0".equals(temp.dealNo)) {
//                            productCode = temp.dealNo;
//                        } else if (!TextUtils.isEmpty(temp.prdid) && !"0".equals(temp.prdid)) {
//                            productCode = temp.prdid;
//                        }

                        // 서버 매장에 대한 처리
                        if (mInfo.sectionList.subMenuList != null
                                && !mInfo.sectionList.subMenuList.isEmpty()) {
                            if (mInfo.sectionList.subMenuList.get(mInfo.tabIndex) != null
                                    && mInfo.sectionList.subMenuList
                                    .get(mInfo.tabIndex).sectionName != null) {
                                sectionName = String.format("%s_%s", mInfo.sectionList.sectionName,
                                        mInfo.sectionList.subMenuList.get(mInfo.tabIndex).sectionName);
                            }
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }

                        //_뷰타입 추가
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                mInfo.contents.get(position).sectionContent.viewType);
                        //상품명 없음
                        label = String.format("%s", Integer.toString(position));
                    }
                }
                ((SplVH) viewHolder).onBindViewHolder(mContext, position, mInfo,
                        action, label, null);
                break;

            case ViewHolderType.VIEW_TYPE_SPC:
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {

                    if (mInfo.contents != null && mInfo.contents.get(position) != null) {
//                        SectionContentList temp = mInfo.contents.get(position).sectionContent;
//                        if (!TextUtils.isEmpty(temp.dealNo) && !"0".equals(temp.dealNo)) {
//                            productCode = temp.dealNo;
//                        } else if (!TextUtils.isEmpty(temp.prdid) && !"0".equals(temp.prdid)) {
//                            productCode = temp.prdid;
//                        }

                        // 서버 매장에 대한 처리
                        if (mInfo.sectionList.subMenuList != null
                                && !mInfo.sectionList.subMenuList.isEmpty()) {
                            if (mInfo.sectionList.subMenuList.get(mInfo.tabIndex) != null
                                    && mInfo.sectionList.subMenuList
                                    .get(mInfo.tabIndex).sectionName != null) {
                                sectionName = String.format("%s_%s", mInfo.sectionList.sectionName,
                                        mInfo.sectionList.subMenuList.get(mInfo.tabIndex).sectionName);
                            }
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }

                        //_뷰타입 추가
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                mInfo.contents.get(position).sectionContent.viewType);
                        //상품명 없음
                        label = String.format("%s", Integer.toString(position));
                    }
                }
                ((SpcVH) viewHolder).onBindViewHolder(mContext, position, mInfo,
                        action, label, null);
                break;

            case ViewHolderType.VIEW_TYPE_B_IG4XN:
                // event item banner
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null
                        && mInfo.contents.get(position) != null) {
                    action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                            mInfo.sectionList.sectionName, mInfo.contents.get(position).sectionContent.viewType);
                    label = String.format("%s", Integer.toString(position));
                }
                ((BIg4xnVH) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;
            case ViewHolderType.VIEW_TYPE_BTL:
                // event item banner
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null
                        && mInfo.contents.get(position) != null) {
                    action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                            mInfo.sectionList.sectionName, mInfo.contents.get(position).sectionContent.viewType);
                    label = String.format("%s", Integer.toString(position));
                }
                ((BtlVH) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;
            case ViewHolderType.VIEW_TYPE_TAB_SL:
                // event item banner
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null
                        && mInfo.contents.get(position) != null) {
                    action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                            mInfo.sectionList.sectionName, mInfo.contents.get(position).sectionContent.viewType);
                    label = String.format("%s", Integer.toString(position));
                }
                ((TabSlVH) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;
            case ViewHolderType.VIEW_TYPE_TCF:
                // event item banner
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null
                        && mInfo.contents.get(position) != null) {
                    action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                            mInfo.sectionList.sectionName, mInfo.contents.get(position).sectionContent.viewType);
                    label = String.format("%s", Integer.toString(position));
                }
                ((TcfVH) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;

            case ViewHolderType.VIEW_TYPE_PDV:
                // event item banner
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null
                        && mInfo.contents.get(position) != null) {
                    action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                            mInfo.sectionList.sectionName, mInfo.contents.get(position).sectionContent.viewType);
                    label = String.format("%s", Integer.toString(position));
                }
                ((PdvVH) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;
            case ViewHolderType.VIEW_TYPE_B_CM:
                // event item banner
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null
                        && mInfo.contents.get(position) != null) {
                    action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                            mInfo.sectionList.sectionName, mInfo.contents.get(position).sectionContent.viewType);
                    label = String.format("%s", Integer.toString(position));
                }
                ((BCmVH) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;
            case ViewHolderType.VIEW_TYPE_MAP_MUT_CATEGORY_GBA:
                mInfo.sectionList.subMenuList = new ArrayList<>();
                for (SectionContentList category : mInfo.contents.get(position).sectionContent.subProductList) {
                    SubMenuList menu = new SubMenuList();
                    menu.sectionName = category.productName;
                    menu.sectionLinkUrl = category.linkUrl;
                    mInfo.sectionList.subMenuList.add(menu);
                }
            case ViewHolderType.VIEW_TYPE_GR_PRD_1_VIP_LIST_GBA:
            case ViewHolderType.VIEW_TYPE_GR_PRD_1_VIP_LIST_GBB:
            case ViewHolderType.VIEW_TYPE_GR_PRD_1_VIP_LIST_GBC:
                viewHolder.setExtension(false);
            case ViewHolderType.VIEW_TYPE_BAN_IMG_SQUARE_GBA: // ai 매장 단품
            case ViewHolderType.VIEW_TYPE_BAN_MUT_H55_GBA:   // ai 매장 타이틀
            case ViewHolderType.VIEW_TYPE_BAN_SLD_GBC:   // ad 구좌
            case ViewHolderType.VIEW_TYPE_BAN_IMG_C3_GBA:   // tv 쇼핑 테마키워드
            case ViewHolderType.BANNER_TYPE_TODAYOPEN_AD:   // 오늘오픈ad
            case ViewHolderType.VIEW_TYPE_FPC_P:
            case ViewHolderType.VIEW_TYPE_BAN_TXT_NODATA:
            case ViewHolderType.VIEW_TYPE_BAN_IMG_C2_GBA:   // TV쇼핑 2단뷰
            case ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_COLOR_GBA:   // 적립금
            case ViewHolderType.VIEW_TYPE_API_LOGIN_BAN_TXT_IMG_COLOR_GBA:   // 적립금(API호출용)
            case ViewHolderType.VIEW_TYPE_CSP_LOGIN_BAN_IMG_GBA:   // 배너(CSP호출용)
            case ViewHolderType.VIEW_TYPE_BAN_TXT_EXP_GBA:
            case ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBA:
            case ViewHolderType.VIEW_TYPE_PRD_C_SQ:
            case ViewHolderType.VIEW_TYPE_MOLOCO_PRD_C_SQ:
            case ViewHolderType.VIEW_TYPE_PRD_C_CST_SQ:
            case ViewHolderType.VIEW_TYPE_RTS_PRD_C_CST_SQ:
            case ViewHolderType.VIEW_TYPE_PRD_C_B1:
            case ViewHolderType.VIEW_TYPE_PMO_T1_PREVIEW_D:
            case ViewHolderType.VIEW_TYPE_PMO_T1_PREVIEW_B:
            case ViewHolderType.VIEW_TYPE_PMO_T1_IMG:
            case ViewHolderType.VIEW_TYPE_PRD_PAS_SQ:
            case ViewHolderType.VIEW_TYPE_BAN_MORE_GBA:
            case ViewHolderType.VIEW_TYPE_BAN_NO_PRD:
            case ViewHolderType.VIEW_TYPE_PMO_T2_PREVIEW:
            case ViewHolderType.VIEW_TYPE_PMO_T2_IMG:
            case ViewHolderType.BANNER_TYPE_RENEWAL_PMO_T3_IMAGE:
            case ViewHolderType.VIEW_TYPE_BAN_TXT_CST_GBA:
            case ViewHolderType.VIEW_TYPE_BAN_VIP_IMG_GBA:
            case ViewHolderType.VIEW_TYPE_BAN_VIP_BENE_GBA:
            case ViewHolderType.VIEW_TYPE_BAN_VIP_CARD_GBA:
            case ViewHolderType.VIEW_TYPE_GR_PRD_2_VIP_LIST_GBA:
            case ViewHolderType.VIEW_TYPE_GR_PRD_2_VIP_LIST_GBB:
            case ViewHolderType.VIEW_TYPE_PRD_C_B1_VIP_GBA:
            case ViewHolderType.VIEW_TYPE_BAN_VIP_GBA:
            case ViewHolderType.VIEW_TYPE_PRD_MOBILE_LIVE:
            case ViewHolderType.VIEW_TYPE_BEST_MOBILE_LIVE:
            case ViewHolderType.VIEW_TYPE_NOTICED_MOBILE_LIVE:
            case ViewHolderType.VIEW_TYPE_PRD_ALARM_MOBILE_LIVE:
            case ViewHolderType.VIEW_TYPE_TITLE_MOBILE_LIVE:
            case ViewHolderType.VIEW_TYPE_TITLE_ADDED_SUB_MOBILE_LIVE:
            case ViewHolderType.VIEW_TYPE_PRD_2_MOBILE_LIVE:
            case ViewHolderType.VIEW_TYPE_PMO_GSR_C_GBA:
            case ViewHolderType.VIEW_TYPE_BAN_SLD_MOBILE_LIVE_SMALL:
            case ViewHolderType.VIEW_TYPE_BAN_SLD_MOBILE_LIVE_BROAD:
            case ViewHolderType.VIEW_TYPE_GR_HOME_CATE_TAB_GATE:
            case ViewHolderType.VIEW_TYPE_TITLE_IMG_TXT_WITH_SEPERATOR :
            case ViewHolderType.VIEW_TYPE_NO_DATA_MOBILE_LIVE :
            case ViewHolderType.VIEW_TYPE_MENU_GIFT_RECOMMEND_FIX:
            case ViewHolderType.VIEW_TYPE_BAN_IMG_ROUNDED :
            case ViewHolderType.VIEW_TYPE_BAN_SLD_BIG :
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null);
                break;
            case ViewHolderType.VIEW_TYPE_TAB_MOBILE_LIVE_MENU :
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null);
                try {
                    ((BanTabMenu)viewHolder).setItemBackground(R.drawable.bg_gradient_left_ef763d_to_right_e3317d);
                }
                catch (ClassCastException e) {
                    Ln.e(e.getMessage());
                }
                break;
            case ViewHolderType.VIEW_TYPE_BAN_IMG_SLD_GBA:
                //dual live 배너.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null && mInfo.tvLiveBanner != null) {
                    {
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                                mInfo.sectionList.sectionName, GTMEnum.GTM_ACTION_LIVE_TAIL);
                        label = String.format("%s", mInfo.tvLiveBanner.productName);
                        sectionName = mInfo.sectionList.sectionName;
                    }
                }
                viewHolder.onBindViewHolder(mContext, position,
                        mInfo, action, null, sectionName);
                break;
        }
    }


    /**
     * @param viewGroup viewGroup
     * @param viewType  viewType
     * @return BaseViewHolder
     */
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //Ln.i("FlexibleShopAdapter onCreateViewHolder - viewType : " + viewType);
        BaseViewHolder viewHolder = null;
        View itemView;

        switch (viewType) {
            /**
             * 홈 내일TV, 홈T3 이미지, 팀장님 이미지 AB테스트
             */
            case ViewHolderType.BANNER_TYPE_RENEWAL_PMO_T3_IMAGE_AB:
                // 이미지 배너.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_image_banner_ab, viewGroup, false);
                viewHolder = new FlexibleBannerImageViewHolder_AB(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_L:
                // 단품 기본형.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_clist, viewGroup, false);
                viewHolder = new LVH(itemView, this, mInfo.sectionList.navigationId);
                break;
            case ViewHolderType.VIEW_TYPE_PRD_1_640:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.renewal_banner_type_l_prd_1_640, viewGroup, false);
                viewHolder = new Prd1_640VH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_PRD_2:
                // 단품 쌍.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.renewal_banner_type_l_prd_2, viewGroup, false);
                //형태가 다름
                viewHolder = new Prd2VH(itemView);

                break;
            case ViewHolderType.VIEW_TYPE_PRD_1_LIST:
                // 편성표 부상품 형태 단품
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.renewal_banner_type_l_prd_1_list, viewGroup, false);
                viewHolder = new Prd1_ListVH(itemView);
                break;
            case ViewHolderType.BANNER_TYPE_FOOTER:
                // 회사정보 풋터.
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_bottom,
                        viewGroup, false);
                viewHolder = new FlexibleBannerFooterViewHolder(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_FXCLIST:
                // 카테고리.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_category, viewGroup, false);
                viewHolder = new FxclistVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_SUB_PRD_LIST_TEXT:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_sub_tab, viewGroup, false);
                viewHolder = new SubPrdListTextVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_B_SIC:
                // 롤링배너.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_roll_image_banner, viewGroup, false);
                viewHolder = new BSicVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_SLD_GBB:
                // 롤링배너.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_roll_image_new_gbb_banner, viewGroup, false);
                viewHolder = new BanSldGbbVH(itemView);
                break;

            case ViewHolderType.BANNER_TYPE_ROLL_IMAGE_NEW_GBA:
                // 롤링배너.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_roll_image_new_gba_banner, viewGroup, false);
                viewHolder = new BanSldGbaVH(itemView);
                break;
            case ViewHolderType.BANNER_TYPE_ROLL_IMAGE_NEW_GBH:
                // 롤링배너.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_roll_image_new_gbh, viewGroup, false);
                viewHolder = new BanSldGbhVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_PMO_T2_IMG_C:
                // 롤링배너.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_roll_image_new_gba_banner, viewGroup, false);
                viewHolder = new BanSldGbbVH(itemView);
                break;
            case ViewHolderType.BANNER_TYPE_BRAND_ROLL_IMAGE:
                // 롤링배너.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_roll_image_banner, viewGroup, false);
                viewHolder = new FlexibleBannerBrandRollImageViewHolder(itemView);

                break;
            case ViewHolderType.BANNER_TYPE_TV_ITEM:
                // 롤링배너 TVShoping.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.live_banner_pager, viewGroup, false);
                viewHolder = new FlexibleBannerTvShopRollImageViewHolder(itemView);

                break;

            case ViewHolderType.VIEW_TYPE_SE:
                // 롤링 이미지 배너형 - 왼쪽/오른쪽 미리보기 포함.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_roll_image_preview_banner, viewGroup, false);
                viewHolder = new SeVH(itemView);
                break;
            case ViewHolderType.BANNER_TYPE_BAND:
                // 띠 배너.
            case ViewHolderType.BANNER_TYPE_IMAGE:
            case ViewHolderType.BANNER_TYPE_BRAND_CONTENT:
            case ViewHolderType.BANNER_TYPE_ROLL_ONE_IMAGE:
            case ViewHolderType.BANNER_TYPE_RENEWAL_PMO_T3_IMAGE:
                // 이미지 배너.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_image_banner, viewGroup, false);
                viewHolder = new FlexibleBannerImageViewHolder(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_MAP_CX_TXT_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_discount_card, viewGroup, false);
                viewHolder = new MapCxTxtGbaVH(itemView);
                break;
            case ViewHolderType.BANNER_TYPE_BAND_NO_PADDING:
                // 이미지 배너.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_image_banner_no_padding, viewGroup, false);
                viewHolder = new FlexibleBannerImageViewHolder(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_B_ISS:
                // 이미지 배너.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_image_inner_banner, viewGroup, false);
                viewHolder = new BIssVH(itemView);
                break;
            case ViewHolderType.BANNER_TYPE_BRAND_CATEGORY:
                //브랜드 카테고리
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.brand_group_layout, viewGroup, false);
                viewHolder = new FlexibleBannerBrandCategoryViewHolder(itemView);
                break;
            case ViewHolderType.BANNER_TYPE_BRAND_DIVIDER:
                //브랜드 divider
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.brand_divier_layout, viewGroup, false);
                viewHolder = new FlexibleBannerBrandDividerViewHolder(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_SL:
                // todays hot 배너.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_todays_hot_banner, viewGroup, false);
                viewHolder = new SlVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_TSL:
                // theme deal 배너.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_theme_deal_banner, viewGroup, false);
                viewHolder = new TslVH(itemView);
                break;
            case ViewHolderType.BANNER_TYPE_TITLE:
            case ViewHolderType.BANNER_MART_TYPE_TITLE:
                // 타이틀 배너.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_mart_title_banner, viewGroup, false);
                viewHolder = new BTscVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBB:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_lnk_gbb, viewGroup, false);
                viewHolder = new BanTxtImgLnkGbbVH(itemView, mInfo.naviId);
                break;
            // 타이틀 배너.
            case ViewHolderType.BANNER_TYPE_TV_LIVE:
                // tv live banner
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_tclist, viewGroup, false);
                viewHolder = new BestDealBannerTVLiveViewHolder(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_B_MIA:
                // 핫 배너.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_now_hot_banner, viewGroup, false);
                viewHolder = new BMiaVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_DSL_X:
                // No1 Deal banner
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_no1_best_deal_banner, viewGroup, false);
                viewHolder = new DslXVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_DSL_A2:
                // No1 Deal banner
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_text_title_deal_banner, viewGroup, false);
                viewHolder = new DslA2VH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_SLD_GBC:
                // No1 Deal banner
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_ad_roll_banner, viewGroup, false);
                viewHolder = new BanSldGbcVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_SUB_SEC_LINE:
            case ViewHolderType.VIEW_TYPE_API_SUB_SEC_LINE:
                // Event item single
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_best_deal_category, viewGroup, false);
                viewHolder = new SubSecLineVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_SRL:
                // 추천딜.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_recommend_list, viewGroup, false);
                viewHolder = new SrlVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_API_SRL:
            case ViewHolderType.VIEW_TYPE_PRD_C_SQ:
                // 추천딜 (ProductList 용은 PRD_C_SQ, 같은 유형이라 PrdCSqVH 을 함께 씀)
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_c_sq, viewGroup, false);
                viewHolder = new PrdCSqVH(itemView, mInfo.naviId);
                break;
            case ViewHolderType.VIEW_TYPE_MOLOCO_PRD_C_SQ:
                // 추천딜 (ProductList 용은 PRD_C_SQ, 같은 유형이라 PrdCSqVH 을 함께 씀)
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_c_sq, viewGroup, false);
                viewHolder = new MolocoPrdCSqVH(itemView, mInfo.naviId);
                break;
            case ViewHolderType.VIEW_TYPE_PRD_C_CST_SQ:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_c_cst_sq, viewGroup, false);
                viewHolder = new PrdCCstSqVH(itemView, mInfo.naviId);
                break;
            case ViewHolderType.VIEW_TYPE_RTS_PRD_C_CST_SQ:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_c_cst_sq, viewGroup, false);
                return new RtsPrdCCstSqVH(itemView, mInfo.naviId);
            case ViewHolderType.VIEW_TYPE_PRD_C_B1:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.renewal_home_row_type_fx_prd_c_b1, viewGroup, false);
                viewHolder = new PrdCB1VH(itemView, mInfo.naviId);
                break;
            case ViewHolderType.VIEW_TYPE_B_HIM:
                // 프로모션 배너
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_promotion, viewGroup, false);
                viewHolder = new BHimVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_PC:
                // 인기 프로그램
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_tv_shopping_popular_program, viewGroup, false);
                viewHolder = new PcVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_B_DHS:
                // 백화점
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_special, viewGroup, false);
                viewHolder = new BDhsVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_SPL:
                // 테마 프로그램.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_theme_scroll_banner, viewGroup, false);
                viewHolder = new SplVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_SPC:
                // 스페셜 프로그램.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_special_scroll_banner, viewGroup, false);
                viewHolder = new SpcVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_B_IG4XN:
                // 마트장보기 빠른장보기
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_mart_quick_shopping, viewGroup, false);
                viewHolder = new BIg4xnVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BTL:
                // 마트장보기
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_mart_special, viewGroup, false);
                viewHolder = new BtlVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_TAB_SL:
                // 마트장보기 탭 형식 딜
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_mart_special_scroll_banner, viewGroup, false);
                viewHolder = new TabSlVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_TCF:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_best_shop_category_banner, viewGroup, false);
                viewHolder = new TcfVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_PDV:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_department_store_special_banner, viewGroup, false);
                viewHolder = new PdvVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_B_CM:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_text_banner, viewGroup, false);
                viewHolder = new BCmVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_MUT_H55_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_ai_title_banner, viewGroup, false);
                viewHolder = new BanMutH55GbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_IMG_SQUARE_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_ai_clist, viewGroup, false);
                viewHolder = new BanImgSquareGbaVH(itemView, this, mInfo.sectionList.sectionCode);
                break;
            case ViewHolderType.VIEW_TYPE_MAP_MUT_CATEGORY_GBA:
                // 마트 카테고리.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_mart_category, viewGroup, false);
                viewHolder = new MapMutCategoryGbaVH(itemView);
                break;
            case ViewHolderType.BANNER_TYPE_TODAYOPEN_AD:
                // 오늘오픈ad
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_todayopen_ad_banner, viewGroup, false);
                viewHolder = new TodayOpenAdViewHolder(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_FPC_P:
                // 카테고리.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_best_shop_category_banner_new, viewGroup, false);
                viewHolder = new DepartmentFpcPVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_TXT_NODATA:
                // 카테고리 내용 없을때
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_todayopen_empty_item, viewGroup, false);
                viewHolder = new BanTxtNoDataVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_IMG_C2_GBA:
                // TV쇼핑 2단뷰
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_pair_banner, viewGroup, false);
                viewHolder = new BanImgC2GbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_COLOR_GBA:
            case ViewHolderType.VIEW_TYPE_API_LOGIN_BAN_TXT_IMG_COLOR_GBA:
                // 적립금
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_reward_info, viewGroup, false);
                viewHolder = new BanTxtImgColorGbaVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_BAN_IMG_C3_GBA:
                // TV쇼핑 테마 키워드
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_tv_shopping_theme_keyword, viewGroup, false);
                viewHolder = new BanImgC3GbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_CSP_LOGIN_BAN_IMG_GBA:
                // CSP
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_csp_image_banner, viewGroup, false);
                viewHolder = new CspLoginBanImgGbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_TXT_EXP_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_popular_searching_banner, viewGroup, false);
                viewHolder = new BanTxtExpGbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_row_type_choice_lnk_gba,
                        viewGroup, false);
                viewHolder = new BanTxtImgLnkGbaVH(itemView, mInfo.naviId);
                break;
            case ViewHolderType.VIEW_TYPE_PMO_T1_IMG:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.renewal_banner_img_background_list, viewGroup, false);
                viewHolder = new PmoT1xxxVH(itemView, PmoT1xxxVH.PROMOTION_TYPE.IMAGE);
                break;
            case ViewHolderType.VIEW_TYPE_PMO_T1_PREVIEW_D:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.renewal_banner_img_background_list, viewGroup, false);
                viewHolder = new PmoT1xxxVH(itemView, PmoT1xxxVH.PROMOTION_TYPE.DEFAULT);
                break;
            case ViewHolderType.VIEW_TYPE_PMO_T1_PREVIEW_B:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.renewal_banner_img_background_list, viewGroup, false);
                viewHolder = new PmoT1xxxVH(itemView, PmoT1xxxVH.PROMOTION_TYPE.BRAND);
                break;
            case ViewHolderType.VIEW_TYPE_PMO_T2_PREVIEW:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.renewal_home_row_type_fx_pmo_t2_common, viewGroup, false);
                viewHolder = new PmoT2xxxVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_IMG_SLD_GBA:
                // 내일TV 타이틀 영역
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_vod_img_sld, viewGroup, false);
                viewHolder = new BanImgSldGbaVH(itemView, mInfo.naviId);
                break;
            case ViewHolderType.VIEW_TYPE_PRD_PAS_SQ:
                // 추천딜 (ProductList 용은 PRD_C_SQ, 같은 유형이라 PrdCSqVH 을 함께 씀)
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_pas_sq, viewGroup, false);
                viewHolder = new PrdPasSqVH(itemView, mInfo.naviId);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_MORE_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_more_gba, viewGroup, false);
                viewHolder = new BanMoreGbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_NO_PRD:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_no_prd, viewGroup, false);
                viewHolder = new BanNoPrdVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_TXT_CST_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_title_cst, viewGroup, false);
                viewHolder = new BanTxtCstGBAVH(itemView);
                break;
            /**
             * VIP 매장
             */
            case ViewHolderType.VIEW_TYPE_BAN_VIP_GBA:
                // VIP 웰컴 메세지
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_vip_gba, viewGroup, false);
                viewHolder = new BanVIPGbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_VIP_IMG_GBA:
                // VIP 이미지 배너
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_vip_img_gba, viewGroup, false);
                viewHolder = new BanVIPImgGbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_VIP_BENE_GBA:
                // VIP 혜택 배너
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_vip_bene_gba, viewGroup, false);
                viewHolder = new BanVIPBeneGbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_VIP_CARD_GBA:
                // VIP 카드 정보 배너
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_vip_card_gba, viewGroup, false);
                viewHolder = new BanVIPCardGbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_GR_PRD_1_VIP_LIST_GBA:
                // VIP  개인화 아이템 찜한 상품, 매직딜데이 그룹
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_gr_prd_1_vip_list_common, viewGroup, false);
                viewHolder = new GrPrd1VIPListGbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_GR_PRD_2_VIP_LIST_GBA:
                // VIP 최근 검색어 추천 상품, 선호 브랜드 인기 상품 그룹 (2단 뷰 4개 상품, 이동형)
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_gr_prd_2_vip_list, viewGroup, false);
                viewHolder = new GrPrd2VIPListGbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_GR_PRD_2_VIP_LIST_GBB:
                // VIP 최근 검색어 추천 상품, 선호 브랜드 인기 상품 그룹 (2단 뷰 4개 상품, 갱신형)
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_gr_prd_2_vip_list, viewGroup, false);
                viewHolder = new GrPrd2VIPListGbbVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_GR_PRD_1_VIP_LIST_GBB:
                // VIP 재구매 상품 / 관련 추천 상품 타이틀 + 부상품 UI
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_gr_prd_1_vip_list_common, viewGroup, false);
                viewHolder = new GrPrd1VIPListGbbVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_GR_PRD_1_VIP_LIST_GBC:
                // VIP VIP 찜상품
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_gr_prd_1_vip_list_common, viewGroup, false);
                viewHolder = new GrPrd1VipListGbcVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_PRD_C_B1_VIP_GBA:
                // VIP 웰컴 메세지
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_c_b1_vip_gba, viewGroup, false);
                viewHolder = new PrdCB1VIPGbaVH(itemView);
                break;
            /**
             * 쇼핑 라이브 매장
             */
            case ViewHolderType.VIEW_TYPE_TITLE_MOBILE_LIVE:
                // 쇼핑라이브 매장 공통 타이틀
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_title_mobile_live, viewGroup, false);
                viewHolder = new TitleMobileLiveVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_PRD_MOBILE_LIVE:
                // 쇼핑라이브 오늘의 라이브
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_shopping_live_mobile_live, viewGroup, false);
                viewHolder = new BanMLPrdMobileLive(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_NOTICED_MOBILE_LIVE:
                // 쇼핑라이브 라이브 예고
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_shopping_live_noticed, viewGroup, false);
                viewHolder = new BanMLPrdNoticed(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BEST_MOBILE_LIVE:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_common_recycler, viewGroup, false);
                viewHolder = new BanMLPrdBest(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_PRD_ALARM_MOBILE_LIVE:
                // 쇼핑라이브 방송알림
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_shoppinglive_alarm, viewGroup, false);
                viewHolder = new BanMLPrdAlarmMobileLive(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_TITLE_ADDED_SUB_MOBILE_LIVE:
                // 서브타이틀 포함된 타이틀
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_shoppinglive_title_sub_added, viewGroup, false);
                viewHolder = new BanMLPrdTitleSubAddedMobileLive(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_PRD_2_MOBILE_LIVE:
                // 쇼핑라이브 2단뷰
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_shopping_live_prd_2, viewGroup, false);
                viewHolder = new BanMLPrd2(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_PMO_GSR_C_GBA:
                // 쇼핑라이브 2단뷰
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_pmo_gsr_c_gba, viewGroup, false);
                viewHolder = new PmoGsrCGba(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_SLD_MOBILE_LIVE_SMALL:
                // 쇼핑라이브 미니배너 구좌.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_sld_mobile_live_small, viewGroup, false);
                viewHolder = new BanSldMobileLiveSmallVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_SLD_MOBILE_LIVE_BROAD:
                // 쇼핑라이브 현재 방송중 상품 구좌.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_sld_mobile_live_broad, viewGroup, false);
                viewHolder = new BanSldMobileLiveBroadVH(itemView, mContext);
                break;
            case ViewHolderType.VIEW_TYPE_GR_HOME_CATE_TAB_GATE:
                // 홈 카테고리 구좌
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_gr_home_cate_tab_gate, viewGroup, false);
                viewHolder = new GrHomeCateTabGateVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_TITLE_IMG_TXT_WITH_SEPERATOR :
                // 새로 추가된 타이틀 (구분자 가운데 있는) 구좌
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_title_wine, viewGroup, false);
                viewHolder = new BanTitleWineVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_TAB_MOBILE_LIVE_MENU :
                // 모바일 라이브 탭 메뉴
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_tab_basic, viewGroup, false);
//                Ln.d("hklim Create BeautyTabMenuTomm")
                viewHolder = new BanTabMenu(itemView, viewGroup.getContext());
                break;
            case ViewHolderType.VIEW_TYPE_NO_DATA_MOBILE_LIVE :
                // 모바일 라이브에도 뷰티 처럼 no data가 내려와서 nodata 처리해준다.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_beauty_no_data, viewGroup, false);
                viewHolder = new BanBeaytyNoData(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_MENU_GIFT_RECOMMEND_FIX:
                // 이거 뭐지?
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_menu_gift_recommend_fix, viewGroup, false);
                viewHolder = new MenuGiftRecommendFixVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_IMG_ROUNDED :
                // 라운드 들어간 이미지 슬라이드 배너
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_image_rounded, viewGroup, false);
                viewHolder = new BannerImgRoundedVH(itemView);
                break;

                /** 익스클루시브에서 추가 됨. 다른데서도 추가될 법한 배너라 이 곳에 추가 **/
            case ViewHolderType.VIEW_TYPE_BAN_SLD_BIG:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_type_ban_sld_common, viewGroup, false);
                viewHolder = new BanSldCommonVH(itemView);
                ((BanSldCommonVH) viewHolder).setBannerHeight(DisplayUtils.getScreenWidth());
                ((BanSldCommonVH) viewHolder).setScaleType(BanSldCommonVH.SCALE_TYPE.type_fit);
                break;
        }

        return viewHolder;
    }


    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.onViewAttachedToWindow();
    }

    @Override
    public void onViewDetachedFromWindow(BaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.onViewDetachedFromWindow();
    }

    /**
     * 상품이 속한 탭메뉴 인덱스를 반환한다.
     *
     * @param position 삼품의 위치
     * @return 소속된 탭메뉴 인덱스
     */
    public int getTabmenuIndex(int position) {
        int tabIndex = 0;
        if (getInfo() != null && getInfo().categoryIndex != null) {
            for (int i = 0; i < getInfo().categoryIndex.size(); i++) {
                if (position >= getInfo().categoryIndex.get(i)) {
                    tabIndex = i;
                }
            }

        }

        return tabIndex;
    }

    /**
     * 모바일라이브 탭매장 신설
     * 모바일라이브 방송알림 아이콘 UI업데이트
     *
     * @param eventType
     */
    public void updateMLAlarmData(String eventType) {
        //notifyItemChanged(position) 인덱스값에서 에러가 날수도있으니 try catch 묶음
        try {
            int position = -1;
            //방송알림 뷰의 위치찾는다
            for (int i = 0; i < mInfo.contents.size(); i++) {
                if (mInfo.contents.get(i).type == ViewHolderType.VIEW_TYPE_PRD_ALARM_MOBILE_LIVE) {
                    position = i;
                    break;
                }
            }

            //방송알림 상태값을 바꿔준다
            if (position > -1) {
                if (Events.AlarmUpdatetMLEvent.MAKE_ALARM_OK.equals(eventType)) {
                    mInfo.contents.get(position).sectionContent.setBroadAlarmDoneYn("Y");
                    notifyItemChanged(position);
                } else {
                    mInfo.contents.get(position).sectionContent.setBroadAlarmDoneYn("N");
                    notifyItemChanged(position);
                }
            }

        } catch (Exception e) {
            Ln.e(e);
        }
    }
}
