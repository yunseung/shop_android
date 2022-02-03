/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.todayopen;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopAdapter;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import roboguice.util.Ln;

/**
 *
 *
 */
public class TodayOpenAdapter extends FlexibleShopAdapter {

    private boolean isFillter = false;
    private int filterPosition = 0;
    private String[] categoryNo;
    private int filterCount;
    private int currentFillterIndex;
    private boolean isFooterChange;

    private  ShopInfo mCategoryInfo;
    private  List<ShopInfo.ShopItem> tempContents;
    private  List<ShopInfo.ShopItem> emptyContents;


    public TodayOpenAdapter(Context context) {
        super(context);
    }

    @Override
    public void setInfo(ShopInfo info) {
        mInfo = info;
        ShopInfo.ShopItem  content = new ShopInfo.ShopItem();
        content.type = ViewHolderType.BANNER_TYPE_FOOTER;

        //카테고리가 비어있는 경우에 표시할 리스트를 미리 만들어 놓는다.
        tempContents = new ArrayList<ShopInfo.ShopItem>();
        emptyContents = new ArrayList<ShopInfo.ShopItem>();
        emptyContents.add(content);

        addContentsItem(mInfo);
    }

    /**
     * footer의 위치를 변경한다.
     * 필터를 사용할때 하단메뉴 표시를 위해 사용
     * @param position
     */
    public void setFooterPosition(int position){
        if(!isFooterChange) {
            isFooterChange = true;
            filterPosition = position + 1;
            try {
                ShopInfo.ShopItem footerItem = (ShopInfo.ShopItem) getItem(mInfo.contents.size() - 1);
                mInfo.contents.remove(footerItem);
                mInfo.contents.add(filterPosition, footerItem);
            }
            catch (NullPointerException e){
                Ln.e(e.getMessage());
            }
        }
    }

    public boolean isFillter() {
        return isFillter;
    }

    /**
     * 필더를 적용한다
     * 만약 footer의 위치가 변경되어있다면 마지막으로 보낸다.
     * @param isFillter
     * @param categoryNo
     * @param filterCount
     */
    public void setFillter(boolean isFillter, String[] categoryNo, int filterCount) {
        if(filterPosition > 0){
            ShopInfo.ShopItem footerItem =  (ShopInfo.ShopItem) getItem(filterPosition);
            mInfo.contents.remove(footerItem);
            mInfo.contents.add(footerItem);
            filterPosition = 0;
        }
        this.isFooterChange = false;
        this.isFillter = isFillter;
        this.categoryNo = categoryNo;
        this.filterCount = filterCount + 1;

        if(isFillter && filterCount == 0) {
            mInfo.contents = emptyContents;
        }else{
            if(mInfo.contents != null && !mInfo.contents.isEmpty() && mInfo.contents.get(0).type == ViewHolderType.BANNER_TYPE_BAND) {
                this.filterCount = this.filterCount + 1;
            }

            if(tempContents != null){
                mInfo.contents = tempContents;
            }
        }

        currentFillterIndex = 0;
    }

    public String[] getCategoryNo() {
        return categoryNo;
    }

    public void setCategoryNo(String[] categoryNo) {
        this.categoryNo = categoryNo;
    }

    public int getFilterCount() {
        return filterCount;
    }

    public int getCurrentFillterIndex() {
        return currentFillterIndex;
    }

    public void currentFillterIndexNaxt() {
        currentFillterIndex++;
    }


    /**
     * @return
     */
    @Override
    public int getItemCount() {
        int count = 0;
        if (mInfo != null) {

            if (isFillter()) {
                count = filterCount;
            } else {
                count = mInfo.contents.size();
            }
        }
        return count;
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case ViewHolderType.VIEW_TYPE_L:
            // 단품 기본형.
                View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.home_row_type_fx_clist, viewGroup, false);
            return  new LVH(itemView, this,mInfo.sectionList.sectionCode);
            default:
                return super.onCreateViewHolder(viewGroup, viewType);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder viewHolder, int position) {
        String action = GTMEnum.GTM_NONE;
        String label = GTMEnum.GTM_NONE;
        // String tab = GTMEnum.GTM_NONE;
        String sectionName = GTMEnum.GTM_NONE;
        String productCode = GTMEnum.GTM_NONE;

        switch (viewHolder.getItemViewType()) {
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

            default:
                super.onBindViewHolder(viewHolder, position);
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void addContentsItem(ShopInfo info) {
        if (info.contents != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                tempContents.addAll(info.contents);
            } else {
                for(ShopInfo.ShopItem item : info.contents) {
                    tempContents.add(item);
                }
            }
        }
    }

}
