package gsshop.mobile.v2.home.shop.ltype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.comholder.FooterViewHolder;
import gsshop.mobile.v2.home.shop.comholder.NoDataViewHolder;
import gsshop.mobile.v2.home.shop.event.renewal.Holder.BanImgCxGbaVH;
import gsshop.mobile.v2.home.shop.event.renewal.Holder.BanImgTxtGbaVH;
import gsshop.mobile.v2.home.shop.event.renewal.Holder.BanTxtImgSldGbaVH;
import gsshop.mobile.v2.home.shop.event.renewal.Holder.EventShopTabViewHolder;
import gsshop.mobile.v2.home.shop.flexible.BanTxtImgLnkGbbVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.BanTxtCstGBAVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.MolocoPrdCSqVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.PmoT1xxxVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.PrdCB1VH;
import gsshop.mobile.v2.home.shop.renewal.flexible.PrdCCstSqVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.PrdCSqVH;
import gsshop.mobile.v2.home.shop.renewal.publicusage.Prd1_550VH;
import gsshop.mobile.v2.home.shop.renewal.publicusage.Prd1_640VH;
import gsshop.mobile.v2.home.shop.renewal.publicusage.Prd2VH;
import gsshop.mobile.v2.home.shop.renewal.views.CommonTitleLayout;
import roboguice.util.Ln;

public class GSChoiceShopAdapter extends RecyclerView.Adapter<BaseViewHolderV2> {

    protected final Context mContext;

    protected List<ModuleList> mModuleList;
    private String ajaxPageUrl;

    private String navigationId;

    // 헤더 뷰 타입 확인을 위한 상수
    private static final int[] HEADER_VIEW_TYPES =
            { ViewHolderType.VIEW_TYPE_TAB_SLD_GBA, ViewHolderType.VIEW_TYPE_TAB_SLD_GBB,
                    ViewHolderType.VIEW_TYPE_TAB_SLD_ANK_GBA, ViewHolderType.VIEW_TYPE_TAB_SLD_ANK_GBB,
                    ViewHolderType.VIEW_TYPE_TAB_IMG_ANCH_GBA
            };

    public GSChoiceShopAdapter(Context context, String strNavigationId) {
        mContext = context;
        navigationId = strNavigationId;
    }

    @Override
    public int getItemViewType(int position) {
        return mModuleList.get(position).tempViewType;
    }

    public String getItemSectionCode(int position) {
        return mModuleList.get(position).tabSeq;
    }

    @Override
    public BaseViewHolderV2 onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        BaseViewHolderV2 viewHolder = null;
        View itemView = null;

        switch (viewType) {
            case ViewHolderType.VIEW_TYPE_BAN_IMG_H000_GBC:
            case ViewHolderType.VIEW_TYPE_BAN_IMG_H000_GBD:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_img_banner, viewGroup, false);
                viewHolder = new BanImgH000GbxVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_SLD_GBE:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_choice_roll_banner, viewGroup, false);
                viewHolder = new BanSldGbeVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_SLD_GBF:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_choice_gbf_list, viewGroup, false);
                viewHolder = new BanSldGbfVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_SLD_GBG:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_sld_gbg, viewGroup, false);
                viewHolder = new BanSldGbgVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_TAB_SLD_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_gschoice_category, viewGroup, false);
                viewHolder = new TabSldGbaVH(itemView, navigationId);
                break;
            case ViewHolderType.VIEW_TYPE_TAB_SLD_GBB:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_gschoice_category_b, viewGroup, false);
                viewHolder = new TabSldGbbVH(itemView, navigationId);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_MUT_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_square_prd, viewGroup, false);
                viewHolder = new BanMutGbaVH(itemView);
                break;
            case ViewHolderType.BANNER_TYPE_FIXED_80_IMG_C_GBA:
            case ViewHolderType.BANNER_TYPE_FIXED_80_IMG_L_GBA:
            case ViewHolderType.BANNER_TYPE_FIXED_80_IMG_R_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_banner_fixed_img, viewGroup, false);
                viewHolder = new BanImgF80XGbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_MAP_CX_GBC:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_choice_banner, viewGroup, false);
                viewHolder = new MapCxGbcVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_PRD_1_640:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.renewal_banner_type_l_prd_1_640, viewGroup, false);
                viewHolder = new Prd1_640VH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_PRD_1_550:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.renewal_banner_type_l_prd_550, viewGroup, false);
                viewHolder = new Prd1_550VH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_PRD_2:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.renewal_banner_type_l_prd_2, viewGroup, false);
                viewHolder = new Prd2VH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_API_SRL:
            case ViewHolderType.VIEW_TYPE_PRD_C_SQ:
                // 추천딜 형태 (PRD_C_SQ)
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_c_sq, viewGroup, false);
                viewHolder = new PrdCSqVH(itemView, navigationId);
                break;
            case ViewHolderType.VIEW_TYPE_MOLOCO_PRD_C_SQ:
                // 추천딜 형태 (PRD_C_SQ)
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_c_sq, viewGroup, false);
                viewHolder = new MolocoPrdCSqVH(itemView, navigationId);
                break;
            case ViewHolderType.VIEW_TYPE_PRD_C_CST_SQ :
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_c_cst_sq, viewGroup, false);
                viewHolder = new PrdCCstSqVH(itemView, navigationId);
                break;
            case ViewHolderType.BANNER_TYPE_FOOTER:
                // 회사정보 풋터.
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_bottom,
                        viewGroup, false);
                viewHolder = new FooterViewHolder(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBA:
            case ViewHolderType.VIEW_TYPE_BAN_TITLE_PRD_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_row_type_choice_lnk_gba,
                        viewGroup, false);
                viewHolder = new BanTxtImgLnkGbaVH(itemView, navigationId);
                // 하단 라인 그리기
                if(viewType == ViewHolderType.VIEW_TYPE_BAN_TITLE_PRD_GBA) {
                    try {
                        CommonTitleLayout tempLayout = itemView.findViewById(R.id.common_title_layout);
                        tempLayout.findViewById(R.id.iv_separator).setVisibility(View.VISIBLE);
                    }
                    catch (ClassCastException e){
                        Ln.e(e.getMessage());
                    }
                    catch (NullPointerException e) {
                        Ln.e(e.getMessage());
                    }
                }
                break;
            case ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBB:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_lnk_gbb, viewGroup, false);
                viewHolder = new BanTxtImgLnkGbbVH(itemView, navigationId);
                break;
            case ViewHolderType.VIEW_TYPE_PRD_C_B1:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.renewal_home_row_type_fx_prd_c_b1, viewGroup, false);
                viewHolder = new PrdCB1VH(itemView, navigationId);
                break;
            case ViewHolderType.VIEW_TYPE_PMO_T1_PREVIEW_B:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.renewal_banner_img_background_list, viewGroup, false);
                viewHolder = new PmoT1xxxVH(itemView, PmoT1xxxVH.PROMOTION_TYPE.BRAND);
                break;

            case ViewHolderType.BANNER_TYPE_NO_DATA:
                // 회사정보 풋터.
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_no_data,
                        viewGroup, false);
                viewHolder = new NoDataViewHolder(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_TAB_ANK_GBA:
            case ViewHolderType.VIEW_TYPE_TAB_ANK_GBA_1ST:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_l_sticky_type_event_tab, viewGroup, false);
                viewHolder = new EventShopTabViewHolder(itemView, navigationId);
                break;

            case ViewHolderType.VIEW_TYPE_TXT_IMG_SLD_GBA:
                // 슬라이드 이미지
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_l_roll_image_sld_gba, viewGroup, false);
                viewHolder = new BanTxtImgSldGbaVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_IMG_CX_GBA:
                // 이미지 메뉴
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_l_menu_img_cx_gba, viewGroup, false);
                viewHolder = new BanImgCxGbaVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_IMG_TXT_GBA:
                // 이미지 텍스트 뷰
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_l_ban_img_txt_gba, viewGroup, false);
                viewHolder = new BanImgTxtGbaVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_TAB_SLD_ANK_GBA:
                // 새벽배송 매장 새로운 앵커 (좌우 플리킹 가능, 최상단 한 개만 존재하는 앵커)
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_gschoice_category_b, viewGroup, false);
                viewHolder = new TabSldAnkGbaVH(itemView, navigationId);
                break;

            case ViewHolderType.VIEW_TYPE_TAB_SLD_ANK_GBB :
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_gschoice_category, viewGroup, false);
                viewHolder = new TabSldAnkGbbVH(itemView, navigationId);
                break;

            case ViewHolderType.VIEW_TYPE_TAB_IMG_ANCH_GBA :
                // 앵커형 이미지 아이템 뷰
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_l_menu_img_cx_gba, viewGroup, false);
                viewHolder = new TabImgAnchGbaVH(itemView, navigationId);
                break;

            case ViewHolderType.VIEW_TYPE_SLD_GBB_MORE :
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_gs_choice_go_brand_shop, viewGroup, false);
                viewHolder = new BanSldGbbMoreVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_GR_BRD_GBA :
                //시그니처매장 상품
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_gr_brd_gba, viewGroup, false);
                viewHolder = new GrBrdGbaVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_BAN_API_MORE_GBA :
                //시그니처매장 브랜드 더보기
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_api_more_gba, viewGroup, false);
                viewHolder = new BanApiMoreGbaVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_BAN_TXT_SUB_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_txt_sub_gba, viewGroup, false);
                viewHolder = new BanTxtSubGbaVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_PRD_VOD_LIST:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_vod_list, viewGroup, false);
                viewHolder = new PrdVodListVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_PRD_MLT_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_mlt_gba, viewGroup, false);
                viewHolder = new PrdMltGbaVH(itemView);
                break;

            case ViewHolderType.VIEW_TYPE_BAN_TXT_CST_GBA :
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_title_cst, viewGroup, false);
                viewHolder = new BanTxtCstGBAVH(itemView);
                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolderV2 viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case ViewHolderType.VIEW_TYPE_BAN_IMG_H000_GBC:
            case ViewHolderType.VIEW_TYPE_BAN_IMG_H000_GBD:
            case ViewHolderType.VIEW_TYPE_BAN_SLD_GBE:
            case ViewHolderType.VIEW_TYPE_BAN_SLD_GBF:
            case ViewHolderType.VIEW_TYPE_BAN_SLD_GBG:
            case ViewHolderType.VIEW_TYPE_TAB_SLD_GBA:
            case ViewHolderType.VIEW_TYPE_TAB_SLD_GBB:
            case ViewHolderType.VIEW_TYPE_BAN_MUT_GBA:
            case ViewHolderType.BANNER_TYPE_FOOTER:
            case ViewHolderType.BANNER_TYPE_NO_DATA:
            case ViewHolderType.BANNER_TYPE_FIXED_80_IMG_C_GBA:
            case ViewHolderType.BANNER_TYPE_FIXED_80_IMG_L_GBA:
            case ViewHolderType.BANNER_TYPE_FIXED_80_IMG_R_GBA:
            case ViewHolderType.VIEW_TYPE_MAP_CX_GBC:
            case ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBA:
            case ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBB:
            case ViewHolderType.VIEW_TYPE_PRD_C_B1:
            case ViewHolderType.VIEW_TYPE_API_SRL:
            case ViewHolderType.VIEW_TYPE_PRD_C_SQ:
            case ViewHolderType.VIEW_TYPE_MOLOCO_PRD_C_SQ:
            case ViewHolderType.VIEW_TYPE_PRD_C_CST_SQ :
            case ViewHolderType.VIEW_TYPE_PMO_T1_PREVIEW_B:
            case ViewHolderType.VIEW_TYPE_PRD_1_640:
            case ViewHolderType.VIEW_TYPE_PRD_1_550:
            case ViewHolderType.VIEW_TYPE_PRD_2:
            case ViewHolderType.VIEW_TYPE_TXT_IMG_SLD_GBA:
            case ViewHolderType.VIEW_TYPE_IMG_CX_GBA:
            case ViewHolderType.VIEW_TYPE_IMG_TXT_GBA:
            case ViewHolderType.VIEW_TYPE_TAB_ANK_GBA:
            case ViewHolderType.VIEW_TYPE_TAB_ANK_GBA_1ST:
            case ViewHolderType.VIEW_TYPE_TAB_SLD_ANK_GBA:
            case ViewHolderType.VIEW_TYPE_TAB_SLD_ANK_GBB:
            case ViewHolderType.VIEW_TYPE_SLD_GBB_MORE :
            case ViewHolderType.VIEW_TYPE_GR_BRD_GBA :
            case ViewHolderType.VIEW_TYPE_BAN_API_MORE_GBA :
            case ViewHolderType.VIEW_TYPE_PRD_VOD_LIST:
            case ViewHolderType.VIEW_TYPE_BAN_TXT_SUB_GBA:
            case ViewHolderType.VIEW_TYPE_PRD_MLT_GBA:
            case ViewHolderType.VIEW_TYPE_BAN_TXT_CST_GBA :
            case ViewHolderType.VIEW_TYPE_TAB_IMG_ANCH_GBA :
            case ViewHolderType.VIEW_TYPE_BAN_TITLE_PRD_GBA:
                viewHolder.onBindViewHolder(mContext, position, mModuleList);
                break;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mModuleList != null) {
            count = mModuleList.size();
        }
        return count;
    }

    public void setInfo(List<ModuleList> info) {
        mModuleList = info;
    }

    public void addInfo(List<ModuleList> info) {
        mModuleList.addAll(info);
    }

    public void addInfo(List<ModuleList> info, int position) {
        if (position < 0) {
            return;
        }
        int itemCount = getItemCount();
        for (int i = itemCount - 1; i > position; i--) {
            mModuleList.remove(i);
        }
        addInfo(info);
        notifyItemRangeChanged(position + 1, info.size());
    }

    public void insertInfo(List<ModuleList> info, int position) {
        //브랜드 더보기 뷰홀더 제거
        mModuleList.remove(position);
        //브랜드 추가
        mModuleList.addAll(position, info);
        notifyItemRangeChanged(position, info.size() + 1);
    }

    public int getHeaderPosition() {
        int headerPosition = -1;
        if (mModuleList != null) {
            for (int i = 0; i < mModuleList.size() - 1; i++) {
                if (isHeaderView(getItemViewType(i)) ||
                        ViewHolderType.VIEW_TYPE_TAB_ANK_GBA == getItemViewType(i)) {
                    headerPosition = i;
                }
            }
        }
        return headerPosition;
    }

    public List<ModuleList> getInfo() {
        return mModuleList;
    }

    public void setAjaxPageUrl(String url) {
        ajaxPageUrl = url;
    }

    public String getAjaxPageUrl() {
        return ajaxPageUrl;
    }

    public void setChoiceList(List<ModuleList> list) {
        mModuleList = list;
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolderV2 holder) {
        super.onViewAttachedToWindow(holder);
        holder.onViewAttachedToWindow();
    }

    @Override
    public void onViewDetachedFromWindow(BaseViewHolderV2 holder) {
        super.onViewDetachedFromWindow(holder);
        holder.onViewDetachedFromWindow();
    }

    public List<ModuleList> getChoiceList() {
        return mModuleList;
    }

    public void addSubChoiceList(ModuleList list) {
        mModuleList.add(list);
    }

    protected boolean isHeaderView(int type) {
        for (int tempType : HEADER_VIEW_TYPES) {
            if (tempType == type) {
                return true;
            }
        }
        return false;
    }
}
